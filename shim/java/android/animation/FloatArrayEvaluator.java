package android.animation;

/** Stub for AOSP compilation. Evaluates float[] values. */
public class FloatArrayEvaluator implements TypeEvaluator<float[]> {
    private float[] mArray;

    public FloatArrayEvaluator() {}
    public FloatArrayEvaluator(float[] reuseArray) { mArray = reuseArray; }

    public float[] evaluate(float fraction, float[] startValue, float[] endValue) {
        float[] array = mArray != null ? mArray : new float[startValue.length];
        for (int i = 0; i < array.length; i++) {
            float start = startValue[i];
            array[i] = start + fraction * (endValue[i] - start);
        }
        return array;
    }
}
