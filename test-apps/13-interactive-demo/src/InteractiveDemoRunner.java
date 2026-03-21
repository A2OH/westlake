import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.demo.*;
import com.ohos.shim.bridge.OHBridge;
import java.util.List;

/**
 * Headless test runner for the A2OH Interactive Demo app.
 * Exercises: Activity navigation, click handlers, widget state changes,
 * back stack, and canvas rendering.
 *
 * Run: java -cp build InteractiveDemoRunner
 * Expected: ~20 PASS, 0 FAIL
 */
public class InteractiveDemoRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== A2OH Interactive Demo Test ===\n");

        try {
            // Initialize MiniServer
            MiniServer.init("com.example.demo");
            MiniServer server = MiniServer.get();
            MiniActivityManager am = server.getActivityManager();
            check("MiniServer initialized", server != null && am != null);

            // ── Screen 1: HomeActivity ──────────────────────────────
            System.out.println("\n--- HomeActivity ---");
            Intent homeIntent = new Intent();
            homeIntent.setComponent(new ComponentName(
                    "com.example.demo", "com.example.demo.HomeActivity"));
            am.startActivity(null, homeIntent, -1);

            Activity homeAct = am.getResumedActivity();
            check("HomeActivity launched", homeAct instanceof HomeActivity);

            // Verify title text exists in view tree
            View homeDecor = homeAct.getWindow().getDecorView();
            check("HomeActivity has title 'A2OH Interactive Demo'",
                    findTextView(homeDecor, "A2OH Interactive Demo") != null);

            // Verify 4 navigation buttons exist
            int buttonCount = countButtons(homeDecor);
            check("HomeActivity has 4 nav buttons", buttonCount == 4);

            // Verify footer text
            check("HomeActivity has footer text",
                    findTextView(homeDecor, "Tap any button to navigate") != null);

            // Canvas render validation for HomeActivity
            List<OHBridge.DrawRecord> homeLog = renderAndGetLog(homeAct, 480, 800);
            check("HomeActivity renders title text",
                    hasDrawText(homeLog, "A2OH"));
            check("HomeActivity renders 'Counter Demo' button",
                    hasDrawText(homeLog, "Counter Demo"));

            // ── Screen 2: CounterActivity ───────────────────────────
            System.out.println("\n--- CounterActivity ---");
            Intent counterIntent = new Intent();
            counterIntent.setComponent(new ComponentName(
                    "com.example.demo", "com.example.demo.CounterActivity"));
            am.startActivity(homeAct, counterIntent, -1);

            Activity counterAct = am.getResumedActivity();
            check("CounterActivity launched", counterAct instanceof CounterActivity);

            CounterActivity counter = (CounterActivity) counterAct;
            check("Counter starts at 0", counter.getCounter() == 0);
            check("Counter display shows '0'", "0".equals(counter.getCounterText()));

            // Simulate + click
            View counterDecor = counterAct.getWindow().getDecorView();
            Button plusBtn = findButton(counterDecor, "+");
            if (plusBtn != null) plusBtn.performClick();
            check("After +: counter = 1", counter.getCounter() == 1);

            // Simulate + again
            if (plusBtn != null) plusBtn.performClick();
            check("After + again: counter = 2", counter.getCounter() == 2);

            // Simulate - click
            Button minusBtn = findButton(counterDecor, "\u2212");
            if (minusBtn != null) minusBtn.performClick();
            check("After -: counter = 1", counter.getCounter() == 1);

            // Simulate Reset click
            Button resetBtn = findButton(counterDecor, "Reset");
            if (resetBtn != null) resetBtn.performClick();
            check("After Reset: counter = 0", counter.getCounter() == 0);

            // Canvas render
            List<OHBridge.DrawRecord> counterLog = renderAndGetLog(counterAct, 480, 800);
            check("CounterActivity renders counter '0'",
                    hasDrawText(counterLog, "0"));

            // Back to Home
            counter.finish();
            Activity afterCounterBack = am.getResumedActivity();
            check("Back from Counter returns to Home",
                    afterCounterBack instanceof HomeActivity);

            // ── Screen 3: FormActivity ──────────────────────────────
            System.out.println("\n--- FormActivity ---");
            Intent formIntent = new Intent();
            formIntent.setComponent(new ComponentName(
                    "com.example.demo", "com.example.demo.FormActivity"));
            am.startActivity(afterCounterBack, formIntent, -1);

            Activity formAct = am.getResumedActivity();
            check("FormActivity launched", formAct instanceof FormActivity);

            FormActivity form = (FormActivity) formAct;

            // Set name
            form.getNameField().setText("Alice");
            // Check subscribe
            form.getSubscribeCheck().setChecked(true);
            // Select Light theme
            form.getThemeGroup().check(101);
            // Submit
            form.clickSubmit();

            String result = form.getResultText().getText().toString();
            check("Form submit shows name 'Alice'", result.contains("Alice"));
            check("Form submit shows Subscribe: Yes", result.contains("Subscribe: Yes"));
            check("Form submit shows Theme: Light", result.contains("Theme: Light"));

            // Back to Home
            form.finish();
            Activity afterFormBack = am.getResumedActivity();
            check("Back from Form returns to Home",
                    afterFormBack instanceof HomeActivity);

            // ── Screen 4: ListActivity ──────────────────────────────
            System.out.println("\n--- ListActivity ---");
            Intent listIntent = new Intent();
            listIntent.setComponent(new ComponentName(
                    "com.example.demo", "com.example.demo.ListActivity"));
            am.startActivity(afterFormBack, listIntent, -1);

            Activity listAct = am.getResumedActivity();
            check("ListActivity launched", listAct instanceof ListActivity);

            com.example.demo.ListActivity listDemo = (com.example.demo.ListActivity) listAct;

            // Add items
            listDemo.addItemByText("Milk");
            listDemo.addItemByText("Bread");
            listDemo.addItemByText("Eggs");
            check("List has 3 items after adding", listDemo.getItemCount() == 3);
            check("Count text shows '3 items'",
                    "3 items".equals(listDemo.getCountText().getText().toString()));

            // Remove first item
            listDemo.removeItemAt(0);
            check("List has 2 items after remove", listDemo.getItemCount() == 2);
            check("First item is now 'Bread'", "Bread".equals(listDemo.getItems().get(0)));

            // Back to Home
            listDemo.finish();
            Activity afterListBack = am.getResumedActivity();
            check("Back from List returns to Home",
                    afterListBack instanceof HomeActivity);

            // ── Screen 5: AboutActivity ─────────────────────────────
            System.out.println("\n--- AboutActivity ---");
            Intent aboutIntent = new Intent();
            aboutIntent.setComponent(new ComponentName(
                    "com.example.demo", "com.example.demo.AboutActivity"));
            am.startActivity(afterListBack, aboutIntent, -1);

            Activity aboutAct = am.getResumedActivity();
            check("AboutActivity launched", aboutAct instanceof AboutActivity);

            View aboutDecor = aboutAct.getWindow().getDecorView();
            check("About shows 'Version: 1.0'",
                    findTextView(aboutDecor, "Version: 1.0") != null);
            check("About shows 'Engine: Westlake'",
                    findTextView(aboutDecor, "Engine: Westlake") != null);

            // Canvas render
            List<OHBridge.DrawRecord> aboutLog = renderAndGetLog(aboutAct, 480, 800);
            check("AboutActivity renders 'About A2OH'",
                    hasDrawText(aboutLog, "About A2OH"));
            check("AboutActivity renders 'Westlake'",
                    hasDrawText(aboutLog, "Westlake"));

            // Back to Home
            aboutAct.finish();
            Activity finalHome = am.getResumedActivity();
            check("Back from About returns to Home",
                    finalHome instanceof HomeActivity);

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

    // ── Helpers ─────────────────────────────────────────────────────

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + name);
            passed++;
        } else {
            System.out.println("  [FAIL] " + name);
            failed++;
        }
    }

    /** Find a TextView containing the given text in a view tree. */
    private static TextView findTextView(View root, String text) {
        if (root instanceof TextView) {
            CharSequence cs = ((TextView) root).getText();
            if (cs != null && cs.toString().contains(text)) {
                return (TextView) root;
            }
        }
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                TextView found = findTextView(vg.getChildAt(i), text);
                if (found != null) return found;
            }
        }
        return null;
    }

    /** Find a Button with the given text. */
    private static Button findButton(View root, String text) {
        if (root instanceof Button && !(root instanceof CheckBox)
                && !(root instanceof RadioButton)) {
            CharSequence cs = ((Button) root).getText();
            if (cs != null && cs.toString().contains(text)) {
                return (Button) root;
            }
        }
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                Button found = findButton(vg.getChildAt(i), text);
                if (found != null) return found;
            }
        }
        return null;
    }

    /** Count Button instances (excluding CheckBox, RadioButton) in a view tree. */
    private static int countButtons(View root) {
        int count = 0;
        if (root instanceof Button && !(root instanceof CheckBox)
                && !(root instanceof RadioButton)) {
            count = 1;
        }
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                count += countButtons(vg.getChildAt(i));
            }
        }
        return count;
    }

    /** Render an Activity and return the draw log. */
    private static List<OHBridge.DrawRecord> renderAndGetLog(Activity activity, int w, int h) {
        activity.onSurfaceCreated(0, w, h);
        activity.renderFrame();
        long surfaceCtx = getSurfaceCtx(activity);
        long canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx);
        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvasHandle);
        return log;
    }

    /** Get Activity's mSurfaceCtx via reflection. */
    private static long getSurfaceCtx(Activity activity) {
        try {
            java.lang.reflect.Field f = Activity.class.getDeclaredField("mSurfaceCtx");
            f.setAccessible(true);
            return f.getLong(activity);
        } catch (Exception e) {
            return 0;
        }
    }

    /** Check if draw log contains a drawText with the given substring. */
    private static boolean hasDrawText(List<OHBridge.DrawRecord> log, String text) {
        for (OHBridge.DrawRecord r : log) {
            if ("drawText".equals(r.op) && r.text != null && r.text.contains(text)) {
                return true;
            }
        }
        return false;
    }
}
