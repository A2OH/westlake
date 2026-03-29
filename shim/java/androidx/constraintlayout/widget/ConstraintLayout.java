/*
 * Minimal ConstraintLayout shim for Android-on-OpenHarmony.
 * Clean-room implementation -- no real ConstraintLayout solver code.
 * Supports parent-relative and basic sibling-relative positioning.
 */
package androidx.constraintlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A minimal ConstraintLayout that positions children based on constraint fields
 * in {@link LayoutParams}. Supports:
 * <ul>
 *   <li>Parent-relative constraints (align/center to parent edges)</li>
 *   <li>Sibling-relative constraints (position relative to another child)</li>
 *   <li>MATCH_CONSTRAINT (0dp) width/height -- stretch to fill constraints</li>
 *   <li>Horizontal and vertical bias for centered views</li>
 * </ul>
 *
 * Not supported: chains, barriers, guidelines, groups, circular constraints.
 */
public class ConstraintLayout extends ViewGroup {

    public ConstraintLayout(Context context) {
        super(context);
    }

    public ConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ---------------------------------------------------------------
    // LayoutParams
    // ---------------------------------------------------------------

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        /** Sentinel: constraint not set. */
        public static final int UNSET = -1;

        /** Pseudo-ID meaning "constrain to parent edge". */
        public static final int PARENT_ID = 0;

        /**
         * Value used in layout_width / layout_height to mean
         * "size is determined by constraints" (the 0dp case).
         */
        public static final int MATCH_CONSTRAINT = 0;

        // Horizontal constraints (left/right)
        public int leftToLeft = UNSET;
        public int leftToRight = UNSET;
        public int rightToLeft = UNSET;
        public int rightToRight = UNSET;

        // Vertical constraints (top/bottom)
        public int topToTop = UNSET;
        public int topToBottom = UNSET;
        public int bottomToTop = UNSET;
        public int bottomToBottom = UNSET;

        // Start/end aliases (treated as left/right in LTR)
        public int startToStart = UNSET;
        public int startToEnd = UNSET;
        public int endToStart = UNSET;
        public int endToEnd = UNSET;

        // Bias: 0.0 = fully toward start/top, 1.0 = fully toward end/bottom
        public float horizontalBias = 0.5f;
        public float verticalBias = 0.5f;

