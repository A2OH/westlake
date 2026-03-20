import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.ohos.shim.bridge.OHBridge;
import java.util.List;

/**
 * Headless test runner for UI Showcase app.
 * Exercises every ported Android widget: measure, layout, draw, and PNG render.
 *
 * Run: java -cp build ShowcaseRunner
 */
public class ShowcaseRunner {
    static int passed = 0, failed = 0;

    public static void main(String[] args) {
        System.out.println("=== UI Showcase Test ===\n");

        try {
            MiniServer.init("com.example.showcase");
            MiniActivityManager am = MiniServer.get().getActivityManager();
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                "com.example.showcase", "com.example.showcase.ShowcaseActivity"));
            am.startActivity(null, intent, -1);

            Activity act = am.getResumedActivity();
            check("ShowcaseActivity launched", act != null);

            // Check view tree
            View decor = act.getWindow().getDecorView();
            int totalViews = countViews(decor);
            check("View tree has 20+ views", totalViews >= 20);
            System.out.println("  Total views in tree: " + totalViews);

            // Find specific widget types in the tree
            check("Has ScrollView", findViewByType(decor, ScrollView.class) != null);
            check("Has LinearLayout", findViewByType(decor, LinearLayout.class) != null);
            check("Has TextView", findViewByType(decor, TextView.class) != null);
            check("Has EditText", findViewByType(decor, EditText.class) != null);
            check("Has Button", findViewByType(decor, Button.class) != null);
            check("Has ToggleButton", findViewByType(decor, ToggleButton.class) != null);
            check("Has CheckBox", findViewByType(decor, CheckBox.class) != null);
            check("Has RadioGroup", findViewByType(decor, RadioGroup.class) != null);
            check("Has RadioButton", findViewByType(decor, RadioButton.class) != null);
            check("Has Switch", findViewByType(decor, Switch.class) != null);
            check("Has ProgressBar", findViewByType(decor, ProgressBar.class) != null);
            check("Has SeekBar", findViewByType(decor, SeekBar.class) != null);
            check("Has RatingBar", findViewByType(decor, RatingBar.class) != null);
            check("Has RelativeLayout", findViewByType(decor, RelativeLayout.class) != null);
            check("Has FrameLayout", findViewByType(decor, FrameLayout.class) != null);
            check("Has ListView", findViewByType(decor, ListView.class) != null);

            // Verify widget state
            ListView lv = (ListView) findViewByType(decor, ListView.class);
            check("ListView has adapter with 5 items",
                lv != null && lv.getAdapter() != null && lv.getAdapter().getCount() == 5);

            CheckBox cb = (CheckBox) findViewByType(decor, CheckBox.class);
            check("First CheckBox is checked", cb != null && cb.isChecked());

            ProgressBar pb = (ProgressBar) findViewByType(decor, ProgressBar.class);
            check("ProgressBar progress=65", pb != null && pb.getProgress() == 65);

            SeekBar seekBar = (SeekBar) findViewByType(decor, SeekBar.class);
            check("SeekBar progress=40", seekBar != null && seekBar.getProgress() == 40);

            // Dump tree
            System.out.println("\n--- View Tree ---");
            dumpTree(decor, 0);

            // Render
            act.onSurfaceCreated(0, 480, 3200);
            act.renderFrame();
            long surfaceCtx = getSurfaceCtx(act);
            long canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx);
            List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvasHandle);
            System.out.println("\n--- Canvas: " + log.size() + " draw ops ---");
            check("Canvas has 10+ draw ops", log.size() >= 10);

            // Check text rendering
            check("Renders 'Plain TextView'", hasText(log, "Plain TextView"));
            check("Renders 'Bold Large Text'", hasText(log, "Bold Large Text"));
            check("Renders 'Standard Button'", hasText(log, "Standard Button"));
            check("Renders 'Option A'", hasText(log, "Option A"));
            check("Renders 'Choice 1'", hasText(log, "Choice 1"));
            check("Renders 'Enable feature'", hasText(log, "Enable feature"));
            check("Renders 'Left'", hasText(log, "Left"));
            check("Renders 'Right'", hasText(log, "Right"));
            check("Renders 'RelativeLayout: centered'", hasText(log, "centered"));
            check("Renders 'FrameLayout: stacked'", hasText(log, "stacked"));
            check("Renders 'ListView Item 1'", hasText(log, "Item 1"));
            check("Renders header 'Text Widgets'", hasText(log, "Text Widgets"));
            check("Renders header 'Buttons'", hasText(log, "Buttons"));
            check("Renders header 'Selection'", hasText(log, "Selection"));
            check("Renders header 'Progress'", hasText(log, "Progress"));
            check("Renders header 'Layout Test'", hasText(log, "Layout Test"));
            check("Renders header 'ListView'", hasText(log, "ListView"));
            check("Renders header 'Summary'", hasText(log, "Summary"));
            // Summary text may be below ScrollView viewport; verify it exists in the view tree
            check("Summary text in view tree", hasTextInTree(decor, "193K lines"));

            // Print some draw ops for debugging
            System.out.println("\n--- Sample draw ops ---");
            int printed = 0;
            for (int i = 0; i < log.size() && printed < 20; i++) {
                OHBridge.DrawRecord r = log.get(i);
                if ("drawText".equals(r.op)) {
                    System.out.println("  drawText: \"" + r.text + "\"");
                    printed++;
                }
            }

            // Render PNG
            try {
                java.awt.image.BufferedImage img = PixelRenderer.render(log, 480, 3200);
                PixelRenderer.savePNG(img, "/tmp/showcase.png");
                System.out.println("\nPNG saved: /tmp/showcase.png");
                check("PNG file created", new java.io.File("/tmp/showcase.png").exists());
                // Copy to Windows if available
                java.io.File dst = new java.io.File("/mnt/c/Users/dspfa/Downloads/showcase.png");
                if (dst.getParentFile().exists()) {
                    java.nio.file.Files.copy(
                        new java.io.File("/tmp/showcase.png").toPath(),
                        dst.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Copied to Windows Downloads");
                }
            } catch (Exception e) {
                System.out.println("PNG render: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(System.out);
            failed++;
        }

        System.out.println("\n=== Results: " + passed + " pass, " + failed + " fail ===");
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

    private static int countViews(View root) {
        int count = 1;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                count += countViews(vg.getChildAt(i));
            }
        }
        return count;
    }

    private static void dumpTree(View v, int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) sb.append("  ");
        String name = v.getClass().getSimpleName();
        if (v instanceof TextView) {
            CharSequence text = ((TextView) v).getText();
            if (text != null && text.length() > 0) {
                String s = text.toString();
                if (s.length() > 40) s = s.substring(0, 40) + "...";
                name = name + " \"" + s + "\"";
            }
        }
        sb.append(name);
        sb.append(" [" + v.getWidth() + "x" + v.getHeight() + "]");
        System.out.println(sb.toString());
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                dumpTree(vg.getChildAt(i), depth + 1);
            }
        }
    }

    private static View findViewByType(View root, Class type) {
        if (type.isInstance(root)) return root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View found = findViewByType(vg.getChildAt(i), type);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static boolean hasText(List<OHBridge.DrawRecord> log, String text) {
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("drawText".equals(r.op) && r.text != null && r.text.contains(text)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasTextInTree(View root, String text) {
        if (root instanceof TextView) {
            CharSequence cs = ((TextView) root).getText();
            if (cs != null && cs.toString().contains(text)) return true;
        }
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                if (hasTextInTree(vg.getChildAt(i), text)) return true;
            }
        }
        return false;
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
}
