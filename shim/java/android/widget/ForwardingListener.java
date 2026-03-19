package android.widget;

import android.view.MotionEvent;
import android.view.View;
import com.android.internal.view.menu.ShowableListMenu;

/**
 * Stub for android.widget.ForwardingListener.
 * Abstract class that forwards touch events to a ShowableListMenu.
 */
public abstract class ForwardingListener implements View.OnTouchListener, View.OnAttachStateChangeListener {

    public ForwardingListener(View src) {
    }

    public abstract ShowableListMenu getPopup();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onViewAttachedToWindow(View v) {}

    @Override
    public void onViewDetachedFromWindow(View v) {}

    public boolean onForwardingStarted() { return false; }
    public boolean onForwardingStopped() { return false; }
}
