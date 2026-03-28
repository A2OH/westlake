/*
 * OHBridge native — dual mode:
 * 1. In-process: draws to host's Android Canvas via JNI callbacks
 * 2. Subprocess (dlist_mode): creates real Bitmap+Canvas via JNI, renders with
 *    real Skia, copies pixels to mmap'd shared memory. Host reads raw pixels.
 */
#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>

#define TAG "OHBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

static JavaVM* g_vm = NULL;

/* ═══ In-process mode globals ═══ */
static jclass hostClass = NULL;
static jmethodID beginFrame, endFrame, newPaintM, getPaintM, newPathM, getPathM;
static jfieldID hostCanvas;

/* ═══ Subprocess pixel buffer mode ═══ */
static int dlist_mode = 0;
static unsigned char* shm_ptr = NULL;
static int shm_seq = 0;
#define SHM_HEADER 128
#define FRAME_W 480
#define FRAME_H 800
#define FRAME_SIZE (FRAME_W * FRAME_H * 4)
#define SHM_TOTAL (SHM_HEADER + 2 * FRAME_SIZE)
#define WLK_MAGIC 0x574C4B46

/* Real Android Canvas/Bitmap for subprocess rendering */
static jobject g_bitmap = NULL;   /* Bitmap (global ref) */
static jobject g_canvas = NULL;   /* Canvas (global ref) */
static int g_activeBuf = 0;

/* Cached method IDs for real Android Canvas */
static jclass canvasClass = NULL;
static jmethodID c_drawColor, c_drawRect, c_drawRoundRect, c_drawCircle;
static jmethodID c_drawLine, c_drawText, c_drawPath, c_save, c_restore;
static jmethodID c_translate, c_scale, c_clipRect;
static jclass paintClass = NULL;
static jmethodID p_init, p_setColor, p_setStrokeWidth, p_setTextSize, p_setAntiAlias;
static jmethodID p_setStyle, p_measureText, p_getFontMetrics, p_setAlpha;
static jclass bitmapClass = NULL;
static jmethodID bm_createBitmap, bm_copyPixelsToBuffer;
static jclass canvasInitClass = NULL;
static jmethodID canvas_init_bmp;
static jclass bbClass = NULL;
static jmethodID bb_allocate, bb_order, bb_array;
static jobject g_le_order = NULL; /* ByteOrder.LITTLE_ENDIAN */

static jclass pathClass = NULL;
static jmethodID path_init, path_moveTo, path_lineTo, path_close, path_reset;

/* Paint pool for pen/brush/font handles */
#define MAX_PAINTS 256
static jobject g_paints[MAX_PAINTS];
static int g_next_paint = 1;

static int alloc_paint(JNIEnv* e) {
    int idx = g_next_paint++;
    if (idx >= MAX_PAINTS) idx = g_next_paint = 1; /* wrap */
    jobject p = (*e)->NewObject(e, paintClass, p_init, 1); /* ANTI_ALIAS_FLAG */
    if (g_paints[idx]) (*e)->DeleteGlobalRef(e, g_paints[idx]);
    g_paints[idx] = (*e)->NewGlobalRef(e, p);
    (*e)->DeleteLocalRef(e, p);
    return idx;
}

static jobject get_paint(int idx) {
    if (idx <= 0 || idx >= MAX_PAINTS) return NULL;
    return g_paints[idx];
}

static void shm_init() {
    const char* path = getenv("WESTLAKE_SHM");
    if (!path || !path[0]) path = "/data/local/tmp/westlake/westlake_shm";
    int fd = open(path, O_RDWR);
    if (fd < 0) { LOGI("shm open FAIL: %s", path); return; }
    off_t sz = lseek(fd, 0, SEEK_END);
    if (sz < SHM_TOTAL) { ftruncate(fd, SHM_TOTAL); }
    shm_ptr = mmap(NULL, SHM_TOTAL, PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);
    close(fd);
    if (shm_ptr == MAP_FAILED) { shm_ptr = NULL; LOGI("shm mmap FAIL"); return; }
    *(int*)(shm_ptr+0) = WLK_MAGIC;
    *(int*)(shm_ptr+4) = 1; /* version=1 pixel buffer */
    *(int*)(shm_ptr+8) = FRAME_W;
    *(int*)(shm_ptr+12) = FRAME_H;
    msync(shm_ptr, SHM_HEADER, MS_SYNC);
    dlist_mode = 1;
    LOGI("shm_init: PIXEL mode (real Canvas), path=%s, total=%d bytes", path, SHM_TOTAL);
}

