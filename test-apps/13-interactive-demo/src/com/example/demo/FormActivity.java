package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.*;

public class FormActivity extends Activity {
    private EditText nameField;
    private CheckBox subscribeCheck;
    private RadioGroup themeGroup;
    private RadioButton lightRadio;
    private RadioButton darkRadio;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 48, 32, 32);

        TextView title = new TextView(this);
        title.setText("Form Demo");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        // Name field
        TextView nameLabel = new TextView(this);
        nameLabel.setText("Your name:");
        root.addView(nameLabel);

        nameField = new EditText(this);
        nameField.setHint("Enter your name");
        root.addView(nameField);

        // Subscribe checkbox
        subscribeCheck = new CheckBox(this);
        subscribeCheck.setText("Subscribe to newsletter");
        root.addView(subscribeCheck);

        // Theme radio group
        TextView themeLabel = new TextView(this);
        themeLabel.setText("Theme:");
        root.addView(themeLabel);

        themeGroup = new RadioGroup(this);
        themeGroup.setOrientation(LinearLayout.VERTICAL);

        lightRadio = new RadioButton(this);
        lightRadio.setText("Light");
        lightRadio.setId(101);
        themeGroup.addView(lightRadio);

        darkRadio = new RadioButton(this);
        darkRadio.setText("Dark");
        darkRadio.setId(102);
        themeGroup.addView(darkRadio);

        root.addView(themeGroup);

        // Submit button
        Button submitBtn = new Button(this);
        submitBtn.setText("Submit");
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doSubmit();
            }
        });
        root.addView(submitBtn);

        // Result display
        resultText = new TextView(this);
        resultText.setText("");
        root.addView(resultText);

        // Back button
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

    private void doSubmit() {
        String name = nameField.getText().toString();
        boolean subscribe = subscribeCheck.isChecked();
        int checkedId = themeGroup.getCheckedRadioButtonId();
        String theme = "None";
        if (checkedId == 101) {
            theme = "Light";
        } else if (checkedId == 102) {
            theme = "Dark";
        }
        String result = "Name: " + name
                + ", Subscribe: " + (subscribe ? "Yes" : "No")
                + ", Theme: " + theme;
        resultText.setText(result);
    }

    /** Expose for testing */
    public EditText getNameField() { return nameField; }
    public CheckBox getSubscribeCheck() { return subscribeCheck; }
    public RadioGroup getThemeGroup() { return themeGroup; }
    public RadioButton getLightRadio() { return lightRadio; }
    public RadioButton getDarkRadio() { return darkRadio; }
    public TextView getResultText() { return resultText; }

    /** Programmatic submit for testing */
    public void clickSubmit() { doSubmit(); }
}
