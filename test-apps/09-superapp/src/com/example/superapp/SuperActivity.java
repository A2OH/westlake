package com.example.superapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * Main activity tying the SuperApp together.
 * In a real app, this would host UI for all the test features.
 * For now it just runs the test suite on create.
 */
public class SuperActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // In headless mode, tests run from SuperAppRunner.main()
        // This Activity exists to test Activity lifecycle integration
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
