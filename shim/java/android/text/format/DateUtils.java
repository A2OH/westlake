package android.text.format;

/**
 * Shim for android.text.format.DateUtils.
 * Provides date/time formatting constants and utilities.
 */
public class DateUtils {

    public static final long SECOND_IN_MILLIS = 1000L;
    public static final long MINUTE_IN_MILLIS = 60000L;
    public static final long HOUR_IN_MILLIS = 3600000L;
    public static final long DAY_IN_MILLIS = 86400000L;
    public static final long WEEK_IN_MILLIS = 604800000L;
    public static final long YEAR_IN_MILLIS = 31449600000L;

    public static final int FORMAT_SHOW_TIME = 1;
    public static final int FORMAT_SHOW_DATE = 16;
    public static final int FORMAT_SHOW_YEAR = 4;
    public static final int FORMAT_SHOW_WEEKDAY = 2;
    public static final int FORMAT_ABBREV_ALL = 0x80;
    public static final int FORMAT_ABBREV_MONTH = 0x40;
    public static final int FORMAT_NO_YEAR = 8;
    public static final int FORMAT_24HOUR = 0x100;
    public static final int FORMAT_ABBREV_WEEKDAY = 0x200;
    public static final int FORMAT_ABBREV_RELATIVE = 0x400;
    public static final int FORMAT_ABBREV_TIME = 0x800;
    public static final int FORMAT_UTC = 0x1000;
    public static final int FORMAT_NO_MONTH = 0x2000;
    public static final int FORMAT_NO_NOON = 0x4000;
    public static final int FORMAT_NO_MIDNIGHT = 0x8000;
    public static final int FORMAT_NUMERIC_DATE = 0x10000;

    public static CharSequence getRelativeTimeSpanString(long timeMillis) {
        return "";
    }

    public static String formatDateTime(Object context, long millis, int flags) {
        return "";
    }

    public static String formatDateRange(Object context, long startMillis, long endMillis, int flags) {
        return "";
    }

    public static boolean isToday(long timeMillis) {
        return false;
    }

    public static String formatElapsedTime(long elapsedSeconds) {
        return formatElapsedTime(null, elapsedSeconds);
    }

    public static String formatElapsedTime(StringBuilder recycle, long elapsedSeconds) {
        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;
        StringBuilder sb = recycle != null ? recycle : new StringBuilder(8);
        sb.setLength(0);
        if (hours > 0) {
            sb.append(hours).append(':');
            sb.append(String.format("%02d", minutes)).append(':');
        } else {
            sb.append(minutes).append(':');
        }
        sb.append(String.format("%02d", seconds));
        return sb.toString();
    }
}
