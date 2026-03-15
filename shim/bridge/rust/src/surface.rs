//! Surface lifecycle management: bitmap-backed canvas → NativeWindow blitting.
//!
//! Each Activity gets a SurfaceContext when its XComponent surface is created.
//! The context owns an OH_Drawing_Bitmap + Canvas that the Java view tree draws into.
//! On flush, the bitmap pixels are copied into the NativeWindow buffer for display.

use std::collections::HashMap;
use std::sync::Mutex;

use jni::objects::JClass;
use jni::sys::{jint, jlong};
use jni::JNIEnv;
use once_cell::sync::Lazy;

use crate::oh_ffi;

struct SurfaceContext {
    bitmap: *mut oh_ffi::OH_Drawing_Bitmap,
    canvas: *mut oh_ffi::OH_Drawing_Canvas,
    native_window: *mut oh_ffi::OHNativeWindow,
    width: u32,
    height: u32,
}

// Safety: pointers are only accessed under the mutex lock
unsafe impl Send for SurfaceContext {}

static SURFACES: Lazy<Mutex<HashMap<jlong, SurfaceContext>>> =
    Lazy::new(|| Mutex::new(HashMap::new()));

static NEXT_ID: std::sync::atomic::AtomicI64 = std::sync::atomic::AtomicI64::new(1);

unsafe fn create_bitmap_canvas(w: u32, h: u32) -> (*mut oh_ffi::OH_Drawing_Bitmap, *mut oh_ffi::OH_Drawing_Canvas) {
    let bitmap = oh_ffi::OH_Drawing_BitmapCreate();
    let fmt = oh_ffi::OH_Drawing_BitmapFormat {
        color_type: 3, // ARGB_8888
        alpha_type: 2, // premul
    };
    oh_ffi::OH_Drawing_BitmapBuild(bitmap, w, h, &fmt);

    let canvas = oh_ffi::OH_Drawing_CanvasCreate();
    oh_ffi::OH_Drawing_CanvasBind(canvas, bitmap);

    (bitmap, canvas)
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceCreate(
    _env: JNIEnv,
    _class: JClass,
    _xcomponent: jlong,
    width: jint,
    height: jint,
) -> jlong {
    let w = width as u32;
    let h = height as u32;
    let (bitmap, canvas) = create_bitmap_canvas(w, h);

    let id = NEXT_ID.fetch_add(1, std::sync::atomic::Ordering::Relaxed);
    let ctx = SurfaceContext {
        bitmap,
        canvas,
        native_window: std::ptr::null_mut(), // Set later when NativeWindow is available
        width: w,
        height: h,
    };
    SURFACES.lock().unwrap().insert(id, ctx);
    id
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceDestroy(
    _env: JNIEnv,
    _class: JClass,
    surface_ctx: jlong,
) {
    if let Some(ctx) = SURFACES.lock().unwrap().remove(&surface_ctx) {
        oh_ffi::OH_Drawing_CanvasDestroy(ctx.canvas);
        oh_ffi::OH_Drawing_BitmapDestroy(ctx.bitmap);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceResize(
    _env: JNIEnv,
    _class: JClass,
    surface_ctx: jlong,
    width: jint,
    height: jint,
) {
    let mut map = SURFACES.lock().unwrap();
    if let Some(ctx) = map.get_mut(&surface_ctx) {
        oh_ffi::OH_Drawing_CanvasDestroy(ctx.canvas);
        oh_ffi::OH_Drawing_BitmapDestroy(ctx.bitmap);

        let w = width as u32;
        let h = height as u32;
        let (bitmap, canvas) = create_bitmap_canvas(w, h);

        ctx.bitmap = bitmap;
        ctx.canvas = canvas;
        ctx.width = w;
        ctx.height = h;
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceGetCanvas(
    _env: JNIEnv,
    _class: JClass,
    surface_ctx: jlong,
) -> jlong {
    let map = SURFACES.lock().unwrap();
    match map.get(&surface_ctx) {
        Some(ctx) => ctx.canvas as jlong,
        None => 0,
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceFlush(
    _env: JNIEnv,
    _class: JClass,
    surface_ctx: jlong,
) -> jint {
    let map = SURFACES.lock().unwrap();
    let ctx = match map.get(&surface_ctx) {
        Some(c) => c,
        None => return -1,
    };

    if ctx.native_window.is_null() {
        // No NativeWindow attached yet — nothing to blit to
        return 0;
    }

    // Get bitmap pixels
    let src_pixels = oh_ffi::OH_Drawing_BitmapGetPixels(ctx.bitmap);
    if src_pixels.is_null() {
        return -2;
    }

    // Request a NativeWindow buffer
    let mut buffer: *mut oh_ffi::OHNativeWindowBuffer = std::ptr::null_mut();
    let mut fence_fd: std::os::raw::c_int = -1;
    let ret = oh_ffi::OH_NativeWindow_NativeWindowRequestBuffer(
        ctx.native_window,
        &mut buffer,
        &mut fence_fd,
    );
    if ret != 0 || buffer.is_null() {
        return -3;
    }

    // Get buffer handle for stride and virtual address
    let handle = oh_ffi::OH_NativeWindow_GetBufferHandleFromNative(buffer);
    if handle.is_null() {
        return -4;
    }

    let dst_ptr = (*handle).virAddr as *mut u8;
    let dst_stride = (*handle).stride as usize;
    let src_stride = (ctx.width * 4) as usize; // ARGB_8888 = 4 bytes/pixel

    // Copy row by row (src and dst may have different strides)
    let src_ptr = src_pixels as *const u8;
    for row in 0..ctx.height as usize {
        std::ptr::copy_nonoverlapping(
            src_ptr.add(row * src_stride),
            dst_ptr.add(row * dst_stride),
            src_stride,
        );
    }

    // Flush to display
    oh_ffi::OH_NativeWindow_NativeWindowFlushBuffer(
        ctx.native_window,
        buffer,
        fence_fd,
        std::ptr::null_mut(), // full-surface region
    );

    0
}

/// Called from C++ when the XComponent surface is created.
/// Associates a NativeWindow with an existing SurfaceContext.
#[no_mangle]
pub unsafe extern "C" fn shim_surface_set_native_window(
    surface_id: jlong,
    window: *mut oh_ffi::OHNativeWindow,
) {
    let mut map = SURFACES.lock().unwrap();
    if let Some(ctx) = map.get_mut(&surface_id) {
        ctx.native_window = window;
    }
}
