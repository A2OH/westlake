import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.example.mockdonalds.*;
import com.ohos.shim.bridge.OHBridge;
import java.util.List;

/**
 * Headless test runner for MockDonalds app.
 * Exercises: SQLite, ListView, Intent extras, Activity lifecycle, SharedPreferences,
 * and Canvas rendering validation (draw log verification).
 *
 * Run: java -cp build MockDonaldsRunner
 * Expected: 14 PASS, 0 FAIL
 */
public class MockDonaldsRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== MockDonalds End-to-End Test ===\n");

        try {
            // Initialize MiniServer
            MiniServer.init("com.example.mockdonalds");
            MiniServer server = MiniServer.get();
            MiniActivityManager am = server.getActivityManager();
            check("MiniServer initialized", server != null && am != null);

            // Launch MenuActivity
            Intent menuIntent = new Intent();
            menuIntent.setComponent(new ComponentName(
                    "com.example.mockdonalds", "com.example.mockdonalds.MenuActivity"));
            am.startActivity(null, menuIntent, -1);

            Activity menuAct = am.getResumedActivity();
            check("MenuActivity launched", menuAct instanceof MenuActivity);
            MenuActivity menu = (MenuActivity) menuAct;

            // Verify menu loaded from SQLite
            List<MenuItem> items = menu.getMenuItems();
            check("MenuActivity has 8 menu items", items.size() == 8);

            // Verify ListView populated
            View decor = menu.getWindow().getDecorView();
            ListView listView = findListView(decor);
            check("ListView populated", listView != null && listView.getChildCount() == 8);

            // Simulate clicking item 0 (Big Mock Burger)
            MenuItem firstItem = items.get(0);
            Intent detailIntent = new Intent();
            detailIntent.setComponent(new ComponentName(
                    "com.example.mockdonalds", "com.example.mockdonalds.ItemDetailActivity"));
            detailIntent.putExtra("item_id", firstItem.id);
            detailIntent.putExtra("item_name", firstItem.name);
            detailIntent.putExtra("item_price", firstItem.price);
            detailIntent.putExtra("item_description", firstItem.description);
            am.startActivity(menuAct, detailIntent, 100);

            Activity detailAct = am.getResumedActivity();
            check("ItemDetailActivity launched", detailAct instanceof ItemDetailActivity);
            ItemDetailActivity detail = (ItemDetailActivity) detailAct;
            check("Item name = Big Mock Burger, price = $5.99",
                    "Big Mock Burger".equals(detail.getItemName()) &&
                    Math.abs(detail.getItemPrice() - 5.99) < 0.01);

            // Simulate "Add to Cart" button click
            CartManager cart = new CartManager(detail);
            MenuItem itemToAdd = menu.getDbHelper().getItem(firstItem.id);
            int cartCount = cart.addToCart(itemToAdd);
            check("Add to Cart: count = 1", cartCount == 1);

            // Go to CartActivity
            detail.finish();
            Intent cartIntent = new Intent();
            cartIntent.setComponent(new ComponentName(
                    "com.example.mockdonalds", "com.example.mockdonalds.CartActivity"));
            am.startActivity(menuAct, cartIntent, 200);

            Activity cartAct = am.getResumedActivity();
            check("CartActivity shows 1 item, total = $5.99",
                    cartAct instanceof CartActivity &&
                    ((CartActivity) cartAct).getCartItems().size() == 1 &&
                    Math.abs(((CartActivity) cartAct).getCartManager().getCartTotal() - 5.99) < 0.01);

            // Checkout
            Intent checkoutIntent = new Intent();
            checkoutIntent.setComponent(new ComponentName(
                    "com.example.mockdonalds", "com.example.mockdonalds.CheckoutActivity"));
            checkoutIntent.putExtra("total", "$5.99");
            checkoutIntent.putExtra("item_count", 1);
            am.startActivity(cartAct, checkoutIntent, 300);

            Activity checkoutAct = am.getResumedActivity();
            check("Checkout: order saved",
                    checkoutAct instanceof CheckoutActivity &&
                    ((CheckoutActivity) checkoutAct).getOrderNumber() == 1);

            // Verify cart cleared
            CartManager cartAfter = new CartManager(checkoutAct);
            check("Cart cleared after checkout", cartAfter.getCartCount() == 0);

            // ── Canvas Render Validation ──────────────────────────────────
            System.out.println("\n--- Canvas Render Validation ---");

            // Render MenuActivity
            List<OHBridge.DrawRecord> menuLog = renderAndGetLog(menuAct, 480, 800);
            check("MenuActivity renders 'MockDonalds Menu' text",
                    hasDrawText(menuLog, "MockDonalds Menu"));
            check("MenuActivity renders menu item 'Big Mock Burger'",
                    hasDrawText(menuLog, "Big Mock Burger"));
            check("MenuActivity renders button 'View Cart'",
                    hasDrawText(menuLog, "View Cart") && hasDrawOp(menuLog, "drawRoundRect"));

            // Render CheckoutActivity
            List<OHBridge.DrawRecord> checkoutLog = renderAndGetLog(checkoutAct, 480, 800);
            check("CheckoutActivity renders 'Order Confirmed!'",
                    hasDrawText(checkoutLog, "Order Confirmed!"));

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

    /**
     * Simulate a surface, render the Activity's View tree, and return the draw log.
     */
    private static List<OHBridge.DrawRecord> renderAndGetLog(Activity activity, int w, int h) {
        // Create a mock surface for this Activity
        activity.onSurfaceCreated(0, w, h);
        activity.renderFrame();

        // Get the canvas handle from the surface
        // The surface stores canvas internally; we need to retrieve the draw log
        // from the canvas that renderFrame() used. The mock surface's canvas handle
        // is accessible via surfaceGetCanvas on the Activity's surface context.
        // Since renderFrame uses mSurfaceCtx, we access it through reflection or
        // by re-reading the surface canvas.
        long surfaceCtx = getSurfaceCtx(activity);
        long canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx);
        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvasHandle);
        return log;
    }

    /** Get the Activity's mSurfaceCtx field via reflection. */
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

    /** Check if draw log contains the given operation type. */
    private static boolean hasDrawOp(List<OHBridge.DrawRecord> log, String op) {
        for (OHBridge.DrawRecord r : log) {
            if (op.equals(r.op)) return true;
        }
        return false;
    }

    private static ListView findListView(View root) {
        if (root instanceof ListView) return (ListView) root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                ListView found = findListView(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }
}
