/*
 * activity_manager_adapter.cpp
 *
 * JNI registration for adapter.activity.ActivityManagerAdapter via RegisterNatives.
 * Replaces the legacy Java_adapter_bridge_ActivityManagerAdapter_* exports
 * (and their adapter_activity_ forwarder shims) that previously lived in
 * framework/core/jni/adapter_bridge.cpp.
 *
 * Class:  adapter/activity/ActivityManagerAdapter  (BCP - oh-adapter-framework.jar)
 * Registered from adapter_bridge.cpp's JNI_OnLoad via
 *   register_ActivityManagerAdapter(env).
 *
 * 10 natives: 5 ability + 5 broadcast/CommonEvent.
 */

#include "oh_ability_manager_client.h"
#include "oh_common_event_client.h"

#include <android/log.h>
#include <jni.h>
#include <string>
#include <vector>

#define TAG "OH_AMJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

using namespace oh_adapter;

namespace {

std::string jstr(JNIEnv* env, jstring s) {
    if (!s) return "";
    const char* raw = env->GetStringUTFChars(s, nullptr);
    std::string result(raw);
    env->ReleaseStringUTFChars(s, raw);
    return result;
}

// -------- ability (5) --------

jlong nativeGetOHAbilityManagerService_impl(JNIEnv*, jclass) {
    return (jlong)&OHAbilityManagerClient::getInstance();
}

// 2026-05-19 caller-aware signature: callerOhTokenAddr=0 means system caller;
// non-zero is an OH ability token addr resolved via OhTokenRegistry for
// App-initiated calls (avoids ABMS1361 caller-invalid for normal-apl APKs).
jint nativeStartAbility_impl(JNIEnv* env, jclass,
                             jstring bundleName, jstring abilityName,
                             jstring action, jstring uri, jstring extraJson,
                             jlong callerOhTokenAddr) {
    WantParams want;
    want.bundleName = jstr(env, bundleName);
    want.abilityName = jstr(env, abilityName);
    want.action = jstr(env, action);
    want.uri = jstr(env, uri);
    want.parametersJson = jstr(env, extraJson);

    LOGI("nativeStartAbility: bundle=%s, ability=%s, action=%s, callerOhToken=0x%llx",
         want.bundleName.c_str(), want.abilityName.c_str(), want.action.c_str(),
         static_cast<unsigned long long>(callerOhTokenAddr));

    return OHAbilityManagerClient::getInstance().startAbilityWithCaller(
            want, callerOhTokenAddr);
}

jint nativeConnectAbility_impl(JNIEnv* env, jclass,
                               jstring bundleName, jstring abilityName,
                               jint connectionId) {
    WantParams want;
    want.bundleName = jstr(env, bundleName);
    want.abilityName = jstr(env, abilityName);
    LOGI("nativeConnectAbility: bundle=%s, ability=%s, connId=%d",
         want.bundleName.c_str(), want.abilityName.c_str(), connectionId);
    return OHAbilityManagerClient::getInstance().connectAbility(want, connectionId);
}

jint nativeDisconnectAbility_impl(JNIEnv*, jclass, jint connectionId) {
    LOGI("nativeDisconnectAbility: connectionId=%d", connectionId);
    return OHAbilityManagerClient::getInstance().disconnectAbility(connectionId);
}

jint nativeStopServiceAbility_impl(JNIEnv* env, jclass,
                                   jstring bundleName, jstring abilityName) {
    WantParams want;
    want.bundleName = jstr(env, bundleName);
    want.abilityName = jstr(env, abilityName);
    LOGI("nativeStopServiceAbility: bundle=%s, ability=%s",
         want.bundleName.c_str(), want.abilityName.c_str());
    return OHAbilityManagerClient::getInstance().stopServiceAbility(want);
}

// -------- broadcast / CommonEvent (5) --------

std::vector<std::string> jstrArr(JNIEnv* env, jobjectArray arr) {
    std::vector<std::string> out;
    if (!arr) return out;
    int count = env->GetArrayLength(arr);
    for (int i = 0; i < count; ++i) {
        jstring s = (jstring)env->GetObjectArrayElement(arr, i);
        out.push_back(jstr(env, s));
        if (s) env->DeleteLocalRef(s);
    }
    return out;
}

jint nativeSubscribeCommonEvent_impl(JNIEnv* env, jclass,
                                     jint subscriptionId, jobjectArray ohEventNames,
                                     jint priority, jstring permission) {
    auto events = jstrArr(env, ohEventNames);
    std::string perm = jstr(env, permission);
    return OHCommonEventClient::getInstance().subscribe(
            subscriptionId, events, priority, perm);
}

jint nativeUnsubscribeCommonEvent_impl(JNIEnv*, jclass, jint subscriptionId) {
    return OHCommonEventClient::getInstance().unsubscribe(subscriptionId);
}

jint nativePublishCommonEvent_impl(JNIEnv* env, jclass,
                                   jstring ohAction, jstring extrasJson, jstring uri,
                                   jint code, jstring data,
                                   jboolean ordered, jboolean sticky,
                                   jobjectArray subscriberPermissions) {
    std::string action = jstr(env, ohAction);
    std::string extras = jstr(env, extrasJson);
    std::string uriStr = jstr(env, uri);
    std::string dataStr = jstr(env, data);
    auto permissions = jstrArr(env, subscriberPermissions);
    return OHCommonEventClient::getInstance().publish(
            action, extras, uriStr, code, dataStr, ordered, sticky, permissions);
}

jint nativeFinishCommonEvent_impl(JNIEnv* env, jclass,
                                  jint subscriptionId, jint resultCode,
                                  jstring resultData, jboolean abortEvent) {
    std::string data = jstr(env, resultData);
    return OHCommonEventClient::getInstance().finishReceiver(
            subscriptionId, resultCode, data, abortEvent);
}

jstring nativeGetStickyCommonEvent_impl(JNIEnv* env, jclass, jstring ohEventName) {
    std::string event = jstr(env, ohEventName);
    std::string result = OHCommonEventClient::getInstance().getStickyEvent(event);
    if (result.empty()) return nullptr;
    return env->NewStringUTF(result.c_str());
}

const JNINativeMethod kMethods[] = {
    // ability
    {"nativeGetOHAbilityManagerService", "()J",
        (void*)nativeGetOHAbilityManagerService_impl},
    {"nativeStartAbility",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;"
        "Ljava/lang/String;Ljava/lang/String;J)I",
        (void*)nativeStartAbility_impl},
    {"nativeConnectAbility",
        "(Ljava/lang/String;Ljava/lang/String;I)I",
        (void*)nativeConnectAbility_impl},
    {"nativeDisconnectAbility", "(I)I",
        (void*)nativeDisconnectAbility_impl},
    {"nativeStopServiceAbility",
        "(Ljava/lang/String;Ljava/lang/String;)I",
        (void*)nativeStopServiceAbility_impl},

    // broadcast
    {"nativeSubscribeCommonEvent",
        "(I[Ljava/lang/String;ILjava/lang/String;)I",
        (void*)nativeSubscribeCommonEvent_impl},
    {"nativeUnsubscribeCommonEvent", "(I)I",
        (void*)nativeUnsubscribeCommonEvent_impl},
    {"nativePublishCommonEvent",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;"
        "ILjava/lang/String;ZZ[Ljava/lang/String;)I",
        (void*)nativePublishCommonEvent_impl},
    {"nativeFinishCommonEvent",
        "(IILjava/lang/String;Z)I",
        (void*)nativeFinishCommonEvent_impl},
    {"nativeGetStickyCommonEvent",
        "(Ljava/lang/String;)Ljava/lang/String;",
        (void*)nativeGetStickyCommonEvent_impl},
};

}  // namespace

int register_ActivityManagerAdapter(JNIEnv* env) {
    jclass clazz = env->FindClass("adapter/activity/ActivityManagerAdapter");
    if (!clazz) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        LOGE("register_ActivityManagerAdapter: FindClass returned null");
        return JNI_ERR;
    }
    jint rc = env->RegisterNatives(clazz, kMethods,
                                   sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    if (rc != JNI_OK) {
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        LOGE("register_ActivityManagerAdapter: RegisterNatives failed rc=%d", (int)rc);
    } else {
        LOGI("register_ActivityManagerAdapter: OK 10 methods");
    }
    return rc;
}
