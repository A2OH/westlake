package android.view.animation;
import android.content.Context;

/**
 * Android-compatible AnimationUtils shim. Provides static factory helpers
 * for loading animations and interpolators from resource ids.
 */
public class AnimationUtils {

    private AnimationUtils() {}

    /**
     * Loads an animation from a resource id. Returns a no-op Animation stub.
     *
     * @param context ignored in this shim
     * @param id      resource id (ignored)
     * @return a stub Animation instance
     */
    public static Animation loadAnimation(Context context, int id) {
        return newNoopAnimation();
    }

    public static Animation loadAnimation(Object context, int id) {
        return newNoopAnimation();
    }

    private static Animation newNoopAnimation() {
        return new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {}
        };
    }

    /**
     * Loads a layout animation controller from a resource id.
     */
    public static LayoutAnimationController loadLayoutAnimation(Context context, int id) {
        return new LayoutAnimationController(loadAnimation(context, 0));
    }

    public static LayoutAnimationController loadLayoutAnimation(Object context, int id) {
        return new LayoutAnimationController(loadAnimation(context, 0));
    }

    /**
     * Loads an interpolator from a resource id. Returns a linear interpolator stub.
     *
     * @param context ignored in this shim
     * @param id      resource id (ignored)
     * @return a stub TimeInterpolator that returns input unchanged
     */
    public static Interpolator loadInterpolator(Context context, int id) {
        return new LinearInterpolator();
    }

    public static Interpolator loadInterpolator(Object context, int id) {
        return new LinearInterpolator();
    }

    public static Animation makeInAnimation(Context context, boolean fromLeft) {
        return newNoopAnimation();
    }

    public static Animation makeOutAnimation(Context context, boolean toRight) {
        return newNoopAnimation();
    }

    public static Animation makeInChildBottomAnimation(Context context) {
        return newNoopAnimation();
    }

    /**
     * Returns the current animation time in milliseconds. Equivalent to
     * {@link android.os.SystemClock#uptimeMillis()}.
     */
    public static long currentAnimationTimeMillis() {
        return System.nanoTime() / 1_000_000L;
    }

    /** Lock the animation clock to a specific time (for testing). */
    public static void lockAnimationClock(long timeMillis) {}

    /** Unlock the animation clock. */
    public static void unlockAnimationClock() {}
}
