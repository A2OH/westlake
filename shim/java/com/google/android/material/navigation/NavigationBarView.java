package com.google.android.material.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SimpleMenu;
import android.widget.FrameLayout;

/**
 * NavigationBarView shim — base class for BottomNavigationView.
 */
public class NavigationBarView extends FrameLayout {
    private final NavigationMenu mMenu = new NavigationMenu();
    private android.widget.LinearLayout mMenuRow;
    private OnItemSelectedListener mItemSelectedListener;
    private OnItemReselectedListener mItemReselectedListener;
    private int mSelectedItemId = -1;

    public NavigationBarView(Context context) { super(context); }
    public NavigationBarView(Context context, AttributeSet attrs) { super(context, attrs); }
    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mItemSelectedListener = listener;
    }
    public void setOnItemReselectedListener(OnItemReselectedListener listener) {
        mItemReselectedListener = listener;
    }
    public void setItemIconTintList(android.content.res.ColorStateList tint) {}
    public void setItemTextColor(android.content.res.ColorStateList color) {}
    public void setItemIconSize(int size) {}
    public void setItemActiveIndicatorEnabled(boolean enabled) {}
    public void setLabelVisibilityMode(int mode) {}
    public void inflateMenu(int resId) {
        try {
            new MenuInflater(getContext()).inflate(resId, mMenu);
        } catch (Throwable ignored) {
        }
        syncMenuViews();
    }
    public Menu getMenu() { return mMenu; }
    public int getSelectedItemId() { return mSelectedItemId; }
    public void setSelectedItemId(int id) {
        if (id == mSelectedItemId) {
            MenuItem item = mMenu.findItem(id);
            if (item != null && mItemReselectedListener != null) {
                mItemReselectedListener.onNavigationItemReselected(item);
            }
            return;
        }
        mSelectedItemId = id;
        MenuItem item = mMenu.findItem(id);
        if (item != null && mItemSelectedListener != null) {
            mItemSelectedListener.onNavigationItemSelected(item);
        }
        syncMenuViews();
    }

    private void syncMenuViews() {
        if (mMenuRow == null) {
            mMenuRow = new android.widget.LinearLayout(getContext());
            mMenuRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            addView(mMenuRow, new FrameLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
        mMenuRow.removeAllViews();
        int count = mMenu.size();
        for (int i = 0; i < count; i++) {
            MenuItem item = mMenu.getItem(i);
            if (item == null) {
                continue;
            }
            com.google.android.material.bottomnavigation.BottomNavigationItemView itemView =
                    new com.google.android.material.bottomnavigation.BottomNavigationItemView(getContext());
            itemView.bind(item, item.getItemId() == mSelectedItemId);
            mMenuRow.addView(itemView, new android.widget.LinearLayout.LayoutParams(
                    0, LayoutParams.WRAP_CONTENT, 1f));
        }
    }

    private class NavigationMenu extends SimpleMenu {
        @Override
        public MenuItem add(CharSequence title) {
            MenuItem item = super.add(title);
            syncMenuViews();
            return item;
        }

        @Override
        public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
            MenuItem item = super.add(groupId, itemId, order, title);
            syncMenuViews();
            return item;
        }

        @Override
        public MenuItem add(int groupId, int itemId, int order, int titleRes) {
            CharSequence title = "@" + titleRes;
            try {
                title = getContext().getString(titleRes);
            } catch (Throwable ignored) {
            }
            return add(groupId, itemId, order, title);
        }

        @Override
        public MenuItem add(int titleRes) {
            return add(0, size(), size(), titleRes);
        }

        @Override
        public void clear() {
            super.clear();
            syncMenuViews();
        }

        @Override
        public void removeItem(int id) {
            super.removeItem(id);
            syncMenuViews();
        }

        @Override
        public void removeGroup(int groupId) {
            super.removeGroup(groupId);
            syncMenuViews();
        }
    }

    public interface OnItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem item);
    }

    public interface OnItemReselectedListener {
        void onNavigationItemReselected(MenuItem item);
    }
}
