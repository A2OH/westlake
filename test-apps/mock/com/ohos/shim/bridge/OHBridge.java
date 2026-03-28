package com.ohos.shim.bridge;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock OHBridge for local JVM testing (no native libraries needed).
 *
 * Replaces the real JNI bridge with in-memory mocks:
 * - Preferences: backed by ConcurrentHashMap
 * - RdbStore/Cursor: backed by in-memory SQLite (or stub)
 * - Log: prints to stdout
 * - Network: returns stub values
 * - DeviceInfo: returns mock values
 * - ArkUI nodes: tracked in memory with handle counter
 *
 * Usage: compile tests with this mock instead of the real OHBridge.
 *        javac -sourcepath test-apps/mock:shim/java ...
 */
public class OHBridge {

    // No native library load — all mock

    /** Mock always returns true so Canvas/Bitmap/Path create native handles. */
    public static boolean isNativeAvailable() { return true; }

    // ── Handle counter ──
    private static final AtomicLong sNextHandle = new AtomicLong(1);

    // ── Preferences mock ──

    private static final ConcurrentHashMap<Long, ConcurrentHashMap<String, Object>> sPrefs = new ConcurrentHashMap<>();

    public static long preferencesOpen(String name) {
        long h = sNextHandle.getAndIncrement();
        sPrefs.put(h, new ConcurrentHashMap<>());
        return h;
    }

    private static ConcurrentHashMap<String, Object> getPrefsMap(long handle) {
        ConcurrentHashMap<String, Object> map = sPrefs.get(handle);
        return map != null ? map : new ConcurrentHashMap<>();
    }

    public static String preferencesGetString(long h, String key, String def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof String ? (String) v : def;
    }
    public static int preferencesGetInt(long h, String key, int def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof Integer ? (Integer) v : def;
    }
    public static long preferencesGetLong(long h, String key, long def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof Long ? (Long) v : def;
    }
    public static float preferencesGetFloat(long h, String key, float def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof Float ? (Float) v : def;
    }
    public static boolean preferencesGetBoolean(long h, String key, boolean def) {
        Object v = getPrefsMap(h).get(key);
        return v instanceof Boolean ? (Boolean) v : def;
    }
    public static void preferencesPutString(long h, String key, String value) { getPrefsMap(h).put(key, value); }
    public static void preferencesPutInt(long h, String key, int value) { getPrefsMap(h).put(key, value); }
    public static void preferencesPutLong(long h, String key, long value) { getPrefsMap(h).put(key, value); }
    public static void preferencesPutFloat(long h, String key, float value) { getPrefsMap(h).put(key, value); }
    public static void preferencesPutBoolean(long h, String key, boolean value) { getPrefsMap(h).put(key, value); }
    public static void preferencesFlush(long h) { /* no-op */ }
    public static void preferencesRemove(long h, String key) { getPrefsMap(h).remove(key); }
    public static void preferencesClear(long h) { getPrefsMap(h).clear(); }
    public static void preferencesClose(long h) { sPrefs.remove(h); }

    // ── RdbStore mock (stub) ──

    public static long rdbStoreOpen(String dbName, int version) { return sNextHandle.getAndIncrement(); }
    public static void rdbStoreExecSQL(long h, String sql) { /* stub */ }
    public static long rdbStoreQuery(long h, String sql, String[] args) { return 0; }
    public static long rdbStoreInsert(long h, String table, String json) { return sNextHandle.getAndIncrement(); }
    public static int rdbStoreUpdate(long h, String json, String table, String where, String[] args) { return 0; }
    public static int rdbStoreDelete(long h, String table, String where, String[] args) { return 0; }
    public static void rdbStoreBeginTransaction(long h) { }
    public static void rdbStoreCommit(long h) { }
    public static void rdbStoreRollback(long h) { }
    public static void rdbStoreClose(long h) { }

    // ── ResultSet mock (stub) ──

    public static boolean resultSetGoToFirstRow(long h) { return false; }
    public static boolean resultSetGoToNextRow(long h) { return false; }
    public static int resultSetGetColumnIndex(long h, String name) { return -1; }
    public static String resultSetGetString(long h, int idx) { return ""; }
    public static int resultSetGetInt(long h, int idx) { return 0; }
    public static long resultSetGetLong(long h, int idx) { return 0; }
    public static float resultSetGetFloat(long h, int idx) { return 0; }
    public static double resultSetGetDouble(long h, int idx) { return 0; }
    public static byte[] resultSetGetBlob(long h, int idx) { return new byte[0]; }
    public static boolean resultSetIsNull(long h, int idx) { return true; }
    public static int resultSetGetRowCount(long h) { return 0; }
    public static int resultSetGetColumnCount(long h) { return 0; }
    public static String resultSetGetColumnName(long h, int idx) { return ""; }
    public static void resultSetClose(long h) { }

