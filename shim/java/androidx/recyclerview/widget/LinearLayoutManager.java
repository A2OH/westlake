package androidx.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Android-compatible LinearLayoutManager shim.
 * This is a standalone class that delegates to the identically-named inner
 * class inside RecyclerView.  Many apps import LinearLayoutManager at the
 * top level:
 *
 *   import androidx.recyclerview.widget.LinearLayoutManager;
 *
 * so this file ensures that import resolves.  It extends
 * RecyclerView.LayoutManager (not the inner LinearLayoutManager) to avoid
 * a circular dependency, and duplicates the same minimal logic.
 */
public class LinearLayoutManager extends RecyclerView.LayoutManager {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int INVALID_OFFSET = Integer.MIN_VALUE;

    private int mOrientation = VERTICAL;
    private boolean mReverseLayout = false;
    private boolean mStackFromEnd = false;

    public LinearLayoutManager(Context context) {
        // vertical by default
    }

    public LinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        mOrientation = orientation;
        mReverseLayout = reverseLayout;
    }

    public LinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // vertical by default
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("invalid orientation:" + orientation);
        }
        mOrientation = orientation;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setReverseLayout(boolean reverseLayout) {
        mReverseLayout = reverseLayout;
    }

    public boolean getReverseLayout() {
        return mReverseLayout;
    }

    public void setStackFromEnd(boolean stackFromEnd) {
        mStackFromEnd = stackFromEnd;
    }

    public boolean getStackFromEnd() {
        return mStackFromEnd;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    public int findFirstVisibleItemPosition() {
        return 0;
    }

    public int findLastVisibleItemPosition() {
        return getItemCount() - 1;
    }

    public int findFirstCompletelyVisibleItemPosition() {
        return 0;
    }

    public int findLastCompletelyVisibleItemPosition() {
        return getItemCount() - 1;
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        // no-op in this minimal shim
    }
}