        // Match-constraint default behavior (0 = spread)
        public int matchConstraintDefaultWidth = 0;
        public int matchConstraintDefaultHeight = 0;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            leftToLeft = source.leftToLeft;
            leftToRight = source.leftToRight;
            rightToLeft = source.rightToLeft;
            rightToRight = source.rightToRight;
            topToTop = source.topToTop;
            topToBottom = source.topToBottom;
            bottomToTop = source.bottomToTop;
            bottomToBottom = source.bottomToBottom;
            startToStart = source.startToStart;
            startToEnd = source.startToEnd;
            endToStart = source.endToStart;
            endToEnd = source.endToEnd;
            horizontalBias = source.horizontalBias;
            verticalBias = source.verticalBias;
            matchConstraintDefaultWidth = source.matchConstraintDefaultWidth;
            matchConstraintDefaultHeight = source.matchConstraintDefaultHeight;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            // XML attribute parsing would go here; for programmatic use
            // callers set the constraint fields directly.
        }

        /**
         * Resolve start/end into left/right. Call before layout.
         * Only handles LTR for now.
         */
        void resolveStartEnd() {
            if (startToStart != UNSET && leftToLeft == UNSET) leftToLeft = startToStart;
            if (startToEnd != UNSET && leftToRight == UNSET) leftToRight = startToEnd;
            if (endToStart != UNSET && rightToLeft == UNSET) rightToLeft = endToStart;
            if (endToEnd != UNSET && rightToRight == UNSET) rightToRight = endToEnd;
        }
    }

    // ---------------------------------------------------------------
    // LayoutParams plumbing
    // ---------------------------------------------------------------

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) p);
        } else if (p instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    // ---------------------------------------------------------------
    // Measurement
    // ---------------------------------------------------------------

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int paddingH = getPaddingLeft() + getPaddingRight();
        int paddingV = getPaddingTop() + getPaddingBottom();

        int maxChildRight = 0;
        int maxChildBottom = 0;

        // First pass: measure every visible child with an initial spec.
        // Children with MATCH_CONSTRAINT (0dp) get measured as WRAP_CONTENT
        // for now; they will be re-measured during layout once we know the
        // actual constraint bounds.
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.resolveStartEnd();

            int childWidthSpec = makeChildMeasureSpec(
                    widthSize - paddingH, lp.width, lp.leftMargin + lp.rightMargin);
            int childHeightSpec = makeChildMeasureSpec(
                    heightSize - paddingV, lp.height, lp.topMargin + lp.bottomMargin);

            child.measure(childWidthSpec, childHeightSpec);

            // Track max extent for wrap_content sizing
            maxChildRight = Math.max(maxChildRight,
                    lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin);
            maxChildBottom = Math.max(maxChildBottom,
                    lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin);
        }

        int resolvedWidth;
        if (widthMode == MeasureSpec.EXACTLY) {
            resolvedWidth = widthSize;
        } else {
            resolvedWidth = maxChildRight + paddingH;
            if (widthMode == MeasureSpec.AT_MOST) {
                resolvedWidth = Math.min(resolvedWidth, widthSize);
            }
        }

        int resolvedHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            resolvedHeight = heightSize;
        } else {
            resolvedHeight = maxChildBottom + paddingV;
            if (heightMode == MeasureSpec.AT_MOST) {
                resolvedHeight = Math.min(resolvedHeight, heightSize);
            }
        }

        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    /**
     * Build a child MeasureSpec.  For MATCH_CONSTRAINT (0) we measure as
     * AT_MOST(parentAvailable) so the child reports its desired size; the
     * actual constrained size is applied in {@link #onLayout}.
     */
    private static int makeChildMeasureSpec(int parentAvailable, int childDimension, int margins) {
        int available = Math.max(0, parentAvailable - margins);
        if (childDimension > 0) {
            // Exact size in pixels
            return MeasureSpec.makeMeasureSpec(childDimension, MeasureSpec.EXACTLY);
        } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
            return MeasureSpec.makeMeasureSpec(available, MeasureSpec.EXACTLY);
        } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST);
        } else {
            // 0 == MATCH_CONSTRAINT -- measure as wrap for now
            return MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST);
        }
    }

    // ---------------------------------------------------------------
    // Layout
    // ---------------------------------------------------------------

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int parentLeft = getPaddingLeft();
        int parentTop = getPaddingTop();
        int parentRight = r - l - getPaddingRight();
        int parentBottom = b - t - getPaddingBottom();

        int count = getChildCount();

        // Resolved positions: left, top, right, bottom for each child index.
        // -1 means "not yet resolved".
        int[] childLeft = new int[count];
        int[] childTop = new int[count];
        int[] childRight = new int[count];
        int[] childBottom = new int[count];
        boolean[] resolved = new boolean[count];

        for (int i = 0; i < count; i++) {
            childLeft[i] = -1;
            childTop[i] = -1;
            childRight[i] = -1;
            childBottom[i] = -1;
        }

        // Iterative resolution: try to resolve each child.  Repeat until
        // all are resolved or no further progress is made (handles simple
        // dependency ordering without topological sort).
        int resolvedCount = 0;
        int prevResolved;
        do {
            prevResolved = resolvedCount;
            for (int i = 0; i < count; i++) {
                if (resolved[i]) continue;
                View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    resolved[i] = true;
                    resolvedCount++;
                    continue;
                }

                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                // --- Resolve horizontal ---
                int hLeft = resolveEdge(lp.leftToLeft, lp.leftToRight,
                        true, true, parentLeft, parentRight,
                        count, childLeft, childRight, resolved);
                int hRight = resolveEdge(lp.rightToRight, lp.rightToLeft,
                        false, true, parentLeft, parentRight,
                        count, childLeft, childRight, resolved);

                // --- Resolve vertical ---
                int vTop = resolveEdge(lp.topToTop, lp.topToBottom,
                        true, false, parentTop, parentBottom,
                        count, childTop, childBottom, resolved);
                int vBottom = resolveEdge(lp.bottomToBottom, lp.bottomToTop,
                        false, false, parentTop, parentBottom,
                        count, childTop, childBottom, resolved);

                // Check if we could resolve all needed edges.
                // An edge is "needed" only if a constraint references it.
                boolean hReady = (hLeft != Integer.MIN_VALUE || !needsEdge(lp.leftToLeft, lp.leftToRight))
                        && (hRight != Integer.MIN_VALUE || !needsEdge(lp.rightToRight, lp.rightToLeft));
                boolean vReady = (vTop != Integer.MIN_VALUE || !needsEdge(lp.topToTop, lp.topToBottom))
                        && (vBottom != Integer.MIN_VALUE || !needsEdge(lp.bottomToBottom, lp.bottomToTop));

                if (!hReady || !vReady) continue; // dependency not yet resolved

                // ---- Horizontal position ----
                int cw = child.getMeasuredWidth();
                boolean matchW = (lp.width == LayoutParams.MATCH_CONSTRAINT);
                int cl, cr;

                if (hLeft != Integer.MIN_VALUE && hRight != Integer.MIN_VALUE) {
                    // Constrained on both sides
                    int leftEdge = hLeft + lp.leftMargin;
                    int rightEdge = hRight - lp.rightMargin;
                    if (matchW) {
                        // Stretch to fill
                        cl = leftEdge;
                        cr = rightEdge;
                        cw = cr - cl;
                        if (cw < 0) cw = 0;
                    } else {
                        // Center (with bias) between the two edges
                        int space = rightEdge - leftEdge - cw;
                        int offset = (int) (space * lp.horizontalBias);
                        cl = leftEdge + offset;
                        cr = cl + cw;
                    }
                } else if (hLeft != Integer.MIN_VALUE) {
                    cl = hLeft + lp.leftMargin;
                    cr = cl + cw;
                } else if (hRight != Integer.MIN_VALUE) {
                    cr = hRight - lp.rightMargin;
                    cl = cr - cw;
                } else {
                    // No horizontal constraint -- default to parent left
                    cl = parentLeft + lp.leftMargin;
                    cr = cl + cw;
                }

                // ---- Vertical position ----
                int ch = child.getMeasuredHeight();
                boolean matchH = (lp.height == LayoutParams.MATCH_CONSTRAINT);
                int ct, cb;

                if (vTop != Integer.MIN_VALUE && vBottom != Integer.MIN_VALUE) {
                    int topEdge = vTop + lp.topMargin;
                    int bottomEdge = vBottom - lp.bottomMargin;
                    if (matchH) {
                        ct = topEdge;
                        cb = bottomEdge;
                        ch = cb - ct;
                        if (ch < 0) ch = 0;
                    } else {
                        int space = bottomEdge - topEdge - ch;
                        int offset = (int) (space * lp.verticalBias);
                        ct = topEdge + offset;
                        cb = ct + ch;
                    }
                } else if (vTop != Integer.MIN_VALUE) {
                    ct = vTop + lp.topMargin;
                    cb = ct + ch;
                } else if (vBottom != Integer.MIN_VALUE) {
                    cb = vBottom - lp.bottomMargin;
                    ct = cb - ch;
                } else {
                    ct = parentTop + lp.topMargin;
                    cb = ct + ch;
                }

                // If MATCH_CONSTRAINT changed the size, re-measure the child
                // so it knows its real dimensions.
                if (matchW || matchH) {
                    int newW = matchW ? Math.max(0, cr - cl) : cw;
                    int newH = matchH ? Math.max(0, cb - ct) : ch;
                    child.measure(
                            MeasureSpec.makeMeasureSpec(newW, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(newH, MeasureSpec.EXACTLY));
                }

                childLeft[i] = cl;
                childTop[i] = ct;
                childRight[i] = cr;
                childBottom[i] = cb;
                resolved[i] = true;
                resolvedCount++;
            }
        } while (resolvedCount > prevResolved && resolvedCount < count);

        // Place all resolved children; anything still unresolved goes at (0,0).
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            if (resolved[i]) {
                child.layout(childLeft[i], childTop[i], childRight[i], childBottom[i]);
            } else {
                // Fallback -- unresolved (circular or missing dependency)
                int cw = child.getMeasuredWidth();
                int ch = child.getMeasuredHeight();
                child.layout(parentLeft, parentTop, parentLeft + cw, parentTop + ch);
            }
        }
    }

    /**
     * Resolve one edge of a child.
     *
     * @param sameEdgeId   constraint to the same edge of another view
     *                     (e.g. leftToLeft or rightToRight)
     * @param crossEdgeId  constraint to the opposite edge of another view
     *                     (e.g. leftToRight or rightToLeft)
     * @param isStartEdge  true if resolving the start edge (left/top)
     * @param isHorizontal true for horizontal, false for vertical
     * @param parentStart  parent content area start (left or top)
     * @param parentEnd    parent content area end (right or bottom)
     * @param childCount   total number of children
     * @param starts       resolved start positions of all children
     * @param ends         resolved end positions of all children
     * @param resolved     which children are resolved
     * @return the resolved pixel position, or Integer.MIN_VALUE if not yet resolvable
     */
    private int resolveEdge(int sameEdgeId, int crossEdgeId,
                            boolean isStartEdge, boolean isHorizontal,
                            int parentStart, int parentEnd,
                            int childCount, int[] starts, int[] ends, boolean[] resolved) {

        // Try same-edge constraint first (higher priority)
        if (sameEdgeId != LayoutParams.UNSET) {
            if (sameEdgeId == LayoutParams.PARENT_ID) {
                return isStartEdge ? parentStart : parentEnd;
            }
            int idx = findChildIndexById(sameEdgeId, childCount);
            if (idx >= 0 && resolved[idx]) {
                return isStartEdge ? starts[idx] : ends[idx];
            }
            if (idx >= 0) return Integer.MIN_VALUE; // not yet resolved
            // ID not found -- ignore this constraint
        }

        // Try cross-edge constraint
        if (crossEdgeId != LayoutParams.UNSET) {
            if (crossEdgeId == LayoutParams.PARENT_ID) {
                // leftToRight=PARENT means anchor left to parent's right,
                // rightToLeft=PARENT means anchor right to parent's left.
                return isStartEdge ? parentEnd : parentStart;
            }
            int idx = findChildIndexById(crossEdgeId, childCount);
            if (idx >= 0 && resolved[idx]) {
                // Cross: the opposite edge of the target
                return isStartEdge ? ends[idx] : starts[idx];
            }
            if (idx >= 0) return Integer.MIN_VALUE;
        }

        return Integer.MIN_VALUE; // no constraint on this edge
    }

    /** Whether at least one of the two constraint IDs is set. */
    private static boolean needsEdge(int sameEdgeId, int crossEdgeId) {
        return sameEdgeId != LayoutParams.UNSET || crossEdgeId != LayoutParams.UNSET;
    }

    /**
     * Find the child index for a given view ID.
     * Returns -1 if no child has that ID.
     */
    private int findChildIndexById(int id, int count) {
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getId() == id) return i;
        }
        return -1;
    }
}