    // ── Notification mock ──

    public static void notificationPublish(int id, String title, String text, String channelId, int priority) {
        System.out.println("[MOCK] Notification #" + id + ": " + title + " — " + text);
    }
    public static void notificationCancel(int id) {
        System.out.println("[MOCK] Cancel notification #" + id);
    }
    public static void notificationAddSlot(String channelId, String channelName, int importance) {
        System.out.println("[MOCK] Add slot: " + channelId);
    }

    // ── Reminder mock ──

    private static int sReminderId = 1;
    public static int reminderScheduleTimer(int delay, String title, String content, String target, String params) {
        System.out.println("[MOCK] Reminder in " + delay + "s: " + title);
        return sReminderId++;
    }
    public static void reminderCancel(int id) {
        System.out.println("[MOCK] Cancel reminder #" + id);
    }

    // ── Navigation mock ──

    public static void startAbility(String bundle, String ability, String params) {
        System.out.println("[MOCK] Start ability: " + bundle + "/" + ability);
    }
    public static void terminateSelf() {
        System.out.println("[MOCK] Terminate self");
    }

    // ── Logging mock ──

    public static void logDebug(String tag, String msg) { System.out.println("[D/" + tag + "] " + msg); }
    public static void logInfo(String tag, String msg) { System.out.println("[I/" + tag + "] " + msg); }
    public static void logWarn(String tag, String msg) { System.out.println("[W/" + tag + "] " + msg); }
    public static void logError(String tag, String msg) { System.out.println("[E/" + tag + "] " + msg); }

    // ── Toast mock ──

    public static void showToast(String message, int duration) {
        System.out.println("[MOCK] Toast: " + message + " (" + duration + "ms)");
    }

    // ── Network mock ──

    public static String httpRequest(String url, String method, String headers, String body) {
        System.out.println("[MOCK] HTTP " + method + " " + url);
        return "{\"mock\":true}";
    }
    public static boolean isNetworkAvailable() { return true; }
    public static int getNetworkType() { return 1; }

    // ── WiFi mock ──

    private static volatile boolean sWifiEnabled = true;

    public static boolean wifiIsEnabled() { return sWifiEnabled; }
    public static boolean wifiSetEnabled(boolean enabled) {
        sWifiEnabled = enabled;
        return true;
    }
    public static int wifiGetState() {
        return sWifiEnabled ? 3 /* WIFI_STATE_ENABLED */ : 1 /* WIFI_STATE_DISABLED */;
    }
    public static String wifiGetSSID() { return "MockWiFi"; }
    public static int wifiGetRssi() { return -50; }
    public static int wifiGetLinkSpeed() { return 72; }
    public static int wifiGetFrequency() { return 2412; }

    // ── Device Info mock ──

    public static String getDeviceBrand() { return "MockBrand"; }
    public static String getDeviceModel() { return "MockModel"; }
    public static String getOSVersion() { return "OpenHarmony-Mock-4.0"; }
    public static int getSDKVersion() { return 12; }

    // ── Telephony mock ──

    public static String telephonyGetDeviceId()           { return "mock-device-id-12345"; }
    public static String telephonyGetLine1Number()        { return "+1555000000"; }
    public static String telephonyGetNetworkOperatorName(){ return "MockCarrier"; }
    public static int telephonyGetSimState()              { return 5; /* SIM_STATE_READY */ }
    public static int telephonyGetPhoneType()             { return 1; /* PHONE_TYPE_GSM */ }
    public static int telephonyGetNetworkType()           { return 13; /* NETWORK_TYPE_LTE */ }

    // ── Location mock ──

    /** Mock: returns San Francisco coordinates [lat, lon, alt]. */
    public static double[] locationGetLast() {
        return new double[]{37.7749, -122.4194, 10.0};
    }

    /** Mock: location is always enabled. */
    public static boolean locationIsEnabled() {
        return true;
    }

    // ── ArkUI Node API mock ──

