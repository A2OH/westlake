/*
 * ArkUI Native Bridge for Dalvik — thin JNI wrapper around ArkUI Node-API (C).
 *
 * Links against libace_ndk.z.so (prebuilt ARM32 from OHOS headless build).
 * Provides JNI methods that OHBridge.java calls to create/manage ArkUI nodes.
 *
 * Node types match OHBridge constants:
 *   TEXT=1, IMAGE=4, SCROLL=9, LIST=10, BUTTON=13, COLUMN=16, ROW=17
 */

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>

/* ArkUI Node-API types (from native_node.h / native_type.h) */
typedef enum {
    ARKUI_NODE_CUSTOM = 0,
    ARKUI_NODE_TEXT = 1,
    ARKUI_NODE_IMAGE = 4,
    ARKUI_NODE_TOGGLE = 5,
    ARKUI_NODE_TEXT_INPUT = 7,
    ARKUI_NODE_STACK = 8,
    ARKUI_NODE_SCROLL = 9,
    ARKUI_NODE_LIST = 10,
    ARKUI_NODE_BUTTON = 13,
    ARKUI_NODE_COLUMN = 16,
    ARKUI_NODE_ROW = 17,
    ARKUI_NODE_LIST_ITEM = 19,
} ArkUI_NodeType;

typedef struct ArkUI_Node* ArkUI_NodeHandle;

typedef union {
    float f32;
    int32_t i32;
    uint32_t u32;
} ArkUI_NumberValue;

typedef struct {
    const ArkUI_NumberValue* value;
    int32_t size;
    const char* string;
    void* object;
} ArkUI_AttributeItem;

typedef enum {
    NODE_WIDTH = 0,
    NODE_HEIGHT = 1,
    NODE_BACKGROUND_COLOR = 2,
    NODE_FONT_SIZE = 100,
    NODE_FONT_COLOR = 101,
    /* Add more as needed from native_node.h */
} ArkUI_NodeAttributeType;

/* Function pointers loaded from libace_ndk.z.so */
static ArkUI_NodeHandle (*pCreateNode)(ArkUI_NodeType) = NULL;
static void (*pDisposeNode)(ArkUI_NodeHandle) = NULL;
static int32_t (*pAddChild)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child) = NULL;
static int32_t (*pRemoveChild)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child) = NULL;
static int32_t (*pInsertChildAt)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child, int32_t pos) = NULL;
static int32_t (*pSetAttribute)(ArkUI_NodeHandle node, ArkUI_NodeAttributeType attr, const ArkUI_AttributeItem* item) = NULL;
static const ArkUI_AttributeItem* (*pGetAttribute)(ArkUI_NodeHandle node, ArkUI_NodeAttributeType attr) = NULL;

static int g_initialized = 0;

static int loadArkUI() {
    if (g_initialized) return g_initialized > 0 ? 0 : -1;

    void* lib = dlopen("libace_ndk.z.so", RTLD_NOW);
    if (!lib) {
        fprintf(stderr, "[ArkUI Bridge] dlopen libace_ndk.z.so failed: %s\n", dlerror());
        /* Try with full path */
        lib = dlopen("/system/lib/libace_ndk.z.so", RTLD_NOW);
    }
    if (!lib) {
        fprintf(stderr, "[ArkUI Bridge] Cannot load ArkUI native library\n");
        g_initialized = -1;
        return -1;
    }

    pCreateNode = (ArkUI_NodeHandle(*)(ArkUI_NodeType)) dlsym(lib, "OH_ArkUI_NodeContent_CreateNode");
    if (!pCreateNode) pCreateNode = (ArkUI_NodeHandle(*)(ArkUI_NodeType)) dlsym(lib, "OH_ArkUI_CreateNode");
    pDisposeNode = (void(*)(ArkUI_NodeHandle)) dlsym(lib, "OH_ArkUI_NodeContent_DisposeNode");
    if (!pDisposeNode) pDisposeNode = (void(*)(ArkUI_NodeHandle)) dlsym(lib, "OH_ArkUI_DisposeNode");
    pAddChild = (int32_t(*)(ArkUI_NodeHandle, ArkUI_NodeHandle)) dlsym(lib, "OH_ArkUI_NodeContent_AddChild");
    if (!pAddChild) pAddChild = (int32_t(*)(ArkUI_NodeHandle, ArkUI_NodeHandle)) dlsym(lib, "OH_ArkUI_AddChild");
    pRemoveChild = (int32_t(*)(ArkUI_NodeHandle, ArkUI_NodeHandle)) dlsym(lib, "OH_ArkUI_NodeContent_RemoveChild");
    if (!pRemoveChild) pRemoveChild = (int32_t(*)(ArkUI_NodeHandle, ArkUI_NodeHandle)) dlsym(lib, "OH_ArkUI_RemoveChild");
    pSetAttribute = (int32_t(*)(ArkUI_NodeHandle, ArkUI_NodeAttributeType, const ArkUI_AttributeItem*))
        dlsym(lib, "OH_ArkUI_NodeContent_SetAttribute");
    if (!pSetAttribute) pSetAttribute = (int32_t(*)(ArkUI_NodeHandle, ArkUI_NodeAttributeType, const ArkUI_AttributeItem*))
        dlsym(lib, "OH_ArkUI_SetAttribute");

    if (pCreateNode) {
        fprintf(stderr, "[ArkUI Bridge] Loaded libace_ndk.z.so successfully\n");
        g_initialized = 1;
        return 0;
    }

    fprintf(stderr, "[ArkUI Bridge] Symbol lookup failed\n");
    g_initialized = -1;
    return -1;
}

