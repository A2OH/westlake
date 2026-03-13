package com.example.flashnote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Switch;

public class SettingsActivity extends Activity {
    private static final String PREFS_SETTINGS = "flashnote_settings";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);

        // Dark mode toggle
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(prefs.getBoolean("dark_mode", false));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
        });

        // Font size slider
        SeekBar seekFontSize = findViewById(R.id.seekFontSize);
        seekFontSize.setProgress(prefs.getInt("font_size", 16));
        seekFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int fontSize = Math.max(12, progress); // minimum 12sp
                prefs.edit().putInt("font_size", fontSize).apply();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
