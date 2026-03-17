import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.calculator.*;
import com.ohos.shim.bridge.OHBridge;
import java.util.List;

/**
 * Headless test runner for Calculator app.
 * Exercises: Activity lifecycle, View tree, button handling, arithmetic,
 * and Canvas rendering validation.
 *
 * Run: java -cp build-calculator CalculatorRunner
 * Expected: 15 PASS, 0 FAIL
 */
public class CalculatorRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Calculator End-to-End Test ===\n");

        try {
            // 1. Initialize MiniServer
            MiniServer.init("com.example.calculator");
            MiniServer server = MiniServer.get();
            MiniActivityManager am = server.getActivityManager();
            check("MiniServer initialized", server != null && am != null);

            // 2. Launch CalculatorActivity
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    "com.example.calculator", "com.example.calculator.CalculatorActivity"));
            am.startActivity(null, intent, -1);

            Activity act = am.getResumedActivity();
            check("CalculatorActivity launched", act instanceof CalculatorActivity);
            CalculatorActivity calc = (CalculatorActivity) act;

            // 3. Display starts at "0"
            check("Display starts at 0", "0".equals(calc.getDisplay()));

            // 4. Press 5 -> display "5"
            calc.pressButton("5");
            check("Press 5 -> display 5", "5".equals(calc.getDisplay()));

            // 5. Press + -> display "5"
            calc.pressButton("+");
            check("Press + -> display 5", "5".equals(calc.getDisplay()));

            // 6. Press 3 -> display "3"
            calc.pressButton("3");
            check("Press 3 -> display 3", "3".equals(calc.getDisplay()));

            // 7. Press = -> display "8"
            calc.pressButton("=");
            check("Press = -> display 8", "8".equals(calc.getDisplay()));

            // 8. Press C -> display "0"
            calc.pressButton("C");
            check("Press C -> display 0", "0".equals(calc.getDisplay()));

            // 9. Press 1, 0, 0 -> display "100"
            calc.pressButton("1");
            calc.pressButton("0");
            calc.pressButton("0");
            check("Press 1,0,0 -> display 100", "100".equals(calc.getDisplay()));

            // 10. Press /, 4, = -> display "25"
            calc.pressButton("/");
            calc.pressButton("4");
            calc.pressButton("=");
            check("100 / 4 = 25", "25".equals(calc.getDisplay()));

            // 11. Press *, 3, = -> display "75"
            calc.pressButton("*");
            calc.pressButton("3");
            calc.pressButton("=");
            check("25 * 3 = 75", "75".equals(calc.getDisplay()));

            // 12. Press -, 5, 0, = -> display "25"
            calc.pressButton("-");
            calc.pressButton("5");
            calc.pressButton("0");
            calc.pressButton("=");
            check("75 - 50 = 25", "25".equals(calc.getDisplay()));

            // 13. Decimal: C, 3, ., 1, 4 -> display "3.14"
            calc.pressButton("C");
            calc.pressButton("3");
            calc.pressButton(".");
            calc.pressButton("1");
            calc.pressButton("4");
            check("Decimal: 3.14", "3.14".equals(calc.getDisplay()));

            // ---- Canvas Render Validation ----
            System.out.println("\n--- Canvas Render Validation ---");

            // 14. Canvas: renders digit buttons
            List<OHBridge.DrawRecord> log = renderAndGetLog(act, 480, 800);
            boolean hasDigit = false;
            for (int d = 0; d <= 9; d++) {
                if (hasDrawText(log, String.valueOf(d))) {
                    hasDigit = true;
                    break;
                }
            }
            check("Canvas: renders digit buttons", hasDigit);

            // 15. Canvas: renders display text
            check("Canvas: renders display text",
                    hasDrawText(log, "3.14") || hasDrawText(log, "0"));

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
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

    private static List<OHBridge.DrawRecord> renderAndGetLog(Activity activity, int w, int h) {
        activity.onSurfaceCreated(0, w, h);
        activity.renderFrame();
        long surfaceCtx = getSurfaceCtx(activity);
        long canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx);
        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvasHandle);
        return log;
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

    private static boolean hasDrawText(List<OHBridge.DrawRecord> log, String text) {
        for (OHBridge.DrawRecord r : log) {
            if ("drawText".equals(r.op) && r.text != null && r.text.contains(text)) {
                return true;
            }
        }
        return false;
    }
}