    private static final ConcurrentHashMap<Long, String> sNodes = new ConcurrentHashMap<>();

    public static int arkuiInit() {
        System.out.println("[MOCK] ArkUI init");
        return 0;
    }

    public static long nodeCreate(int nodeType) {
        long h = sNextHandle.getAndIncrement();
        sNodes.put(h, "node_type_" + nodeType);
        return h;
    }

    public static void nodeDispose(long node) { sNodes.remove(node); }
    public static void nodeAddChild(long parent, long child) { /* tracked in Java ViewGroup */ }
    public static void nodeRemoveChild(long parent, long child) { }
    public static void nodeInsertChildAt(long parent, long child, int pos) { }

    public static int nodeSetAttrFloat(long node, int attr, float v0, float v1, float v2, float v3, int count) { return 0; }
    public static int nodeSetAttrColor(long node, int attr, int color) { return 0; }
    public static int nodeSetAttrInt(long node, int attr, int value) { return 0; }
    public static int nodeSetAttrString(long node, int attr, String value) { return 0; }

    public static int nodeRegisterEvent(long node, int eventType, int eventId) { return 0; }
    public static void nodeUnregisterEvent(long node, int eventType) { }
    public static void nodeMarkDirty(long node, int flag) { }

    // ── Clipboard mock ──

    private static String sClipboardText = null;

    public static void clipboardSet(String text) {
        sClipboardText = text;
        System.out.println("[MOCK] Clipboard set: " + text);
    }

    public static String clipboardGet() {
        return sClipboardText;
    }

    // ── AudioManager mock ──

    // Per-stream volumes (indices 0..5), max volume = 15, default = 7
    private static final int AUDIO_STREAM_COUNT = 6;
    private static final int[] sStreamVolume    = {7, 7, 7, 7, 7, 7};
    private static final int   AUDIO_MAX_VOL    = 15;
    private static int sRingerMode = 2; // RINGER_MODE_NORMAL

    public static int audioGetStreamVolume(int streamType) {
        if (streamType < 0 || streamType >= AUDIO_STREAM_COUNT) return 7;
        return sStreamVolume[streamType];
    }

    public static int audioGetStreamMaxVolume(int streamType) {
        return AUDIO_MAX_VOL;
    }

    public static void audioSetStreamVolume(int streamType, int index, int flags) {
        if (streamType >= 0 && streamType < AUDIO_STREAM_COUNT) {
            sStreamVolume[streamType] = Math.max(0, Math.min(AUDIO_MAX_VOL, index));
        }
    }

    public static int audioGetRingerMode() {
        return sRingerMode;
    }

    public static void audioSetRingerMode(int mode) {
        sRingerMode = mode;
    }

    public static boolean audioIsMusicActive() {
        return false;
    }

    // ── MediaPlayer mock ──

    private enum MediaPlayerState { IDLE, PLAYING, PAUSED, STOPPED }
    private static final ConcurrentHashMap<Long, MediaPlayerState> sMediaPlayers = new ConcurrentHashMap<>();

    public static long mediaPlayerCreate() {
        long h = sNextHandle.getAndIncrement();
        sMediaPlayers.put(h, MediaPlayerState.IDLE);
        System.out.println("[MOCK] MediaPlayer create handle=" + h);
        return h;
    }

