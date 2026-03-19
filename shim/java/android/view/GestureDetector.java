package android.view;
import android.content.Context;
import android.os.Handler;

public class GestureDetector {
    public GestureDetector(Context p0, OnGestureListener p1) {}
    public GestureDetector(Context p0, OnGestureListener p1, Handler p2) {}
    public GestureDetector(Context p0, OnGestureListener p1, Handler p2, boolean p3) {}

    public boolean isLongpressEnabled() { return false; }
    public boolean onGenericMotionEvent(MotionEvent p0) { return false; }
    public boolean onTouchEvent(MotionEvent p0) { return false; }
    public void setContextClickListener(OnContextClickListener p0) {}
    public void setIsLongpressEnabled(boolean p0) {}
    public void setOnDoubleTapListener(OnDoubleTapListener p0) {}

    public interface OnGestureListener {
        boolean onDown(MotionEvent e);
        void onShowPress(MotionEvent e);
        boolean onSingleTapUp(MotionEvent e);
        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
        void onLongPress(MotionEvent e);
        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }

    public interface OnDoubleTapListener {
        boolean onSingleTapConfirmed(MotionEvent e);
        boolean onDoubleTap(MotionEvent e);
        boolean onDoubleTapEvent(MotionEvent e);
    }

    public interface OnContextClickListener {
        boolean onContextClick(MotionEvent e);
    }

    public static class SimpleOnGestureListener implements OnGestureListener, OnDoubleTapListener, OnContextClickListener {
        public boolean onDown(MotionEvent e) { return false; }
        public void onShowPress(MotionEvent e) {}
        public boolean onSingleTapUp(MotionEvent e) { return false; }
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
        public void onLongPress(MotionEvent e) {}
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }
        public boolean onSingleTapConfirmed(MotionEvent e) { return false; }
        public boolean onDoubleTap(MotionEvent e) { return false; }
        public boolean onDoubleTapEvent(MotionEvent e) { return false; }
        public boolean onContextClick(MotionEvent e) { return false; }
    }
}