static void cache_canvas_classes(JNIEnv* e) {
    /* Cache classes and methods for real Android Canvas/Bitmap */
    if (canvasClass) return;

    canvasClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "android/graphics/Canvas"));
    c_drawColor = (*e)->GetMethodID(e, canvasClass, "drawColor", "(I)V");
    c_drawRect = (*e)->GetMethodID(e, canvasClass, "drawRect", "(FFFFLandroid/graphics/Paint;)V");
    c_drawRoundRect = (*e)->GetMethodID(e, canvasClass, "drawRoundRect", "(FFFFFFLandroid/graphics/Paint;)V");
    c_drawCircle = (*e)->GetMethodID(e, canvasClass, "drawCircle", "(FFFLandroid/graphics/Paint;)V");
    c_drawLine = (*e)->GetMethodID(e, canvasClass, "drawLine", "(FFFFLandroid/graphics/Paint;)V");
    c_drawText = (*e)->GetMethodID(e, canvasClass, "drawText", "(Ljava/lang/String;FFLandroid/graphics/Paint;)V");
    c_drawPath = (*e)->GetMethodID(e, canvasClass, "drawPath", "(Landroid/graphics/Path;Landroid/graphics/Paint;)V");
    c_save = (*e)->GetMethodID(e, canvasClass, "save", "()I");
    c_restore = (*e)->GetMethodID(e, canvasClass, "restore", "()V");
    c_translate = (*e)->GetMethodID(e, canvasClass, "translate", "(FF)V");
    c_scale = (*e)->GetMethodID(e, canvasClass, "scale", "(FF)V");
    c_clipRect = (*e)->GetMethodID(e, canvasClass, "clipRect", "(FFFF)Z");
    canvas_init_bmp = (*e)->GetMethodID(e, canvasClass, "<init>", "(Landroid/graphics/Bitmap;)V");

    paintClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "android/graphics/Paint"));
    p_init = (*e)->GetMethodID(e, paintClass, "<init>", "(I)V");
    p_setColor = (*e)->GetMethodID(e, paintClass, "setColor", "(I)V");
    p_setStrokeWidth = (*e)->GetMethodID(e, paintClass, "setStrokeWidth", "(F)V");
    p_setTextSize = (*e)->GetMethodID(e, paintClass, "setTextSize", "(F)V");
    p_setAntiAlias = (*e)->GetMethodID(e, paintClass, "setAntiAlias", "(Z)V");
    p_measureText = (*e)->GetMethodID(e, paintClass, "measureText", "(Ljava/lang/String;)F");
    p_getFontMetrics = (*e)->GetMethodID(e, paintClass, "getFontMetrics", "()Landroid/graphics/Paint$FontMetrics;");
    jclass fmClass = (*e)->FindClass(e, "android/graphics/Paint$FontMetrics");
    /* fm_ascent/descent are used in in-process mode only */

    bitmapClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "android/graphics/Bitmap"));
    jclass configClass = (*e)->FindClass(e, "android/graphics/Bitmap$Config");
    jfieldID argb8888 = (*e)->GetStaticFieldID(e, configClass, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
    jobject config = (*e)->GetStaticObjectField(e, configClass, argb8888);
    bm_createBitmap = (*e)->GetStaticMethodID(e, bitmapClass, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    bm_copyPixelsToBuffer = (*e)->GetMethodID(e, bitmapClass, "copyPixelsToBuffer", "(Ljava/nio/Buffer;)V");

    /* Create the render bitmap + canvas */
    jobject bmp = (*e)->CallStaticObjectMethod(e, bitmapClass, bm_createBitmap, FRAME_W, FRAME_H, config);
    g_bitmap = (*e)->NewGlobalRef(e, bmp);
    jobject cvs = (*e)->NewObject(e, canvasClass, canvas_init_bmp, bmp);
    g_canvas = (*e)->NewGlobalRef(e, cvs);

    /* ByteBuffer for pixel copy */
    bbClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "java/nio/ByteBuffer"));
    bb_allocate = (*e)->GetStaticMethodID(e, bbClass, "allocate", "(I)Ljava/nio/ByteBuffer;");
    bb_array = (*e)->GetMethodID(e, bbClass, "array", "()[B");

    pathClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "android/graphics/Path"));
    path_init = (*e)->GetMethodID(e, pathClass, "<init>", "()V");
    path_moveTo = (*e)->GetMethodID(e, pathClass, "moveTo", "(FF)V");
    path_lineTo = (*e)->GetMethodID(e, pathClass, "lineTo", "(FF)V");
    path_close = (*e)->GetMethodID(e, pathClass, "close", "()V");
    path_reset = (*e)->GetMethodID(e, pathClass, "reset", "()V");

    LOGI("Real Canvas cached: bitmap=%p canvas=%p", g_bitmap, g_canvas);
}

