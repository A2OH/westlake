package androidx.constraintlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * ConstraintLayout shim — extends FrameLayout.
 * This is still a lightweight shim, but it honors the small set of parent and
 * sibling constraints needed by projected real-app screens to share hit bounds
 * with the visible frame.
 */
public class ConstraintLayout extends FrameLayout {
    public ConstraintLayout(Context context) { super(context); }
    public ConstraintLayout(Context context, AttributeSet attrs) { super(context, attrs); }
    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public static final int UNSET = -1;
        public static final int PARENT_ID = 0;

        public int startToStart = UNSET, startToEnd = UNSET;
        public int endToStart = UNSET, endToEnd = UNSET;
        public int topToTop = UNSET, topToBottom = UNSET;
        public int bottomToTop = UNSET, bottomToBottom = UNSET;
        public float horizontalBias = 0.5f, verticalBias = 0.5f;
        public int matchConstraintDefaultWidth, matchConstraintDefaultHeight;

        public LayoutParams(int width, int height) { super(width, height); }
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            if (attrs != null) {
                for (int i = 0; i < attrs.getAttributeCount(); i++) {
                    String name = attrs.getAttributeName(i);
                    String value = attrs.getAttributeValue(i);
                    if (name == null) {
                        continue;
                    }
                    int id = parseConstraintTarget(c, value);
                    if ("layout_constraintStart_toStartOf".equals(name)) {
                        startToStart = id;
                    } else if ("layout_constraintStart_toEndOf".equals(name)) {
                        startToEnd = id;
                    } else if ("layout_constraintEnd_toStartOf".equals(name)) {
                        endToStart = id;
                    } else if ("layout_constraintEnd_toEndOf".equals(name)) {
                        endToEnd = id;
                    } else if ("layout_constraintTop_toTopOf".equals(name)) {
                        topToTop = id;
                    } else if ("layout_constraintTop_toBottomOf".equals(name)) {
                        topToBottom = id;
                    } else if ("layout_constraintBottom_toTopOf".equals(name)) {
                        bottomToTop = id;
                    } else if ("layout_constraintBottom_toBottomOf".equals(name)) {
                        bottomToBottom = id;
                    } else if ("layout_constraintHorizontal_bias".equals(name)) {
                        horizontalBias = parseFloat(value, horizontalBias);
                    } else if ("layout_constraintVertical_bias".equals(name)) {
                        verticalBias = parseFloat(value, verticalBias);
                    }
                }
            }
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
            gravity = source.gravity;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return lp;
        }
        if (lp instanceof FrameLayout.LayoutParams) {
            return new LayoutParams((FrameLayout.LayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = right - left - getPaddingRight();
        final int parentBottom = bottom - top - getPaddingBottom();
        final int parentWidth = Math.max(0, parentRight - parentLeft);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }
            ViewGroup.LayoutParams raw = child.getLayoutParams();
            if (!(raw instanceof LayoutParams)) {
                super.onLayout(changed, left, top, right, bottom);
                return;
            }
            LayoutParams lp = (LayoutParams) raw;
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            int childLeft = parentLeft + lp.leftMargin;
            int childTop = parentTop + lp.topMargin;

            if (lp.width == 0 && lp.startToStart == LayoutParams.PARENT_ID
                    && lp.endToStart != LayoutParams.UNSET) {
                width = Math.max(width, parentWidth / 2 - lp.leftMargin - lp.rightMargin);
            } else if (lp.width == 0 && lp.startToEnd != LayoutParams.UNSET
                    && lp.endToEnd == LayoutParams.PARENT_ID) {
                width = Math.max(width, parentWidth / 2 - lp.leftMargin - lp.rightMargin);
            } else if (lp.width == 0 && constrainedToParentHorizontally(lp)) {
                width = Math.max(width, parentWidth - lp.leftMargin - lp.rightMargin);
            }

            if (lp.startToEnd != LayoutParams.UNSET) {
                View ref = findViewById(lp.startToEnd);
                childLeft = ref != null ? ref.getRight() + lp.leftMargin
                        : parentLeft + parentWidth / 2 + lp.leftMargin;
            } else if (lp.endToStart != LayoutParams.UNSET) {
                View ref = findViewById(lp.endToStart);
                childLeft = ref != null ? ref.getLeft() - width - lp.rightMargin
                        : parentLeft + lp.leftMargin;
            } else if (lp.endToEnd == LayoutParams.PARENT_ID
                    && lp.startToStart != LayoutParams.PARENT_ID) {
                childLeft = parentRight - width - lp.rightMargin;
            } else if (constrainedToParentHorizontally(lp)
                    && width < parentWidth - lp.leftMargin - lp.rightMargin) {
                int available = parentWidth - width - lp.leftMargin - lp.rightMargin;
                childLeft = parentLeft + lp.leftMargin
                        + Math.round(Math.max(0, available) * lp.horizontalBias);
            }

            if (lp.topToBottom != LayoutParams.UNSET) {
                View ref = findViewById(lp.topToBottom);
                childTop = ref != null ? ref.getBottom() + lp.topMargin : childTop;
            } else if (lp.bottomToTop != LayoutParams.UNSET) {
                View ref = findViewById(lp.bottomToTop);
                childTop = ref != null ? ref.getTop() - height - lp.bottomMargin : childTop;
            } else if (lp.bottomToBottom == LayoutParams.PARENT_ID
                    && lp.topToTop != LayoutParams.PARENT_ID) {
                childTop = parentBottom - height - lp.bottomMargin;
            } else if (lp.topToTop == LayoutParams.PARENT_ID
                    && lp.bottomToBottom == LayoutParams.PARENT_ID) {
                int available = parentBottom - parentTop - height
                        - lp.topMargin - lp.bottomMargin;
                childTop = parentTop + lp.topMargin
                        + Math.round(Math.max(0, available) * lp.verticalBias);
            }

            child.layout(childLeft, childTop, childLeft + width, childTop + height);
        }
    }

    private static boolean constrainedToParentHorizontally(LayoutParams lp) {
        return lp.startToStart == LayoutParams.PARENT_ID
                && lp.endToEnd == LayoutParams.PARENT_ID;
    }

    private static int parseConstraintTarget(Context context, String value) {
        if (value == null) {
            return LayoutParams.UNSET;
        }
        if ("parent".equals(value)) {
            return LayoutParams.PARENT_ID;
        }
        if (value.startsWith("@id/") || value.startsWith("@+id/")) {
            String name = value.substring(value.indexOf('/') + 1);
            try {
                return context.getResources().getIdentifier(name, "id", context.getPackageName());
            } catch (Throwable ignored) {
                return LayoutParams.UNSET;
            }
        }
        if (value.startsWith("@")) {
            value = value.substring(1);
        }
        try {
            if (value.startsWith("0x") || value.startsWith("0X")) {
                return (int) Long.parseLong(value.substring(2), 16);
            }
            return Integer.parseInt(value);
        } catch (Throwable ignored) {
            return LayoutParams.UNSET;
        }
    }

    private static float parseFloat(String value, float fallback) {
        try {
            return value != null ? Float.parseFloat(value) : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }
}
