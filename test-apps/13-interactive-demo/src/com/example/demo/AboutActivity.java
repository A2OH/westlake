package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.*;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 48, 32, 32);

        TextView title = new TextView(this);
        title.setText("About A2OH");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        addInfoLine(root, "");
        addInfoLine(root, "A2OH runs unmodified Android APKs");
        addInfoLine(root, "on OpenHarmony using the Westlake engine.");
        addInfoLine(root, "");
        addInfoLine(root, "Version: 1.0");
        addInfoLine(root, "Engine: Westlake");
        addInfoLine(root, "AOSP: 193K lines");
        addInfoLine(root, "Tests: 2400+");
        addInfoLine(root, "");
        addInfoLine(root, "Dalvik VM ported to x86_64 + OHOS aarch64.");
        addInfoLine(root, "99% of API calls stay inside the VM as pure Java.");

        Button backBtn = new Button(this);
        backBtn.setText("Back");
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        root.addView(backBtn);

        setContentView(root);
    }

    private void addInfoLine(LinearLayout parent, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        parent.addView(tv);
    }
}
