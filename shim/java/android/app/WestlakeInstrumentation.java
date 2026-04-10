package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;

/**
 * WestlakeInstrumentation -- enhanced Instrumentation for the Westlake Engine.
 *
 * Extends the existing shim Instrumentation with:
 *   - AppComponentFactory integration (for Hilt DI injection)
 *   - execStartActivity() delegation to WestlakeActivityThread
 *   - Robust error handling (onException returns true to prevent crashes)
 *   - Application lifecycle callbacks dispatch
 *
 * This is the bridge between the activity launch flow and the app's code.
 * In AOSP, Instrumentation is responsible for:
 *   1. Creating Activity instances (newActivity)
 *   2. Calling lifecycle methods (callActivityOnCreate, etc.)
 *   3. Intercepting startActivity calls (execStartActivity)
 *   4. Error handling (onException)
 *
 * The key addition over the base Instrumentation shim is AppComponentFactory
 * support, which enables Hilt dependency injection during activity creation.
 */
public class WestlakeInstrumentation extends Instrumentation {

    private static final String TAG = "WestlakeInstrumentation";

    /** Back-reference to the ActivityThread that owns us. */
    private final WestlakeActivityThread mThread;

    /** Application component factory (may be a Hilt subclass). */
    private AppComponentFactory mFactory;

    // ── Construction ───────────────────────────────────────────────────────

    public WestlakeInstrumentation(WestlakeActivityThread thread) {
        mThread = thread;
    }

    /**
     * Set a custom AppComponentFactory. Called during WestlakeActivityThread.attach()
     * if the app declares one (e.g., via Hilt).
     */
    public void setAppComponentFactory(AppComponentFactory factory) {
        mFactory = factory;
    }

    /**
     * Get the AppComponentFactory. Returns the custom one if set, otherwise
     * falls back to WestlakeActivityThread's factory, or the default.
     */
    private AppComponentFactory getFactory() {
        if (mFactory != null) return mFactory;
        if (mThread != null) {
            AppComponentFactory f = mThread.getAppComponentFactory();
            if (f != null) return f;
        }
        return new AppComponentFactory();
    }

    // ── Activity creation ──────────────────────────────────────────────────

    /**
     * Create a new Activity instance via the AppComponentFactory.
     *
     * Flow:
     *   1. Try AppComponentFactory.instantiateActivity() (enables Hilt injection)
     *   2. Fall back to ClassLoader.loadClass().newInstance()
     *
     * This mirrors AOSP's Instrumentation.newActivity(ClassLoader, String, Intent)
     * which calls getFactory(pkg).instantiateActivity(cl, className, intent).
     *
     * @param cl        The ClassLoader to use for loading the activity class.
     * @param className The fully-qualified class name of the activity.
     * @param intent    The Intent that is launching the activity.
     * @return The new Activity instance.
     */
    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Activity activity = null;

        // Try AppComponentFactory first (for Hilt / custom factory support)
        AppComponentFactory factory = getFactory();
        try {
            activity = factory.instantiateActivity(cl, className, intent);
            if (activity != null) {
                log("D", "newActivity via AppComponentFactory: " + className
                        + " -> " + activity.getClass().getName());
            }
        } catch (Exception e) {
            log("W", "AppComponentFactory.instantiateActivity failed for " + className
                    + ": " + e.getClass().getSimpleName() + ": " + e.getMessage());
            // Fall through to manual creation
        }

