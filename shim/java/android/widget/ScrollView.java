package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ScrollView → ARKUI_NODE_SCROLL
 *
 * ArkUI Scroll is a single-child scrollable container.
 * Only the first child is used (matching Android ScrollView behavior).
 *
 * AOSP features extracted:
 *   - onInterceptTouchEvent: intercept vertical scroll gestures
 *   - onTouchEvent: scroll tracking with velocity-like drag
 *   - scrollTo clamping: don't scroll past content bounds
 *   - fillViewport: stretch child to fill viewport when content is smaller
 *   - computeScroll / fling: basic fling support via velocity accumulation
 */
public class ScrollView extends FrameLayout {
    static final int NODE_TYPE_SCROLL = 9;
    static final int ATTR_SCROLL_BAR_DISPLAY = 9000;
    static final int ATTR_SCROLL_OFFSET = 9001;

    // Scroll indicator colors
    private static final int SCROLL_INDICATOR_COLOR = 0x66888888; // semi-transparent gray

    // Touch slop: minimum drag distance to start scrolling
    private static final int TOUCH_SLOP = 8;

    // Scroll state
    private boolean mIsBeingDragged;
    private float mLastMotionY;
    private float mInitialMotionY;
    private int mActivePointerId = -1;

    // Fling state (simplified: no real timer, but stores velocity)
    private float mVelocityY;
    private boolean mFlingActive;
    private int mFlingTargetY;

    // fillViewport
    private boolean mFillViewport;

    // smoothScrollTo flag
    private boolean mSmoothScrollingEnabled = true;

    public ScrollView() {
        super();
    }

    public ScrollView(android.content.Context context) {
        this();
    }

    public ScrollView(android.content.Context context, android.util.AttributeSet attrs) {
        this();
    }

    public ScrollView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        this();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  fillViewport
    // ════════════════════════════════════════════════════════════════════════

    public void setFillViewport(boolean fillViewport) {
        if (mFillViewport != fillViewport) {
            mFillViewport = fillViewport;
            requestLayout();
        }
    }

    public boolean isFillViewport() { return mFillViewport; }

    // ════════════════════════════════════════════════════════════════════════
    //  Scrollbar control
    // ════════════════════════════════════════════════════════════════════════

    /** Control scrollbar visibility */
    public void setScrollBarStyle(int style) {
        // 0=OFF, 1=AUTO, 2=ON
    }

