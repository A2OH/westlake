//! Input dispatch: forwards touch and key events from C++ to Java via JNI.
//!
//! C++ XComponent callbacks → shim_dispatch_touch/key → JNI → OHBridge.dispatchTouchEvent/KeyEvent

use jni::objects::JValue;
use std::os::raw::{c_float, c_int, c_longlong};

use crate::view; // view.rs stores the JVM reference in a OnceCell

/// Called from C++ when the XComponent receives a touch event.
/// Calls OHBridge.dispatchTouchEvent(int action, float x, float y, long timestamp)
#[no_mangle]
pub unsafe extern "C" fn shim_dispatch_touch(
    action: c_int,
    x: c_float,
    y: c_float,
    timestamp: c_longlong,
) {
    let jvm = match view::get_jvm() {
        Some(vm) => vm,
        None => return,
    };

    let mut env = match jvm.attach_current_thread() {
        Ok(e) => e,
        Err(_) => return,
    };

    let class = match env.find_class("com/ohos/shim/bridge/OHBridge") {
        Ok(c) => c,
        Err(_) => return,
    };

    let _ = env.call_static_method(
        class,
        "dispatchTouchEvent",
        "(IFFJ)V",
        &[
            JValue::Int(action as i32),
            JValue::Float(x),
            JValue::Float(y),
            JValue::Long(timestamp as i64),
        ],
    );
}

/// Called from C++ when a key event is received.
/// Calls OHBridge.dispatchKeyEvent(int action, int keyCode, long timestamp)
#[no_mangle]
pub unsafe extern "C" fn shim_dispatch_key(
    action: c_int,
    key_code: c_int,
    timestamp: c_longlong,
) {
    let jvm = match view::get_jvm() {
        Some(vm) => vm,
        None => return,
    };

    let mut env = match jvm.attach_current_thread() {
        Ok(e) => e,
        Err(_) => return,
    };

    let class = match env.find_class("com/ohos/shim/bridge/OHBridge") {
        Ok(c) => c,
        Err(_) => return,
    };

    let _ = env.call_static_method(
        class,
        "dispatchKeyEvent",
        "(IIJ)V",
        &[
            JValue::Int(action as i32),
            JValue::Int(key_code as i32),
            JValue::Long(timestamp as i64),
        ],
    );
}
