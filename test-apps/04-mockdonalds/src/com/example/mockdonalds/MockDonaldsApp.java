package com.example.mockdonalds;

import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import com.ohos.shim.bridge.OHBridge;

/**
 * OHOS entry point for MockDonalds app.
 * Initializes MiniServer, sets up OHBridge rendering surface, and launches MenuActivity.
 *
 * Run on OHOS ART:
 *   dalvikvm -classpath app.dex com.example.mockdonalds.MockDonaldsApp
 */
public class MockDonaldsApp {
    private static final String TAG = "MockDonaldsApp";
    private static final int SURFACE_WIDTH = 480;
    private static final int SURFACE_HEIGHT = 800;

    public static void main(String[] args) {
        System.out.println("[MockDonaldsApp] Starting on OHOS + ART ...");

        // Check native bridge availability
        boolean nativeOk = OHBridge.isNativeAvailable();
        System.out.println("[MockDonaldsApp] OHBridge native: " + (nativeOk ? "LOADED" : "UNAVAILABLE"));

        // Initialize ArkUI if available
        if (nativeOk) {
            int rc = OHBridge.arkuiInit();
            System.out.println("[MockDonaldsApp] arkuiInit() = " + rc);
        }

        // Initialize MiniServer (replaces Android SystemServer)
        MiniServer.init("com.example.mockdonalds");
        MiniServer server = MiniServer.get();
        MiniActivityManager am = server.getActivityManager();
        System.out.println("[MockDonaldsApp] MiniServer initialized");

        // Launch MenuActivity
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.example.mockdonalds", "com.example.mockdonalds.MenuActivity"));
        am.startActivity(null, intent, -1);

        Activity menuActivity = am.getResumedActivity();
        if (menuActivity == null) {
            System.out.println("[MockDonaldsApp] ERROR: MenuActivity failed to launch");
            return;
        }
        System.out.println("[MockDonaldsApp] MenuActivity launched: " + menuActivity.getClass().getName());

        // Create rendering surface and do initial render
        if (nativeOk) {
            System.out.println("[MockDonaldsApp] Creating surface " + SURFACE_WIDTH + "x" + SURFACE_HEIGHT);
            menuActivity.onSurfaceCreated(0, SURFACE_WIDTH, SURFACE_HEIGHT);
            menuActivity.renderFrame();
            System.out.println("[MockDonaldsApp] Initial frame rendered");

            // Enter render loop - wait for touch events from native side
            System.out.println("[MockDonaldsApp] Entering event loop...");
            renderLoop(menuActivity, am);
        } else {
            // Headless mode - just verify the app works
            System.out.println("[MockDonaldsApp] Running in headless mode (no native bridge)");
            System.out.println("[MockDonaldsApp] Menu items: " + ((MenuActivity) menuActivity).getMenuItems().size());
            System.out.println("[MockDonaldsApp] App initialized successfully in headless mode");
        }
    }

    /**
     * Simple render loop: re-render the current Activity's view tree.
     * Touch events arrive via OHBridge.dispatchTouchEvent() from native.
     */
    private static void renderLoop(Activity initialActivity, MiniActivityManager am) {
        long frameCount = 0;
        while (true) {
            try {
                Thread.sleep(16); // ~60fps
            } catch (InterruptedException e) {
                break;
            }

            Activity current = am.getResumedActivity();
            if (current == null) {
                System.out.println("[MockDonaldsApp] No resumed activity, exiting");
                break;
            }

            // Re-render if the view tree is dirty
            current.renderFrame();
            frameCount++;

            if (frameCount % 600 == 0) { // Log every ~10 seconds
                System.out.println("[MockDonaldsApp] Frame " + frameCount
                        + " activity=" + current.getClass().getSimpleName());
            }
        }
        System.out.println("[MockDonaldsApp] Render loop ended after " + frameCount + " frames");
    }
}
