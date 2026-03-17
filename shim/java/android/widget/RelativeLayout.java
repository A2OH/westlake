package android.widget;

import android.view.View;
import android.view.ViewGroup;
import android.util.ArrayMap;
import android.util.SparseArray;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Shim: android.widget.RelativeLayout -- full constraint-solving layout.
 *
 * Ported from AOSP RelativeLayout. Positions children relative to each other
 * and the parent using rules (ABOVE, BELOW, LEFT_OF, RIGHT_OF, ALIGN_*, CENTER_*).
 *
 * Algorithm:
 * 1. Build dependency graph from children's rules
 * 2. Topological sort for horizontal rules, then vertical rules
 * 3. Two passes: horizontal first (resolve left/right), then vertical (resolve top/bottom)
 * 4. Apply centering and gravity adjustments
 */
public class RelativeLayout extends ViewGroup {

    static final int NODE_TYPE_FLEX = 8;

    // RelativeLayout rule verbs -- must match AOSP constants exactly
    public static final int LEFT_OF             =  0;
    public static final int RIGHT_OF            =  1;
    public static final int ABOVE               =  2;
    public static final int BELOW               =  3;
    public static final int ALIGN_BASELINE      =  4;
    public static final int ALIGN_LEFT          =  5;
    public static final int ALIGN_TOP           =  6;
    public static final int ALIGN_RIGHT         =  7;
    public static final int ALIGN_BOTTOM        =  8;
    public static final int ALIGN_PARENT_LEFT   =  9;
    public static final int ALIGN_PARENT_TOP    = 10;
    public static final int ALIGN_PARENT_RIGHT  = 11;
    public static final int ALIGN_PARENT_BOTTOM = 12;
    public static final int CENTER_IN_PARENT    = 13;
    public static final int CENTER_HORIZONTAL   = 14;
    public static final int CENTER_VERTICAL     = 15;
    public static final int START_OF            = 16;
    public static final int END_OF              = 17;
    public static final int ALIGN_START         = 18;
    public static final int ALIGN_END           = 19;
    public static final int ALIGN_PARENT_START  = 20;
    public static final int ALIGN_PARENT_END    = 21;

    private static final int VERB_COUNT         = 22;

    public static final int TRUE = -1;

    private static final int[] RULES_VERTICAL = {
        ABOVE, BELOW, ALIGN_BASELINE, ALIGN_TOP, ALIGN_BOTTOM
    };

    private static final int[] RULES_HORIZONTAL = {
        LEFT_OF, RIGHT_OF, ALIGN_LEFT, ALIGN_RIGHT, START_OF, END_OF, ALIGN_START, ALIGN_END
    };

    private static final int VALUE_NOT_SET = Integer.MIN_VALUE;

    private boolean mDirtyHierarchy = true;
    private View[] mSortedHorizontalChildren;
    private View[] mSortedVerticalChildren;
    private final DependencyGraph mGraph = new DependencyGraph();
    private int mGravity = 0x800003; // Gravity.START | Gravity.TOP

