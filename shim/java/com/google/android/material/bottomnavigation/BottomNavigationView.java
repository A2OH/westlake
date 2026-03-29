package com.google.android.material.bottomnavigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Minimal BottomNavigationView shim — renders as a horizontal LinearLayout with tab labels.
 */
public class BottomNavigationView extends LinearLayout {
    private OnNavigationItemSelectedListener mListener;
    private int mSelectedItemId = -1;

    public BottomNavigationView(Context context) { super(context); init(); }
    public BottomNavigationView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        setOrientation(HORIZONTAL);
        setBackgroundColor(0xFF212121);
        setPadding(0, 8, 0, 8);
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        mListener = listener;
    }

    public int getSelectedItemId() { return mSelectedItemId; }
    public void setSelectedItemId(int id) { mSelectedItemId = id; }
    public android.view.Menu getMenu() { return null; }
    public void setLabelVisibilityMode(int mode) {}
    public void setItemIconTintList(android.content.res.ColorStateList tint) {}
    public void setItemTextColor(android.content.res.ColorStateList color) {}

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem item);
    }
}
