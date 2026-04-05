package com.google.android.material.bottomnavigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationBarView;

/**
 * BottomNavigationView shim — extends NavigationBarView.
 * Renders as a horizontal bar with tab labels at the bottom.
 */
public class BottomNavigationView extends NavigationBarView {
    private OnNavigationItemSelectedListener mListener;

    public BottomNavigationView(Context context) { super(context); init(); }
    public BottomNavigationView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        setBackgroundColor(0xFF27251F); // McD dark
        setPadding(0, 8, 0, 8);
        // Set a minimum height so it's visible
        setMinimumHeight(112);
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        mListener = listener;
    }

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem item);
    }
}
