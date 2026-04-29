package android.text.format;

import android.content.Context;

public class DateFormat {
    public DateFormat() {}

    public static CharSequence format(CharSequence format, Object timeObj) { return ""; }
    public static CharSequence format(CharSequence format, java.util.Calendar cal) { return ""; }
    public static CharSequence format(CharSequence format, java.util.Date date) { return ""; }
    public static CharSequence format(CharSequence format, long millis) { return ""; }
    public static Object getBestDateTimePattern(Object locale, Object skeleton) { return ""; }
    public static Object getDateFormat(Object context) { return null; }
    public static char getDateFormatOrder(Object context) { return ' '; }
    public static Object getLongDateFormat(Object context) { return null; }
    public static Object getMediumDateFormat(Object context) { return null; }
    public static Object getTimeFormat(Object context) { return null; }
    public static boolean is24HourFormat(Object context) { return false; }
    public static boolean is24HourFormat(Context context) { return false; }
    public static boolean is24HourFormat(Context context, int userHandle) { return false; }

    /** Returns the time format string for the given context. */
    public static String getTimeFormatString(Context context) { return "h:mm a"; }

    /** Returns true if the given format string contains seconds. */
    public static boolean hasSeconds(CharSequence format) {
        if (format == null) return false;
        String s = format.toString();
        return s.indexOf('s') >= 0 || s.indexOf('S') >= 0;
    }
}
