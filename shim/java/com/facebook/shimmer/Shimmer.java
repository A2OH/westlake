package com.facebook.shimmer;

public class Shimmer {
    public static class Builder<T extends Builder<T>> {
        public T setAlphaShimmer(boolean alphaShimmer) { return (T) this; }
        public T setAutoStart(boolean autoStart) { return (T) this; }
        public T setBaseAlpha(float baseAlpha) { return (T) this; }
        public T setBaseColor(int baseColor) { return (T) this; }
        public T setClipToChildren(boolean clipToChildren) { return (T) this; }
        public T setDirection(int direction) { return (T) this; }
        public T setDropoff(float dropoff) { return (T) this; }
        public T setDuration(long duration) { return (T) this; }
        public T setFixedHeight(int fixedHeight) { return (T) this; }
        public T setFixedWidth(int fixedWidth) { return (T) this; }
        public T setHeightRatio(float heightRatio) { return (T) this; }
        public T setHighlightAlpha(float highlightAlpha) { return (T) this; }
        public T setHighlightColor(int highlightColor) { return (T) this; }
        public T setIntensity(float intensity) { return (T) this; }
        public T setRepeatCount(int repeatCount) { return (T) this; }
        public T setRepeatDelay(long repeatDelay) { return (T) this; }
        public T setRepeatMode(int repeatMode) { return (T) this; }
        public T setShape(int shape) { return (T) this; }
        public T setTilt(float tilt) { return (T) this; }
        public T setWidthRatio(float widthRatio) { return (T) this; }
        public Shimmer build() { return new Shimmer(); }
    }

    public static class AlphaHighlightBuilder extends Builder<AlphaHighlightBuilder> {}
    public static class ColorHighlightBuilder extends Builder<ColorHighlightBuilder> {}
}
