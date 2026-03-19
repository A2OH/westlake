package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Stub delegate for CalendarView holo/legacy mode.
 */
class CalendarViewLegacyDelegate extends CalendarView.AbstractCalendarViewDelegate {
    CalendarViewLegacyDelegate(CalendarView delegator, Context context,
            AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(delegator, context);
    }

    public void setShownWeekCount(int count) {}
    public int getShownWeekCount() { return 6; }
    public void setSelectedWeekBackgroundColor(int color) {}
    public int getSelectedWeekBackgroundColor() { return 0; }
    public void setFocusedMonthDateColor(int color) {}
    public int getFocusedMonthDateColor() { return 0; }
    public void setUnfocusedMonthDateColor(int color) {}
    public int getUnfocusedMonthDateColor() { return 0; }
    public void setWeekNumberColor(int color) {}
    public int getWeekNumberColor() { return 0; }
    public void setWeekSeparatorLineColor(int color) {}
    public int getWeekSeparatorLineColor() { return 0; }
    public void setSelectedDateVerticalBar(int resourceId) {}
    public void setSelectedDateVerticalBar(Drawable drawable) {}
    public Drawable getSelectedDateVerticalBar() { return null; }
    public void setWeekDayTextAppearance(int resourceId) {}
    public int getWeekDayTextAppearance() { return 0; }
    public void setDateTextAppearance(int resourceId) {}
    public int getDateTextAppearance() { return 0; }
    public void setMinDate(long minDate) {}
    public long getMinDate() { return 0; }
    public void setMaxDate(long maxDate) {}
    public long getMaxDate() { return 0; }
    public void setShowWeekNumber(boolean showWeekNumber) {}
    public boolean getShowWeekNumber() { return false; }
    public void setFirstDayOfWeek(int firstDayOfWeek) {}
    public int getFirstDayOfWeek() { return 0; }
    public void setDate(long date) {}
    public void setDate(long date, boolean animate, boolean center) {}
    public long getDate() { return 0; }
    public boolean getBoundsForDate(long date, Rect outBounds) { return false; }
    public void setOnDateChangeListener(CalendarView.OnDateChangeListener listener) {}
    public void onConfigurationChanged(Configuration newConfig) {}
}
