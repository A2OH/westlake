package android.widget;
import android.app.INotificationManager;
import android.app.ITransientNotificationCallback;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.IAccessibilityManager;
/** AOSP compilation stub. */
public class ToastPresenter {
    public ToastPresenter(Context context, IAccessibilityManager accessibilityManager,
            INotificationManager notificationManager, String packageName) {}
    public WindowManager.LayoutParams getLayoutParams() {
        return new WindowManager.LayoutParams();
    }
    public View getView() { return null; }
    public void show(View view, IBinder token, IBinder windowToken, int duration,
            int gravity, int xOffset, int yOffset,
            float horizontalMargin, float verticalMargin,
            android.app.ITransientNotificationCallback callback) {}
    public void hide(android.app.ITransientNotificationCallback callback) {}
    public static View getTextToastView(Context context, CharSequence text) { return null; }
}
