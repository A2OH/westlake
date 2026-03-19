package com.android.internal.view.menu;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

/** Stub for MenuPopupHelper used by PopupMenu. */
public class MenuPopupHelper {
    public MenuPopupHelper(Context context, MenuBuilder menu, View anchorView) {}
    public MenuPopupHelper(Context context, MenuBuilder menu, View anchorView, boolean overflowOnly, int popupStyleAttr, int popupStyleRes) {}
    public void setGravity(int gravity) {}
    public int getGravity() { return 0; }
    public void show() {}
    public void dismiss() {}
    public boolean isShowing() { return false; }
    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {}
    public void setForceShowIcon(boolean forceShow) {}
    public void inflate(int menuRes) {}
    public android.view.MenuInflater getMenuInflater() { return null; }
    public ShowableListMenu getPopup() { return null; }
}