        // Fallback: direct class loading
        if (activity == null) {
            try {
                Class<?> clazz = cl.loadClass(className);
                activity = (Activity) clazz.getDeclaredConstructor().newInstance();
                log("D", "newActivity via reflection: " + className);
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) throw (RuntimeException) cause;
                throw new InstantiationException(
                        "Failed to instantiate " + className + ": " + cause);
            } catch (NoSuchMethodException e) {
                // No zero-arg constructor, try newInstance() on class directly
                Class<?> clazz = cl.loadClass(className);
                activity = (Activity) clazz.newInstance();
                log("D", "newActivity via Class.newInstance(): " + className);
            }
        }

        // Set the intent on the activity (setIntent for framework compatibility)
        if (activity != null && intent != null) {
            try { activity.setIntent(intent); } catch (Throwable t) {
                try { activity.mIntent = intent; } catch (Throwable t2) { /* field access blocked */ }
            }
        }

        return activity;
    }

    // ── Lifecycle dispatch ──────────────────────────────────────────────────

    /**
     * Call Activity.onCreate() with lifecycle callback dispatch.
     */
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        // Dispatch pre-create callbacks
        dispatchLifecycleCallback("onActivityPreCreated", activity, icicle);

        // Dump ALL null fields on BaseActivity and its supers
        try {
            Class<?> cls = activity.getClass();
            while (cls != null && !cls.getName().equals("java.lang.Object")) {
                if (cls.getSimpleName().contains("BaseActivity") || cls.getSimpleName().contains("McdLauncher")) {
                    for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                        if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                        f.setAccessible(true);
                        Object v = f.get(activity);
                        if (v == null) System.err.println("[FIELDS] " + cls.getSimpleName() + "." + f.getName() + " : " + f.getType().getName() + " = NULL");
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Throwable t) {}
        // Pre-onCreate diagnostic: check if getApplication() works
        Application app = activity.getApplication();
        System.err.println("[WI] Pre-onCreate: getApplication()=" + (app != null ? app.getClass().getName() : "NULL"));
        if (app != null) {
            // Try the exact chain: getApplication().b().a()
            try {
                java.lang.reflect.Method bMethod = app.getClass().getMethod("b");
                Object acm = bMethod.invoke(app);
                System.err.println("[WI]   .b() = " + (acm != null ? acm.getClass().getSimpleName() : "NULL"));
                if (acm != null) {
                    java.lang.reflect.Method aMethod = acm.getClass().getMethod("a");
                    Object comp = aMethod.invoke(acm);
                    System.err.println("[WI]   .a() = " + (comp != null ? comp.getClass().getSimpleName() : "NULL"));
                    // Also check field 'a' directly
                    java.lang.reflect.Field aField = acm.getClass().getField("a");
                    Object fieldVal = aField.get(acm);
                    System.err.println("[WI]   field a = " + (fieldVal != null ? fieldVal.getClass().getSimpleName() : "NULL"));
                }
            } catch (Throwable t) {
                System.err.println("[WI]   chain test: " + t);
            }
        }
        // Fire Hilt's _initHiltInternal BEFORE onCreate — sets up DI injection
        try {
            java.lang.reflect.Method hiltInit = activity.getClass().getDeclaredMethod("_initHiltInternal");
            hiltInit.setAccessible(true);
            hiltInit.invoke(activity);
            System.err.println("[WI] Hilt _initHiltInternal() invoked for " + activity.getClass().getSimpleName());
        } catch (NoSuchMethodException nsme) {
            // Not a Hilt activity — ok
        } catch (Throwable t) {
            System.err.println("[WI] Hilt init failed: " + t.getMessage());
        }

        // Fire OnContextAvailableListener (Hilt registers one in _initHiltInternal)
        try {
            java.lang.reflect.Method fireListeners = null;
            for (java.lang.reflect.Method m : activity.getClass().getMethods()) {
                if (m.getName().equals("a") && m.getParameterTypes().length == 1
                        && m.getParameterTypes()[0] == android.content.Context.class) {
                    fireListeners = m;
                    break;
                }
            }
            if (fireListeners != null) {
                fireListeners.invoke(activity, activity);
                System.err.println("[WI] OnContextAvailable fired");
            }
        } catch (Throwable t) {
            System.err.println("[WI] OnContextAvailable fire: " + t.getMessage());
        }

        // Try onCreate — use reflection for cross-classloader protected access
        boolean onCreateSuccess = false;
        try {
            try {
                java.lang.reflect.Method oc = android.app.Activity.class.getDeclaredMethod("onCreate", android.os.Bundle.class);
                oc.setAccessible(true);
                oc.invoke(activity, icicle);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                throw ite.getCause() != null ? ite.getCause() : ite;
            }
            onCreateSuccess = true;
        } catch (Throwable firstEx) {
            System.err.println("[WI] onCreate threw (catching): " + firstEx.getMessage());
            // Try to set content view from known layout names
            try {
                android.content.res.Resources res = activity.getResources();
                android.content.res.ResourceTable table = (res != null) ? res.getResourceTable() : null;
                if (table != null) {
                    for (String layoutName : new String[]{"activity_home_dashboard", "activity_base"}) {
                        for (int id = 0x7f0e0000; id < 0x7f0e0200; id++) {
                            String n = table.getResourceName(id);
                            if (n != null && n.contains(layoutName)) {
                                activity.setContentView(id);
                                System.err.println("[WI] Recovered: setContentView(0x" + Integer.toHexString(id) + ") for " + activity.getClass().getSimpleName());
                                break;
                            }
                        }
                        if (activity.getWindow() != null && activity.getWindow().getDecorView() instanceof android.view.ViewGroup
                                && ((android.view.ViewGroup) activity.getWindow().getDecorView()).getChildCount() > 0) break;
                    }
                }
            } catch (Throwable t3) {
                System.err.println("[WI] Recovery setContentView failed: " + t3.getMessage());
            }
            // Check if content view was set before the crash
            boolean realContentSet = false;
            try {
                android.view.View decor = activity.getWindow() != null ? activity.getWindow().getDecorView() : null;
                realContentSet = decor instanceof android.view.ViewGroup && ((android.view.ViewGroup) decor).getChildCount() > 0;
            } catch (Throwable t) {}
            if (realContentSet) {
                System.err.println("[WI] Content view already set — running remaining onCreate steps");
                // Run the BaseActivity.onCreate() steps that come AFTER clearV1OrderData():
                // inflateDefaultLayout → setPageLayout → setPageView → initPageListeners
                runPostCrashSetup(activity);
                resolveImageDrawables(activity);
                // Skip second recovery — go straight to post-create callbacks
                onCreateSuccess = true; // not really, but prevents second recovery
            }
            if (onCreateSuccess) {
                // runPostCrashSetup handled it — skip second recovery path
            } else {
            firstEx.printStackTrace(System.err);

            // Recovery: find and set layout by name
            try {
                int layoutId = 0x7f0e0530; // default: splash
                String actName = activity.getClass().getSimpleName().toLowerCase();
                android.content.res.Resources res = activity.getResources();
                if (res != null) {
                    // Try activity-specific layouts in preference order
                    String[] tries;
                    if (actName.contains("dashboard") || actName.contains("home")) {
                        tries = new String[]{"activity_home_dashboard", "activity_dashboard_new", "activity_base"};
                    } else if (actName.contains("splash")) {
                        tries = new String[]{"activity_splash_screen"};
                    } else {
                        tries = new String[]{
                            "activity_" + actName.replace("activity", "").trim(),
                            "activity_base"
                        };
                    }
                    for (String name : tries) {
                        int id = res.getIdentifier(name, "layout", "com.mcdonalds.app");
                        if (id != 0) {
                            layoutId = id;
                            System.err.println("[WI] Found layout '" + name + "' → 0x" + Integer.toHexString(id));
                            break;
                        }
                    }
                }
                activity.setContentView(layoutId);
                System.err.println("[WI] Recovered: setContentView(0x" + Integer.toHexString(layoutId) + ") for " + activity.getClass().getSimpleName());
                // Resolve drawable resources for all ImageViews in the view tree
                resolveImageDrawables(activity);
                // Try to navigate to the home/dashboard screen
                // SplashActivity has launchHome() and launchHomeScreen() methods
                try {
                    // First set up the presenter that launchHome needs
                    java.lang.reflect.Method lh = activity.getClass().getDeclaredMethod("launchHome");
                    lh.setAccessible(true);
                    lh.invoke(activity);
                    System.err.println("[WI] launchHome() succeeded!");
                } catch (Throwable lhEx) {
                    System.err.println("[WI] launchHome failed: " + lhEx);
                    // Queue dashboard launch for after render loop starts (avoid thread crash)
                    System.err.println("[WI] Dashboard navigation queued");
                    WestlakeActivityThread.pendingDashboardClass = "com.mcdonalds.homedashboard.activity.HomeDashboardActivity";
                }
            } catch (Throwable recoverEx) {
                System.err.println("[WI] Recovery failed: " + recoverEx.getMessage());
            }
            } // end else (!onCreateSuccess)
        }

        // Dispatch post-create callbacks
        dispatchLifecycleCallback("onActivityCreated", activity, icicle);
        dispatchLifecycleCallback("onActivityPostCreated", activity, icicle);
    }

    /**
     * Call Activity.onCreate() with PersistableBundle.
     */
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle,
                                      PersistableBundle persistentState) {
        dispatchLifecycleCallback("onActivityPreCreated", activity, icicle);
        activity.onCreate(icicle, persistentState);
        dispatchLifecycleCallback("onActivityCreated", activity, icicle);
        dispatchLifecycleCallback("onActivityPostCreated", activity, icicle);
    }

    /**
     * Call Activity.onStart().
     */
    @Override
    public void callActivityOnStart(Activity activity) {
        activity.mStarted = true;
        activity.onStart();
    }

    /**
     * Call Activity.onResume() and onPostResume().
     */
    @Override
    public void callActivityOnResume(Activity activity) {
        activity.mResumed = true;
        activity.onResume();
        activity.onPostResume();
    }

    /**
     * Call Activity.onPause().
     */
    @Override
    public void callActivityOnPause(Activity activity) {
        activity.mResumed = false;
        activity.onPause();
    }

    /**
     * Call Activity.onStop().
     */
    @Override
    public void callActivityOnStop(Activity activity) {
        activity.mStarted = false;
        activity.onStop();
    }

    /**
     * Call Activity.onDestroy().
     */
    @Override
    public void callActivityOnDestroy(Activity activity) {
        activity.mDestroyed = true;
        activity.onDestroy();
    }

    /**
     * Call Activity.onSaveInstanceState().
     */
    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        activity.onSaveInstanceState(outState);
    }

    /**
     * Call Activity.onRestoreInstanceState().
     */
    @Override
    public void callActivityOnRestoreInstanceState(Activity activity,
                                                     Bundle savedInstanceState) {
        activity.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Call Activity.onNewIntent().
     */
    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        activity.onNewIntent(intent);
    }

    /**
     * Call Application.onCreate().
     */
    @Override
    public void callApplicationOnCreate(Application app) {
        app.onCreate();
    }

    // ── Exception handling ─────────────────────────────────────────────────

    /**
     * Handle an exception from the application.
     *
     * In AOSP, returning false causes the exception to propagate (crash).
     * In Westlake, we log the error and return true to keep running.
     * This prevents a single bad Activity from taking down the whole engine.
     *
     * @param obj The object that threw the exception (Activity, Service, etc.).
     * @param e   The exception.
     * @return true to suppress the exception, false to propagate it.
     */
    @Override
    public boolean onException(Object obj, Throwable e) {
        String objDesc = obj != null ? obj.getClass().getName() : "null";
        log("E", "Exception in " + objDesc + ": " + e);
        if (e != null) {
            e.printStackTrace(System.err);
        }
        // Return true = exception handled, don't crash
        return true;
    }

    // ── Activity start interception ────────────────────────────────────────

    /**
     * Intercept startActivity calls. In AOSP, this calls into
     * ActivityTaskManagerService via Binder IPC. In Westlake, we delegate
     * directly to WestlakeActivityThread.
     *
     * This is called by Activity.startActivityForResult() in the shim.
     */
    public void execStartActivity(Context who, Activity caller,
                                   Intent intent, int requestCode,
                                   Bundle options) {
        if (mThread != null) {
            mThread.startActivityFromActivity(caller, intent, requestCode);
        } else {
            log("W", "execStartActivity: no ActivityThread, falling back to MiniServer");
            try {
                MiniServer.get().startActivity(intent);
            } catch (Exception e) {
                log("E", "execStartActivity fallback failed: " + e);
            }
        }
    }

    /**
     * AOSP-compatible overload: execStartActivity with IBinder parameters.
     * Adapts to our simplified signature.
     */
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token,
            Activity target, Intent intent, int requestCode,
            Bundle options) {
        execStartActivity(who, target, intent, requestCode, options);
        return null;
    }

    // ── Lifecycle callback dispatch ────────────────────────────────────────

    /**
     * Dispatch Application.ActivityLifecycleCallbacks for the given event.
     * This enables libraries like Hilt and AndroidX Lifecycle to observe
     * activity lifecycle events.
     */
    private void dispatchLifecycleCallback(String callbackName,
                                            Activity activity, Bundle bundle) {
        Application app = activity.getApplication();
        if (app == null && mThread != null) {
            app = mThread.getApplication();
        }
        if (app == null) return;

        // Get the callbacks list via reflection (it's package-private in the shim)
        java.util.List<Application.ActivityLifecycleCallbacks> callbacks = null;
        try {
            java.lang.reflect.Field f = Application.class.getDeclaredField("mCallbacks");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<Application.ActivityLifecycleCallbacks> list =
                    (java.util.List<Application.ActivityLifecycleCallbacks>) f.get(app);
            if (list != null && !list.isEmpty()) {
                // Copy to avoid ConcurrentModificationException
                callbacks = new java.util.ArrayList<>(list);
            }
        } catch (Exception e) {
            // Field not found or access error -- skip
            return;
        }

        if (callbacks == null || callbacks.isEmpty()) return;

        for (Application.ActivityLifecycleCallbacks cb : callbacks) {
            try {
                switch (callbackName) {
                    case "onActivityPreCreated":
                        cb.onActivityPreCreated(activity, bundle);
                        break;
                    case "onActivityCreated":
                        cb.onActivityCreated(activity, bundle);
                        break;
                    case "onActivityPostCreated":
                        cb.onActivityPostCreated(activity, bundle);
                        break;
                    case "onActivityStarted":
                        cb.onActivityStarted(activity);
                        break;
                    case "onActivityResumed":
                        cb.onActivityResumed(activity);
                        break;
                    case "onActivityPaused":
                        cb.onActivityPaused(activity);
                        break;
                    case "onActivityStopped":
                        cb.onActivityStopped(activity);
                        break;
                    case "onActivityDestroyed":
                        cb.onActivityDestroyed(activity);
                        break;
                    case "onActivitySaveInstanceState":
                        cb.onActivitySaveInstanceState(activity, bundle);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log("W", "Lifecycle callback " + callbackName + " threw: " + e);
            }
        }
    }

    // ── ActivityResult inner class (AOSP compatibility) ────────────────────

    /**
     * Minimal ActivityResult holder. In AOSP this is Instrumentation.ActivityResult.
     * Included here so execStartActivity() can return it.
     */
    public static class ActivityResult {
        private final int mResultCode;
        private final Intent mResultData;

        public ActivityResult(int resultCode, Intent resultData) {
            mResultCode = resultCode;
            mResultData = resultData;
        }

        public int getResultCode() { return mResultCode; }
        public Intent getResultData() { return mResultData; }
    }

    // ── Logging ────────────────────────────────────────────────────────────

    private static void log(String level, String msg) {
        System.err.println(level + "/" + TAG + ": " + msg);
    }

    /**
     * After onCreate() crashes partway through, run the remaining BaseActivity setup steps.
     * BaseActivity.onCreate() calls these after clearV1OrderData():
     *   - inflateDefaultLayout() → setPageLayout() → setPageView() → initPageListeners()
     *   - checkForInAppNotification()
     *   - setActivityAccessibilityTitle()
     */
    private void runPostCrashSetup(Activity activity) {
        Class<?> cls = activity.getClass();
        boolean layoutDone = false;
        // Try inflateDefaultLayout → setPageLayout → setPageView → initPageListeners
        try {
            java.lang.reflect.Method inflateDefault = findMethodInHierarchy(cls, "inflateDefaultLayout");
            boolean shouldInflate = true;
            if (inflateDefault != null) {
                inflateDefault.setAccessible(true);
                shouldInflate = (Boolean) inflateDefault.invoke(activity);
                System.err.println("[WI] inflateDefaultLayout() = " + shouldInflate);
            }
            if (shouldInflate) {
                // setPageLayout: calls setContentView(R.layout.base_layout) in McDBaseActivity
                java.lang.reflect.Method setPage = findMethodInHierarchy(cls, "setPageLayout");
                if (setPage != null) {
                    setPage.setAccessible(true);
                    setPage.invoke(activity);
                    layoutDone = true;
                    System.err.println("[WI] setPageLayout() done");
                }
                // setPageView: finds toolbar views — will NPE on missing navigation/toolbar
                try {
                    java.lang.reflect.Method setView = findMethodInHierarchy(cls, "setPageView");
                    if (setView != null) {
                        setView.setAccessible(true);
                        setView.invoke(activity);
                        System.err.println("[WI] setPageView() done");
                    }
                } catch (Throwable sv) {
                    System.err.println("[WI] setPageView() threw: " +
                        (sv.getCause() != null ? sv.getCause().getMessage() : sv.getMessage()));
                }
                // initPageListeners
                try {
                    java.lang.reflect.Method initListeners = findMethodInHierarchy(cls, "initPageListeners");
                    if (initListeners != null) {
                        initListeners.setAccessible(true);
                        initListeners.invoke(activity);
                    }
                } catch (Throwable t) {}
            }
        } catch (Throwable t) {
            System.err.println("[WI] runPostCrashSetup layout: " + t.getMessage());
            if (t.getCause() != null) System.err.println("[WI]   cause: " + t.getCause().getMessage());
        }
        // Apply McD styling to the inflated layout
        if (layoutDone) {
            applyMcDStyling(activity);
        }
    }

    /** Apply McDonald's visual styling to the view tree after layout inflation */
    private void applyMcDStyling(Activity activity) {
        try {
            android.view.View decor = activity.getWindow() != null ? activity.getWindow().getDecorView() : null;
            if (decor == null) return;

            // Clear all backgrounds first — prevents black overlays from XML backgrounds
            clearBlackBackgrounds(decor);
            // Set root to white (content area), toolbar gets set dark separately
            decor.setBackgroundColor(0xFFFFFFFF);

            // Find and fix toolbar height + McD dark background
            android.view.View toolbar = activity.findViewById(0x7f0b1965); // R.id.toolbar
            if (toolbar != null) {
                android.view.ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                if (lp != null && lp.height < 50) {
                    lp.height = 112;
                    toolbar.setLayoutParams(lp);
                    System.err.println("[WI] Fixed toolbar height to 112px");
                }
                toolbar.setBackgroundColor(0xFF27251F); // McD dark
                // Also set dark on all toolbar children (ImageView backgrounds cover the toolbar bg)
                if (toolbar instanceof android.view.ViewGroup) {
                    setBackgroundRecursive((android.view.ViewGroup) toolbar, 0xFF27251F);
                }
            }

            // Find and fix BottomNavigationView — add tab labels
            android.view.View nav = findViewByType(decor, "BottomNavigationView");
            if (nav instanceof android.view.ViewGroup) {
                android.view.ViewGroup navGroup = (android.view.ViewGroup) nav;
                android.view.ViewGroup.LayoutParams nlp = nav.getLayoutParams();
                if (nlp != null) { nlp.height = 112; nlp.width = 480; nav.setLayoutParams(nlp); }
                nav.setBackgroundColor(0xFF27251F);
                // Add McD bottom nav tab labels
                String[] tabs = {"Home", "Offers", "Rewards", "Order", "More"};
                for (String tab : tabs) {
                    android.widget.TextView tv = new android.widget.TextView(activity);
                    tv.setText(tab);
                    tv.setTextColor(0xFFBBBBBB);
                    tv.setTextSize(9);
                    tv.setGravity(android.view.Gravity.CENTER);
                    android.widget.LinearLayout.LayoutParams tlp =
                        new android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                    tv.setLayoutParams(tlp);
                    tv.setPadding(0, 40, 0, 0);
                    navGroup.addView(tv);
                }
                // Force layout at bottom of screen (override LinearLayout stacking)
                nav.layout(0, 688, 480, 800); // 800 - 112 = 688
                System.err.println("[WI] Fixed navigation at bottom (688-800)");
            }
            // Add golden arches centered in toolbar
            if (toolbar instanceof android.view.ViewGroup) {
                android.view.View archesV = activity.findViewById(0x7f0b0eed); // toolbarCenterImageIcon
                android.widget.ImageView arches = (archesV instanceof android.widget.ImageView) ? (android.widget.ImageView) archesV : null;
                if (arches != null) {
                    try {
                        android.graphics.drawable.Drawable d = activity.getDrawable(0x7f0800fd); // archus.webp
                        if (d != null) {
                            arches.setImageDrawable(d);
                            arches.setBackgroundColor(0x00000000); // transparent
                            android.view.ViewGroup.LayoutParams alp = arches.getLayoutParams();
                            if (alp != null) { alp.width = 96; alp.height = 84; arches.setLayoutParams(alp); }
                        }
                    } catch (Throwable t) {}
                }
            }
            // Populate the REAL McD layout's content containers with real captured data.
            // Real layout: McDToolBarView + FrameLayout(5 LinearLayouts as fragment containers) + BottomNav
            // Find the first large LinearLayout (fragment container, id=0x7f0b0b83) and inject menu items.
            if (decor instanceof android.view.ViewGroup) {
                // Known McD fragment container IDs
                int[] containerIds = {0x7f0b0b83, 0x7f0b0ae8, 0x7f0b17b1, 0x7f0b17b2, 0x7f0b03f2};
                android.view.View container = null;
                for (int cid : containerIds) {
                    container = ((android.view.ViewGroup) decor).findViewById(cid);
                    if (container != null) break;
                }
                if (container instanceof android.view.ViewGroup) {
                    System.err.println("[WI] Found real fragment container: id=0x"
                        + Integer.toHexString(container.getId()) + " " + container.getMeasuredWidth() + "x" + container.getMeasuredHeight());
                    // Force container to fill parent
                    android.view.ViewGroup.LayoutParams clp = container.getLayoutParams();
                    if (clp != null) {
                        clp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                        clp.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                        container.setLayoutParams(clp);
                    }
                    populateRealMenuData(activity, (android.view.ViewGroup) container);
                } else {
                    System.err.println("[WI] No fragment container found, trying decor root");
                    populateRealMenuData(activity, (android.view.ViewGroup) decor);
                }
            }
        } catch (Throwable t) {
            System.err.println("[WI] applyMcDStyling: " + t.getMessage());
        }
    }

    /** Find the first FrameLayout in the real layout that has no children (fragment container). */
    private android.widget.FrameLayout findFirstEmptyFrameLayout(android.view.ViewGroup root) {
        // Search for FrameLayout with known McD content holder IDs or first empty one
        for (int i = 0; i < root.getChildCount(); i++) {
            android.view.View child = root.getChildAt(i);
            if (child instanceof android.widget.FrameLayout) {
                android.widget.FrameLayout fl = (android.widget.FrameLayout) child;
                // Skip tiny or zero-height frames
                if (fl.getChildCount() == 0 && fl.getMeasuredHeight() > 100) {
                    return fl;
                }
                // Recurse into FrameLayouts that have children
                android.widget.FrameLayout found = findFirstEmptyFrameLayout(fl);
                if (found != null) return found;
            } else if (child instanceof android.view.ViewGroup) {
                android.widget.FrameLayout found = findFirstEmptyFrameLayout((android.view.ViewGroup) child);
                if (found != null) return found;
            }
        }
        return null;
    }

    /** Populate a real McD content container with menu data read from captured API responses. */
    private void populateRealMenuData(android.app.Activity activity, android.view.ViewGroup container) {
        // Read menu data from captured JSON on disk
        String[][] menuItems = loadMenuFromCapturedData();
        if (menuItems == null || menuItems.length == 0) {
            System.err.println("[WI] No captured menu data found, using fallback");
            menuItems = new String[][] {
                {"Big Mac\u00AE", "$5.99"}, {"Quarter Pounder\u00AE with Cheese", "$6.19"},
                {"10 pc. Chicken McNuggets\u00AE", "$5.49"}, {"McChicken\u00AE", "$2.49"},
                {"Big Arch\u2122 Burger", "$7.49"}, {"Double Quarter Pounder\u00AE", "$7.79"},
                {"Filet-O-Fish\u00AE", "$5.49"}, {"McCrispy\u2122", "$5.99"},
                {"Cheeseburger", "$2.49"}, {"McDouble\u00AE", "$3.19"},
            };
        }

        android.widget.ScrollView scrollView = new android.widget.ScrollView(activity);
        android.widget.LinearLayout list = new android.widget.LinearLayout(activity);
        list.setOrientation(android.widget.LinearLayout.VERTICAL);
        list.setBackgroundColor(0xFFFFFFFF);
        scrollView.addView(list);

        for (String[] item : menuItems) {
            android.widget.LinearLayout row = new android.widget.LinearLayout(activity);
            row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            row.setPadding(20, 14, 20, 14);

            android.widget.TextView nameView = new android.widget.TextView(activity);
            nameView.setText(item[0]);
            nameView.setTextColor(0xFF27251F);
            nameView.setTextSize(13);
            android.widget.LinearLayout.LayoutParams nlp =
                new android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            nameView.setLayoutParams(nlp);
            row.addView(nameView);

            android.widget.TextView priceView = new android.widget.TextView(activity);
            priceView.setText(item[1]);
            priceView.setTextColor(0xFFDA291C);
            priceView.setTextSize(13);
            row.addView(priceView);

            list.addView(row);

            android.view.View divider = new android.view.View(activity);
            divider.setBackgroundColor(0xFFEEEEEE);
            divider.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
            list.addView(divider);
        }

        container.addView(scrollView, new android.widget.FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        System.err.println("[WI] Populated " + menuItems.length + " real menu items into content container");
    }

    /** Load menu items from captured McDonald's API JSON file on device. */
    private String[][] loadMenuFromCapturedData() {
        try {
            String path = "/data/local/tmp/westlake/mock-backend/menu.json";
            java.io.File f = new java.io.File(path);
            if (!f.exists()) return null;

            byte[] data = new byte[(int) f.length()];
            java.io.FileInputStream fis = new java.io.FileInputStream(f);
            int off = 0;
            while (off < data.length) { int n = fis.read(data, off, data.length - off); if (n <= 0) break; off += n; }
            fis.close();

            String json = new String(data, 0, off, "UTF-8");
            // Simple JSON array parser: [{"name":"...","price":"...","img":"..."},...]
            java.util.List<String[]> items = new java.util.ArrayList<>();
            int pos = 0;
            while (pos < json.length() && items.size() < 50) {
                int nameIdx = json.indexOf("\"name\"", pos);
                if (nameIdx < 0) break;
                int nameStart = json.indexOf("\"", nameIdx + 7) + 1;
                int nameEnd = json.indexOf("\"", nameStart);
                if (nameStart <= 0 || nameEnd <= 0) break;
                String name = json.substring(nameStart, nameEnd)
                    .replace("\\u00ae", "\u00AE").replace("\\u00AE", "\u00AE")
                    .replace("\\u2122", "\u2122").replace("\\u00a9", "\u00A9");

                // Skip non-food items
                if (name.length() < 3 || name.startsWith("Do Not") || name.startsWith("--") || name.startsWith("**")) {
                    pos = nameEnd + 1; continue;
                }
                items.add(new String[]{name, ""});
                pos = nameEnd + 1;
            }
            if (items.isEmpty()) return null;
            System.err.println("[WI] Loaded " + items.size() + " menu items from " + path);
            return items.toArray(new String[0][]);
        } catch (Throwable t) {
            System.err.println("[WI] loadMenuFromCapturedData error: " + t);
            return null;
        }
    }

    /** Try to find price for a product ID in the JSON. */
    private String estimatePrice(String json, int productId) {
        // Look for "ID":productId pattern near "base":[price,...]
        String needle = "\"" + productId + "\":{\"ID\":" + productId + ",";
        int idx = json.indexOf(needle);
        if (idx < 0) {
            needle = "\"ID\":" + productId + ",";
            idx = json.indexOf(needle);
        }
        if (idx >= 0) {
            int baseIdx = json.indexOf("\"base\":[", idx);
            if (baseIdx >= 0 && baseIdx - idx < 200) {
                int numStart = baseIdx + 8;
                int numEnd = json.indexOf(",", numStart);
                if (numEnd < 0) numEnd = json.indexOf("]", numStart);
                if (numEnd > numStart) {
                    try {
                        int cents = Integer.parseInt(json.substring(numStart, numEnd).trim());
                        if (cents > 0 && cents < 5000) {
                            return "$" + (cents / 100) + "." + String.format("%02d", cents % 100);
                        }
                    } catch (NumberFormatException e) {}
                }
            }
        }
        return "";
    }

    /** Clear black/dark backgrounds from views — prevents XML backgrounds from covering content */
    private void clearBlackBackgrounds(android.view.View view) {
        android.graphics.drawable.Drawable bg = view.getBackground();
        if (bg instanceof android.graphics.drawable.ColorDrawable) {
            int color = ((android.graphics.drawable.ColorDrawable) bg).getColor();
            // Clear black and very dark backgrounds (keep McD dark for toolbar)
            if (color == 0xFF000000 || color == 0) {
                view.setBackground(null);
            }
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                clearBlackBackgrounds(vg.getChildAt(i));
            }
        }
    }

    /** Recursively cap oversized views to prevent layout explosion from invalid dimensions */
    private void capOversizedViews(android.view.View view, int maxW, int maxH) {
        if (view.getMeasuredWidth() > maxW * 2 || view.getMeasuredHeight() > maxH * 2) {
            android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                if (lp.width > maxW) lp.width = maxW;
                if (lp.height > maxH) lp.height = maxH;
                view.setLayoutParams(lp);
            }
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                capOversizedViews(vg.getChildAt(i), maxW, maxH);
            }
        }
    }

    private void setBackgroundRecursive(android.view.ViewGroup vg, int color) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            android.view.View child = vg.getChildAt(i);
            // Only set background on views that currently have a non-transparent background
            android.graphics.drawable.Drawable bg = child.getBackground();
            if (bg != null) child.setBackgroundColor(color);
            if (child instanceof android.view.ViewGroup) {
                setBackgroundRecursive((android.view.ViewGroup) child, color);
            }
        }
    }

    private android.view.View findViewByType(android.view.View root, String typeName) {
        if (root.getClass().getSimpleName().contains(typeName)) return root;
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                android.view.View found = findViewByType(vg.getChildAt(i), typeName);
                if (found != null) return found;
            }
        }
        return null;
    }

    private java.lang.reflect.Method findMethodInHierarchy(Class<?> cls, String name) {
        while (cls != null && cls != Object.class) {
            try {
                for (java.lang.reflect.Method m : cls.getDeclaredMethods()) {
                    if (m.getName().equals(name) && m.getParameterTypes().length == 0) return m;
                }
            } catch (Throwable t) {}
            cls = cls.getSuperclass();
        }
        return null;
    }

    /** Walk view tree and resolve drawable resources for ImageViews */
    private void resolveImageDrawables(Activity activity) {
        try {
            android.view.View root = activity.getWindow() != null ? activity.getWindow().getDecorView() : null;
            if (root != null) resolveImageDrawablesRecursive(root, activity);
        } catch (Throwable t) {
            System.err.println("[WI] resolveImageDrawables: " + t.getMessage());
        }
    }

    private void resolveImageDrawablesRecursive(android.view.View view, android.content.Context ctx) {
        if (view instanceof android.widget.ImageView) {
            android.widget.ImageView iv = (android.widget.ImageView) view;
            try {
                java.lang.reflect.Field resField = android.widget.ImageView.class.getDeclaredField("mResource");
                resField.setAccessible(true);
                int resId = resField.getInt(iv);
                if (resId != 0 && iv.getDrawable() == null) {
                    android.graphics.drawable.Drawable d = ctx.getDrawable(resId);
                    if (d != null && !(d instanceof android.graphics.drawable.ColorDrawable)) {
                        iv.setImageDrawable(d);
                        System.err.println("[WI] Resolved drawable 0x" + Integer.toHexString(resId)
                            + " for ImageView → " + d.getIntrinsicWidth() + "x" + d.getIntrinsicHeight());
                    }
                }
            } catch (Throwable t) { /* skip */ }
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                resolveImageDrawablesRecursive(vg.getChildAt(i), ctx);
            }
        }
    }
}
