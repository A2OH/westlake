package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ListView → ARKUI_NODE_LIST
 *
 * ArkUI List is a scrollable list container with ListItem children.
 * The Adapter pattern is preserved — when setAdapter is called,
 * we populate the list by creating ListItem nodes for each row.
 */
public class ListView extends AbsListView {
    static final int NODE_TYPE_LIST = 10;
    static final int NODE_TYPE_LIST_ITEM = 19;

    private android.widget.ListAdapter adapter;

    /** Internal observer that re-populates children when adapter data changes. */
    private final BaseAdapter.DataSetObserver dataObserver = new BaseAdapter.DataSetObserver() {
        @Override public void onChanged() { populateFromAdapter(); }
        @Override public void onInvalidated() { removeAllViews(); }
    };

    public ListView() {
        super(NODE_TYPE_LIST);
    }

    public void setAdapter(android.widget.ListAdapter adapter) {
        // Unregister from old adapter
        if (this.adapter instanceof BaseAdapter) {
            ((BaseAdapter) this.adapter).unregisterDataSetObserver(dataObserver);
        }

        // Remove old items
        removeAllViews();
        this.adapter = adapter;
        if (adapter == null) return;

        // Register for future data changes
        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).registerDataSetObserver(dataObserver);
        }

        // Populate list items from adapter
        populateFromAdapter();
    }

    /** Default padding for list items (dp-like values). */
    private static final int ITEM_PADDING_H = 16;
    private static final int ITEM_PADDING_V = 12;

    /** Divider height between items (pixels). */
    private int mDividerHeight = 1;

    public void setDividerHeight(int height) { mDividerHeight = height; }
    public int getDividerHeight() { return mDividerHeight; }

    /** Re-creates all child views from the adapter. */
    private void populateFromAdapter() {
        removeAllViews();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            View itemView = adapter.getView(i, null, this);
            if (itemView != null) {
                // Apply default padding to item views if they have none
                if (itemView.getPaddingLeft() == 0 && itemView.getPaddingTop() == 0
                    && itemView.getPaddingRight() == 0 && itemView.getPaddingBottom() == 0) {
                    itemView.setPadding(ITEM_PADDING_H, ITEM_PADDING_V, ITEM_PADDING_H, ITEM_PADDING_V);
                }
                addView(itemView);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int totalHeight = 0;
        int maxWidth = 0;
        int visibleCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            totalHeight += child.getMeasuredHeight();
            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            visibleCount++;
        }
        // Add divider heights between items
        if (visibleCount > 1) {
            totalHeight += mDividerHeight * (visibleCount - 1);
        }
        int wMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
        int wSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
        int hMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
        int hSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

        int measuredW;
        if (wMode == android.view.View.MeasureSpec.EXACTLY) {
            measuredW = wSize;
        } else if (wMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredW = Math.min(maxWidth + getPaddingLeft() + getPaddingRight(), wSize);
        } else {
            measuredW = maxWidth + getPaddingLeft() + getPaddingRight();
        }

        int measuredH;
        if (hMode == android.view.View.MeasureSpec.EXACTLY) {
            measuredH = hSize;
        } else if (hMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredH = Math.min(totalHeight + getPaddingTop() + getPaddingBottom(), hSize);
        } else {
            measuredH = totalHeight + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(measuredW, measuredH);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        int childTop = getPaddingTop();
        int childLeft = getPaddingLeft();
        int availWidth = (r - l) - getPaddingLeft() - getPaddingRight();
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int cw = child.getMeasuredWidth() > 0 ? child.getMeasuredWidth() : availWidth;
            // Ensure items fill the available width
            if (cw < availWidth) cw = availWidth;
            int ch = child.getMeasuredHeight() > 0 ? child.getMeasuredHeight() : 0;
            child.layout(childLeft, childTop, childLeft + cw, childTop + ch);
            childTop += ch + mDividerHeight;
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        // Draw divider lines between items
        if (mDividerHeight > 0 && getChildCount() > 1) {
            android.graphics.Paint dividerPaint = new android.graphics.Paint();
            dividerPaint.setColor(0xFFCCCCCC);
            dividerPaint.setStyle(android.graphics.Paint.Style.FILL);
            for (int i = 0; i < getChildCount() - 1; i++) {
                android.view.View child = getChildAt(i);
                if (child.getVisibility() == GONE) continue;
                int dividerTop = child.getBottom();
                canvas.drawRect(getPaddingLeft(), dividerTop,
                    getWidth() - getPaddingRight(), dividerTop + mDividerHeight, dividerPaint);
            }
        }
    }

    public android.widget.ListAdapter getAdapter() { return adapter; }

    /**
     * Simulates a click on the item at the given position.
     * Dispatches to the OnItemClickListener if one is set.
     */
    public boolean performItemClick(View view, int position, long id) {
        dispatchItemClick(view, position, id);
        return getOnItemClickListener() != null;
    }

    @Override
    public int getCount() {
        return adapter != null ? adapter.getCount() : 0;
    }

    @Override
    public Object getItemAtPosition(int position) {
        return adapter != null ? adapter.getItem(position) : null;
    }

    /** Alias so code can reference ListView.ListAdapter. */
    public interface ListAdapter extends android.widget.ListAdapter {}
}
