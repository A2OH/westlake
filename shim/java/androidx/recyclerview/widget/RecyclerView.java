package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.IdentityHashMap;

/**
 * Android-compatible RecyclerView shim.
 * Minimal clean-room implementation for A2OH migration.
 *
 * No recycling, no animations, no scroll physics. Creates all visible item
 * views and lays them out vertically, similar to a simple ListView.
 */
public class RecyclerView extends ViewGroup {

    private Adapter mAdapter;
    private LayoutManager mLayoutManager;
    private OnFlingListener mOnFlingListener;
    private final IdentityHashMap<View, ViewHolder> mAttachedViewHolders =
            new IdentityHashMap<View, ViewHolder>();

    // ---------------------------------------------------------------
    //  Constructors
    // ---------------------------------------------------------------

    public RecyclerView(Context context) {
        super(context);
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ---------------------------------------------------------------
    //  Adapter
    // ---------------------------------------------------------------

    public void setAdapter(Adapter adapter) {
        if (mAdapter == adapter) {
            return;
        }
        if (mAdapter != null) {
            try {
                mAdapter.onDetachedFromRecyclerView(this);
            } catch (Throwable ignored) {
            }
            mAdapter.mRecyclerView = null;
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.mRecyclerView = this;
            try {
                mAdapter.onAttachedToRecyclerView(this);
            } catch (Throwable ignored) {
            }
        }
        requestLayout();
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    // ---------------------------------------------------------------
    //  LayoutManager
    // ---------------------------------------------------------------

    public void setLayoutManager(LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        if (mLayoutManager != null) {
            mLayoutManager.mRecyclerView = this;
        }
        requestLayout();
    }

    public LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    // ---------------------------------------------------------------
    //  ItemDecoration (stub)
    // ---------------------------------------------------------------

    public void addItemDecoration(ItemDecoration decor) {
        // no-op in this minimal shim
    }

    public void addItemDecoration(ItemDecoration decor, int index) {
        // no-op in this minimal shim
    }

    public void removeItemDecoration(ItemDecoration decor) {
        // no-op in this minimal shim
    }

    // ---------------------------------------------------------------
    //  Scroll (stubs for API compatibility)
    // ---------------------------------------------------------------

    public void scrollToPosition(int position) {
        // no-op in this minimal shim
    }

    public void smoothScrollToPosition(int position) {
        // no-op in this minimal shim
    }

    public void smoothScrollBy(int dx, int dy) {
        scrollBy(dx, dy);
    }

    public int getMinFlingVelocity() {
        return 50;
    }

    public int getChildAdapterPosition(View child) {
        return indexOfChild(child);
    }

    public int getChildLayoutPosition(View child) {
        return getChildAdapterPosition(child);
    }

    public int getChildViewHolderPosition(View child) {
        return getChildAdapterPosition(child);
    }

    public ViewHolder getChildViewHolder(View child) {
        return child != null ? mAttachedViewHolders.get(child) : null;
    }

    public boolean fling(int velocityX, int velocityY) {
        return mOnFlingListener != null && mOnFlingListener.onFling(velocityX, velocityY);
    }

    public void setOnFlingListener(OnFlingListener onFlingListener) {
        mOnFlingListener = onFlingListener;
    }

    public OnFlingListener getOnFlingListener() {
        return mOnFlingListener;
    }

    // ---------------------------------------------------------------
    //  Measure / Layout
    // ---------------------------------------------------------------

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Default: use parent-supplied size
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter == null) {
            return;
        }

        // Remove all previously-added children
        removeAllViews();
        mAttachedViewHolders.clear();

        int parentWidth = r - l;
        int parentHeight = b - t;
        boolean horizontal = isHorizontalLayout();
        int childTop = getPaddingTop();
        int childLeft = getPaddingLeft();
        int availableWidth = parentWidth - getPaddingLeft() - getPaddingRight();
        int availableHeight = parentHeight - getPaddingTop() - getPaddingBottom();
        int count = mAdapter.getItemCount();

        for (int i = 0; i < count; i++) {
            if ((!horizontal && childTop >= parentHeight)
                    || (horizontal && childLeft >= parentWidth)) {
                break;
            }

            int viewType = mAdapter.getItemViewType(i);

            // Create and bind
            ViewHolder vh = mAdapter.onCreateViewHolder(this, viewType);
            vh.mPosition = i;
            vh.mItemViewType = viewType;
            mAdapter.onBindViewHolder(vh, i);

            View child = vh.itemView;
            ensureRecyclerLayoutParams(child);

            int childWidthSpec;
            int childHeightSpec;
            if (horizontal) {
                int itemWidth = availableWidth / 2;
                if (itemWidth < 120) itemWidth = 120;
                if (itemWidth > 220) itemWidth = 220;
                childWidthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);
                childHeightSpec = MeasureSpec.makeMeasureSpec(
                        Math.max(1, availableHeight), MeasureSpec.AT_MOST);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(
                        Math.max(1, availableWidth), MeasureSpec.EXACTLY);
                childHeightSpec = MeasureSpec.makeMeasureSpec(
                        Math.max(1, parentHeight - childTop), MeasureSpec.AT_MOST);
            }
            child.measure(childWidthSpec, childHeightSpec);

            int childHeight = child.getMeasuredHeight();
            int childWidth = child.getMeasuredWidth();
            if (childWidth <= 0) childWidth = horizontal ? MeasureSpec.getSize(childWidthSpec) : availableWidth;
            if (childHeight <= 0) childHeight = horizontal
                    ? Math.max(44, Math.min(availableHeight, 160)) : 44;

            // Add and position
            addView(child);
            mAttachedViewHolders.put(child, vh);
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            if (horizontal) {
                childLeft += childWidth;
            } else {
                childTop += childHeight;
            }
        }
    }

    private boolean isHorizontalLayout() {
        if (mLayoutManager == null) {
            return false;
        }
        try {
            if (mLayoutManager.canScrollHorizontally()) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        if (mLayoutManager instanceof RecyclerView.LinearLayoutManager) {
            return ((RecyclerView.LinearLayoutManager) mLayoutManager).getOrientation()
                    == RecyclerView.LinearLayoutManager.HORIZONTAL;
        }
        if (mLayoutManager instanceof androidx.recyclerview.widget.LinearLayoutManager) {
            return ((androidx.recyclerview.widget.LinearLayoutManager) mLayoutManager).getOrientation()
                    == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
        }
        return false;
    }

    private void ensureRecyclerLayoutParams(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof LayoutParams) {
            return;
        }
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            child.setLayoutParams(new LayoutParams((ViewGroup.MarginLayoutParams) lp));
        } else if (lp != null) {
            child.setLayoutParams(new LayoutParams(lp));
        } else {
            child.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }

    // ---------------------------------------------------------------
    //  Inner class: ViewHolder
    // ---------------------------------------------------------------

    public static abstract class ViewHolder {
        public final View itemView;
        int mPosition;
        int mItemViewType;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }

        public int getAdapterPosition() {
            return mPosition;
        }

        public int getLayoutPosition() {
            return mPosition;
        }

        public int getAbsoluteAdapterPosition() {
            return mPosition;
        }

        public int getBindingAdapterPosition() {
            return mPosition;
        }

        public final int getItemViewType() {
            return mItemViewType;
        }
    }

    // ---------------------------------------------------------------
    //  Inner class: Adapter<VH>
    // ---------------------------------------------------------------

    public static abstract class Adapter<VH extends ViewHolder> {
        RecyclerView mRecyclerView;

        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);
        public abstract void onBindViewHolder(VH holder, int position);
        public abstract int getItemCount();

        public int getItemViewType(int position) {
            return 0;
        }

        public long getItemId(int position) {
            return -1;
        }

        public boolean hasStableIds() {
            return false;
        }

        public void setHasStableIds(boolean hasStableIds) {
            // no-op
        }

        public void onViewRecycled(VH holder) {
            // no-op
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            // no-op
        }

        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            // no-op
        }

        /**
         * Notify the RecyclerView that the data set has changed and it should
         * re-create and re-bind all visible views.
         */
        public final void notifyDataSetChanged() {
            if (mRecyclerView != null) {
                mRecyclerView.requestLayout();
            }
        }

        /**
         * Notify that a single item at {@code position} has changed.
         */
        public final void notifyItemChanged(int position) {
            notifyDataSetChanged();
        }

        /**
         * Notify that an item has been inserted at {@code position}.
         */
        public final void notifyItemInserted(int position) {
            notifyDataSetChanged();
        }

        /**
         * Notify that an item has been removed from {@code position}.
         */
        public final void notifyItemRemoved(int position) {
            notifyDataSetChanged();
        }

        /**
         * Notify that {@code itemCount} items starting at {@code positionStart}
         * have changed.
         */
        public final void notifyItemRangeChanged(int positionStart, int itemCount) {
            notifyDataSetChanged();
        }

        /**
         * Notify that {@code itemCount} items have been inserted starting at
         * {@code positionStart}.
         */
        public final void notifyItemRangeInserted(int positionStart, int itemCount) {
            notifyDataSetChanged();
        }

        /**
         * Notify that {@code itemCount} items have been removed starting at
         * {@code positionStart}.
         */
        public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
            notifyDataSetChanged();
        }
    }

    // ---------------------------------------------------------------
    //  Inner class: LayoutManager
    // ---------------------------------------------------------------

    public static abstract class LayoutManager {
        RecyclerView mRecyclerView;

        /**
         * Called when the RecyclerView needs a new layout pass.
         * In this minimal shim the actual layout is done by RecyclerView.onLayout
         * directly, so this is a no-op hook for subclasses.
         */
        public void onLayoutChildren(Recycler recycler, State state) {
            // no-op: layout is handled by RecyclerView.onLayout
        }

        public boolean canScrollVertically() {
            return false;
        }

        public boolean canScrollHorizontally() {
            return false;
        }

        public int getWidth() {
            return mRecyclerView != null ? mRecyclerView.getWidth() : 0;
        }

        public int getHeight() {
            return mRecyclerView != null ? mRecyclerView.getHeight() : 0;
        }

        public int getPaddingLeft() {
            return mRecyclerView != null ? mRecyclerView.getPaddingLeft() : 0;
        }

        public int getPaddingTop() {
            return mRecyclerView != null ? mRecyclerView.getPaddingTop() : 0;
        }

        public int getPaddingRight() {
            return mRecyclerView != null ? mRecyclerView.getPaddingRight() : 0;
        }

        public int getPaddingBottom() {
            return mRecyclerView != null ? mRecyclerView.getPaddingBottom() : 0;
        }

        public int getChildCount() {
            return mRecyclerView != null ? mRecyclerView.getChildCount() : 0;
        }

        public View getChildAt(int index) {
            return mRecyclerView != null ? mRecyclerView.getChildAt(index) : null;
        }

        public int getPosition(View view) {
            return mRecyclerView != null ? mRecyclerView.getChildAdapterPosition(view) : -1;
        }

        public int getItemCount() {
            Adapter a = mRecyclerView != null ? mRecyclerView.getAdapter() : null;
            return a != null ? a.getItemCount() : 0;
        }

        public View findViewByPosition(int position) {
            if (mRecyclerView == null || position < 0 || position >= mRecyclerView.getChildCount()) {
                return null;
            }
            return mRecyclerView.getChildAt(position);
        }

        public int getDecoratedLeft(View child) {
            return child != null ? child.getLeft() : 0;
        }

        public int getDecoratedTop(View child) {
            return child != null ? child.getTop() : 0;
        }

        public int getDecoratedRight(View child) {
            return child != null ? child.getRight() : 0;
        }

        public int getDecoratedBottom(View child) {
            return child != null ? child.getBottom() : 0;
        }

        public int getDecoratedMeasuredWidth(View child) {
            return child != null ? child.getMeasuredWidth() : 0;
        }

        public int getDecoratedMeasuredHeight(View child) {
            return child != null ? child.getMeasuredHeight() : 0;
        }

        public int getWidthMode() {
            return View.MeasureSpec.EXACTLY;
        }

        public int getHeightMode() {
            return View.MeasureSpec.EXACTLY;
        }

        public void getTransformedBoundingBox(View child, boolean includeDecorInsets, Rect out) {
            if (out == null) {
                return;
            }
            if (child == null) {
                out.set(0, 0, 0, 0);
                return;
            }
            out.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }

        public void startSmoothScroll(SmoothScroller smoothScroller) {
            if (smoothScroller != null) {
                smoothScroller.mLayoutManager = this;
            }
        }

        public void requestLayout() {
            if (mRecyclerView != null) {
                mRecyclerView.requestLayout();
            }
        }

        public void setMeasurementCacheEnabled(boolean measurementCacheEnabled) {
            // Measurement caching is a performance knob in AndroidX. This shim
            // lays out directly each frame, so there is no cache to toggle.
        }
    }

    public abstract static class SmoothScroller {
        LayoutManager mLayoutManager;

        public interface ScrollVectorProvider {
            PointF computeScrollVectorForPosition(int targetPosition);
        }
    }

    // ---------------------------------------------------------------
    //  Inner class: LinearLayoutManager
    // ---------------------------------------------------------------

    /**
     * A minimal LinearLayoutManager that supports vertical orientation.
     * In this shim the actual layout is performed by RecyclerView.onLayout,
     * so this mainly exists to satisfy {@code new LinearLayoutManager(ctx)}
     * call sites and orientation queries.
     */
    public static class LinearLayoutManager extends LayoutManager
            implements SmoothScroller.ScrollVectorProvider {
        public static final int HORIZONTAL = 0;
        public static final int VERTICAL = 1;

        private int mOrientation = VERTICAL;
        private boolean mReverseLayout = false;

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

        @Override
        public boolean canScrollVertically() {
            return mOrientation == VERTICAL;
        }

        @Override
        public boolean canScrollHorizontally() {
            return mOrientation == HORIZONTAL;
        }

        public int findFirstVisibleItemPosition() {
            return getItemCount() > 0 && getChildCount() > 0 ? 0 : -1;
        }

        public int findLastVisibleItemPosition() {
            int itemCount = getItemCount();
            int childCount = getChildCount();
            return itemCount > 0 && childCount > 0 ? Math.min(itemCount, childCount) - 1 : -1;
        }

        public int findFirstCompletelyVisibleItemPosition() {
            return findFirstVisibleItemPosition();
        }

        public int findLastCompletelyVisibleItemPosition() {
            return findLastVisibleItemPosition();
        }

        public void scrollToPositionWithOffset(int position, int offset) {
            // no-op
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            int direction = mReverseLayout ? -1 : 1;
            return mOrientation == HORIZONTAL
                    ? new PointF(direction, 0f)
                    : new PointF(0f, direction);
        }
    }

    // ---------------------------------------------------------------
    //  Inner class: ItemDecoration
    // ---------------------------------------------------------------

    public static abstract class ItemDecoration {
        /**
         * Called to draw decorations under the item views.
         * No-op in this minimal shim.
         */
        public void onDraw(Object c, RecyclerView parent, State state) {
            // no-op
        }

        /**
         * Called to draw decorations over the item views.
         * No-op in this minimal shim.
         */
        public void onDrawOver(Object c, RecyclerView parent, State state) {
            // no-op
        }

        /**
         * Retrieve item offsets for the given position.
         * The default implementation sets all offsets to 0.
         */
        public void getItemOffsets(Object outRect, View view, RecyclerView parent, State state) {
            // no-op: all offsets remain 0
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            if (outRect != null) {
                outRect.set(0, 0, 0, 0);
            }
        }
    }

    // ---------------------------------------------------------------
    //  Inner class: Recycler (stub for LayoutManager API)
    // ---------------------------------------------------------------

    public final class Recycler {
        /**
         * Obtain a view for the given position. In this minimal shim we
         * always create a fresh view via the adapter.
         */
        public View getViewForPosition(int position) {
            if (mAdapter == null) {
                throw new IllegalStateException("No adapter set");
            }
            int viewType = mAdapter.getItemViewType(position);
            ViewHolder vh = mAdapter.onCreateViewHolder(RecyclerView.this, viewType);
            vh.mPosition = position;
            mAdapter.onBindViewHolder(vh, position);
            return vh.itemView;
        }
    }

    // ---------------------------------------------------------------
    //  Inner class: State (stub for LayoutManager API)
    // ---------------------------------------------------------------

    public static class State {
        private int mItemCount;

        public int getItemCount() {
            return mItemCount;
        }

        /** @hide */
        void setItemCount(int count) {
            mItemCount = count;
        }
    }

    // ---------------------------------------------------------------
    //  Inner class: OnScrollListener (stub)
    // ---------------------------------------------------------------

    public abstract static class OnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            // no-op
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            // no-op
        }
    }

    public void addOnScrollListener(OnScrollListener listener) {
        // no-op
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        // no-op
    }

    public void clearOnScrollListeners() {
        // no-op
    }

    // Scroll state constants for API compatibility
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    public abstract static class OnFlingListener {
        public abstract boolean onFling(int velocityX, int velocityY);
    }

    // ---------------------------------------------------------------
    //  Convenience: find child ViewHolder
    // ---------------------------------------------------------------

    public ViewHolder findViewHolderForAdapterPosition(int position) {
        for (View child : mAttachedViewHolders.keySet()) {
            ViewHolder holder = mAttachedViewHolders.get(child);
            if (holder != null && holder.getAdapterPosition() == position) {
                return holder;
            }
        }
        return null;
    }

    public ViewHolder findViewHolderForLayoutPosition(int position) {
        return findViewHolderForAdapterPosition(position);
    }

    // ---------------------------------------------------------------
    //  OnItemTouchListener (stub)
    // ---------------------------------------------------------------

    public interface OnItemTouchListener {
        boolean onInterceptTouchEvent(RecyclerView rv, Object e);
        void onTouchEvent(RecyclerView rv, Object e);
        void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept);
    }

    public void addOnItemTouchListener(OnItemTouchListener listener) {
        // no-op
    }

    public void removeOnItemTouchListener(OnItemTouchListener listener) {
        // no-op
    }

    // ---------------------------------------------------------------
    //  RecycledViewPool (stub)
    // ---------------------------------------------------------------

    public static class RecycledViewPool {
        public void setMaxRecycledViews(int viewType, int max) {
            // no-op
        }

        public void clear() {
            // no-op
        }
    }

    public void setRecycledViewPool(RecycledViewPool pool) {
        // no-op
    }

    public RecycledViewPool getRecycledViewPool() {
        return new RecycledViewPool();
    }

    // ---------------------------------------------------------------
    //  setHasFixedSize (stub)
    // ---------------------------------------------------------------

    public void setHasFixedSize(boolean hasFixedSize) {
        // no-op
    }

    // ---------------------------------------------------------------
    //  setItemAnimator (stub)
    // ---------------------------------------------------------------

    public void setItemAnimator(Object animator) {
        // no-op
    }

    public Object getItemAnimator() {
        return null;
    }

    // ---------------------------------------------------------------
    //  setNestedScrollingEnabled (stub)
    // ---------------------------------------------------------------

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        // no-op
    }
}
