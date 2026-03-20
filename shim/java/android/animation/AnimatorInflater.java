package android.animation;

import android.content.Context;
import android.content.res.Resources;

/** Stub for AOSP compilation. */
public class AnimatorInflater {
    public static Animator loadAnimator(Context context, int id) throws Resources.NotFoundException {
        return new ValueAnimator();
    }
    public static Animator loadAnimator(Resources resources, Resources.Theme theme, int id)
            throws Resources.NotFoundException {
        return new ValueAnimator();
    }
    public static Animator loadAnimator(Resources resources, Resources.Theme theme, int id,
            float pathErrorScale) throws Resources.NotFoundException {
        return new ValueAnimator();
    }
    public static StateListAnimator loadStateListAnimator(Context context, int id)
            throws Resources.NotFoundException {
        return new StateListAnimator();
    }
}
