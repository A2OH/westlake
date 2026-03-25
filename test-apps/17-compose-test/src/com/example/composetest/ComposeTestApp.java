package com.example.composetest;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.Gravity;

/**
 * Tests Jetpack Compose loading on the Westlake engine.
 *
 * Phase 5 test: Can we load Compose classes, create a ComposeView,
 * and render setContent { Text("Hello Compose!") } ?
 */
public class ComposeTestApp {

    static Context ctx;
    static Activity hostActivity;

    public static void init(Context context) {
        ctx = context;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            hostActivity = (Activity) host.getField("instance").get(null);
        } catch (Exception e) {}
    }

    static int dp(int d) { return (int)(d * ctx.getResources().getDisplayMetrics().density); }

    static void show(final View view) {
        if (hostActivity == null) return;
        hostActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (view.getParent() != null)
                    ((ViewGroup) view.getParent()).removeView(view);
                hostActivity.setContentView(view);
            }
        });
    }

    /**
     * Test Compose class loading and basic setup.
     * Shows diagnostic output for each step.
     */
    public static void testCompose() {
        System.out.println("[ComposeTest] Starting Compose test...");

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFFFFFFF);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));

        // Back button
        android.widget.Button back = new android.widget.Button(ctx);
        back.setText("← Back");
        back.setTextSize(14);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try { com.example.apklauncher.ApkLauncher.showHome(); }
                catch (Exception e) {}
            }
        });
        root.addView(back);

        TextView title = new TextView(ctx);
        title.setText("Jetpack Compose Test");
        title.setTextSize(22);
        title.setTextColor(0xFF1565C0);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, dp(8), 0, dp(16));
        root.addView(title);

        ClassLoader cl = ComposeTestApp.class.getClassLoader();

        // Step 1: Test Compose runtime classes
        testClass(root, cl, "androidx.compose.runtime.Composer", "Compose Runtime");
        testClass(root, cl, "androidx.compose.runtime.Recomposer", "Recomposer");
        testClass(root, cl, "androidx.compose.runtime.Composition", "Composition");

        // Step 2: Test Compose UI classes
        testClass(root, cl, "androidx.compose.ui.platform.ComposeView", "ComposeView");
        testClass(root, cl, "androidx.compose.ui.platform.AbstractComposeView", "AbstractComposeView");
        testClass(root, cl, "androidx.compose.ui.platform.AndroidComposeView", "AndroidComposeView");

        // Step 3: Test Compose Foundation/Material
        testClass(root, cl, "androidx.compose.foundation.layout.ColumnKt", "Column");
        testClass(root, cl, "androidx.compose.foundation.layout.RowKt", "Row");
        testClass(root, cl, "androidx.compose.material3.TextKt", "Material3 Text");
        testClass(root, cl, "androidx.compose.material3.ButtonKt", "Material3 Button");

        // Step 4: Test Lifecycle integration
        testClass(root, cl, "androidx.lifecycle.LifecycleOwner", "LifecycleOwner");
        testClass(root, cl, "androidx.lifecycle.LifecycleRegistry", "LifecycleRegistry");
        testClass(root, cl, "androidx.lifecycle.ViewTreeLifecycleOwner", "ViewTreeLifecycleOwner");

        // Step 5: Test Activity integration
        testClass(root, cl, "androidx.activity.ComponentActivity", "ComponentActivity");
        testClass(root, cl, "androidx.activity.compose.ComponentActivityKt", "setContent extension");

        // Step 6: Test Kotlin runtime
        testClass(root, cl, "kotlin.Unit", "Kotlin Unit");
        testClass(root, cl, "kotlinx.coroutines.CoroutineScope", "Coroutines");

        // Step 7: Test our Activity implements LifecycleOwner
        addStatus(root, "---", "");
        addStatus(root, "⏳", "Testing Activity lifecycle support...");
        try {
            // Check our shim interface
            boolean isShimLO = false;
            try { isShimLO = cl.loadClass("androidx.lifecycle.LifecycleOwner").isInstance(ctx); }
            catch (Exception e) {}
            addStatus(root, isShimLO ? "✅" : "⚠️",
                "LifecycleOwner: " + isShimLO);

            // Check if phone's Activity has getLifecycle() (real AndroidX)
            try {
                java.lang.reflect.Method glm = hostActivity.getClass().getMethod("getLifecycle");
                Object lifecycle = glm.invoke(hostActivity);
                addStatus(root, "✅", "Phone Activity.getLifecycle(): " +
                    (lifecycle != null ? lifecycle.getClass().getSimpleName() : "null"));
                if (lifecycle != null) {
                    java.lang.reflect.Method gsm = lifecycle.getClass().getMethod("getCurrentState");
                    Object state = gsm.invoke(lifecycle);
                    addStatus(root, "✅", "Phone Lifecycle state: " + state);
                }
            } catch (NoSuchMethodException e) {
                addStatus(root, "❌", "Phone Activity has no getLifecycle()");
            }
        } catch (Exception e) {
            addStatus(root, "❌", "Lifecycle test failed: " + e.getMessage());
        }

        // Step 8: Try creating a ComposeView
        addStatus(root, "---", "");
        addStatus(root, "⏳", "Creating ComposeView + wiring lifecycle...");
        try {
            Class<?> composeViewClass = cl.loadClass("androidx.compose.ui.platform.ComposeView");
            final View composeView = (View) composeViewClass.getConstructor(Context.class).newInstance(hostActivity);
            addStatus(root, "✅", "ComposeView created");

            // Create LifecycleOwner via compose.dex classes (reflection)
            // Must use compose.dex's LifecycleOwner interface, not our shim
            Class<?> loInterface = cl.loadClass("androidx.lifecycle.LifecycleOwner");
            Class<?> lrClass = cl.loadClass("androidx.lifecycle.LifecycleRegistry");

            // Create a dynamic proxy implementing compose.dex's LifecycleOwner
            final Object[] lcRegHolder = new Object[1];
            Object lcOwner = java.lang.reflect.Proxy.newProxyInstance(cl,
                new Class[]{loInterface},
                new java.lang.reflect.InvocationHandler() {
                    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                        if ("getLifecycle".equals(method.getName())) return lcRegHolder[0];
                        return null;
                    }
                });

            // Create LifecycleRegistry from compose.dex
            Object lifecycle = lrClass.getConstructor(loInterface).newInstance(lcOwner);
            lcRegHolder[0] = lifecycle;

            View decorView = hostActivity.getWindow().getDecorView();

            // Move lifecycle to RESUMED via reflection
            Class<?> eventClass = cl.loadClass("androidx.lifecycle.Lifecycle$Event");
            java.lang.reflect.Method handleEvent = lrClass.getMethod("handleLifecycleEvent", eventClass);
            handleEvent.invoke(lifecycle, Enum.valueOf((Class<Enum>) eventClass, "ON_CREATE"));
            handleEvent.invoke(lifecycle, Enum.valueOf((Class<Enum>) eventClass, "ON_START"));
            handleEvent.invoke(lifecycle, Enum.valueOf((Class<Enum>) eventClass, "ON_RESUME"));
            addStatus(root, "✅", "Lifecycle → RESUMED (compose.dex)");

            // Use compose.dex's ViewTreeLifecycleOwner.set() — ensures correct R$id + interface
            Class<?> vtloClass = cl.loadClass("androidx.lifecycle.ViewTreeLifecycleOwner");
            java.lang.reflect.Method vtloSet = vtloClass.getMethod("set", View.class, loInterface);
            vtloSet.invoke(null, decorView, lcOwner);
            vtloSet.invoke(null, root, lcOwner);
            addStatus(root, "✅", "ViewTreeLifecycleOwner.set() via compose.dex ✓");

            // Verify with compose.dex's get()
            java.lang.reflect.Method vtloGet = vtloClass.getMethod("get", View.class);
            Object readBack = vtloGet.invoke(null, root);
            addStatus(root, readBack != null ? "✅" : "❌",
                "ViewTreeLifecycleOwner.get(root) = " + (readBack != null ? "OK" : "null"));

            // Set SavedState + ViewModel on DecorView too
            try {
                Class<?> ssrClass = cl.loadClass("androidx.savedstate.SavedStateRegistry");
                final Object ssr = ssrClass.newInstance();
                Class<?> ssroI = cl.loadClass("androidx.savedstate.SavedStateRegistryOwner");
                Object ssro = java.lang.reflect.Proxy.newProxyInstance(cl,
                    new Class[]{ssroI, loInterface},
                    new java.lang.reflect.InvocationHandler() {
                        public Object invoke(Object proxy, java.lang.reflect.Method m, Object[] a) {
                            if ("getLifecycle".equals(m.getName())) return lcRegHolder[0];
                            if ("getSavedStateRegistry".equals(m.getName())) return ssr;
                            return null;
                        }
                    });
                cl.loadClass("androidx.savedstate.ViewTreeSavedStateRegistryOwner")
                    .getMethod("set", View.class, ssroI).invoke(null, decorView, ssro);
                addStatus(root, "✅", "SavedStateRegistryOwner set");
            } catch (Exception se) { addStatus(root, "⚠️", "SavedState: " + se.getMessage()); }

            try {
                Class<?> vmsI = cl.loadClass("androidx.lifecycle.ViewModelStoreOwner");
                Class<?> vmsC = cl.loadClass("androidx.lifecycle.ViewModelStore");
                final Object vms = vmsC.newInstance();
                Object vmso = java.lang.reflect.Proxy.newProxyInstance(cl, new Class[]{vmsI},
                    new java.lang.reflect.InvocationHandler() {
                        public Object invoke(Object proxy, java.lang.reflect.Method m, Object[] a) {
                            if ("getViewModelStore".equals(m.getName())) return vms;
                            return null;
                        }
                    });
                cl.loadClass("androidx.lifecycle.ViewTreeViewModelStoreOwner")
                    .getMethod("set", View.class, vmsI).invoke(null, decorView, vmso);
                addStatus(root, "✅", "ViewModelStoreOwner set");
            } catch (Exception ve) { addStatus(root, "⚠️", "ViewModel: " + ve.getMessage()); }

            // Add ComposeView to layout
            addStatus(root, "⏳", "Adding ComposeView...");
            try {
                root.addView(composeView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(300)));
                addStatus(root, "✅", "ComposeView added!");
            } catch (Exception addEx) {
                addStatus(root, "❌", "ComposeView add crashed: " + addEx.getMessage());
            }

        } catch (Exception e) {
            addStatus(root, "❌", "Failed: " + e.getClass().getSimpleName());
            addStatus(root, "❌", "" + e.getMessage());
            Throwable cause = e.getCause();
            while (cause != null) {
                addStatus(root, "❌", "→ " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
                cause = cause.getCause();
            }
        }

        show(root);
    }

    static void testClass(LinearLayout root, ClassLoader cl, String className, String label) {
        try {
            Class<?> c = cl.loadClass(className);
            addStatus(root, "✅", label + " (" + className.substring(className.lastIndexOf('.') + 1) + ")");
        } catch (ClassNotFoundException e) {
            addStatus(root, "❌", label + " — NOT FOUND");
        } catch (Exception e) {
            addStatus(root, "⚠️", label + " — " + e.getClass().getSimpleName());
        }
    }

    static void addStatus(LinearLayout parent, String icon, String text) {
        System.out.println("[ComposeTest] " + icon + " " + text);
        LinearLayout row = new LinearLayout(parent.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(3), 0, dp(3));

        TextView iconTv = new TextView(parent.getContext());
        iconTv.setText(icon + " ");
        iconTv.setTextSize(13);
        row.addView(iconTv);

        TextView textTv = new TextView(parent.getContext());
        textTv.setText(text);
        textTv.setTextSize(12);
        textTv.setTextColor(0xFF424242);
        row.addView(textTv);

        parent.addView(row);
    }
}