/* Map OHBridge node type constants to ArkUI_NodeType */
static ArkUI_NodeType mapNodeType(jint type) {
    switch (type) {
        case 1:  return ARKUI_NODE_TEXT;
        case 4:  return ARKUI_NODE_IMAGE;
        case 9:  return ARKUI_NODE_SCROLL;
        case 10: return ARKUI_NODE_LIST;
        case 13: return ARKUI_NODE_BUTTON;
        case 16: return ARKUI_NODE_COLUMN;
        case 17: return ARKUI_NODE_ROW;
        case 19: return ARKUI_NODE_LIST_ITEM;
        default: return ARKUI_NODE_CUSTOM;
    }
}

/* ── JNI Methods ── */

extern "C" {

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeCreate(
    JNIEnv* env, jclass, jint nodeType)
{
    if (loadArkUI() < 0 || !pCreateNode) return 0;
    ArkUI_NodeHandle node = pCreateNode(mapNodeType(nodeType));
    return (jlong)(uintptr_t) node;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeDispose(
    JNIEnv* env, jclass, jlong handle)
{
    if (!pDisposeNode || !handle) return;
    pDisposeNode((ArkUI_NodeHandle)(uintptr_t) handle);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeAddChild(
    JNIEnv* env, jclass, jlong parent, jlong child)
{
    if (!pAddChild || !parent || !child) return;
    pAddChild((ArkUI_NodeHandle)(uintptr_t) parent,
              (ArkUI_NodeHandle)(uintptr_t) child);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeRemoveChild(
    JNIEnv* env, jclass, jlong parent, jlong child)
{
    if (!pRemoveChild || !parent || !child) return;
    pRemoveChild((ArkUI_NodeHandle)(uintptr_t) parent,
                 (ArkUI_NodeHandle)(uintptr_t) child);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrString(
    JNIEnv* env, jclass, jlong handle, jstring jkey, jstring jvalue)
{
    if (!pSetAttribute || !handle) return;
    const char* key = env->GetStringUTFChars(jkey, NULL);
    const char* val = env->GetStringUTFChars(jvalue, NULL);

    ArkUI_AttributeItem item = {};
    item.string = val;

    /* Map string key to attribute type — simplified mapping */
    ArkUI_NodeAttributeType attr = (ArkUI_NodeAttributeType) 0;
    /* The actual mapping would use the full enum from native_node.h */
    /* For now, we'll use the string approach if available */

    pSetAttribute((ArkUI_NodeHandle)(uintptr_t) handle, attr, &item);

    env->ReleaseStringUTFChars(jvalue, val);
    env->ReleaseStringUTFChars(jkey, key);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrInt(
    JNIEnv* env, jclass, jlong handle, jstring jkey, jint value)
{
    if (!pSetAttribute || !handle) return;
    ArkUI_NumberValue numVal;
    numVal.i32 = value;
    ArkUI_AttributeItem item = {};
    item.value = &numVal;
    item.size = 1;
    pSetAttribute((ArkUI_NodeHandle)(uintptr_t) handle, (ArkUI_NodeAttributeType) 0, &item);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrColor(
    JNIEnv* env, jclass, jlong handle, jstring jkey, jint argb)
{
    if (!pSetAttribute || !handle) return;
    ArkUI_NumberValue numVal;
    numVal.u32 = (uint32_t) argb;
    ArkUI_AttributeItem item = {};
    item.value = &numVal;
    item.size = 1;
    pSetAttribute((ArkUI_NodeHandle)(uintptr_t) handle,
                  (ArkUI_NodeAttributeType) NODE_BACKGROUND_COLOR, &item);
}

} /* extern "C" */
