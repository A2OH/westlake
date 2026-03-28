package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * MiniActivityManager — manages the Activity back stack for a single app.
 *
 * Replaces Android's ActivityManagerService + ActivityTaskManagerService.
 * Handles:
 * - Activity instantiation via reflection
 * - Lifecycle dispatch (onCreate → onStart → onResume → onPause → onStop → onDestroy)
 * - Back stack navigation (push on startActivity, pop on finish)
 * - startActivityForResult / onActivityResult round-trip
 * - Only one Activity is "resumed" at a time
 */
public class MiniActivityManager {
    private static final String TAG = "MiniActivityManager";

    private final MiniServer mServer;
    private final ArrayList<ActivityRecord> mStack = new ArrayList<>();
    private final Map<String, Class<?>> mRegisteredClasses = new HashMap<>();

    /** Register an Activity class loaded from an external DEX/APK */
    public void registerActivityClass(String className, Class<?> cls) {
        mRegisteredClasses.put(className, cls);
    }
    private ActivityRecord mResumed;

    MiniActivityManager(MiniServer server) {
        mServer = server;
    }

    /**
     * Start an Activity.
     * @param caller The calling Activity (null for initial launch)
     * @param intent The intent describing the Activity to start
     * @param requestCode -1 for startActivity, >= 0 for startActivityForResult
     */
    public void startActivity(Activity caller, Intent intent, int requestCode) {
        if (intent == null) {
            Log.w(TAG, "startActivity: null intent");
            return;
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            // Implicit intent resolution via MiniPackageManager
            android.content.pm.MiniPackageManager pm = mServer.getPackageManager();
            if (pm != null) {
                android.content.pm.ResolveInfo ri = pm.resolveActivity(intent);
                if (ri != null && ri.resolvedComponentName != null) {
                    component = ri.resolvedComponentName;
                    intent.setComponent(component);
                }
            }
            if (component == null) {
                Log.w(TAG, "startActivity: cannot resolve intent, action=" + intent.getAction());
                return;
            }
        }

        String className = component.getClassName();
        Log.d(TAG, "startActivity: " + className);

        // Instantiate the Activity class
        Activity activity;
        try {
            Class<?> cls = mRegisteredClasses.get(className);
            if (cls == null) {
                // Use context classloader (set by ART to app's PathClassLoader)
                // NOT boot classloader (MiniActivityManager is on boot classpath)
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) cl = ClassLoader.getSystemClassLoader();
                cls = cl.loadClass(className);
            }
            activity = (Activity) cls.newInstance();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Activity class not found: " + className);
            return;
        } catch (Exception e) {
            Log.e(TAG, "Failed to instantiate " + className + ": " + e.getMessage());
            return;
        }

        // Initialize framework state (via reflection — on real Android, Activity fields differ)
        ShimCompat.setActivityField(activity, "mIntent", intent);
        ShimCompat.setActivityField(activity, "mComponent", component);
        ShimCompat.setActivityField(activity, "mApplication", mServer.getApplication());
        ShimCompat.setActivityField(activity, "mFinished", Boolean.FALSE);
        ShimCompat.setActivityField(activity, "mDestroyed", Boolean.FALSE);

        // Create the ActivityRecord
        ActivityRecord record = new ActivityRecord();
        record.activity = activity;
        record.intent = intent;
        record.component = component;
        if (caller != null && requestCode >= 0) {
            record.caller = findRecord(caller);
            record.requestCode = requestCode;
        }

        // Pause the current top activity
        if (mResumed != null) {
            ActivityRecord prev = mResumed;
            performPause(prev);
            performStop(prev);
        }

