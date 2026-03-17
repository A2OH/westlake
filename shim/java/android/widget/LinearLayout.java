package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.LinearLayout → ARKUI_NODE_COLUMN (vertical) / ARKUI_NODE_ROW (horizontal)
 *
 * Default orientation is VERTICAL (COLUMN).
 */
public class LinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    static final int NODE_TYPE_COLUMN = 16;
    static final int NODE_TYPE_ROW = 17;

    private int orientation = VERTICAL;

    private static final int DEFAULT_ROOT_PADDING = 8;

    public LinearLayout() {
        super(NODE_TYPE_COLUMN);
    }

    public LinearLayout(android.content.Context context) {
        this();
    }

    public LinearLayout(android.content.Context context, android.util.AttributeSet attrs) {
        this();
    }

    public LinearLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        this();
    }

    /** Create with specific orientation.
     *  Note: changing orientation after creation requires node recreation.
     *  For now, set orientation before adding children. */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        // If orientation differs from what was created, we'd need to recreate.
        // For the shim, we create the correct type based on first setOrientation call.
    }

    public int getOrientation() { return orientation; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
        int wSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
        int hMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
        int hSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

        if (orientation == VERTICAL) {
            // First pass: measure non-expandable children with AT_MOST for remaining space
            int usedHeight = getPaddingTop() + getPaddingBottom();
            int maxWidth = 0;
            int expandableCount = 0;
            // Give children the full width of the parent (EXACTLY if parent is EXACTLY)
            int childWidthSpec;
            if (wMode == android.view.View.MeasureSpec.EXACTLY) {
                childWidthSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                    wSize - getPaddingLeft() - getPaddingRight(), android.view.View.MeasureSpec.EXACTLY);
            } else if (wMode == android.view.View.MeasureSpec.AT_MOST) {
                childWidthSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                    wSize - getPaddingLeft() - getPaddingRight(), android.view.View.MeasureSpec.AT_MOST);
            } else {
                childWidthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
            }

            // Identify expandable children (ListView or children with weight > 0)
            for (int i = 0; i < getChildCount(); i++) {
                android.view.View child = getChildAt(i);
                if (child.getVisibility() == GONE) continue;
                if (isExpandableChild(child)) {
                    expandableCount++;
                } else {
                    int childHSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                        hMode == android.view.View.MeasureSpec.UNSPECIFIED ? 0 : hSize - usedHeight,
                        hMode == android.view.View.MeasureSpec.UNSPECIFIED ?
                            android.view.View.MeasureSpec.UNSPECIFIED : android.view.View.MeasureSpec.AT_MOST);
                    child.measure(childWidthSpec, childHSpec);
                    usedHeight += child.getMeasuredHeight();
                    maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                }
            }

            // Second pass: expandable children share remaining space
            int remainingHeight = 0;
            if (hMode == android.view.View.MeasureSpec.EXACTLY) {
                remainingHeight = hSize - usedHeight;
            } else if (hMode == android.view.View.MeasureSpec.AT_MOST) {
                remainingHeight = hSize - usedHeight;
            }
            if (remainingHeight < 0) remainingHeight = 0;

            if (expandableCount > 0) {
                int perChild = remainingHeight / expandableCount;
                for (int i = 0; i < getChildCount(); i++) {
                    android.view.View child = getChildAt(i);
                    if (child.getVisibility() == GONE) continue;
                    if (isExpandableChild(child)) {
                        int childHSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                            perChild, android.view.View.MeasureSpec.EXACTLY);
                        child.measure(childWidthSpec, childHSpec);
                        usedHeight += child.getMeasuredHeight();
                        maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                    }
                }
            }

            setMeasuredDimension(
                resolveSize(maxWidth + getPaddingLeft() + getPaddingRight(), widthMeasureSpec),
                resolveSize(usedHeight, heightMeasureSpec));
        } else {
            // HORIZONTAL: measure children and sum widths
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            int totalWidth = 0;
            int maxHeight = 0;
            for (int i = 0; i < getChildCount(); i++) {
                android.view.View child = getChildAt(i);
                if (child.getVisibility() == GONE) continue;
                totalWidth += child.getMeasuredWidth();
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            setMeasuredDimension(
                resolveSize(totalWidth + getPaddingLeft() + getPaddingRight(), widthMeasureSpec),
                resolveSize(maxHeight + getPaddingTop() + getPaddingBottom(), heightMeasureSpec));
        }
    }

    /** Check if a child should expand to fill remaining space (like ListView). */
    private boolean isExpandableChild(android.view.View child) {
        if (child instanceof ListView || child instanceof AbsListView) return true;
        // Also check for weight in LayoutParams
        Object lp = child.getLayoutParams();
        if (lp instanceof LayoutParams && ((LayoutParams) lp).weight > 0) return true;
        return false;
    }

    // resolveSize is inherited from View

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int parentWidth = r - l - getPaddingLeft() - getPaddingRight();
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int cw = child.getMeasuredWidth() > 0 ? child.getMeasuredWidth() : parentWidth;
            int ch = child.getMeasuredHeight() > 0 ? child.getMeasuredHeight() : 0;
            if (orientation == VERTICAL) {
                // In vertical mode, children fill the parent width
                int childWidth = Math.max(cw, parentWidth);
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + ch);
                childTop += ch;
            } else {
                // In horizontal mode, vertically center children in the row
                int parentHeight = b - t - getPaddingTop() - getPaddingBottom();
                int childTopH = childTop + (parentHeight - ch) / 2;
                if (childTopH < childTop) childTopH = childTop;
                child.layout(childLeft, childTopH, childLeft + cw, childTopH + ch);
                childLeft += cw;
            }
        }
    }

    // ── LayoutParams with weight support ──

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public float weight;
        public int gravity = -1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}
