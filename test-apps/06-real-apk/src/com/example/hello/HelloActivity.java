package com.example.hello;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.view.View;

/**
 * A real Hello World Activity that exercises:
 * - Activity lifecycle (onCreate/onResume)
 * - Programmatic View tree (LinearLayout + TextView + Button)
 * - Resource lookups (getResources().getString())
 * - OnClickListener wiring
 * - Canvas rendering pipeline
 */
public class HelloActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Build a programmatic layout that proves Activity lifecycle works
        LinearLayout layout = new LinearLayout();

        TextView title = new TextView();
        title.setText("Hello from Real APK!");
        layout.addView(title);

        TextView info = new TextView();
        String pkg = getPackageName();
        info.setText("Package: " + (pkg != null ? pkg : "unknown"));
        layout.addView(info);

        // Prove resources work
        android.content.res.Resources res = getResources();
        if (res != null) {
            // Use a well-known resource ID pattern: 0x7f0a0001
            // In tests, we register this via registerStringResource()
            String appName = res.getString(0x7f0a0001);
            TextView resView = new TextView();
            resView.setText("App name from resources: " + appName);
            layout.addView(resView);
        }

        Button btn = new Button();
        btn.setText("Click Me");
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Button clicked!");
            }
        });
        layout.addView(btn);

        // Set the view tree via Window
        getWindow().setContentView(layout);

        System.out.println("=== HelloActivity.onCreate() ===");
        System.out.println("Package: " + pkg);
        System.out.println("View tree created with " + layout.getChildCount() + " children");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("=== HelloActivity.onResume() ===");
    }
}
