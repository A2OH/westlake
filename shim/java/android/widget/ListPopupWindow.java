package android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Android-compatible ListPopupWindow shim.
 * A popup window that displays a list of items. All operations are stubs;
 * no native UI is created in this migration shim.
 */
public class ListPopupWindow implements com.android.internal.view.menu.ShowableListMenu {

    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    public static final int POSITION_PROMPT_ABOVE = 0;
    public static final int POSITION_PROMPT_BELOW = 1;

    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED         = 1;
    public static final int INPUT_METHOD_NOT_NEEDED     = 2;

    private ListAdapter mAdapter;
    private View mAnchorView;
    private int mWidth = WRAP_CONTENT;
    private int mHeight = WRAP_CONTENT;
    private boolean mShowing = false;
    private boolean mModal = false;
    private int mHorizontalOffset;
    private int mVerticalOffset;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private View mPromptView;
    private int mPromptPosition;

    public ListPopupWindow(Context context) {}
    public ListPopupWindow(Context context, AttributeSet attrs) {}
    public ListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {}
    public ListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {}

    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
    }

    public void setAnchorView(View anchor) {
        mAnchorView = anchor;
    }

    public View getAnchorView() { return mAnchorView; }

    public void setWidth(int width) { mWidth = width; }
    public int getWidth() { return mWidth; }
    public void setHeight(int height) { mHeight = height; }
    public int getHeight() { return mHeight; }

    public void setHorizontalOffset(int offset) { mHorizontalOffset = offset; }
    public int getHorizontalOffset() { return mHorizontalOffset; }
    public void setVerticalOffset(int offset) { mVerticalOffset = offset; }
    public int getVerticalOffset() { return mVerticalOffset; }

    @Override
    public void show() { mShowing = true; }

    @Override
    public void dismiss() { mShowing = false; }

    @Override
    public boolean isShowing() { return mShowing; }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    public void setModal(boolean modal) { mModal = modal; }
    public boolean isModal() { return mModal; }

    @Override
    public ListView getListView() { return null; }

    public void setListSelector(Drawable selector) {}
    public void setBackgroundDrawable(Drawable d) {}
    public Drawable getBackground() { return null; }

    public void setContentWidth(int width) { mWidth = width; }
    public void setInputMethodMode(int mode) {}
    public int getInputMethodMode() { return 0; }
    public void setSoftInputMode(int mode) {}
    public void setEpicenterBounds(android.graphics.Rect bounds) {}

    public void setPromptView(View prompt) { mPromptView = prompt; }
    public void setPromptPosition(int position) { mPromptPosition = position; }

    public void setSelection(int position) {}
    public int getSelectedItemPosition() { return -1; }
    public Object getSelectedItem() { return null; }
    public long getSelectedItemId() { return -1; }
    public View getSelectedView() { return null; }

    public boolean performItemClick(int position) { return false; }

    public void clearListSelection() {}
    public boolean isInputMethodNotNeeded() { return false; }

    public void postShow() { show(); }

    public int measureHeightOfChildren(int widthMeasureSpec, int startPosition,
            int endPosition, int maxHeight, int disallowPartialChildPosition) {
        return 0;
    }

    public void setAnimationStyle(int animationStyle) {}
    public int getAnimationStyle() { return 0; }
    public boolean isDropDownAlwaysVisible() { return false; }
    public void setDropDownAlwaysVisible(boolean dropDownAlwaysVisible) {}
    public boolean onKeyUp(int keyCode, android.view.KeyEvent event) { return false; }
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) { return false; }
    public boolean onKeyPreIme(int keyCode, android.view.KeyEvent event) { return false; }
    public void setForceIgnoreOutsideTouch(boolean forceIgnoreOutsideTouch) {}
    public void setDropDownGravity(int gravity) {}
    public void setListItemExpandMax(int max) {}
}
