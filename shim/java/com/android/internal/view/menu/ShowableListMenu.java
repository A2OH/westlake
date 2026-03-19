package com.android.internal.view.menu;

import android.widget.ListView;

/**
 * Stub for com.android.internal.view.menu.ShowableListMenu.
 */
public interface ShowableListMenu {
    void show();
    void dismiss();
    boolean isShowing();
    ListView getListView();
}