    public RelativeLayout() {
        super(NODE_TYPE_FLEX);
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public int getGravity() {
        return mGravity;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        mDirtyHierarchy = true;
    }

    private void sortChildren() {
        int count = getChildCount();
        if (mSortedVerticalChildren == null || mSortedVerticalChildren.length != count) {
            mSortedVerticalChildren = new View[count];
        }
        if (mSortedHorizontalChildren == null || mSortedHorizontalChildren.length != count) {
            mSortedHorizontalChildren = new View[count];
        }

        DependencyGraph graph = mGraph;
        graph.clear();

        for (int i = 0; i < count; i++) {
            graph.add(getChildAt(i));
        }

        graph.getSortedViews(mSortedVerticalChildren, RULES_VERTICAL);
        graph.getSortedViews(mSortedHorizontalChildren, RULES_HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDirtyHierarchy) {
            mDirtyHierarchy = false;
            sortChildren();
        }

        int myWidth = -1;
        int myHeight = -1;

        int width = 0;
        int height = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.UNSPECIFIED) {
            myWidth = widthSize;
        }
        if (heightMode != MeasureSpec.UNSPECIFIED) {
            myHeight = heightSize;
        }
        if (widthMode == MeasureSpec.EXACTLY) {
            width = myWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = myHeight;
        }

        boolean offsetHorizontalAxis = false;
        boolean offsetVerticalAxis = false;

        boolean isWrapContentWidth = widthMode != MeasureSpec.EXACTLY;
        boolean isWrapContentHeight = heightMode != MeasureSpec.EXACTLY;

        // Horizontal pass
        View[] views = mSortedHorizontalChildren;
        int count = views.length;

        for (int i = 0; i < count; i++) {
            View child = views[i];
            if (child.getVisibility() != GONE) {
                LayoutParams params = getChildLayoutParams(child);
                int[] rules = params.getRules();

                applyHorizontalSizeRules(params, myWidth, rules);
                measureChildHorizontal(child, params, myWidth, myHeight);

                if (positionChildHorizontal(child, params, myWidth, isWrapContentWidth)) {
                    offsetHorizontalAxis = true;
                }
            }
        }

        // Vertical pass
        views = mSortedVerticalChildren;
        count = views.length;

        for (int i = 0; i < count; i++) {
            View child = views[i];
            if (child.getVisibility() != GONE) {
                LayoutParams params = getChildLayoutParams(child);

                applyVerticalSizeRules(params, myHeight, child.getBaseline());
                measureChild(child, params, myWidth, myHeight);
                if (positionChildVertical(child, params, myHeight, isWrapContentHeight)) {
                    offsetVerticalAxis = true;
                }

                if (isWrapContentWidth) {
                    width = Math.max(width, params.mRight + params.rightMargin);
                }

                if (isWrapContentHeight) {
                    height = Math.max(height, params.mBottom + params.bottomMargin);
                }
            }
        }

        if (isWrapContentWidth) {
            width += getPaddingRight();
            width = Math.max(width, getSuggestedMinimumWidth());
            width = resolveSize(width, widthMeasureSpec);

            if (offsetHorizontalAxis) {
                for (int i = 0; i < count; i++) {
                    View child = views[i];
                    if (child.getVisibility() != GONE) {
                        LayoutParams params = getChildLayoutParams(child);
                        int[] rules = params.getRules();
                        if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_HORIZONTAL] != 0) {
                            centerHorizontal(child, params, width);
                        } else if (rules[ALIGN_PARENT_RIGHT] != 0) {
                            int childWidth = child.getMeasuredWidth();
                            params.mLeft = width - getPaddingRight() - childWidth;
                            params.mRight = params.mLeft + childWidth;
                        }
                    }
                }
            }
        }

        if (isWrapContentHeight) {
            height += getPaddingBottom();
            height = Math.max(height, getSuggestedMinimumHeight());
            height = resolveSize(height, heightMeasureSpec);

            if (offsetVerticalAxis) {
                for (int i = 0; i < count; i++) {
                    View child = views[i];
                    if (child.getVisibility() != GONE) {
                        LayoutParams params = getChildLayoutParams(child);
                        int[] rules = params.getRules();
                        if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_VERTICAL] != 0) {
                            centerVertical(child, params, height);
                        } else if (rules[ALIGN_PARENT_BOTTOM] != 0) {
                            int childHeight = child.getMeasuredHeight();
                            params.mTop = height - getPaddingBottom() - childHeight;
                            params.mBottom = params.mTop + childHeight;
                        }
                    }
                }
            }
        }

        setMeasuredDimension(width, height);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams st = getChildLayoutParams(child);
                child.layout(st.mLeft, st.mTop, st.mRight, st.mBottom);
            }
        }
    }

    /**
     * Convenience: measure + layout in one call (useful for testing).
     */
    public void doLayout(int width, int height) {
        int wSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        mDirtyHierarchy = true;
        measure(wSpec, hSpec);
        layout(0, 0, width, height);
        onLayout(true, 0, 0, width, height);
    }

    // -- Private layout helpers (ported from AOSP) --

    private LayoutParams getChildLayoutParams(View child) {
        Object lp = child.getLayoutParams();
        if (lp instanceof LayoutParams) {
            return (LayoutParams) lp;
        }
        // Fallback: create default params
        LayoutParams p = new LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        child.setLayoutParams(p);
        return p;
    }

    private void applyHorizontalSizeRules(LayoutParams childParams, int myWidth, int[] rules) {
        childParams.mLeft = VALUE_NOT_SET;
        childParams.mRight = VALUE_NOT_SET;

        LayoutParams anchorParams;

        anchorParams = getRelatedViewParams(rules, LEFT_OF);
        if (anchorParams != null) {
            childParams.mRight = anchorParams.mLeft - (anchorParams.leftMargin +
                    childParams.rightMargin);
        }

        anchorParams = getRelatedViewParams(rules, RIGHT_OF);
        if (anchorParams != null) {
            childParams.mLeft = anchorParams.mRight + (anchorParams.rightMargin +
                    childParams.leftMargin);
        }

        anchorParams = getRelatedViewParams(rules, ALIGN_LEFT);
        if (anchorParams != null) {
            childParams.mLeft = anchorParams.mLeft + childParams.leftMargin;
        }

        anchorParams = getRelatedViewParams(rules, ALIGN_RIGHT);
        if (anchorParams != null) {
            childParams.mRight = anchorParams.mRight - childParams.rightMargin;
        }

        if (0 != rules[ALIGN_PARENT_LEFT]) {
            childParams.mLeft = getPaddingLeft() + childParams.leftMargin;
        }

        if (0 != rules[ALIGN_PARENT_RIGHT]) {
            if (myWidth >= 0) {
                childParams.mRight = myWidth - getPaddingRight() - childParams.rightMargin;
            }
        }
    }

    private void applyVerticalSizeRules(LayoutParams childParams, int myHeight, int myBaseline) {
        int[] rules = childParams.getRules();

        // Baseline alignment
        int baselineOffset = getRelatedViewBaselineOffset(rules);
        if (baselineOffset != -1) {
            if (myBaseline != -1) {
                baselineOffset -= myBaseline;
            }
            childParams.mTop = baselineOffset;
            childParams.mBottom = VALUE_NOT_SET;
            return;
        }

        childParams.mTop = VALUE_NOT_SET;
        childParams.mBottom = VALUE_NOT_SET;

        LayoutParams anchorParams;

        anchorParams = getRelatedViewParams(rules, ABOVE);
        if (anchorParams != null) {
            childParams.mBottom = anchorParams.mTop - (anchorParams.topMargin +
                    childParams.bottomMargin);
        }

        anchorParams = getRelatedViewParams(rules, BELOW);
        if (anchorParams != null) {
            childParams.mTop = anchorParams.mBottom + (anchorParams.bottomMargin +
                    childParams.topMargin);
        }

        anchorParams = getRelatedViewParams(rules, ALIGN_TOP);
        if (anchorParams != null) {
            childParams.mTop = anchorParams.mTop + childParams.topMargin;
        }

        anchorParams = getRelatedViewParams(rules, ALIGN_BOTTOM);
        if (anchorParams != null) {
            childParams.mBottom = anchorParams.mBottom - childParams.bottomMargin;
        }

        if (0 != rules[ALIGN_PARENT_TOP]) {
            childParams.mTop = getPaddingTop() + childParams.topMargin;
        }

        if (0 != rules[ALIGN_PARENT_BOTTOM]) {
            if (myHeight >= 0) {
                childParams.mBottom = myHeight - getPaddingBottom() - childParams.bottomMargin;
            }
        }
    }

    private void measureChildHorizontal(View child, LayoutParams params, int myWidth, int myHeight) {
        int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft, params.mRight,
                params.width, params.leftMargin, params.rightMargin,
                getPaddingLeft(), getPaddingRight(), myWidth);

        int childHeightMeasureSpec;
        if (myHeight < 0) {
            if (params.height >= 0) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        params.height, MeasureSpec.EXACTLY);
            } else {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
        } else {
            int maxHeight = Math.max(0, myHeight - getPaddingTop() - getPaddingBottom()
                    - params.topMargin - params.bottomMargin);
            int heightMode;
            if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                heightMode = MeasureSpec.EXACTLY;
            } else {
                heightMode = MeasureSpec.AT_MOST;
            }
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, heightMode);
        }

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private void measureChild(View child, LayoutParams params, int myWidth, int myHeight) {
        int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft,
                params.mRight, params.width,
                params.leftMargin, params.rightMargin,
                getPaddingLeft(), getPaddingRight(), myWidth);
        int childHeightMeasureSpec = getChildMeasureSpec(params.mTop,
                params.mBottom, params.height,
                params.topMargin, params.bottomMargin,
                getPaddingTop(), getPaddingBottom(), myHeight);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private int getChildMeasureSpec(int childStart, int childEnd,
            int childSize, int startMargin, int endMargin, int startPadding,
            int endPadding, int mySize) {
        int childSpecMode = 0;
        int childSpecSize = 0;

        boolean isUnspecified = mySize < 0;
        if (isUnspecified) {
            if (childStart != VALUE_NOT_SET && childEnd != VALUE_NOT_SET) {
                childSpecSize = Math.max(0, childEnd - childStart);
                childSpecMode = MeasureSpec.EXACTLY;
            } else if (childSize >= 0) {
                childSpecSize = childSize;
                childSpecMode = MeasureSpec.EXACTLY;
            } else {
                childSpecSize = 0;
                childSpecMode = MeasureSpec.UNSPECIFIED;
            }
            return MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
        }

        int tempStart = childStart;
        int tempEnd = childEnd;

        if (tempStart == VALUE_NOT_SET) {
            tempStart = startPadding + startMargin;
        }
        if (tempEnd == VALUE_NOT_SET) {
            tempEnd = mySize - endPadding - endMargin;
        }

        int maxAvailable = tempEnd - tempStart;

        if (childStart != VALUE_NOT_SET && childEnd != VALUE_NOT_SET) {
            childSpecMode = isUnspecified ? MeasureSpec.UNSPECIFIED : MeasureSpec.EXACTLY;
            childSpecSize = Math.max(0, maxAvailable);
        } else {
            if (childSize >= 0) {
                childSpecMode = MeasureSpec.EXACTLY;
                if (maxAvailable >= 0) {
                    childSpecSize = Math.min(maxAvailable, childSize);
                } else {
                    childSpecSize = childSize;
                }
            } else if (childSize == ViewGroup.LayoutParams.MATCH_PARENT) {
                childSpecMode = isUnspecified ? MeasureSpec.UNSPECIFIED : MeasureSpec.EXACTLY;
                childSpecSize = Math.max(0, maxAvailable);
            } else if (childSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
                if (maxAvailable >= 0) {
                    childSpecMode = MeasureSpec.AT_MOST;
                    childSpecSize = maxAvailable;
                } else {
                    childSpecMode = MeasureSpec.UNSPECIFIED;
                    childSpecSize = 0;
                }
            }
        }

        return MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
    }

    private boolean positionChildHorizontal(View child, LayoutParams params, int myWidth,
            boolean wrapContent) {
        int[] rules = params.getRules();

        if (params.mLeft == VALUE_NOT_SET && params.mRight != VALUE_NOT_SET) {
            params.mLeft = params.mRight - child.getMeasuredWidth();
        } else if (params.mLeft != VALUE_NOT_SET && params.mRight == VALUE_NOT_SET) {
            params.mRight = params.mLeft + child.getMeasuredWidth();
        } else if (params.mLeft == VALUE_NOT_SET && params.mRight == VALUE_NOT_SET) {
            if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_HORIZONTAL] != 0) {
                if (!wrapContent) {
                    centerHorizontal(child, params, myWidth);
                } else {
                    params.mLeft = getPaddingLeft() + params.leftMargin;
                    params.mRight = params.mLeft + child.getMeasuredWidth();
                }
                return true;
            } else {
                params.mLeft = getPaddingLeft() + params.leftMargin;
                params.mRight = params.mLeft + child.getMeasuredWidth();
            }
        }
        return rules[ALIGN_PARENT_END] != 0;
    }

    private boolean positionChildVertical(View child, LayoutParams params, int myHeight,
            boolean wrapContent) {
        int[] rules = params.getRules();

        if (params.mTop == VALUE_NOT_SET && params.mBottom != VALUE_NOT_SET) {
            params.mTop = params.mBottom - child.getMeasuredHeight();
        } else if (params.mTop != VALUE_NOT_SET && params.mBottom == VALUE_NOT_SET) {
            params.mBottom = params.mTop + child.getMeasuredHeight();
        } else if (params.mTop == VALUE_NOT_SET && params.mBottom == VALUE_NOT_SET) {
            if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_VERTICAL] != 0) {
                if (!wrapContent) {
                    centerVertical(child, params, myHeight);
                } else {
                    params.mTop = getPaddingTop() + params.topMargin;
                    params.mBottom = params.mTop + child.getMeasuredHeight();
                }
                return true;
            } else {
                params.mTop = getPaddingTop() + params.topMargin;
                params.mBottom = params.mTop + child.getMeasuredHeight();
            }
        }
        return rules[ALIGN_PARENT_BOTTOM] != 0;
    }

    private View getRelatedView(int[] rules, int relation) {
        int id = rules[relation];
        if (id != 0) {
            DependencyGraph.Node node = mGraph.mKeyNodes.get(id);
            if (node == null) return null;
            View v = node.view;

            // Find the first non-GONE view up the chain
            while (v.getVisibility() == View.GONE) {
                Object lp = v.getLayoutParams();
                if (!(lp instanceof LayoutParams)) return null;
                int[] vRules = ((LayoutParams) lp).getRules();
                node = mGraph.mKeyNodes.get(vRules[relation]);
                if (node == null || v == node.view) return null;
                v = node.view;
            }

            return v;
        }
        return null;
    }

    private LayoutParams getRelatedViewParams(int[] rules, int relation) {
        View v = getRelatedView(rules, relation);
        if (v != null) {
            Object params = v.getLayoutParams();
            if (params instanceof LayoutParams) {
                return (LayoutParams) params;
            }
        }
        return null;
    }

    private int getRelatedViewBaselineOffset(int[] rules) {
        View v = getRelatedView(rules, ALIGN_BASELINE);
        if (v != null) {
            int baseline = v.getBaseline();
            if (baseline != -1) {
                Object params = v.getLayoutParams();
                if (params instanceof LayoutParams) {
                    LayoutParams anchorParams = (LayoutParams) params;
                    return anchorParams.mTop + baseline;
                }
            }
        }
        return -1;
    }

    private static void centerHorizontal(View child, LayoutParams params, int myWidth) {
        int childWidth = child.getMeasuredWidth();
        int left = (myWidth - childWidth) / 2;
        params.mLeft = left;
        params.mRight = left + childWidth;
    }

    private static void centerVertical(View child, LayoutParams params, int myHeight) {
        int childHeight = child.getMeasuredHeight();
        int top = (myHeight - childHeight) / 2;
        params.mTop = top;
        params.mBottom = top + childHeight;
    }

    // -- LayoutParams --

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        private int[] mRules = new int[VERB_COUNT];

        // Computed positions (package-private for RelativeLayout access)
        int mLeft;
        int mTop;
        int mRight;
        int mBottom;

        /**
         * When true, uses the parent as the anchor if the anchor doesn't exist or if
         * the anchor's visibility is GONE.
         */
        public boolean alignWithParent;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source.width, source.height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.alignWithParent = source.alignWithParent;
            System.arraycopy(source.mRules, 0, this.mRules, 0, VERB_COUNT);
        }

        /**
         * Adds a layout rule to be interpreted by the RelativeLayout.
         * For boolean-type rules (e.g. ALIGN_PARENT_LEFT, CENTER_IN_PARENT).
         */
        public void addRule(int verb) {
            addRule(verb, TRUE);
        }

        /**
         * Adds a layout rule with a subject (view id or TRUE/-1 for booleans).
         */
        public void addRule(int verb, int subject) {
            if (verb >= 0 && verb < VERB_COUNT) {
                mRules[verb] = subject;
            }
        }

        public void removeRule(int verb) {
            addRule(verb, 0);
        }

        public int getRule(int verb) {
            if (verb >= 0 && verb < VERB_COUNT) {
                return mRules[verb];
            }
            return 0;
        }

        /**
         * Returns the rules array. The index is the rule verb constant,
         * and the value is the subject (view id or TRUE).
         */
        public int[] getRules() {
            return mRules;
        }

        /**
         * Returns the rules array resolved for a given layout direction.
         * In this shim we always treat as LTR, so START=LEFT, END=RIGHT.
         */
        public int[] getRules(int layoutDirection) {
            // Resolve START/END to LEFT/RIGHT for LTR
            if (mRules[ALIGN_START] != 0 && mRules[ALIGN_LEFT] == 0) {
                mRules[ALIGN_LEFT] = mRules[ALIGN_START];
            }
            if (mRules[ALIGN_END] != 0 && mRules[ALIGN_RIGHT] == 0) {
                mRules[ALIGN_RIGHT] = mRules[ALIGN_END];
            }
            if (mRules[START_OF] != 0 && mRules[LEFT_OF] == 0) {
                mRules[LEFT_OF] = mRules[START_OF];
            }
            if (mRules[END_OF] != 0 && mRules[RIGHT_OF] == 0) {
                mRules[RIGHT_OF] = mRules[END_OF];
            }
            if (mRules[ALIGN_PARENT_START] != 0 && mRules[ALIGN_PARENT_LEFT] == 0) {
                mRules[ALIGN_PARENT_LEFT] = mRules[ALIGN_PARENT_START];
            }
            if (mRules[ALIGN_PARENT_END] != 0 && mRules[ALIGN_PARENT_RIGHT] == 0) {
                mRules[ALIGN_PARENT_RIGHT] = mRules[ALIGN_PARENT_END];
            }
            return mRules;
        }

        public boolean hasRule(int verb) {
            return verb >= 0 && verb < VERB_COUNT && mRules[verb] != 0;
        }
    }

    // -- DependencyGraph: topological sort of children by rule dependencies --

    private static class DependencyGraph {
        private ArrayList<Node> mNodes = new ArrayList<Node>();
        SparseArray<Node> mKeyNodes = new SparseArray<Node>();
        private ArrayDeque<Node> mRoots = new ArrayDeque<Node>();

        void clear() {
            ArrayList<Node> nodes = mNodes;
            int count = nodes.size();
            for (int i = 0; i < count; i++) {
                nodes.get(i).release();
            }
            nodes.clear();
            mKeyNodes.clear();
            mRoots.clear();
        }

        void add(View view) {
            int id = view.getId();
            Node node = Node.acquire(view);
            if (id != View.NO_ID) {
                mKeyNodes.put(id, node);
            }
            mNodes.add(node);
        }

        void getSortedViews(View[] sorted, int... rules) {
            ArrayDeque<Node> roots = findRoots(rules);
            int index = 0;

            Node node;
            while ((node = roots.pollLast()) != null) {
                View view = node.view;
                int key = view.getId();
                sorted[index++] = view;

                ArrayMap<Node, DependencyGraph> dependents = node.dependents;
                int count = dependents.size();
                for (int i = 0; i < count; i++) {
                    Node dependent = dependents.keyAt(i);
                    SparseArray<Node> dependencies = dependent.dependencies;
                    dependencies.remove(key);
                    if (dependencies.size() == 0) {
                        roots.add(dependent);
                    }
                }
            }

            if (index < sorted.length) {
                throw new IllegalStateException("Circular dependencies cannot exist"
                        + " in RelativeLayout");
            }
        }

        private ArrayDeque<Node> findRoots(int[] rulesFilter) {
            SparseArray<Node> keyNodes = mKeyNodes;
            ArrayList<Node> nodes = mNodes;
            int count = nodes.size();

            // Clear all dependents and dependencies
            for (int i = 0; i < count; i++) {
                Node node = nodes.get(i);
                node.dependents.clear();
                node.dependencies.clear();
            }

            // Build up dependents and dependencies for each node
            for (int i = 0; i < count; i++) {
                Node node = nodes.get(i);
                Object lp = node.view.getLayoutParams();
                if (!(lp instanceof LayoutParams)) continue;

                int[] nodeRules = ((LayoutParams) lp).getRules();
                int rulesCount = rulesFilter.length;

                for (int j = 0; j < rulesCount; j++) {
                    int rule = nodeRules[rulesFilter[j]];
                    if (rule > 0) {
                        Node dependency = keyNodes.get(rule);
                        if (dependency == null || dependency == node) {
                            continue;
                        }
                        dependency.dependents.put(node, DependencyGraph.this);
                        node.dependencies.put(rule, dependency);
                    }
                }
            }

            ArrayDeque<Node> roots = mRoots;
            roots.clear();

            for (int i = 0; i < count; i++) {
                Node node = nodes.get(i);
                if (node.dependencies.size() == 0) {
                    roots.addLast(node);
                }
            }

            return roots;
        }

        static class Node {
            View view;
            final ArrayMap<Node, DependencyGraph> dependents =
                    new ArrayMap<Node, DependencyGraph>();
            final SparseArray<Node> dependencies = new SparseArray<Node>();

            static Node acquire(View view) {
                Node node = new Node();
                node.view = view;
                return node;
            }

            void release() {
                view = null;
                dependents.clear();
                dependencies.clear();
            }
        }
    }
}
