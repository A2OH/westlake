package android.util;

public class TimeUtils {
    public static final long NANOS_PER_MS = 1000000L;

    public TimeUtils() {}

    public static java.util.TimeZone getTimeZone(int offset, boolean dst, long when, String country) {
        return java.util.TimeZone.getDefault();
    }

    public static String getTimeZoneDatabaseVersion() {
        return "2024a";
    }

    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        long ms = millis % 1000;
        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(h).append("h");
        if (m > 0) sb.append(m).append("m");
        if (s > 0 || ms > 0) sb.append(s).append("s");
        if (ms > 0) sb.append(ms).append("ms");
        return sb.length() == 0 ? "0s" : sb.toString();
    }

    public static boolean isTimeBetween(Object p0, Object p1, Object p2) { return false; }

    public static String formatUptime(long millis) {
        return formatDuration(millis);
    }
}
