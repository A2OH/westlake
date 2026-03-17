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
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = 0, maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
        }
        int specW = android.view.View.MeasureSpec.getMode(widthMeasureSpec) == android.view.View.MeasureSpec.EXACTLY
            ? android.view.View.MeasureSpec.getSize(widthMeasureSpec)
            : maxWidth + getPaddingLeft() + getPaddingRight();
        int specH = android.view.View.MeasureSpec.getMode(heightMeasureSpec) == android.view.View.MeasureSpec.EXACTLY
            ? android.view.View.MeasureSpec.getSize(heightMeasureSpec)
            : maxHeight + getPaddingTop() + getPaddingBottom();
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
            int cw = child.getMeasuredWidth() > 0 ? child.getMeasuredWidth() : parentWidth;
            int ch = child.getMeasuredHeight() > 0 ? child.getMeasuredHeight() : parentHeight;

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
