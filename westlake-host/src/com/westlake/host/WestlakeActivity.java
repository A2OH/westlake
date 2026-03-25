package com.westlake.host;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Westlake Engine Host — runs the Westlake shim + app on the phone's own ART.
 *
 * Architecture:
 * - This Activity provides a SurfaceView for rendering
 * - Loads aosp-shim.dex + app.dex via a child-first DexClassLoader
 * - The shim's OHBridge calls back to this Activity for Canvas drawing
 * - Touch events dispatch directly to the shim's Activity
 *
 * The child-first classloader ensures the shim's android.app.Activity
 * shadows the phone's version. Core java.* classes still come from boot.
 */
public class WestlakeActivity extends Activity implements SurfaceHolder.Callback,
        androidx.lifecycle.LifecycleOwner {
    private static final String TAG = "Westlake";
    private SurfaceView surfaceView;
    private volatile boolean surfaceReady;
    private Thread engineThread;

    // AndroidX Lifecycle — needed for Jetpack Compose
    private androidx.lifecycle.LifecycleRegistry lifecycleRegistry;
    private Bundle savedStateForCompose;

    // Rendering state — accessed by OHBridge native methods
    public static Canvas currentCanvas;
    public static SurfaceHolder currentHolder;
    public static int canvasWidth, canvasHeight;
    public static Paint[] paintPool = new Paint[256];
    public static Path[] pathPool = new Path[256];
    public static int paintNext = 1, pathNext = 1;
    public static WestlakeActivity instance;

    // The shim's current Activity for touch dispatch
    public static Object shimActivity;
    public static Method shimDispatchTouch;

    // Store the shim's root view — set by intercepted setContentView
    public static android.view.View shimRootView;

    @Override
    public androidx.lifecycle.Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init lifecycle BEFORE super.onCreate
        lifecycleRegistry = new androidx.lifecycle.LifecycleRegistry(this);
        super.onCreate(savedInstanceState);
        instance = this;

        // Set ViewTree owners on DecorView so Compose can find them
        android.view.View decorView = getWindow().getDecorView();
        androidx.lifecycle.ViewTreeLifecycleOwner.set(decorView, this);
        Log.i(TAG, "ViewTreeLifecycleOwner set on DecorView");

        // Set SavedStateRegistryOwner + ViewModelStoreOwner on DecorView
        // These are set later (after compose.dex loads) via setupComposeViewTree()
        savedStateForCompose = savedInstanceState;

        lifecycleRegistry.handleLifecycleEvent(
            androidx.lifecycle.Lifecycle.Event.ON_CREATE);

        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.handleLifecycleEvent(
            androidx.lifecycle.Lifecycle.Event.ON_START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleRegistry.handleLifecycleEvent(
            androidx.lifecycle.Lifecycle.Event.ON_RESUME);
    }

    @Override
    protected void onPause() {
        lifecycleRegistry.handleLifecycleEvent(
            androidx.lifecycle.Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        lifecycleRegistry.handleLifecycleEvent(
            androidx.lifecycle.Lifecycle.Event.ON_STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(
            androidx.lifecycle.Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        currentHolder = holder;
        canvasWidth = surfaceView.getWidth();
        canvasHeight = surfaceView.getHeight();
        surfaceReady = true;
        Log.i(TAG, "Surface ready: " + canvasWidth + "x" + canvasHeight);

        engineThread = new Thread("WestlakeEngine") {
            public void run() { runEngine(); }
        };
        engineThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder h, int fmt, int w, int h2) {
        canvasWidth = w;
        canvasHeight = h2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        currentHolder = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && shimActivity != null && shimDispatchTouch != null) {
            try {
                // Scale to 480x800
                float x = event.getX() * 480f / canvasWidth;
                float y = event.getY() * 800f / canvasHeight;
                // Create shim MotionEvent and dispatch
                // For now, just log it — will wire up properly
                Log.i(TAG, "Touch UP at (" + (int)x + "," + (int)y + ")");
            } catch (Exception e) {
                Log.e(TAG, "Touch dispatch error", e);
            }
        }
        return true;
    }

    private void runEngine() {
        android.os.Looper.prepare();
        try {
            Log.i(TAG, "Extracting DEX files...");
            File cacheDir = getCacheDir();
            File shimDex = extractAsset("aosp-shim.dex", cacheDir);

            File appDex = extractAsset("app.dex", cacheDir);

            if (shimDex == null || appDex == null) {
                Log.e(TAG, "Failed to extract DEX files");
                return;
            }

            // Load compose DEX files if available
            // compose.dex may be a ZIP with classes.dex + classes2.dex inside
            // Extract all DEX entries into separate files for DexClassLoader
            File composeDexAsset = extractAsset("compose.dex", cacheDir);
            String composeDexPaths = "";
            if (composeDexAsset != null) {
                try {
                    java.io.FileInputStream fis = new java.io.FileInputStream(composeDexAsset);
                    byte[] magic = new byte[4];
                    fis.read(magic);
                    fis.close();
                    if (magic[0] == 'P' && magic[1] == 'K') {
                        // ZIP — extract each classes*.dex separately
                        java.util.zip.ZipFile zf = new java.util.zip.ZipFile(composeDexAsset);
                        java.util.Enumeration<?> entries = zf.entries();
                        while (entries.hasMoreElements()) {
                            java.util.zip.ZipEntry ze = (java.util.zip.ZipEntry) entries.nextElement();
                            if (ze.getName().endsWith(".dex")) {
                                File outDex = new File(cacheDir, "compose_" + ze.getName());
                                java.io.InputStream zis = zf.getInputStream(ze);
                                java.io.FileOutputStream fos = new java.io.FileOutputStream(outDex);
                                byte[] buf = new byte[8192];
                                int n;
                                while ((n = zis.read(buf)) > 0) fos.write(buf, 0, n);
                                fos.close();
                                zis.close();
                                composeDexPaths += ":" + outDex.getAbsolutePath();
                                Log.i(TAG, "Compose DEX extracted: " + ze.getName() + " (" + outDex.length() + " bytes)");
                            }
                        }
                        zf.close();
                    } else {
                        composeDexPaths = ":" + composeDexAsset.getAbsolutePath();
                        Log.i(TAG, "Compose DEX (raw): " + composeDexAsset.length() + " bytes");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Compose DEX extract error: " + e);
                    composeDexPaths = ":" + composeDexAsset.getAbsolutePath();
                }
            }

            // Create child-first classloader
            String dexPath = shimDex.getAbsolutePath() + ":" + appDex.getAbsolutePath() + composeDexPaths;
            File optDir = new File(cacheDir, "oat");
            optDir.mkdirs();
            String nativeLibDir = getApplicationInfo().nativeLibraryDir;

            final DexClassLoader dexLoader = new DexClassLoader(
                dexPath, optDir.getAbsolutePath(), nativeLibDir,
                getClassLoader());

            // Child-first wrapper: use findClass to bypass parent delegation
            // findClass only searches the DEX, doesn't delegate to parent
            final java.lang.reflect.Method findClassMethod;
            try {
                findClassMethod = ClassLoader.class.getDeclaredMethod("findClass", String.class);
                findClassMethod.setAccessible(true);
            } catch (Exception ex) { throw new RuntimeException(ex); }

            ClassLoader childFirst = new ClassLoader(getClassLoader()) {
                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    // Check if already loaded
                    Class<?> c = findLoadedClass(name);
                    if (c != null) return c;
                    // Core java.* always from boot
                    if (name.startsWith("java.") || name.startsWith("javax.") ||
                        name.startsWith("sun.") || name.startsWith("dalvik.system.")) {
                        return super.loadClass(name, resolve);
                    }
                    // Try DEX FIRST via findClass (bypasses parent delegation)
                    try {
                        c = (Class<?>) findClassMethod.invoke(dexLoader, name);
                        if (c != null) return c;
                    } catch (Exception e) { /* not in DEX */ }
                    // Fall back to parent
                    return super.loadClass(name, resolve);
                }
            };

            Thread.currentThread().setContextClassLoader(childFirst);

            // Setup Compose ViewTree owners now that compose.dex is loaded
            setupComposeViewTree(childFirst);

            Log.i(TAG, "Loading MockDonaldsApp...");
            Class<?> appClass = childFirst.loadClass("com.example.mockdonalds.MockDonaldsApp");
            Method main = appClass.getMethod("main", String[].class);
            Log.i(TAG, "Running...");
            main.invoke(null, (Object) new String[0]);
            Log.i(TAG, "Engine finished");

        } catch (Exception e) {
            Log.e(TAG, "Engine error: " + e.getMessage(), e);
        }
    }

    /**
     * Set up SavedStateRegistry + ViewModelStore on DecorView using compose.dex classes.
     * Must be called AFTER compose.dex is loaded in the classloader.
     */
    private void setupComposeViewTree(final ClassLoader cl) {
        try {
            final android.view.View decorView = getWindow().getDecorView();

            // Create SavedStateRegistryController and perform restore
            Class<?> ssrcClass = cl.loadClass("androidx.savedstate.SavedStateRegistryController");
            Class<?> ssroInterface = cl.loadClass("androidx.savedstate.SavedStateRegistryOwner");
            Class<?> loInterface = cl.loadClass("androidx.lifecycle.LifecycleOwner");

            // Create SavedStateRegistryController with a temporary owner
            final Object[] ssrcHolder = new Object[1];
            Object tempOwner = java.lang.reflect.Proxy.newProxyInstance(cl,
                new Class[]{ssroInterface, loInterface},
                new java.lang.reflect.InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                        if ("getLifecycle".equals(method.getName())) return lifecycleRegistry;
                        if ("getSavedStateRegistry".equals(method.getName())) {
                            return ssrcClass.getMethod("getSavedStateRegistry").invoke(ssrcHolder[0]);
                        }
                        return null;
                    }
                });
            ssrcHolder[0] = ssrcClass.getMethod("create", ssroInterface).invoke(null, tempOwner);

            // Attach lifecycle then restore saved state
            try {
                // performAttach is needed before performRestore (lifecycle 2.6+)
                Class<?> lifecycleClass = cl.loadClass("androidx.lifecycle.Lifecycle");
                ssrcClass.getMethod("performAttach").invoke(ssrcHolder[0]);
            } catch (NoSuchMethodException e) {
                // Older API — try with Lifecycle param
                try {
                    Class<?> lifecycleClass = cl.loadClass("androidx.lifecycle.Lifecycle");
                    ssrcClass.getMethod("performAttach", lifecycleClass).invoke(ssrcHolder[0], lifecycleRegistry);
                } catch (Exception e2) {
                    Log.w(TAG, "performAttach failed: " + e2);
                }
            }
            ssrcClass.getMethod("performRestore", Bundle.class).invoke(ssrcHolder[0], savedStateForCompose);
            Log.i(TAG, "SavedStateRegistry.performRestore() called");

            // Get the actual SavedStateRegistryOwner proxy for ViewTree
            final Object savedStateRegistry = ssrcClass.getMethod("getSavedStateRegistry").invoke(ssrcHolder[0]);
            Object ssroOwner = java.lang.reflect.Proxy.newProxyInstance(cl,
                new Class[]{ssroInterface, loInterface},
                new java.lang.reflect.InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getLifecycle".equals(method.getName())) return lifecycleRegistry;
                        if ("getSavedStateRegistry".equals(method.getName())) return savedStateRegistry;
                        return null;
                    }
                });

            // Set ViewTreeSavedStateRegistryOwner on DecorView
            Class<?> vtssro = cl.loadClass("androidx.savedstate.ViewTreeSavedStateRegistryOwner");
            vtssro.getMethod("set", android.view.View.class, ssroInterface).invoke(null, decorView, ssroOwner);
            Log.i(TAG, "ViewTreeSavedStateRegistryOwner set on DecorView");

            // Set ViewTreeViewModelStoreOwner on DecorView
            Class<?> vmsInterface = cl.loadClass("androidx.lifecycle.ViewModelStoreOwner");
            Class<?> vmsClass = cl.loadClass("androidx.lifecycle.ViewModelStore");
            final Object vmStore = vmsClass.newInstance();
            Object vmsOwner = java.lang.reflect.Proxy.newProxyInstance(cl,
                new Class[]{vmsInterface},
                new java.lang.reflect.InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getViewModelStore".equals(method.getName())) return vmStore;
                        return null;
                    }
                });
            Class<?> vtvms = cl.loadClass("androidx.lifecycle.ViewTreeViewModelStoreOwner");
            vtvms.getMethod("set", android.view.View.class, vmsInterface).invoke(null, decorView, vmsOwner);
            Log.i(TAG, "ViewTreeViewModelStoreOwner set on DecorView");

        } catch (Exception e) {
            Log.e(TAG, "setupComposeViewTree failed: " + e.getMessage(), e);
        }
    }

    private File extractAsset(String name, File dir) {
        try {
            InputStream is = getAssets().open(name);
            File out = new File(dir, name);
            FileOutputStream fos = new FileOutputStream(out);
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) fos.write(buf, 0, n);
            fos.close();
            is.close();
            return out;
        } catch (Exception e) {
            Log.e(TAG, "Extract " + name + ": " + e.getMessage());
            return null;
        }
    }

    // ═══ Called by OHBridge native methods for rendering ═══

    public static void beginFrame() {
        if (currentHolder == null) return;
        currentCanvas = currentHolder.lockCanvas();
    }

    public static void endFrame() {
        if (currentHolder != null && currentCanvas != null) {
            currentHolder.unlockCanvasAndPost(currentCanvas);
            currentCanvas = null;
        }
    }

    public static Paint getPaint(int id) {
        if (id > 0 && id < 256) return paintPool[id];
        return null;
    }

    public static int newPaint(int style) {
        int id = paintNext++;
        if (id >= 256) { id = 1; paintNext = 2; }
        paintPool[id] = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPool[id].setStyle(style == 0 ? Paint.Style.FILL : Paint.Style.STROKE);
        return id;
    }

    public static Path getPath(int id) {
        if (id > 0 && id < 256) return pathPool[id];
        return null;
    }

    public static int newPath() {
        int id = pathNext++;
        if (id >= 256) { id = 1; pathNext = 2; }
        pathPool[id] = new Path();
        return id;
    }
}
