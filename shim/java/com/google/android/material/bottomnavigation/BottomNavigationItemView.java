package com.google.android.material.bottomnavigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Bottom navigation item shim used by stock apps that inspect item child views.
 */
public class BottomNavigationItemView extends LinearLayout {
    private final TextView mIcon;
    private final TextView mLabel;

    public BottomNavigationItemView(Context context) {
        this(context, null);
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(android.view.Gravity.CENTER);
        setPadding(4, 6, 4, 6);
        mIcon = new TextView(context);
        mIcon.setTextSize(16);
        mIcon.setTextColor(0xFF292929);
        mIcon.setGravity(android.view.Gravity.CENTER);
        addView(mIcon, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mLabel = new TextView(context);
        mLabel.setTextSize(11);
        mLabel.setTextColor(0xFF292929);
        mLabel.setGravity(android.view.Gravity.CENTER);
        addView(mLabel, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void bind(MenuItem item, boolean selected) {
        if (item == null) {
            return;
        }
        setId(item.getItemId());
        setSelected(selected);
        item.setChecked(selected);
        CharSequence title = item.getTitle();
        mIcon.setText(selected ? "*" : "");
        mLabel.setText(title != null ? title : "");
        int color = selected ? 0xFFDA291C : 0xFF292929;
        mIcon.setTextColor(color);
        mLabel.setTextColor(color);
    }
}