    public static void mediaPlayerSetDataSource(long h, String path) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] setDataSource: " + path);
    }

    public static void mediaPlayerPrepare(long h) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] prepare");
    }

    public static void mediaPlayerStart(long h) {
        sMediaPlayers.put(h, MediaPlayerState.PLAYING);
        System.out.println("[MOCK] MediaPlayer[" + h + "] start");
    }

    public static void mediaPlayerPause(long h) {
        sMediaPlayers.put(h, MediaPlayerState.PAUSED);
        System.out.println("[MOCK] MediaPlayer[" + h + "] pause");
    }

    public static void mediaPlayerStop(long h) {
        sMediaPlayers.put(h, MediaPlayerState.STOPPED);
        System.out.println("[MOCK] MediaPlayer[" + h + "] stop");
    }

    public static void mediaPlayerRelease(long h) {
        sMediaPlayers.remove(h);
        System.out.println("[MOCK] MediaPlayer[" + h + "] release");
    }

    public static void mediaPlayerSeekTo(long h, int msec) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] seekTo " + msec + "ms");
    }

    public static void mediaPlayerReset(long h) {
        sMediaPlayers.put(h, MediaPlayerState.IDLE);
        System.out.println("[MOCK] MediaPlayer[" + h + "] reset");
    }

    public static int mediaPlayerGetDuration(long h) {
        return 30000; // mock 30 seconds
    }

    public static int mediaPlayerGetCurrentPosition(long h) {
        return 0;
    }

    public static boolean mediaPlayerIsPlaying(long h) {
        return MediaPlayerState.PLAYING.equals(sMediaPlayers.get(h));
    }

    public static void mediaPlayerSetVolume(long h, float left, float right) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] setVolume left=" + left + " right=" + right);
    }

    public static void mediaPlayerSetLooping(long h, boolean looping) {
        System.out.println("[MOCK] MediaPlayer[" + h + "] setLooping=" + looping);
    }

    // ── OH_Drawing: Canvas mock ──

    public static class DrawRecord {
        public final String op;
        public final float[] args;
        public final String text;
        public final int color;
        public DrawRecord(String op, float[] args, String text, int color) {
            this.op = op; this.args = args; this.text = text; this.color = color;
        }
    }

    private static final ConcurrentHashMap<Long, java.util.List<DrawRecord>> sCanvasDrawLog = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Integer> sHandleColors = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Float> sHandleWidths = new ConcurrentHashMap<>();

    public static long canvasCreate(long bitmapHandle) {
        long h = sNextHandle.getAndIncrement();
        sCanvasDrawLog.put(h, java.util.Collections.synchronizedList(new java.util.ArrayList<>()));
        return h;
    }
    public static void canvasDestroy(long canvas) { sCanvasDrawLog.remove(canvas); }
    public static void canvasDrawRect(long canvas, float l, float t, float r, float b, long pen, long brush) {
        record(canvas, "drawRect", new float[]{l, t, r, b}, null, getColor(brush != 0 ? brush : pen));
    }
    public static void canvasDrawCircle(long canvas, float cx, float cy, float r, long pen, long brush) {
        record(canvas, "drawCircle", new float[]{cx, cy, r}, null, getColor(brush != 0 ? brush : pen));
    }
    public static void canvasDrawLine(long canvas, float x1, float y1, float x2, float y2, long pen) {
        Float w = sHandleWidths.get(pen);
        float strokeWidth = w != null ? w : 1.0f;
        record(canvas, "drawLine", new float[]{x1, y1, x2, y2, strokeWidth}, null, getColor(pen));
    }
    public static void canvasDrawText(long canvas, String text, float x, float y, long font, long pen, long brush) {
        Float fs = sHandleFontSizes.get(font);
        float fontSize = fs != null ? fs : 14.0f;
        record(canvas, "drawText", new float[]{x, y, fontSize}, text, getColor(brush != 0 ? brush : pen));
    }
    public static void canvasDrawPath(long canvas, long path, long pen, long brush) {
        record(canvas, "drawPath", new float[]{}, null, getColor(brush != 0 ? brush : pen));
    }
    public static void canvasDrawBitmap(long canvas, long bitmap, float x, float y) {
        record(canvas, "drawBitmap", new float[]{x, y}, null, 0);
    }
    public static void canvasDrawColor(long canvas, int argb) {
        record(canvas, "drawColor", new float[]{}, null, argb);
    }
    public static void canvasDrawArc(long canvas, float l, float t, float r, float b, float startAngle, float sweepAngle, boolean useCenter, long pen, long brush) {
        record(canvas, "drawArc", new float[]{l, t, r, b, startAngle, sweepAngle, useCenter ? 1 : 0}, null, getColor(brush != 0 ? brush : pen));
    }
    public static void canvasDrawOval(long canvas, float l, float t, float r, float b, long pen, long brush) {
        record(canvas, "drawOval", new float[]{l, t, r, b}, null, getColor(brush != 0 ? brush : pen));
    }
    public static void canvasDrawRoundRect(long canvas, float l, float t, float r, float b, float rx, float ry, long pen, long brush) {
        record(canvas, "drawRoundRect", new float[]{l, t, r, b, rx, ry}, null, getColor(brush != 0 ? brush : pen));
    }
    public static void canvasConcat(long canvas, float[] matrix9) {
        record(canvas, "concat", matrix9 != null ? matrix9 : new float[0], null, 0);
    }
    public static void canvasSave(long canvas) { record(canvas, "save", new float[]{}, null, 0); }
    public static void canvasRestore(long canvas) { record(canvas, "restore", new float[]{}, null, 0); }
    public static void canvasTranslate(long canvas, float dx, float dy) {
        record(canvas, "translate", new float[]{dx, dy}, null, 0);
    }
    public static void canvasScale(long canvas, float sx, float sy) {
        record(canvas, "scale", new float[]{sx, sy}, null, 0);
    }
    public static void canvasRotate(long canvas, float degrees, float px, float py) {
        record(canvas, "rotate", new float[]{degrees, px, py}, null, 0);
    }
    public static void canvasClipRect(long canvas, float l, float t, float r, float b) {
        record(canvas, "clipRect", new float[]{l, t, r, b}, null, 0);
    }
    public static void canvasClipPath(long canvas, long path) {
        record(canvas, "clipPath", new float[]{}, null, 0);
    }

    // ── OH_Drawing: Pen mock ──

    public static long penCreate() { long h = sNextHandle.getAndIncrement(); sHandleColors.put(h, 0xFF000000); return h; }
    public static void penDestroy(long pen) { sHandleColors.remove(pen); sHandleWidths.remove(pen); }
    public static void penSetColor(long pen, int argb) { sHandleColors.put(pen, argb); }
    public static void penSetWidth(long pen, float w) { sHandleWidths.put(pen, w); }
    public static void penSetAntiAlias(long pen, boolean aa) {}
    public static void penSetCap(long pen, int cap) {}
    public static void penSetJoin(long pen, int join) {}

    // ── OH_Drawing: Brush mock ──

    public static long brushCreate() { long h = sNextHandle.getAndIncrement(); sHandleColors.put(h, 0xFF000000); return h; }
    public static void brushDestroy(long brush) { sHandleColors.remove(brush); }
    public static void brushSetColor(long brush, int argb) { sHandleColors.put(brush, argb); }

    // ── OH_Drawing: Path mock ──

    public static long pathCreate() { return sNextHandle.getAndIncrement(); }
    public static void pathDestroy(long path) {}
    public static void pathMoveTo(long path, float x, float y) {}
    public static void pathLineTo(long path, float x, float y) {}
    public static void pathQuadTo(long path, float x1, float y1, float x2, float y2) {}
    public static void pathCubicTo(long path, float x1, float y1, float x2, float y2, float x3, float y3) {}
    public static void pathClose(long path) {}
    public static void pathReset(long path) {}
    public static void pathAddRect(long path, float l, float t, float r, float b, int dir) {}
    public static void pathAddCircle(long path, float cx, float cy, float r, int dir) {}

    // ── OH_Drawing: Bitmap mock ──

    private static final ConcurrentHashMap<Long, int[]> sBitmapDims = new ConcurrentHashMap<>();
    public static long bitmapCreate(int w, int h, int fmt) {
        long bh = sNextHandle.getAndIncrement(); sBitmapDims.put(bh, new int[]{w, h}); return bh;
    }
    public static void bitmapDestroy(long bmp) { sBitmapDims.remove(bmp); }
    public static int bitmapGetWidth(long bmp) { int[] d = sBitmapDims.get(bmp); return d != null ? d[0] : 0; }
    public static int bitmapGetHeight(long bmp) { int[] d = sBitmapDims.get(bmp); return d != null ? d[1] : 0; }
    public static void bitmapSetPixel(long bmp, int x, int y, int argb) {}
    public static int bitmapGetPixel(long bmp, int x, int y) { return 0; }

    // ── OH_Drawing: Font mock ──

    public static long fontCreate() { return sNextHandle.getAndIncrement(); }
    public static void fontDestroy(long font) {}
    private static final ConcurrentHashMap<Long, Float> sHandleFontSizes = new ConcurrentHashMap<>();
    public static void fontSetSize(long font, float size) { sHandleFontSizes.put(font, size); }
    public static float[] fontGetMetrics(long font) {
        Float size = sHandleFontSizes.get(font);
        float s = (size != null) ? size : 14f;
        return new float[]{-s * 0.8f, s * 0.2f, s * 0.1f}; // ascent, descent, leading
    }

    // ── Surface mock ──

    private static final ConcurrentHashMap<Long, long[]> sSurfaces = new ConcurrentHashMap<>();

    public static long surfaceCreate(long xcomponentHandle, int width, int height) {
        long h = sNextHandle.getAndIncrement();
        // Create a backing bitmap + canvas for this surface
        long bitmapH = bitmapCreate(width, height, 0);
        long canvasH = canvasCreate(bitmapH);
        sSurfaces.put(h, new long[]{canvasH, bitmapH, width, height});
        return h;
    }

    public static void surfaceDestroy(long surfaceCtx) {
        long[] ctx = sSurfaces.remove(surfaceCtx);
        if (ctx != null) {
            canvasDestroy(ctx[0]);
            bitmapDestroy(ctx[1]);
        }
    }

    public static void surfaceResize(long surfaceCtx, int width, int height) {
        long[] ctx = sSurfaces.get(surfaceCtx);
        if (ctx != null) {
            canvasDestroy(ctx[0]);
            bitmapDestroy(ctx[1]);
            long bitmapH = bitmapCreate(width, height, 0);
            long canvasH = canvasCreate(bitmapH);
            ctx[0] = canvasH;
            ctx[1] = bitmapH;
            ctx[2] = width;
            ctx[3] = height;
        }
    }

    public static long surfaceGetCanvas(long surfaceCtx) {
        long[] ctx = sSurfaces.get(surfaceCtx);
        return ctx != null ? ctx[0] : 0;
    }

    // ── Display list shm writer (version=2) ──
    private static java.io.RandomAccessFile sShmRaf;
    private static int sFrameSeq = 0;
    private static final int SHM_HEADER_SIZE = 128;
    private static final int SHM_DLIST_MAX = 512 * 1024;
    private static final int SHM_TOTAL = SHM_HEADER_SIZE + SHM_DLIST_MAX + 64;
    private static final int OP_DRAW_COLOR = 1, OP_DRAW_RECT = 2, OP_DRAW_TEXT = 3;
    private static final int OP_DRAW_LINE = 4, OP_SAVE = 5, OP_RESTORE = 6;
    private static final int OP_TRANSLATE = 7, OP_CLIP_RECT = 8, OP_DRAW_ROUND_RECT = 9;
    private static final int OP_DRAW_CIRCLE = 10;

    private static java.io.RandomAccessFile getShmRaf() {
        if (sShmRaf != null) return sShmRaf;
        String path = System.getenv("WESTLAKE_SHM");
        if (path == null || path.isEmpty()) path = "/data/local/tmp/westlake/westlake_shm";
        try {
            sShmRaf = new java.io.RandomAccessFile(path, "rw");
            if (sShmRaf.length() < SHM_TOTAL) sShmRaf.setLength(SHM_TOTAL);
            byte[] hdr = new byte[SHM_HEADER_SIZE];
            putInt(hdr, 0, 0x574C4B46); // WLK_MAGIC
            putInt(hdr, 4, 2);           // version=2 = display list
            putInt(hdr, 8, 480);
            putInt(hdr, 12, 800);
            sShmRaf.seek(0);
            sShmRaf.write(hdr);
            System.out.println("[OHBridge] shm_init: DLIST mode, path=" + path + ", total=" + SHM_TOTAL + " bytes");
        } catch (Exception e) {
            System.out.println("[OHBridge] shm_init FAILED: " + e);
            sShmRaf = null;
        }
        return sShmRaf;
    }

    private static void putInt(byte[] buf, int off, int v) {
        buf[off]   = (byte)(v);       buf[off+1] = (byte)(v >> 8);
        buf[off+2] = (byte)(v >> 16); buf[off+3] = (byte)(v >> 24);
    }
    private static void putFloat(byte[] buf, int off, float v) {
        putInt(buf, off, Float.floatToRawIntBits(v));
    }
    private static void putShort(byte[] buf, int off, short v) {
        buf[off] = (byte)(v); buf[off+1] = (byte)(v >> 8);
    }

    public static int surfaceFlush(long surfaceCtx) {
        long[] ctx = sSurfaces.get(surfaceCtx);
        if (ctx == null) return -1;
        long canvasH = ctx[0];

        java.util.List<DrawRecord> log = sCanvasDrawLog.get(canvasH);
        if (log == null || log.isEmpty()) return 0;

        // Serialize DrawRecords to display list binary format
        byte[] buf = new byte[SHM_DLIST_MAX];
        int pos = 0;
        int maxPos = SHM_DLIST_MAX - 64;

        for (DrawRecord rec : log) {
            if (pos >= maxPos) break;
            try {
                switch (rec.op) {
                    case "drawColor":
                        buf[pos++] = (byte)OP_DRAW_COLOR;
                        putInt(buf, pos, rec.color); pos += 4;
                        break;
                    case "drawRect":
                        if (rec.args.length >= 4) {
                            buf[pos++] = (byte)OP_DRAW_RECT;
                            putFloat(buf, pos, rec.args[0]); pos += 4;
                            putFloat(buf, pos, rec.args[1]); pos += 4;
                            putFloat(buf, pos, rec.args[2]); pos += 4;
                            putFloat(buf, pos, rec.args[3]); pos += 4;
                            putInt(buf, pos, rec.color); pos += 4;
                        } break;
                    case "drawRoundRect":
                        if (rec.args.length >= 6) {
                            buf[pos++] = (byte)OP_DRAW_ROUND_RECT;
                            putFloat(buf, pos, rec.args[0]); pos += 4;
                            putFloat(buf, pos, rec.args[1]); pos += 4;
                            putFloat(buf, pos, rec.args[2]); pos += 4;
                            putFloat(buf, pos, rec.args[3]); pos += 4;
                            putFloat(buf, pos, rec.args[4]); pos += 4;
                            putFloat(buf, pos, rec.args[5]); pos += 4;
                            putInt(buf, pos, rec.color); pos += 4;
                        } break;
                    case "drawCircle":
                        if (rec.args.length >= 3) {
                            buf[pos++] = (byte)OP_DRAW_CIRCLE;
                            putFloat(buf, pos, rec.args[0]); pos += 4;
                            putFloat(buf, pos, rec.args[1]); pos += 4;
                            putFloat(buf, pos, rec.args[2]); pos += 4;
                            putInt(buf, pos, rec.color); pos += 4;
                        } break;
                    case "drawText":
                        if (rec.text != null && rec.args.length >= 3) {
                            byte[] t = rec.text.getBytes("UTF-8");
                            if (pos + 19 + t.length < maxPos) {
                                buf[pos++] = (byte)OP_DRAW_TEXT;
                                putFloat(buf, pos, rec.args[0]); pos += 4;
                                putFloat(buf, pos, rec.args[1]); pos += 4;
                                putFloat(buf, pos, rec.args[2]); pos += 4;
                                putInt(buf, pos, rec.color); pos += 4;
                                putShort(buf, pos, (short)t.length); pos += 2;
                                System.arraycopy(t, 0, buf, pos, t.length); pos += t.length;
                            }
                        } break;
                    case "drawLine":
                        if (rec.args.length >= 4) {
                            buf[pos++] = (byte)OP_DRAW_LINE;
                            putFloat(buf, pos, rec.args[0]); pos += 4;
                            putFloat(buf, pos, rec.args[1]); pos += 4;
                            putFloat(buf, pos, rec.args[2]); pos += 4;
                            putFloat(buf, pos, rec.args[3]); pos += 4;
                            putInt(buf, pos, rec.color); pos += 4;
                            // strokeWidth (host expects 6 floats for line)
                            float sw = (rec.args.length > 4) ? rec.args[4] : 1.0f;
                            putFloat(buf, pos, sw); pos += 4;
                        } break;
                    case "save": buf[pos++] = (byte)OP_SAVE; break;
                    case "restore": buf[pos++] = (byte)OP_RESTORE; break;
                    case "translate":
                        if (rec.args.length >= 2) {
                            buf[pos++] = (byte)OP_TRANSLATE;
                            putFloat(buf, pos, rec.args[0]); pos += 4;
                            putFloat(buf, pos, rec.args[1]); pos += 4;
                        } break;
                    case "clipRect":
                        if (rec.args.length >= 4) {
                            buf[pos++] = (byte)OP_CLIP_RECT;
                            putFloat(buf, pos, rec.args[0]); pos += 4;
                            putFloat(buf, pos, rec.args[1]); pos += 4;
                            putFloat(buf, pos, rec.args[2]); pos += 4;
                            putFloat(buf, pos, rec.args[3]); pos += 4;
                        } break;
                }
            } catch (Exception e) { break; }
        }

        sFrameSeq++;
        int dlistSize = pos;

        // Write display list then header to shm
        java.io.RandomAccessFile raf = getShmRaf();
        if (raf != null) {
            try {
                raf.seek(SHM_HEADER_SIZE);
                raf.write(buf, 0, pos);
                byte[] seqBuf = new byte[8];
                putInt(seqBuf, 0, sFrameSeq);
                putInt(seqBuf, 4, dlistSize);
                raf.seek(16);
                raf.write(seqBuf);
                raf.getFD().sync();
            } catch (Exception e) {
                System.out.println("[OHBridge] shm write error: " + e);
            }
        }

        log.clear();
        return 0;
    }

    // ── Sensor mock ──

    public static boolean sensorIsAvailable(int sensorType) {
        // Mock: accelerometer(1), gyroscope(4), light(5) are available
        return sensorType == 1 || sensorType == 4 || sensorType == 5;
    }

    public static float[] sensorGetData(int sensorType) {
        switch (sensorType) {
            case 1: return new float[]{0.0f, 0.0f, 9.8f};   // accelerometer
            case 4: return new float[]{0.0f, 0.0f, 0.0f};   // gyroscope
            case 5: return new float[]{250.0f};               // light (lux)
            default: return null;
        }
    }

    // ── Drawing test helpers ──

    public static java.util.List<DrawRecord> getDrawLog(long canvasHandle) {
        java.util.List<DrawRecord> log = sCanvasDrawLog.get(canvasHandle);
        return log != null ? log : java.util.Collections.emptyList();
    }
    public static void clearDrawLog(long canvasHandle) {
        java.util.List<DrawRecord> log = sCanvasDrawLog.get(canvasHandle);
        if (log != null) log.clear();
    }

    private static void record(long canvas, String op, float[] args, String text, int color) {
        java.util.List<DrawRecord> log = sCanvasDrawLog.get(canvas);
        if (log != null) log.add(new DrawRecord(op, args, text, color));
    }
    private static int getColor(long handle) {
        Integer c = sHandleColors.get(handle);
        return c != null ? c : 0;
    }

    // ── Permissions mock ──

    public static int checkPermission(String permission) { return 0; } // GRANTED

    // ── Vibrator mock ──

    private static volatile boolean sVibrating = false;
    private static volatile long sLastVibrateDuration = 0;

    public static boolean vibratorHasVibrator() { return true; }

    public static void vibratorVibrate(long milliseconds) {
        sLastVibrateDuration = milliseconds;
        sVibrating = true;
        System.out.println("[MOCK] Vibrator vibrate " + milliseconds + "ms");
    }

    public static void vibratorCancel() {
        sVibrating = false;
        System.out.println("[MOCK] Vibrator cancel");
    }

    /** Test helper: returns the last vibrate duration passed to vibratorVibrate(). */
    public static long getLastVibrateDuration() { return sLastVibrateDuration; }

    /** Test helper: returns true if vibrating (vibratorVibrate called, not yet cancelled). */
    public static boolean isVibrating() { return sVibrating; }

    // ── Input dispatch ──

    public static void dispatchTouchEvent(int action, float x, float y, long timestamp) {
        android.view.MotionEvent event = android.view.MotionEvent.obtain(action, x, y, timestamp);
        android.app.Activity activity = getResumedActivity();
        if (activity != null) {
            activity.dispatchTouchEvent(event);
        }
        event.recycle();
    }

    public static void dispatchKeyEvent(int action, int keyCode, long timestamp) {
        android.view.KeyEvent event = new android.view.KeyEvent(action, keyCode);
        android.app.Activity activity = getResumedActivity();
        if (activity != null) {
            activity.dispatchKeyEvent(event);
        }
    }

    private static android.app.Activity getResumedActivity() {
        try {
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null) {
                return server.getActivityManager().getResumedActivity();
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    // ── Event dispatch ──

    public static void dispatchNodeEvent(int eventId, long nodeHandle, int eventKind, String stringData) {
        // No-op in mock - AOSP View doesn't have onNativeEvent
    }

    // ── Font measurement ──

    public static float fontMeasureText(long font, String text) {
        if (text == null) return 0f;
        Float size = sHandleFontSizes.get(font);
        float fontSize = (size != null) ? size : 14.0f;
        // Proportional measurement: wider chars get more weight
        float total = 0f;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float w;
            if (c == ' ') w = 0.25f;
            else if (c == 'i' || c == 'l' || c == '!' || c == '|' || c == '.' || c == ',') w = 0.3f;
            else if (c == 'W' || c == 'M' || c == 'm' || c == 'w') w = 0.75f;
            else if (c >= 'A' && c <= 'Z') w = 0.65f;
            else w = 0.55f;
            total += w * fontSize;
        }
        return total;
    }
}
