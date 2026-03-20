package com.example.showcase;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.ArrayAdapter;
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

public class ShowcaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = this;
        ScrollView scroll = new ScrollView(ctx);
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        // === Section 1: Text ===
        addHeader(root, "Text Widgets");

        TextView tv1 = new TextView(ctx);
        tv1.setText("Plain TextView");
        root.addView(tv1);

        TextView tv2 = new TextView(ctx);
        tv2.setText("Bold Large Text");
        tv2.setTextSize(24);
        root.addView(tv2);

        TextView tv3 = new TextView(ctx);
        tv3.setText("This is a very long text that should wrap to multiple lines when the width is not enough to contain it all on one line. Word wrapping should work correctly.");
        root.addView(tv3);

        EditText et = new EditText(ctx);
        et.setHint("Enter text here...");
        root.addView(et);

        // === Section 2: Buttons ===
        addHeader(root, "Buttons");

        Button btn1 = new Button(ctx);
        btn1.setText("Standard Button");
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Button clicked!");
            }
        });
        root.addView(btn1);

        ToggleButton toggle = new ToggleButton(ctx);
        toggle.setTextOn("ON");
        toggle.setTextOff("OFF");
        root.addView(toggle);

        // === Section 3: Selection ===
        addHeader(root, "Selection Widgets");

        CheckBox cb1 = new CheckBox(ctx);
        cb1.setText("Option A");
        cb1.setChecked(true);
        root.addView(cb1);

        CheckBox cb2 = new CheckBox(ctx);
        cb2.setText("Option B");
        root.addView(cb2);

        RadioGroup rg = new RadioGroup(ctx);
        rg.setOrientation(LinearLayout.VERTICAL);
        RadioButton rb1 = new RadioButton(ctx);
        rb1.setText("Choice 1");
        rb1.setId(101);
        rg.addView(rb1);
        RadioButton rb2 = new RadioButton(ctx);
        rb2.setText("Choice 2");
        rb2.setId(102);
        rg.addView(rb2);
        rg.check(101);
        root.addView(rg);

        Switch sw = new Switch(ctx);
        sw.setTextOn("ON");
        sw.setTextOff("OFF");
        sw.setText("Enable feature");
        root.addView(sw);

        // === Section 4: Progress ===
        addHeader(root, "Progress");

        ProgressBar pb = new ProgressBar(ctx);
        pb.setMax(100);
        pb.setProgress(65);
        root.addView(pb);

        SeekBar seekBar = new SeekBar(ctx);
        seekBar.setMax(100);
        seekBar.setProgress(40);
        root.addView(seekBar);

        RatingBar ratingBar = new RatingBar(ctx);
        ratingBar.setNumStars(5);
        ratingBar.setRating(3.5f);
        root.addView(ratingBar);

        // === Section 5: Layouts ===
        addHeader(root, "Layout Test");

        // Horizontal LinearLayout
        LinearLayout hRow = new LinearLayout(ctx);
        hRow.setOrientation(LinearLayout.HORIZONTAL);
        Button leftBtn = new Button(ctx);
        leftBtn.setText("Left");
        Button rightBtn = new Button(ctx);
        rightBtn.setText("Right");
        hRow.addView(leftBtn);
        hRow.addView(rightBtn);
        root.addView(hRow);

        // RelativeLayout
        RelativeLayout rl = new RelativeLayout(ctx);
        TextView rlLabel = new TextView(ctx);
        rlLabel.setText("RelativeLayout: centered");
        rlLabel.setId(201);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rl.addView(rlLabel, rlParams);
        root.addView(rl);

        // FrameLayout
        FrameLayout fl = new FrameLayout(ctx);
        TextView flLabel = new TextView(ctx);
        flLabel.setText("FrameLayout: stacked");
        fl.addView(flLabel);
        root.addView(fl);

        // === Section 6: Lists ===
        addHeader(root, "ListView");

        ListView lv = new ListView(ctx);
        String[] items = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
        ArrayAdapter adapter = new ArrayAdapter(ctx, 0, items) {
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView tv = new TextView(parent.getContext());
                tv.setText((String) getItem(position));
                tv.setPadding(16, 12, 16, 12);
                return tv;
            }
        };
        lv.setAdapter(adapter);
        root.addView(lv);

        // === Section 7: Summary ===
        addHeader(root, "Summary");

        TextView summary = new TextView(ctx);
        summary.setText("Widgets: TextView, EditText, Button, ToggleButton, " +
            "CheckBox, RadioButton, RadioGroup, Switch, ProgressBar, SeekBar, " +
            "RatingBar, LinearLayout, RelativeLayout, FrameLayout, ListView, " +
            "ScrollView. All running on AOSP code (193K lines).");
        root.addView(summary);

        scroll.addView(root);
        setContentView(scroll);

        System.out.println("=== ShowcaseActivity: " + root.getChildCount() + " children ===");
    }

    private void addHeader(LinearLayout parent, String text) {
        TextView header = new TextView(parent.getContext());
        header.setText("-- " + text + " --");
        header.setTextSize(18);
        header.setPadding(0, 24, 0, 8);
        parent.addView(header);
    }
}
