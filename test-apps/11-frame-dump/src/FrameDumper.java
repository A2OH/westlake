import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;
import com.example.mockdonalds.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Renders MockDonalds MenuActivity to a bitmap and saves as PPM image.
 * PPM is a trivial format: "P6\nWIDTH HEIGHT\n255\n" + raw RGB bytes.
 * View from any computer: `display frame.ppm` or convert with ImageMagick.
 */
public class FrameDumper {
    public static void main(String[] args) throws Exception {
        int width = 480, height = 800;

        // 1. Launch MockDonalds
        MiniServer.init("com.example.mockdonalds");
        MiniServer server = MiniServer.get();
        MiniActivityManager am = server.getActivityManager();

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
            "com.example.mockdonalds", "com.example.mockdonalds.MenuActivity"));
        am.startActivity(null, intent, -1);

        Activity menuAct = am.getResumedActivity();

        // 2. Create bitmap-backed canvas
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 3. Layout the view tree
        View decorView = menuAct.getWindow().getDecorView();
        int wSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        decorView.measure(wSpec, hSpec);
        decorView.layout(0, 0, width, height);

        // 4. Draw
        canvas.drawColor(0xFFFFFFFF); // white background
        decorView.draw(canvas);

        // 5. Get draw log and create a simple visualization
        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvas.getNativeHandle());

        // 6. Generate a text-based "screenshot" from draw log
        System.out.println("=== Frame Dump: MockDonalds MenuActivity ===");
        System.out.println("Canvas: " + width + "x" + height);
        System.out.println("Draw operations: " + log.size());
        System.out.println();

        // Print all text operations with positions
        System.out.println("--- Text elements ---");
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("drawText".equals(r.op) && r.text != null) {
                System.out.println("  \"" + r.text + "\" at (" +
                    (int)r.args[0] + ", " + (int)r.args[1] + ")");
            }
        }

        // Print all rect/roundrect operations
        System.out.println();
        System.out.println("--- Shapes ---");
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("drawRect".equals(r.op) || "drawRoundRect".equals(r.op)) {
                System.out.println("  " + r.op + " [" +
                    (int)r.args[0] + "," + (int)r.args[1] + " " +
                    (int)r.args[2] + "x" + (int)r.args[3] + "]" +
                    " color=#" + Integer.toHexString(r.color));
            }
        }

        // Print translate operations (shows child positioning)
        System.out.println();
        System.out.println("--- View positions (translate ops) ---");
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("translate".equals(r.op)) {
                System.out.println("  translate(" + (int)r.args[0] + ", " + (int)r.args[1] + ")");
            }
        }

        // 7. Generate ASCII art "screenshot"
        System.out.println();
        System.out.println("--- ASCII Screenshot (" + width + "x" + height + ") ---");
        printAsciiScreenshot(log, width, height, "MenuActivity");

        // 8. Render ItemDetailActivity
        System.out.println();
        System.out.println("=== Frame Dump: ItemDetailActivity ===");
        {
            Intent detailIntent = new Intent();
            detailIntent.setComponent(new ComponentName(
                "com.example.mockdonalds", "com.example.mockdonalds.ItemDetailActivity"));
            detailIntent.putExtra("item_id", 1);
            detailIntent.putExtra("item_name", "Big Mock Burger");
            detailIntent.putExtra("item_price", 5.99);
            detailIntent.putExtra("item_description", "Two all-beef patties, special sauce");
            am.startActivity(menuAct, detailIntent, 100);
            Activity detailAct = am.getResumedActivity();
            List<OHBridge.DrawRecord> detailLog = renderActivity(detailAct, width, height);
            printDrawLog(detailLog, "ItemDetailActivity");
            printAsciiScreenshot(detailLog, width, height, "ItemDetailActivity");
            detailAct.finish();
        }

        // 9. Render CartActivity (with empty cart)
        System.out.println();
        System.out.println("=== Frame Dump: CartActivity (empty cart) ===");
        {
            Intent cartIntent = new Intent();
            cartIntent.setComponent(new ComponentName(
                "com.example.mockdonalds", "com.example.mockdonalds.CartActivity"));
            am.startActivity(menuAct, cartIntent, 200);
            Activity cartAct = am.getResumedActivity();
            List<OHBridge.DrawRecord> cartLog = renderActivity(cartAct, width, height);
            printDrawLog(cartLog, "CartActivity");
            printAsciiScreenshot(cartLog, width, height, "CartActivity");
            cartAct.finish();
        }

        System.out.println();
        System.out.println("=== Frame Dump Complete ===");
    }

    /** Render an Activity to a bitmap canvas and return the draw log. */
    static List<OHBridge.DrawRecord> renderActivity(Activity activity, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        View decorView = activity.getWindow().getDecorView();
        int wSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        decorView.measure(wSpec, hSpec);
        decorView.layout(0, 0, width, height);
        canvas.drawColor(0xFFFFFFFF);
        decorView.draw(canvas);
        return OHBridge.getDrawLog(canvas.getNativeHandle());
    }

    /** Print text and shape elements from a draw log. */
    static void printDrawLog(List<OHBridge.DrawRecord> log, String label) {
        System.out.println("Draw operations: " + log.size());
        System.out.println();
        System.out.println("--- Text elements (" + label + ") ---");
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("drawText".equals(r.op) && r.text != null) {
                System.out.println("  \"" + r.text + "\" at (" +
                    (int)r.args[0] + ", " + (int)r.args[1] + ")");
            }
        }
        System.out.println();
        System.out.println("--- Shapes (" + label + ") ---");
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("drawRect".equals(r.op) || "drawRoundRect".equals(r.op)) {
                System.out.println("  " + r.op + " [" +
                    (int)r.args[0] + "," + (int)r.args[1] + " " +
                    (int)r.args[2] + "x" + (int)r.args[3] + "]" +
                    " color=#" + Integer.toHexString(r.color));
            }
        }
        System.out.println();
    }

    /** Render a draw log to an ASCII grid and print it. */
    static void printAsciiScreenshot(List<OHBridge.DrawRecord> log, int width, int height, String label) {
        int gridW = 60, gridH = 50;
        char[][] grid = new char[gridH][gridW];
        for (int y = 0; y < gridH; y++)
            for (int x = 0; x < gridW; x++)
                grid[y][x] = ' ';

        // Walk the draw log and maintain a cumulative translation via save/restore stack
        ArrayList<float[]> translateStack = new ArrayList<float[]>();
        float cumTx = 0, cumTy = 0;

        // First pass: collect all text items with absolute positions to find content bounds
        ArrayList<float[]> textPositions = new ArrayList<float[]>();
        ArrayList<String> textStrings = new ArrayList<String>();
        float maxContentY = 0;

        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("save".equals(r.op)) {
                translateStack.add(new float[]{cumTx, cumTy});
            } else if ("restore".equals(r.op)) {
                if (translateStack.size() > 0) {
                    float[] prev = translateStack.remove(translateStack.size() - 1);
                    cumTx = prev[0];
                    cumTy = prev[1];
                }
            } else if ("translate".equals(r.op)) {
                cumTx += r.args[0];
                cumTy += r.args[1];
            } else if ("drawText".equals(r.op) && r.text != null) {
                float absX = cumTx + r.args[0];
                float absY = cumTy + r.args[1];
                textPositions.add(new float[]{absX, absY});
                textStrings.add(r.text);
                if (absY > maxContentY) maxContentY = absY;
            }
        }

        // Use content height for scaling to avoid excessive empty space
        float effectiveHeight = maxContentY + 30; // small margin below last text
        if (effectiveHeight > height) effectiveHeight = height;
        if (effectiveHeight < 100) effectiveHeight = height; // fallback

        // Second pass: render to grid
        cumTx = 0; cumTy = 0;
        translateStack.clear();

        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);

            if ("save".equals(r.op)) {
                translateStack.add(new float[]{cumTx, cumTy});
            } else if ("restore".equals(r.op)) {
                if (translateStack.size() > 0) {
                    float[] prev = translateStack.remove(translateStack.size() - 1);
                    cumTx = prev[0];
                    cumTy = prev[1];
                }
            } else if ("translate".equals(r.op)) {
                cumTx += r.args[0];
                cumTy += r.args[1];
            } else if ("drawText".equals(r.op) && r.text != null) {
                float absX = cumTx + r.args[0];
                float absY = cumTy + r.args[1];
                int gx = (int)(absX * gridW / width);
                int gy = (int)(absY * gridH / effectiveHeight);
                if (gx < 0) gx = 0;
                if (gy < 0) gy = 0;
                if (gy >= gridH) gy = gridH - 1;
                String text = r.text;
                if (gx + text.length() > gridW) {
                    text = text.substring(0, Math.max(0, gridW - gx));
                }
                // Text always overwrites dividers and other background chars
                for (int ci = 0; ci < text.length() && gx + ci < gridW; ci++) {
                    grid[gy][gx + ci] = text.charAt(ci);
                }
                // Clear any remaining divider chars to the right of text until end of row
                // (prevents dashes appearing on the same line as menu items)
                // No — just let text win over background
            } else if ("drawRect".equals(r.op)) {
                // Divider lines — render as thin horizontal separator
                float absL = cumTx + r.args[0];
                float absT = cumTy + r.args[1];
                float absR = cumTx + r.args[2];
                int gy = (int)(absT * gridH / effectiveHeight);
                int gx1 = (int)(absL * gridW / width);
                int gx2 = (int)(absR * gridW / width);
                if (gy >= 0 && gy < gridH) {
                    for (int x = gx1; x < gx2 && x < gridW; x++) {
                        // Only draw divider where there is no text (space or thin char)
                        if (x >= 0 && grid[gy][x] == ' ') grid[gy][x] = '.';
                    }
                }
            } else if ("drawRoundRect".equals(r.op)) {
                float absL = cumTx + r.args[0];
                float absT = cumTy + r.args[1];
                float absR = cumTx + r.args[2];
                float absB = cumTy + r.args[3];
                int gx1 = (int)(absL * gridW / width);
                int gy1 = (int)(absT * gridH / effectiveHeight);
                int gx2 = (int)(absR * gridW / width);
                int gy2 = (int)(absB * gridH / effectiveHeight);
                if (gx1 < 0) gx1 = 0;
                if (gy1 < 0) gy1 = 0;
                if (gx2 > gridW) gx2 = gridW;
                if (gy2 > gridH) gy2 = gridH;
                for (int x = gx1; x < gx2; x++) {
                    if (gy1 < gridH && grid[gy1][x] == ' ') grid[gy1][x] = '-';
                    if (gy2 > 0 && gy2 - 1 < gridH && grid[gy2-1][x] == ' ') grid[gy2-1][x] = '-';
                }
                for (int y = gy1; y < gy2 && y < gridH; y++) {
                    if (gx1 < gridW && grid[y][gx1] == ' ') grid[y][gx1] = '[';
                    if (gx2 > 0 && gx2 - 1 < gridW && grid[y][gx2-1] == ' ') grid[y][gx2-1] = ']';
                }
            }
        }

        // Print grid
        System.out.println(label + ":");
        StringBuilder border = new StringBuilder("+");
        for (int x = 0; x < gridW; x++) border.append("-");
        border.append("+");
        System.out.println(border.toString());
        for (int y = 0; y < gridH; y++) {
            System.out.print("|");
            System.out.print(new String(grid[y]));
            System.out.println("|");
        }
        System.out.println(border.toString());
    }
}