    public void setVerticalScrollBarEnabled(boolean enabled) {
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_SCROLL_BAR_DISPLAY,
                enabled ? 1 : 0);
        }
    }

    public void setSmoothScrollingEnabled(boolean enabled) {
        mSmoothScrollingEnabled = enabled;
    }

    public boolean isSmoothScrollingEnabled() { return mSmoothScrollingEnabled; }

    // ════════════════════════════════════════════════════════════════════════
    //  Scroll range helpers
    // ════════════════════════════════════════════════════════════════════════

    /** Returns the total scroll range (content height - viewport height). */
    private int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            int childHeight = child.getHeight();
            if (childHeight == 0) childHeight = child.getMeasuredHeight();
            int viewportHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            scrollRange = Math.max(0, childHeight - viewportHeight);
        }
        return scrollRange;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Touch handling (AOSP pattern)
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        // Don't intercept if no scrollable content
        if (getScrollRange() <= 0) {
            mIsBeingDragged = false;
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionY = event.getY();
                mInitialMotionY = event.getY();
                mActivePointerId = event.getPointerId(0);
                mIsBeingDragged = false;
                // Stop any fling in progress
                mFlingActive = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId < 0) break;
                float y = event.getY();
                float yDiff = Math.abs(y - mInitialMotionY);
                if (yDiff > TOUCH_SLOP) {
                    mIsBeingDragged = true;
                    mLastMotionY = y;
                    // Disable parent intercept
                    android.view.ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointerId = -1;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getScrollRange() <= 0) {
            return false;
        }

        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionY = event.getY();
                mInitialMotionY = event.getY();
                mActivePointerId = event.getPointerId(0);
                mFlingActive = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId < 0) break;
                float y = event.getY();

                if (!mIsBeingDragged) {
                    float yDiff = Math.abs(y - mInitialMotionY);
                    if (yDiff > TOUCH_SLOP) {
                        mIsBeingDragged = true;
                        mLastMotionY = y;
                    }
                }

                if (mIsBeingDragged) {
                    float deltaY = mLastMotionY - y;
                    mLastMotionY = y;

                    // Track velocity (simplified)
                    mVelocityY = -deltaY;

                    // Scroll, clamped to range
                    int newScrollY = getScrollY() + (int) deltaY;
                    int maxScroll = getScrollRange();
                    if (newScrollY < 0) newScrollY = 0;
                    if (newScrollY > maxScroll) newScrollY = maxScroll;

                    scrollTo(0, newScrollY);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    // Start fling if velocity is sufficient
                    if (Math.abs(mVelocityY) > 50) {
                        fling((int) -mVelocityY);
                    }
                }
                mIsBeingDragged = false;
                mActivePointerId = -1;
                mVelocityY = 0;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                mActivePointerId = -1;
                mVelocityY = 0;
                break;
            }
        }

        return true;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Scroll / fling
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Start a fling with the given initial velocity (pixels per frame, roughly).
     * Simplified: immediately sets target and scrolls (no animation timer).
     */
    public void fling(int velocityY) {
        int maxScroll = getScrollRange();
        if (maxScroll <= 0) return;

        // Simplified fling: compute target position from velocity
        int startY = getScrollY();
        // Rough distance = velocity * deceleration factor
        int distance = velocityY / 2;
        int targetY = startY + distance;

        // Clamp
        if (targetY < 0) targetY = 0;
        if (targetY > maxScroll) targetY = maxScroll;

        mFlingActive = true;
        mFlingTargetY = targetY;

        // Immediately scroll (no animation in headless)
        scrollTo(0, targetY);
        mFlingActive = false;
    }

    public void smoothScrollTo(int x, int y) {
        int maxScroll = getScrollRange();
        if (y < 0) y = 0;
        if (y > maxScroll) y = maxScroll;
        scrollTo(x, y);
    }

    public void smoothScrollBy(int dx, int dy) {
        smoothScrollTo(getScrollX() + dx, getScrollY() + dy);
    }

    /** Full scroll in the given direction. */
    public boolean fullScroll(int direction) {
        // direction: View.FOCUS_UP = 33, View.FOCUS_DOWN = 130
        if (direction == 33) { // FOCUS_UP
            scrollTo(0, 0);
        } else {
            scrollTo(0, getScrollRange());
        }
        return true;
    }

    /** Scroll so that the given child view is visible. */
    public boolean isChildVisible(View child) {
        if (child == null) return false;
        int childTop = child.getTop();
        int childBottom = child.getBottom();
        int scrollY = getScrollY();
        int viewportHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        return childBottom > scrollY && childTop < scrollY + viewportHeight;
    }

    @Override
    public void scrollTo(int x, int y) {
        // Clamp scroll position
        int maxScroll = getScrollRange();
        if (y < 0) y = 0;
        if (y > maxScroll) y = maxScroll;

        super.scrollTo(x, y);
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_SCROLL_OFFSET, (float) y, 0, 0, 0, 1);
        }
    }

    /** Whether we can scroll vertically in the given direction. */
    public boolean canScrollVertically(int direction) {
        int scrollY = getScrollY();
        int range = getScrollRange();
        if (direction < 0) {
            return scrollY > 0;
        } else {
            return scrollY < range;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Measure (with fillViewport)
    // ════════════════════════════════════════════════════════════════════════

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure children first
        int count = getChildCount();
        int maxWidth = 0;
        int maxHeight = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // For ScrollView, give child unlimited height
                int childWidthSpec = widthMeasureSpec;
                int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                child.measure(childWidthSpec, childHeightSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }

        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Set our measured dimensions respecting the parent spec
        setMeasuredDimension(
            resolveSize(maxWidth, widthMeasureSpec),
            resolveSize(maxHeight, heightMeasureSpec));

        // fillViewport: if child is shorter than viewport, stretch it
        if (mFillViewport && count > 0) {
            View child = getChildAt(0);
            if (child.getVisibility() != GONE) {
                int ourHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
                if (child.getMeasuredHeight() < ourHeight) {
                    int childWidthSpec = widthMeasureSpec;
                    int childHeightSpec = MeasureSpec.makeMeasureSpec(ourHeight, MeasureSpec.EXACTLY);
                    child.measure(childWidthSpec, childHeightSpec);
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Draw
    // ════════════════════════════════════════════════════════════════════════

    @Override
    protected void dispatchDraw(android.graphics.Canvas canvas) {
        // Apply scroll offset: translate by -scrollY before drawing children
        int scrollY = getScrollY();
        if (scrollY != 0) {
            canvas.save();
            canvas.translate(0, -scrollY);
        }

        // Draw children via parent implementation
        super.dispatchDraw(canvas);

        if (scrollY != 0) {
            canvas.restore();
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        // Draw scroll indicators if content exceeds viewport
        int viewportHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int contentHeight = 0;

        // Get content height from first child (ScrollView uses single child)
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            contentHeight = child.getMeasuredHeight() > 0 ? child.getMeasuredHeight() : child.getHeight();
        }

        if (contentHeight > viewportHeight && viewportHeight > 0) {
            android.graphics.Paint indicatorPaint = new android.graphics.Paint();
            indicatorPaint.setColor(SCROLL_INDICATOR_COLOR);
            indicatorPaint.setStyle(android.graphics.Paint.Style.FILL);

            int scrollY = getScrollY();
            int maxScroll = contentHeight - viewportHeight;
            int w = getWidth();

            // Scroll indicator dimensions
            float indicatorWidth = 4f;
            float trackX = w - getPaddingRight() - indicatorWidth;

            // Calculate indicator position and size proportionally
            float indicatorRatio = (float) viewportHeight / contentHeight;
            float indicatorHeight = Math.max(20f, viewportHeight * indicatorRatio);
            float scrollRatio = maxScroll > 0 ? (float) scrollY / maxScroll : 0f;
            float indicatorTop = getPaddingTop() + scrollRatio * (viewportHeight - indicatorHeight);

            // Draw the scroll indicator track (lighter)
            android.graphics.Paint trackPaint = new android.graphics.Paint();
            trackPaint.setColor(0x22888888);
            trackPaint.setStyle(android.graphics.Paint.Style.FILL);
            canvas.drawRect(trackX, getPaddingTop(), trackX + indicatorWidth,
                getPaddingTop() + viewportHeight, trackPaint);

            // Draw the scroll indicator thumb
            canvas.drawRoundRect(trackX, indicatorTop, trackX + indicatorWidth,
                indicatorTop + indicatorHeight, 2f, 2f, indicatorPaint);
        }
    }
}
