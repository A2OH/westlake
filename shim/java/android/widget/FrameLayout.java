package android.widget;
import android.view.ViewGroup;
import android.view.ViewGroup;

import android.view.ViewGroup;

/**
 * Shim: android.widget.FrameLayout → ARKUI_NODE_STACK
 *
 * STACK is ArkUI's overlay container — children are stacked on top of each other.
 */
public class FrameLayout extends ViewGroup {
    static final int NODE_TYPE_STACK = 8;

    public FrameLayout() {
        super(NODE_TYPE_STACK);
    }

    public FrameLayout(Object context) {
        this();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
        int wSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
        int hMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
        int hSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

        // FrameLayout children default to MATCH_PARENT — pass parent constraints directly
        int childWidthSpec;
        int childHeightSpec;
        if (wMode == android.view.View.MeasureSpec.EXACTLY) {
            childWidthSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                wSize - getPaddingLeft() - getPaddingRight(), android.view.View.MeasureSpec.EXACTLY);
        } else if (wMode == android.view.View.MeasureSpec.AT_MOST) {
            childWidthSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                wSize - getPaddingLeft() - getPaddingRight(), android.view.View.MeasureSpec.AT_MOST);
        } else {
            childWidthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        }
        if (hMode == android.view.View.MeasureSpec.EXACTLY) {
            childHeightSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                hSize - getPaddingTop() - getPaddingBottom(), android.view.View.MeasureSpec.EXACTLY);
        } else if (hMode == android.view.View.MeasureSpec.AT_MOST) {
            childHeightSpec = android.view.View.MeasureSpec.makeMeasureSpec(
                hSize - getPaddingTop() - getPaddingBottom(), android.view.View.MeasureSpec.AT_MOST);
        } else {
            childHeightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        }

        int maxWidth = 0, maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            child.measure(childWidthSpec, childHeightSpec);
            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
        }
        int specW = wMode == android.view.View.MeasureSpec.EXACTLY
            ? wSize : maxWidth + getPaddingLeft() + getPaddingRight();
        int specH = hMode == android.view.View.MeasureSpec.EXACTLY
            ? hSize : maxHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(specW, specH);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        int parentWidth = r - l - getPaddingLeft() - getPaddingRight();
        int parentHeight = b - t - getPaddingTop() - getPaddingBottom();
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            // Default to full parent size (MATCH_PARENT behavior)
            int cw = child.getMeasuredWidth() > 0 ? Math.max(child.getMeasuredWidth(), parentWidth) : parentWidth;
            int ch = child.getMeasuredHeight() > 0 ? Math.max(child.getMeasuredHeight(), parentHeight) : parentHeight;

            int gravity = -1;
            Object lp = child.getLayoutParams();
            if (lp instanceof LayoutParams) {
                gravity = ((LayoutParams) lp).gravity;
            }

            int childLeft = getPaddingLeft();
            int childTop = getPaddingTop();

            if (gravity != -1) {
                int hGravity = gravity & 0x07; // horizontal: 1=LEFT, 3=H_CENTER, 5=RIGHT
                int vGravity = gravity & 0x70; // vertical: 0x10=TOP, 0x30=V_CENTER, 0x50=BOTTOM

                if (hGravity == 5) { // RIGHT
                    childLeft = getPaddingLeft() + parentWidth - cw;
                } else if (hGravity == 1) { // CENTER_HORIZONTAL
                    childLeft = getPaddingLeft() + (parentWidth - cw) / 2;
                }

                if (vGravity == 0x50) { // BOTTOM
                    childTop = getPaddingTop() + parentHeight - ch;
                } else if (vGravity == 0x10) { // CENTER_VERTICAL
                    childTop = getPaddingTop() + (parentHeight - ch) / 2;
                }
            }

            child.layout(childLeft, childTop, childLeft + cw, childTop + ch);
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity = -1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}
