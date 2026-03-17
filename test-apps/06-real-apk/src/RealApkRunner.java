import android.app.Activity;
import android.app.ActivityThread;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import com.ohos.shim.bridge.OHBridge;
import java.util.List;

/**
 * Tests the real APK loading pipeline:
 *   MiniServer.init() -> startActivity -> Activity lifecycle
 *   -> View tree -> Canvas rendering -> draw log verification
 *
 * Run: java -cp build-real-apk RealApkRunner
 */
public class RealApkRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Real APK Pipeline Test ===\n");

        try {
            // Initialize MiniServer with the hello package
            MiniServer.init("com.example.hello");
            MiniServer server = MiniServer.get();
            MiniActivityManager am = server.getActivityManager();

            // Register a string resource so HelloActivity can look it up
            server.getApplication().getResources()
                    .registerStringResource(0x7f0a0001, "Hello World APK");

            check("MiniServer initialized", server != null);
            check("ActivityManager ready", am != null);

            // Launch HelloActivity via explicit intent
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    "com.example.hello", "com.example.hello.HelloActivity"));
            am.startActivity(null, intent, -1);

            Activity act = am.getResumedActivity();
            check("HelloActivity launched",
                    act != null && act.getClass().getName().equals("com.example.hello.HelloActivity"));

            // Check lifecycle state
            check("Activity is resumed", act != null && !act.isDestroyed());

            // Check View tree via Window
            if (act != null) {
                android.view.Window win = act.getWindow();
                check("Window exists", win != null);

                View decor = win.getDecorView();
                check("DecorView exists", decor != null);
                check("DecorView is ViewGroup",
                        decor instanceof ViewGroup);

                if (decor instanceof ViewGroup) {
                    ViewGroup decorGroup = (ViewGroup) decor;
                    check("DecorView has children", decorGroup.getChildCount() > 0);

                    // The content view should be the LinearLayout (first child of decor)
                    View content = decorGroup.getChildAt(0);
                    check("Content is LinearLayout", content instanceof LinearLayout);

                    if (content instanceof LinearLayout) {
                        LinearLayout ll = (LinearLayout) content;
                        check("LinearLayout has 4 children", ll.getChildCount() == 4);

                        // Child 0: title TextView
                        if (ll.getChildCount() > 0) {
                            View child0 = ll.getChildAt(0);
                            check("Child 0 is TextView", child0 instanceof TextView);
                            if (child0 instanceof TextView) {
                                String text = ((TextView) child0).getText().toString();
                                check("Title says 'Hello from Real APK!'",
                                        "Hello from Real APK!".equals(text));
                            }
                        }

                        // Child 1: info TextView with package name
                        if (ll.getChildCount() > 1) {
                            View child1 = ll.getChildAt(1);
                            check("Child 1 is TextView", child1 instanceof TextView);
                            if (child1 instanceof TextView) {
                                String text = ((TextView) child1).getText().toString();
                                check("Info contains package name",
                                        text.indexOf("com.example.hello") >= 0);
                            }
                        }

                        // Child 2: resource string TextView
                        if (ll.getChildCount() > 2) {
                            View child2 = ll.getChildAt(2);
                            check("Child 2 is TextView", child2 instanceof TextView);
                            if (child2 instanceof TextView) {
                                String text = ((TextView) child2).getText().toString();
                                check("Resource string found",
                                        text.indexOf("Hello World APK") >= 0);
                            }
                        }

                        // Child 3: Button
                        if (ll.getChildCount() > 3) {
                            View child3 = ll.getChildAt(3);
                            check("Child 3 is Button", child3 instanceof Button);
                            if (child3 instanceof Button) {
                                String text = ((Button) child3).getText().toString();
                                check("Button says 'Click Me'",
                                        "Click Me".equals(text));
                            }
                        }
                    }
                }

                // Canvas render validation
                act.onSurfaceCreated(0, 480, 800);
                act.renderFrame();
                long surfaceCtx = getSurfaceCtx(act);
                check("Surface context created", surfaceCtx != 0);

                long canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx);
                check("Canvas handle valid", canvasHandle != 0);

                List drawLog = OHBridge.getDrawLog(canvasHandle);
                check("Draw log not empty", drawLog != null && drawLog.size() > 0);

                if (drawLog != null) {
                    check("Canvas renders title text",
                            hasDrawText(drawLog, "Hello from Real APK!"));
                    check("Canvas renders button text",
                            hasDrawText(drawLog, "Click Me"));
                    check("Canvas has drawRoundRect (button bg)",
                            hasDrawOp(drawLog, "drawRoundRect"));
                    check("Canvas has drawColor (background clear)",
                            hasDrawOp(drawLog, "drawColor"));
                }
            }

            // Test ActivityThread singleton
            ActivityThread at = ActivityThread.currentActivityThread();
            check("ActivityThread singleton exists", at != null);

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        if (failed == 0) {
            System.out.println("ALL TESTS PASSED");
        } else {
            System.out.println("SOME TESTS FAILED");
        }
        System.exit(failed);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + name);
            passed++;
        } else {
            System.out.println("  [FAIL] " + name);
            failed++;
        }
    }

    private static long getSurfaceCtx(Activity activity) {
        try {
            java.lang.reflect.Field f = Activity.class.getDeclaredField("mSurfaceCtx");
            f.setAccessible(true);
            return f.getLong(activity);
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean hasDrawText(List log, String text) {
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = (OHBridge.DrawRecord) log.get(i);
            if ("drawText".equals(r.op) && r.text != null && r.text.indexOf(text) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasDrawOp(List log, String op) {
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = (OHBridge.DrawRecord) log.get(i);
            if (op.equals(r.op)) {
                return true;
            }
        }
        return false;
    }
}