static void cache(JNIEnv* e) {
    if (hostClass || dlist_mode) return;
    jclass cls = (*e)->FindClass(e, "com/westlake/host/WestlakeActivity");
    if (!cls || (*e)->ExceptionCheck(e)) {
        (*e)->ExceptionClear(e);
        LOGI("Subprocess mode — using real Canvas + pixel buffer mmap");
        cache_canvas_classes(e);
        shm_init();
        return;
    }
    hostClass = (*e)->NewGlobalRef(e, cls);
    beginFrame = (*e)->GetStaticMethodID(e, hostClass, "beginFrame", "()V");
    endFrame = (*e)->GetStaticMethodID(e, hostClass, "endFrame", "()V");
    newPaintM = (*e)->GetStaticMethodID(e, hostClass, "newPaint", "(I)I");
    getPaintM = (*e)->GetStaticMethodID(e, hostClass, "getPaint", "(I)Landroid/graphics/Paint;");
    newPathM = (*e)->GetStaticMethodID(e, hostClass, "newPath", "()I");
    getPathM = (*e)->GetStaticMethodID(e, hostClass, "getPath", "(I)Landroid/graphics/Path;");
    hostCanvas = (*e)->GetStaticFieldID(e, hostClass, "currentCanvas", "Landroid/graphics/Canvas;");

    cache_canvas_classes(e);
    LOGI("In-process JNI cache ready");
}

static jobject getCanvas(JNIEnv* e) {
    if (dlist_mode) return g_canvas;
    return hostClass ? (*e)->GetStaticObjectField(e, hostClass, hostCanvas) : NULL;
}

/* ═══ OHBridge implementations ═══ */

static void OHB_logDebug(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* a=(*e)->GetStringUTFChars(e,t,0); const char* b=(*e)->GetStringUTFChars(e,m,0);
    __android_log_print(ANDROID_LOG_DEBUG, a, "%s", b);
    (*e)->ReleaseStringUTFChars(e,m,b); (*e)->ReleaseStringUTFChars(e,t,a);
}
static void OHB_logInfo(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* a=(*e)->GetStringUTFChars(e,t,0); const char* b=(*e)->GetStringUTFChars(e,m,0);
    __android_log_print(ANDROID_LOG_INFO, a, "%s", b);
    (*e)->ReleaseStringUTFChars(e,m,b); (*e)->ReleaseStringUTFChars(e,t,a);
}
static void OHB_logWarn(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* a=(*e)->GetStringUTFChars(e,t,0); const char* b=(*e)->GetStringUTFChars(e,m,0);
    __android_log_print(ANDROID_LOG_WARN, a, "%s", b);
    (*e)->ReleaseStringUTFChars(e,m,b); (*e)->ReleaseStringUTFChars(e,t,a);
}
static void OHB_logError(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* a=(*e)->GetStringUTFChars(e,t,0); const char* b=(*e)->GetStringUTFChars(e,m,0);
    __android_log_print(ANDROID_LOG_ERROR, a, "%s", b);
    (*e)->ReleaseStringUTFChars(e,m,b); (*e)->ReleaseStringUTFChars(e,t,a);
}

static jint OHB_arkuiInit(JNIEnv* e, jclass c) { cache(e); return 0; }

