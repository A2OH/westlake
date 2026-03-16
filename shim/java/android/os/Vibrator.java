package android.os;

import com.ohos.shim.bridge.OHBridge;

/**
 * Android-compatible Vibrator shim. Routes vibration requests to OHBridge
 * which maps to OpenHarmony's vibrator service.
 */
public class Vibrator {

    public boolean hasVibrator() {
        return OHBridge.vibratorHasVibrator();
    }

    public void vibrate(long milliseconds) {
        OHBridge.vibratorVibrate(milliseconds);
    }

    public void vibrate(long[] pattern, int repeat) {
        // Sum durations of "on" segments (odd indices) for a single vibration
        long totalOn = 0;
        for (int i = 1; i < pattern.length; i += 2) {
            totalOn += pattern[i];
        }
        if (totalOn > 0) {
            OHBridge.vibratorVibrate(totalOn);
        }
    }

    public void vibrate(VibrationEffect effect) {
        if (effect == null) return;
        long duration = effect.getDuration();
        if (duration > 0) {
            OHBridge.vibratorVibrate(duration);
        }
    }

    public void vibrate(VibrationEffect effect, VibrationAttributes attrs) {
        // attrs are advisory; delegate to the single-arg version
        vibrate(effect);
    }

    public void cancel() {
        OHBridge.vibratorCancel();
    }
}
