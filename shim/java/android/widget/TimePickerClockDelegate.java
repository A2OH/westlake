package android.widget;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

/**
 * Stub delegate for TimePicker clock mode.
 */
class TimePickerClockDelegate extends TimePicker.AbstractTimePickerDelegate {
    TimePickerClockDelegate(TimePicker delegator, Context context,
            AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(delegator, context);
    }

    public void setHour(int hour) {}
    public int getHour() { return 0; }
    public void setMinute(int minute) {}
    public int getMinute() { return 0; }
    public void setDate(int hour, int minute) {}
    public void setIs24Hour(boolean is24Hour) {}
    public boolean is24Hour() { return false; }
    public boolean validateInput() { return true; }
    public void setEnabled(boolean enabled) {}
    public boolean isEnabled() { return true; }
    public int getBaseline() { return -1; }
    public Parcelable onSaveInstanceState(Parcelable superState) { return superState; }
    public void onRestoreInstanceState(Parcelable state) {}
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) { return false; }
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {}
    public View getHourView() { return null; }
    public View getMinuteView() { return null; }
    public View getAmView() { return null; }
    public View getPmView() { return null; }
}