/* Surface */
static jlong OHB_surfaceCreate(JNIEnv* e, jclass c, jlong u, jint w, jint h) { return 1; }
static jlong OHB_surfaceGetCanvas(JNIEnv* e, jclass c, jlong s) {
    if (dlist_mode) return 1;
    if (hostClass) (*e)->CallStaticVoidMethod(e, hostClass, beginFrame);
    return 1;
}
static jint OHB_surfaceFlush(JNIEnv* e, jclass c, jlong s) {
    if (dlist_mode && shm_ptr && g_bitmap) {
        /* Copy pixels from real Bitmap to mmap'd shm */
        jobject bb = (*e)->CallStaticObjectMethod(e, bbClass, bb_allocate, FRAME_SIZE);
        (*e)->CallVoidMethod(e, g_bitmap, bm_copyPixelsToBuffer, bb);
        jbyteArray arr = (*e)->CallObjectMethod(e, bb, bb_array);
        jbyte* pixels = (*e)->GetByteArrayElements(e, arr, NULL);

        int offset = SHM_HEADER + g_activeBuf * FRAME_SIZE;
        memcpy(shm_ptr + offset, pixels, FRAME_SIZE);
        (*e)->ReleaseByteArrayElements(e, arr, pixels, JNI_ABORT);
        (*e)->DeleteLocalRef(e, bb);
        (*e)->DeleteLocalRef(e, arr);

        shm_seq++;
        *(int*)(shm_ptr + 20) = g_activeBuf;
        __sync_synchronize();
        *(int*)(shm_ptr + 16) = shm_seq;
        msync(shm_ptr + offset, FRAME_SIZE, MS_SYNC);
        msync(shm_ptr, SHM_HEADER, MS_SYNC);
        g_activeBuf = 1 - g_activeBuf;
        return 0;
    }
    if (hostClass) (*e)->CallStaticVoidMethod(e, hostClass, endFrame);
    return 0;
}
static void OHB_surfaceDestroy(JNIEnv* e, jclass c, jlong s) {}
static void OHB_surfaceResize(JNIEnv* e, jclass c, jlong s, jint w, jint h) {}

/* Canvas — delegate to real Android Canvas (works for both modes) */
static jlong OHB_canvasCreate(JNIEnv* e, jclass c, jlong b) { return 1; }
static void OHB_canvasDestroy(JNIEnv* e, jclass c, jlong cn) {}

static void OHB_canvasDrawColor(JNIEnv* e, jclass c, jlong cn, jint col) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallVoidMethod(e, cv, c_drawColor, col);
}
static void OHB_canvasDrawRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject p = get_paint(brush > 0 ? (int)brush : (int)pen);
    if(!p && hostClass) p = brush>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)brush) :
                pen>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen) : NULL;
    if(p) (*e)->CallVoidMethod(e, cv, c_drawRect, l, t, r, b2, p);
}
static void OHB_canvasDrawRoundRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2, jfloat rx, jfloat ry, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject p = get_paint(brush > 0 ? (int)brush : (int)pen);
    if(!p && hostClass) p = brush>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)brush) :
                pen>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen) : NULL;
    if(p) (*e)->CallVoidMethod(e, cv, c_drawRoundRect, l, t, r, b2, rx, ry, p);
}
static void OHB_canvasDrawCircle(JNIEnv* e, jclass c, jlong cn, jfloat cx, jfloat cy, jfloat r, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject p = get_paint(brush > 0 ? (int)brush : (int)pen);
    if(!p && hostClass) p = brush>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)brush) : NULL;
    if(p) (*e)->CallVoidMethod(e, cv, c_drawCircle, cx, cy, r, p);
}
static void OHB_canvasDrawLine(JNIEnv* e, jclass c, jlong cn, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jlong pen) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject p = get_paint((int)pen);
    if(!p && hostClass) p = pen>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen) : NULL;
    if(p) (*e)->CallVoidMethod(e, cv, c_drawLine, x1, y1, x2, y2, p);
}
static void OHB_canvasDrawText(JNIEnv* e, jclass c, jlong cn, jstring text, jfloat x, jfloat y, jlong font, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv || !text) return;
    jobject p = get_paint((int)font);
    if (!p && hostClass) {
        p = font>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)font) : NULL;
        if(!p && pen>0) p = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen);
    }
    if(p) {
        /* Set color from pen */
        jobject penP = get_paint((int)pen);
        if (penP && pen > 0 && font > 0) {
            jmethodID gc = (*e)->GetMethodID(e, paintClass, "getColor", "()I");
            jint col = (*e)->CallIntMethod(e, penP, gc);
            (*e)->CallVoidMethod(e, p, p_setColor, col);
        }
        (*e)->CallVoidMethod(e, cv, c_drawText, text, x, y, p);
    }
}
static void OHB_canvasDrawPath(JNIEnv* e, jclass c, jlong cn, jlong path, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv) return;
    /* Path support is basic — skip in subprocess for now */
}
static void OHB_canvasSave(JNIEnv* e, jclass c, jlong cn) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallIntMethod(e, cv, c_save);
}
static void OHB_canvasRestore(JNIEnv* e, jclass c, jlong cn) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallVoidMethod(e, cv, c_restore);
}
static void OHB_canvasTranslate(JNIEnv* e, jclass c, jlong cn, jfloat dx, jfloat dy) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallVoidMethod(e, cv, c_translate, dx, dy);
}
static void OHB_canvasScale(JNIEnv* e, jclass c, jlong cn, jfloat sx, jfloat sy) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallVoidMethod(e, cv, c_scale, sx, sy);
}
static void OHB_canvasClipRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallBooleanMethod(e, cv, c_clipRect, l, t, r, b2);
}
static void OHB_canvasDrawBitmap(JNIEnv* e, jclass c, jlong cn, jlong bmp, jfloat x, jfloat y) {}
static void OHB_canvasConcat(JNIEnv* e, jclass c, jlong cn, jfloatArray m) {}
static void OHB_canvasRotate(JNIEnv* e, jclass c, jlong cn, jfloat d, jfloat px, jfloat py) {}
static void OHB_canvasClipPath(JNIEnv* e, jclass c, jlong cn, jlong path) {}
static void OHB_canvasDrawArc(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2, jfloat sa, jfloat sw, jboolean uc, jlong pen, jlong brush) {}
static void OHB_canvasDrawOval(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2, jlong pen, jlong brush) {}

