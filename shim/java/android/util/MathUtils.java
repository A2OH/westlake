package android.util;

/**
 * Stub for android.util.MathUtils.
 */
public class MathUtils {
    public static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
    public static long constrain(long amount, long low, long high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
    public static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
    public static float lerp(float start, float stop, float amount) {
        return start + (stop - start) * amount;
    }
    public static float norm(float start, float stop, float value) {
        return (value - start) / (stop - start);
    }
    public static float map(float minStart, float minStop, float maxStart, float maxStop, float value) {
        return maxStart + (maxStop - maxStart) * ((value - minStart) / (minStop - minStart));
    }
}