        // Push and start the new activity
        mStack.add(record);
        performCreate(record, null);
        performStart(record);
        performResume(record);
    }

    /**
     * Finish an Activity. Pops it from the stack and resumes the previous one.
     */
    public void finishActivity(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "finishActivity: null activity");
            return;
        }
        ActivityRecord record = findRecord(activity);
        if (record == null) {
            // Already finished or never in stack — idempotent
            return;
        }

        Log.d(TAG, "finishActivity: " + record.component.getClassName());

        // If this is the resumed activity, pause it first
        if (record == mResumed) {
            performPause(record);
        }
        performStop(record);
        performDestroy(record);

        // Deliver result to caller if startActivityForResult was used
        if (record.caller != null && record.requestCode >= 0) {
            Activity callerActivity = record.caller.activity;
            boolean callerDestroyed = ShimCompat.getActivityBooleanField(callerActivity, "mDestroyed", false);
            if (callerActivity != null && !callerDestroyed) {
                int resultCode = ShimCompat.getActivityIntField(activity, "mResultCode", 0);
                android.content.Intent resultData = ShimCompat.getActivityField(activity, "mResultData", (android.content.Intent) null);
                callerActivity.onActivityResult(
                    record.requestCode,
                    resultCode,
                    resultData
                );
            }
        }

        // Remove from stack
        mStack.remove(record);

        // Resume the new top activity
        if (!mStack.isEmpty()) {
            ActivityRecord top = mStack.get(mStack.size() - 1);
            performRestart(top);
            performStart(top);
            performResume(top);
        }
    }

    /**
     * Handle back button press. Finishes the top activity.
     */
    public void onBackPressed() {
        if (mResumed != null && mResumed.activity != null) {
            Activity top = mResumed.activity;
            top.onBackPressed();
            // finish() is now idempotent and calls finishActivity internally
        }
    }

    /**
     * Finish all activities (shutdown).
     */
    public void finishAll() {
        // Destroy from top to bottom
        while (!mStack.isEmpty()) {
            ActivityRecord top = mStack.get(mStack.size() - 1);
            if (top == mResumed) {
                performPause(top);
            }
            performStop(top);
            performDestroy(top);
            mStack.remove(mStack.size() - 1);
        }
        mResumed = null;
    }

    /** Get the currently resumed Activity, or null. */
    public Activity getResumedActivity() {
        return mResumed != null ? mResumed.activity : null;
    }

    /** Get the Activity stack size. */
    public int getStackSize() {
        return mStack.size();
    }

    /** Get an Activity by index (0 = bottom). */
    public Activity getActivity(int index) {
        if (index < 0 || index >= mStack.size()) return null;
        return mStack.get(index).activity;
    }

    // ── Lifecycle dispatch ──────────────────────────────────────────────────

    private void performCreate(ActivityRecord r, Bundle savedInstanceState) {
        Log.d(TAG, "  performCreate: " + r.component.getClassName());
        // Dispatch lifecycle: restore saved state + ON_CREATE
        dispatchLifecycleEvent(r.activity, "performRestore", savedInstanceState);
        boolean createNPE = false;
        try {
            r.activity.onCreate(savedInstanceState);
        } catch (NullPointerException e) {
            // Non-fatal: some apps crash on null ActionBar etc. but still create valid Views
            Log.w(TAG, "performCreate NPE (non-fatal): " + e.getMessage());
            createNPE = true;
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onCreate", Bundle.class);
                m.setAccessible(true);
                m.invoke(r.activity, savedInstanceState);
            } catch (Exception ex) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                Log.e(TAG, "performCreate failed: " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
                cause.printStackTrace();
            }
        }
        // If onCreate NPE'd (e.g., ActionBar null), try to discover and add missing fragments
        if (createNPE) {
            tryRecoverFragments(r.activity);
        }
        dispatchLifecycleEvent(r.activity, "ON_CREATE");
    }

    /**
     * After an NPE in onCreate (often from getSupportActionBar()), the fragment setup
     * code was skipped. Try to discover Fragment classes and add them to empty containers.
     */
    private void tryRecoverFragments(Activity activity) {
        try {
            // Initialize any null SharedPreferences fields on the activity
            // (these would have been set in onCreate code that was skipped due to NPE)
            initNullSharedPrefsFields(activity);

            // Check if content view exists but has empty fragment containers
            android.view.Window w = activity.getWindow();
            if (w == null) return;
            android.view.View decor = w.getDecorView();
            if (decor == null) return;

            // Look for empty FrameLayouts that should contain fragments
            // Common pattern: Activity has a FrameLayout(id=R.id.main_content) for the main fragment
            String pkg = activity.getPackageName();
            if (pkg == null) pkg = activity.getClass().getPackage().getName();

            // Try to find Fragment classes in the app's package using common naming patterns
            String actPkg = activity.getClass().getPackage().getName();
            String[] fragmentCandidates = {
                actPkg + ".CountersListFragment",     // Counter app specific
                actPkg + ".MainFragment",
                actPkg + ".HomeFragment",
                pkg + ".ui.CountersListFragment",
                pkg + ".ui.MainFragment",
                pkg + ".ui.HomeFragment",
                pkg + ".fragment.MainFragment",
            };

            // Find FragmentManager via reflection (works for both support lib and framework)
            Object fragmentManager = null;
            try {
                java.lang.reflect.Method gsfm = activity.getClass().getMethod("getSupportFragmentManager");
                fragmentManager = gsfm.invoke(activity);
            } catch (Exception e) {
                // try framework FragmentManager
                try {
                    fragmentManager = activity.getFragmentManager();
                } catch (Exception e2) { /* ignore */ }
            }

            if (fragmentManager == null) {
                Log.d(TAG, "  tryRecoverFragments: no FragmentManager available");
                return;
            }

            for (String className : fragmentCandidates) {
                try {
                    Class<?> fragClass = ClassLoader.getSystemClassLoader().loadClass(className);
                    Object fragment = fragClass.newInstance();

                    // Try to add via support FragmentManager
                    java.lang.reflect.Method beginTx = fragmentManager.getClass().getMethod("beginTransaction");
                    Object tx = beginTx.invoke(fragmentManager);

                    // Find the first empty FrameLayout with an ID
                    int containerId = findEmptyFrameLayoutId(decor);
                    if (containerId == 0) containerId = 0x7f0a004a; // fallback to common ID

                    // Try to find the replace(int, Fragment) method — walk up the hierarchy
                    // Also try all declared methods on the transaction in case of name matching
                    boolean added = false;
                    for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
                        try {
                            java.lang.reflect.Method replace = tx.getClass().getMethod("replace", int.class, c);
                            replace.invoke(tx, containerId, fragment);
                            added = true;
                            break;
                        } catch (NoSuchMethodException nsme) { /* try parent */ }
                    }
                    // Fallback: find any 'replace' method with matching arity and try it
                    if (!added) {
                        for (java.lang.reflect.Method m : tx.getClass().getMethods()) {
                            if (m.getName().equals("replace") && m.getParameterTypes().length == 2
                                    && m.getParameterTypes()[0] == int.class
                                    && m.getParameterTypes()[1].isAssignableFrom(fragClass)) {
                                m.invoke(tx, containerId, fragment);
                                added = true;
                                Log.d(TAG, "  tryRecoverFragments: used fallback replace(" + m.getParameterTypes()[1].getName() + ")");
                                break;
                            }
                        }
                    }

                    if (added) {
                        // Commit the transaction
                        java.lang.reflect.Method commit = tx.getClass().getMethod("commitAllowingStateLoss");
                        commit.invoke(tx);
                        // Execute pending transactions immediately
                        try {
                            java.lang.reflect.Method exec = fragmentManager.getClass().getMethod("executePendingTransactions");
                            exec.invoke(fragmentManager);
                        } catch (Exception e) { /* may fail, that's ok */ }
                        Log.i(TAG, "  tryRecoverFragments: added " + fragClass.getSimpleName() + " to container 0x" + Integer.toHexString(containerId));
                        return;
                    }
                } catch (ClassNotFoundException e) {
                    // Try next candidate
                } catch (Exception e) {
                    Log.d(TAG, "  tryRecoverFragments: " + className + " failed: " + e);
                }
            }
            Log.d(TAG, "  tryRecoverFragments: no suitable Fragment class found");
        } catch (Exception e) {
            Log.d(TAG, "  tryRecoverFragments error: " + e);
        }
    }

    /** Find the first FrameLayout child with an ID that has no children */
    private int findEmptyFrameLayoutId(android.view.View v) {
        if (v instanceof android.widget.FrameLayout) {
            android.widget.FrameLayout fl = (android.widget.FrameLayout) v;
            if (fl.getId() != android.view.View.NO_ID && fl.getChildCount() == 0) {
                return fl.getId();
            }
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                int id = findEmptyFrameLayoutId(vg.getChildAt(i));
                if (id != 0) return id;
            }
        }
        return 0;
    }

    /**
     * Find and initialize any null SharedPreferences fields on the activity.
     * When onCreate NPEs before SP initialization, these fields stay null.
     */
    private void initNullSharedPrefsFields(Activity activity) {
        try {
            for (java.lang.reflect.Field f : activity.getClass().getDeclaredFields()) {
                if (f.getType().getName().equals("android.content.SharedPreferences")) {
                    f.setAccessible(true);
                    Object val = f.get(activity);
                    if (val == null) {
                        // Initialize with default SharedPreferences
                        android.content.SharedPreferences sp =
                            android.preference.PreferenceManager.getDefaultSharedPreferences(activity);
                        f.set(activity, sp);
                        Log.i(TAG, "  initNullSharedPrefsFields: initialized " + f.getName());
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "  initNullSharedPrefsFields error: " + e);
        }
    }

    private void performStart(ActivityRecord r) {
        Log.d(TAG, "  performStart: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.TRUE);
        try {
            r.activity.onStart();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onStart");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonStart reflection failed: " + ex); }
        }
        dispatchLifecycleEvent(r.activity, "ON_START");
    }

    private void performResume(ActivityRecord r) {
        Log.d(TAG, "  performResume: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.TRUE);
        mResumed = r;
        try {
            r.activity.onResume();
        } catch (NullPointerException e) {
            Log.w(TAG, "performResume NPE (non-fatal): " + e.getMessage());
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onResume");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonResume reflection failed: " + ex); }
        }
        try {
            r.activity.onPostResume();
        } catch (NullPointerException e) {
            Log.w(TAG, "onPostResume NPE (non-fatal): " + e.getMessage());
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onPostResume");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { /* onPostResume is optional */ }
        }
        dispatchLifecycleEvent(r.activity, "ON_RESUME");
    }

    private void performPause(ActivityRecord r) {
        Log.d(TAG, "  performPause: " + r.component.getClassName());
        dispatchLifecycleEvent(r.activity, "ON_PAUSE");
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.FALSE);
        try {
            r.activity.onPause();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onPause");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonPause reflection failed: " + ex); }
        }
        if (r == mResumed) {
            mResumed = null;
        }
    }

    private void performStop(ActivityRecord r) {
        if (ShimCompat.getActivityBooleanField(r.activity, "mStarted", false) == false) return;
        Log.d(TAG, "  performStop: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.FALSE);
        try {
            r.activity.onStop();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onStop");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonStop reflection failed: " + ex); }
        }
        dispatchLifecycleEvent(r.activity, "ON_STOP");
    }

    private void performDestroy(ActivityRecord r) {
        Log.d(TAG, "  performDestroy: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mDestroyed", Boolean.TRUE);
        try {
            r.activity.onDestroy();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onDestroy");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonDestroy reflection failed: " + ex); }
        }
    }

    private void performRestart(ActivityRecord r) {
        Log.d(TAG, "  performRestart: " + r.component.getClassName());
        try {
            r.activity.onRestart();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onRestart");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { /* optional */ }
        }
    }

    // ── Internal ────────────────────────────────────────────────────────────

    private ActivityRecord findRecord(Activity activity) {
        for (int i = mStack.size() - 1; i >= 0; i--) {
            if (mStack.get(i).activity == activity) {
                return mStack.get(i);
            }
        }
        return null;
    }

    /** Internal record tracking an Activity's state. */
    /** Dispatch lifecycle events — no-op for now, will be wired via Compose's lifecycle */
    private void dispatchLifecycleEvent(Activity activity, String eventName) {}
    private void dispatchLifecycleEvent(Activity activity, String action, Bundle state) {}

    static class ActivityRecord {
        Activity activity;
        Intent intent;
        ComponentName component;
        ActivityRecord caller;
        int requestCode = -1;
    }
}
