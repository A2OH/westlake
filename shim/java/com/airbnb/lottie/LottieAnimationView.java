package com.airbnb.lottie;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * LottieAnimationView shim — extends ImageView.
 * Real Lottie plays JSON animations; our shim is a static ImageView.
 */
public class LottieAnimationView extends ImageView {
    public LottieAnimationView(Context context) { super(context); }
    public LottieAnimationView(Context context, AttributeSet attrs) { super(context, attrs); }
    public LottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // No animation content — measure as 0 height to avoid layout explosion
        int w = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(w, 0);
    }

    public void setAnimation(String assetName) {}
    public void setAnimation(int rawRes) {}
    public void playAnimation() {}
    public void cancelAnimation() {}
    public void pauseAnimation() {}
    public void resumeAnimation() {}
    public void setRepeatCount(int count) {}
    public void setRepeatMode(int mode) {}
    public void setSpeed(float speed) {}
    public void setProgress(float progress) {}
    public float getProgress() { return 0f; }
    public boolean isAnimating() { return false; }
    public void setImageAssetsFolder(String folder) {}
    public void addAnimatorListener(Object listener) {}
    public void removeAllAnimatorListeners() {}
    public void setMinAndMaxProgress(float min, float max) {}
}
