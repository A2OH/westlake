package com.google.android.material.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.FrameLayout;

/**
 * NavigationBarView shim — base class for BottomNavigationView.
 */
public class NavigationBarView extends FrameLayout {
    public NavigationBarView(Context context) { super(context); }
    public NavigationBarView(Context context, AttributeSet attrs) { super(context, attrs); }
    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {}
    public void setOnItemReselectedListener(OnItemReselectedListener listener) {}
    public void setItemIconTintList(android.content.res.ColorStateList tint) {}
    public void setItemTextColor(android.content.res.ColorStateList color) {}
    public void setItemIconSize(int size) {}
    public void setItemActiveIndicatorEnabled(boolean enabled) {}
    public void setLabelVisibilityMode(int mode) {}
    public android.view.Menu getMenu() { return null; }
    public int getSelectedItemId() { return -1; }
    public void setSelectedItemId(int id) {}

    public interface OnItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem item);
    }

    public interface OnItemReselectedListener {
        void onNavigationItemReselected(MenuItem item);
    }
}
