package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.ComponentName;
import android.view.View;
import android.view.Gravity;
import android.widget.*;

public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(32, 48, 32, 32);

        TextView title = new TextView(this);
        title.setText("A2OH Interactive Demo");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Running on Westlake Engine");
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);

        // Navigation buttons
        addNavButton(root, "Counter Demo", "com.example.demo.CounterActivity");
        addNavButton(root, "Form Demo", "com.example.demo.FormActivity");
        addNavButton(root, "Shopping List", "com.example.demo.ListActivity");
        addNavButton(root, "About A2OH", "com.example.demo.AboutActivity");

        TextView footer = new TextView(this);
        footer.setText("Tap any button to navigate");
        footer.setGravity(Gravity.CENTER);
        root.addView(footer);

        setContentView(root);
    }

    private void addNavButton(LinearLayout parent, final String label, final String targetClass) {
        Button btn = new Button(this);
        btn.setText(label + " \u2192");
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.demo", targetClass));
                startActivity(intent);
            }
        });
        parent.addView(btn);
    }
}