/* Pen/Brush/Font → real Android Paint */
static jlong OHB_penCreate(JNIEnv* e, jclass c) {
    if (dlist_mode) return alloc_paint(e);
    return (*e)->CallStaticIntMethod(e,hostClass,newPaintM,1);
}
static void OHB_penSetColor(JNIEnv* e, jclass c, jlong p, jint col) {
    jobject pa = dlist_mode ? get_paint((int)p) : (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)p);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setColor, col);
}
static void OHB_penSetWidth(JNIEnv* e, jclass c, jlong p, jfloat w) {
    jobject pa = dlist_mode ? get_paint((int)p) : (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)p);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setStrokeWidth, w);
}
static void OHB_penSetAntiAlias(JNIEnv* e, jclass c, jlong p, jboolean aa) {}
static void OHB_penSetCap(JNIEnv* e, jclass c, jlong p, jint cap) {}
static void OHB_penSetJoin(JNIEnv* e, jclass c, jlong p, jint j) {}
static void OHB_penDestroy(JNIEnv* e, jclass c, jlong p) {}

static jlong OHB_brushCreate(JNIEnv* e, jclass c) {
    if (dlist_mode) return alloc_paint(e);
    return (*e)->CallStaticIntMethod(e,hostClass,newPaintM,0);
}
static void OHB_brushSetColor(JNIEnv* e, jclass c, jlong b, jint col) {
    jobject pa = dlist_mode ? get_paint((int)b) : (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)b);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setColor, col);
}
static void OHB_brushSetAntiAlias(JNIEnv* e, jclass c, jlong b, jboolean aa) {}
static void OHB_brushDestroy(JNIEnv* e, jclass c, jlong b) {}

static jlong OHB_fontCreate(JNIEnv* e, jclass c) {
    if (dlist_mode) { int h = alloc_paint(e); jobject p=get_paint(h); if(p)(*e)->CallVoidMethod(e,p,p_setTextSize,16.0f); return h; }
    int id = (*e)->CallStaticIntMethod(e,hostClass,newPaintM,0);
    jobject pa = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,id);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setTextSize, 16.0f);
    return id;
}
static void OHB_fontSetSize(JNIEnv* e, jclass c, jlong f, jfloat sz) {
    jobject pa = dlist_mode ? get_paint((int)f) : (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)f);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setTextSize, sz);
}
static jfloat OHB_fontMeasureText(JNIEnv* e, jclass c, jlong f, jstring s) {
    if(!s) return 0;
    jobject pa = dlist_mode ? get_paint((int)f) : (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)f);
    if(!pa) return 0;
    return (*e)->CallFloatMethod(e, pa, p_measureText, s);
}
static void OHB_fontDestroy(JNIEnv* e, jclass c, jlong f) {}
static jfloatArray OHB_fontGetMetrics(JNIEnv* e, jclass c, jlong f) {
    jfloatArray arr = (*e)->NewFloatArray(e, 4);
    jobject pa = dlist_mode ? get_paint((int)f) : (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)f);
    if(pa) {
        jobject fm = (*e)->CallObjectMethod(e, pa, p_getFontMetrics);
        if(fm) {
            jclass fmClass = (*e)->GetObjectClass(e, fm);
            jfieldID fa = (*e)->GetFieldID(e, fmClass, "ascent", "F");
            jfieldID fd = (*e)->GetFieldID(e, fmClass, "descent", "F");
            jfloat m[4];
            m[0] = (*e)->GetFloatField(e, fm, fa);
            m[1] = (*e)->GetFloatField(e, fm, fd);
            m[2] = 0;
            m[3] = m[1] - m[0];
            (*e)->SetFloatArrayRegion(e, arr, 0, 4, m);
        }
    }
    return arr;
}

