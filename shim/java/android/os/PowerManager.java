package android.os;

/**
 * Android-compatible PowerManager shim. Stub for power/wakelock management.
 */
public class PowerManager {
    public static final int PARTIAL_WAKE_LOCK = 1;
    public static final int SCREEN_DIM_WAKE_LOCK = 6;
    public static final int SCREEN_BRIGHT_WAKE_LOCK = 10;
    public static final int FULL_WAKE_LOCK = 26;

    public WakeLock newWakeLock(int levelAndFlags, String tag) {
        return new WakeLock(tag);
    }

    public boolean isInteractive() {
        return true; // assume screen on
    }

    public boolean isDeviceIdleMode() {
        return false;
    }

    public boolean isPowerSaveMode() {
        return false;
    }

    public void reboot(String reason) {
        System.out.println("[PowerManager] reboot: " + reason);
    }

    public static class WakeLock {
        private final String mTag;
        private boolean mHeld;

        WakeLock(String tag) {
            mTag = tag;
        }

        public void acquire() {
            mHeld = true;
        }

        public void acquire(long timeout) {
            mHeld = true;
        }

        public void release() {
            mHeld = false;
        }

        public boolean isHeld() {
            return mHeld;
        }

        public void setReferenceCounted(boolean value) {
            // no-op
        }
    }
}
