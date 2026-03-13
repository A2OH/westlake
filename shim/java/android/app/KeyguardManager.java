package android.app;

/**
 * Android-compatible KeyguardManager shim. Stub — device always unlocked.
 */
public class KeyguardManager {

    public boolean isKeyguardLocked() { return false; }
    public boolean isKeyguardSecure() { return false; }
    public boolean isDeviceLocked() { return false; }
    public boolean isDeviceSecure() { return false; }

    public boolean inKeyguardRestrictedInputMode() { return false; }

    public KeyguardLock newKeyguardLock(String tag) {
        return new KeyguardLock();
    }

    public void requestDismissKeyguard(Object activity, KeyguardDismissCallback callback) {
        if (callback != null) callback.onDismissSucceeded();
    }

    public static class KeyguardLock {
        public void disableKeyguard() {}
        public void reenableKeyguard() {}
    }

    public static abstract class KeyguardDismissCallback {
        public void onDismissError() {}
        public void onDismissSucceeded() {}
        public void onDismissCancelled() {}
    }
}