/* Bitmap — stubs (rendering goes through Canvas) */
static jlong OHB_bitmapCreate(JNIEnv* e, jclass c, jint w, jint h, jint fmt) { return 1; }
static void OHB_bitmapDestroy(JNIEnv* e, jclass c, jlong b) {}
static jint OHB_bitmapGetWidth(JNIEnv* e, jclass c, jlong b) { return FRAME_W; }
static jint OHB_bitmapGetHeight(JNIEnv* e, jclass c, jlong b) { return FRAME_H; }
static void OHB_bitmapSetPixel(JNIEnv* e, jclass c, jlong b, jint x, jint y, jint col) {}
static jint OHB_bitmapGetPixel(JNIEnv* e, jclass c, jlong b, jint x, jint y) { return 0; }

/* Path — stubs */
static jlong OHB_pathCreate(JNIEnv* e, jclass c) { return 1; }
static void OHB_pathDestroy(JNIEnv* e, jclass c, jlong p) {}
static void OHB_pathMoveTo(JNIEnv* e, jclass c, jlong p, jfloat x, jfloat y) {}
static void OHB_pathLineTo(JNIEnv* e, jclass c, jlong p, jfloat x, jfloat y) {}
static void OHB_pathClose(JNIEnv* e, jclass c, jlong p) {}
static void OHB_pathReset(JNIEnv* e, jclass c, jlong p) {}
static void OHB_pathQuadTo(JNIEnv* e, jclass c, jlong p, jfloat x1, jfloat y1, jfloat x2, jfloat y2) {}
static void OHB_pathCubicTo(JNIEnv* e, jclass c, jlong p, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jfloat x3, jfloat y3) {}
static void OHB_pathAddRect(JNIEnv* e, jclass c, jlong p, jfloat l, jfloat t, jfloat r, jfloat b, jint dir) {}
static void OHB_pathAddCircle(JNIEnv* e, jclass c, jlong p, jfloat cx, jfloat cy, jfloat r, jint dir) {}

