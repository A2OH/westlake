package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.icu.util.Calendar;

/**
 * Stub delegate for DatePicker spinner mode.
 */
class DatePickerSpinnerDelegate extends DatePicker.AbstractDatePickerDelegate {
    DatePickerSpinnerDelegate(DatePicker delegator, Context context,
            AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(delegator, context);
    }

    public void init(int year, int monthOfYear, int dayOfMonth,
            DatePicker.OnDateChangedListener onDateChangedListener) {}
    public void updateDate(int year, int month, int dayOfMonth) {}
    public int getYear() { return 2000; }
    public int getMonth() { return 0; }
    public int getDayOfMonth() { return 1; }
    public void setFirstDayOfWeek(int firstDayOfWeek) {}
    public int getFirstDayOfWeek() { return 0; }
    public void setMinDate(long minDate) {}
    public Calendar getMinDate() { return Calendar.getInstance(); }
    public void setMaxDate(long maxDate) {}
    public Calendar getMaxDate() { return Calendar.getInstance(); }
    public void setEnabled(boolean enabled) {}
    public boolean isEnabled() { return true; }
    public CalendarView getCalendarView() { return null; }
    public void setCalendarViewShown(boolean shown) {}
    public boolean getCalendarViewShown() { return false; }
    public void setSpinnersShown(boolean shown) {}
    public boolean getSpinnersShown() { return false; }
    public void onConfigurationChanged(Configuration newConfig) {}
    public void setOnDateChangedListener(DatePicker.OnDateChangedListener callback) {}
    public void setAutoFillChangeListener(DatePicker.OnDateChangedListener callback) {}
    public void setValidationCallback(DatePicker.ValidationCallback callback) {}
    public Parcelable onSaveInstanceState(Parcelable superState) { return superState; }
    public void onRestoreInstanceState(Parcelable state) {}
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) { return false; }
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {}
}
