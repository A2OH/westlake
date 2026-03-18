package android.view;
import android.os.Handler;
import java.util.ArrayList;

/**
 * AOSP HandlerActionQueue stub — stores runnables posted before a View is attached.
 * In headless mode, executeActions runs them immediately.
 */
public class HandlerActionQueue {
    private ArrayList<Runnable> mActions;

    public HandlerActionQueue() {}

    public void post(Runnable action) {
        if (action == null) return;
        if (mActions == null) mActions = new ArrayList<>();
        mActions.add(action);
    }

    public void postDelayed(Runnable action, long delayMillis) {
        // In headless mode, treat delayed posts as immediate
        post(action);
    }

    public void removeCallbacks(Runnable action) {
        if (mActions != null) mActions.remove(action);
    }

    public void executeActions(Handler handler) {
        if (mActions == null) return;
        ArrayList<Runnable> copy = new ArrayList<>(mActions);
        mActions.clear();
        for (Runnable action : copy) {
            if (handler != null) {
                handler.post(action);
            } else {
                action.run();
            }
        }
    }
}
