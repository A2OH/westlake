package android.animation;

/** Stub for AOSP compilation. Evaluates int[] values. */
public class IntArrayEvaluator implements TypeEvaluator<int[]> {
    private int[] mArray;

    public IntArrayEvaluator() {}
    public IntArrayEvaluator(int[] reuseArray) { mArray = reuseArray; }

    public int[] evaluate(float fraction, int[] startValue, int[] endValue) {
        int[] array = mArray != null ? mArray : new int[startValue.length];
        for (int i = 0; i < array.length; i++) {
            int start = startValue[i];
            array[i] = (int) (start + fraction * (endValue[i] - start));
        }
        return array;
    }
}
