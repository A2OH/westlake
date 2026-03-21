package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.*;

public class CounterActivity extends Activity {
    private int counter = 0;
    private TextView counterDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(32, 48, 32, 32);

        TextView title = new TextView(this);
        title.setText("Counter");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        counterDisplay = new TextView(this);
        counterDisplay.setText("0");
        counterDisplay.setTextSize(48);
        counterDisplay.setGravity(Gravity.CENTER);
        root.addView(counterDisplay);

        // Button row
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        Button minusBtn = new Button(this);
        minusBtn.setText("\u2212");
        minusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                counter--;
                updateDisplay();
            }
        });
        row.addView(minusBtn);

        Button resetBtn = new Button(this);
        resetBtn.setText("Reset");
        resetBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                counter = 0;
                updateDisplay();
            }
        });
        row.addView(resetBtn);

        Button plusBtn = new Button(this);
        plusBtn.setText("+");
        plusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                counter++;
                updateDisplay();
            }
        });
        row.addView(plusBtn);

        root.addView(row);

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

    private void updateDisplay() {
        counterDisplay.setText(Integer.toString(counter));
    }

    /** Expose counter value for testing */
    public int getCounter() {
        return counter;
    }

    /** Expose counter display text for testing */
    public String getCounterText() {
        return counterDisplay.getText().toString();
    }
}
