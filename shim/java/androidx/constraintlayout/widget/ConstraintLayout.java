package androidx.constraintlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * ConstraintLayout shim — extends FrameLayout.
 * Real ConstraintLayout does constraint-based layout; our shim just stacks children.
 */
public class ConstraintLayout extends FrameLayout {
    public ConstraintLayout(Context context) { super(context); }
    public ConstraintLayout(Context context, AttributeSet attrs) { super(context, attrs); }
    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public int startToStart, startToEnd, endToStart, endToEnd;
        public int topToTop, topToBottom, bottomToTop, bottomToBottom;
        public float horizontalBias = 0.5f, verticalBias = 0.5f;
        public int matchConstraintDefaultWidth, matchConstraintDefaultHeight;

        public LayoutParams(int width, int height) { super(width, height); }
        public LayoutParams(Context c, AttributeSet attrs) { super(c, attrs); }
    }
}