/* ═══ JNI registration ═══ */
static JNINativeMethod methods[] = {
    {"logDebug","(Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_logDebug},
    {"logInfo","(Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_logInfo},
    {"logWarn","(Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_logWarn},
    {"logError","(Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_logError},
    {"arkuiInit","()I",(void*)OHB_arkuiInit},
    {"surfaceCreate","(JII)J",(void*)OHB_surfaceCreate},
    {"surfaceGetCanvas","(J)J",(void*)OHB_surfaceGetCanvas},
    {"surfaceFlush","(J)I",(void*)OHB_surfaceFlush},
    {"surfaceDestroy","(J)V",(void*)OHB_surfaceDestroy},
    {"surfaceResize","(JII)V",(void*)OHB_surfaceResize},
    {"canvasCreate","(J)J",(void*)OHB_canvasCreate},
    {"canvasDestroy","(J)V",(void*)OHB_canvasDestroy},
    {"canvasDrawColor","(JI)V",(void*)OHB_canvasDrawColor},
    {"canvasDrawRect","(JFFFFJJ)V",(void*)OHB_canvasDrawRect},
    {"canvasDrawRoundRect","(JFFFFFFJJ)V",(void*)OHB_canvasDrawRoundRect},
    {"canvasDrawCircle","(JFFFJJ)V",(void*)OHB_canvasDrawCircle},
    {"canvasDrawLine","(JFFFFJ)V",(void*)OHB_canvasDrawLine},
    {"canvasDrawText","(JLjava/lang/String;FFJJJ)V",(void*)OHB_canvasDrawText},
    {"canvasDrawPath","(JJJ J)V",(void*)OHB_canvasDrawPath},
    {"canvasSave","(J)V",(void*)OHB_canvasSave},
    {"canvasRestore","(J)V",(void*)OHB_canvasRestore},
    {"canvasTranslate","(JFF)V",(void*)OHB_canvasTranslate},
    {"canvasScale","(JFF)V",(void*)OHB_canvasScale},
    {"canvasClipRect","(JFFFF)V",(void*)OHB_canvasClipRect},
    {"canvasDrawBitmap","(JJFF)V",(void*)OHB_canvasDrawBitmap},
    {"canvasConcat","(J[F)V",(void*)OHB_canvasConcat},
    {"canvasRotate","(JFFF)V",(void*)OHB_canvasRotate},
    {"canvasClipPath","(JJ)V",(void*)OHB_canvasClipPath},
    {"canvasDrawArc","(JFFFFFFZJJ)V",(void*)OHB_canvasDrawArc},
    {"canvasDrawOval","(JFFFFJJ)V",(void*)OHB_canvasDrawOval},
    {"penCreate","()J",(void*)OHB_penCreate},
    {"penSetColor","(JI)V",(void*)OHB_penSetColor},
    {"penSetWidth","(JF)V",(void*)OHB_penSetWidth},
    {"penSetAntiAlias","(JZ)V",(void*)OHB_penSetAntiAlias},
    {"penSetCap","(JI)V",(void*)OHB_penSetCap},
    {"penSetJoin","(JI)V",(void*)OHB_penSetJoin},
    {"penDestroy","(J)V",(void*)OHB_penDestroy},
    {"brushCreate","()J",(void*)OHB_brushCreate},
    {"brushSetColor","(JI)V",(void*)OHB_brushSetColor},
    {"brushDestroy","(J)V",(void*)OHB_brushDestroy},
    {"fontCreate","()J",(void*)OHB_fontCreate},
    {"fontSetSize","(JF)V",(void*)OHB_fontSetSize},
    {"fontMeasureText","(JLjava/lang/String;)F",(void*)OHB_fontMeasureText},
    {"fontDestroy","(J)V",(void*)OHB_fontDestroy},
    {"fontGetMetrics","(J)[F",(void*)OHB_fontGetMetrics},
    {"bitmapCreate","(III)J",(void*)OHB_bitmapCreate},
    {"bitmapDestroy","(J)V",(void*)OHB_bitmapDestroy},
    {"bitmapGetWidth","(J)I",(void*)OHB_bitmapGetWidth},
    {"bitmapGetHeight","(J)I",(void*)OHB_bitmapGetHeight},
    {"bitmapSetPixel","(JIII)V",(void*)OHB_bitmapSetPixel},
    {"bitmapGetPixel","(JII)I",(void*)OHB_bitmapGetPixel},
    {"pathCreate","()J",(void*)OHB_pathCreate},
    {"pathDestroy","(J)V",(void*)OHB_pathDestroy},
    {"pathMoveTo","(JFF)V",(void*)OHB_pathMoveTo},
    {"pathLineTo","(JFF)V",(void*)OHB_pathLineTo},
    {"pathClose","(J)V",(void*)OHB_pathClose},
    {"pathReset","(J)V",(void*)OHB_pathReset},
    {"pathQuadTo","(JFFFF)V",(void*)OHB_pathQuadTo},
    {"pathCubicTo","(JFFFFFF)V",(void*)OHB_pathCubicTo},
    {"pathAddRect","(JFFFFI)V",(void*)OHB_pathAddRect},
    {"pathAddCircle","(JFFFI)V",(void*)OHB_pathAddCircle},
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_vm = vm;
    JNIEnv* env;
    if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) return -1;
    jclass cls = (*env)->FindClass(env, "com/ohos/shim/bridge/OHBridge");
    if (!cls) return -1;
    int count = sizeof(methods)/sizeof(methods[0]);
    int ok = 0;
    for (int i = 0; i < count; i++) {
        if ((*env)->RegisterNatives(env, cls, &methods[i], 1) == 0) ok++;
        else (*env)->ExceptionClear(env);
    }
    LOGI("[OHBridge ARM64] %d/%d natives registered", ok, count);
    return JNI_VERSION_1_6;
}
