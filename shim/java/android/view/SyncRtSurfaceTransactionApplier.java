package android.view;

/**
 * Stub for SyncRtSurfaceTransactionApplier used by SurfaceView.
 */
public class SyncRtSurfaceTransactionApplier {
    public SyncRtSurfaceTransactionApplier(View targetView) {}

    public void scheduleApply(SurfaceParams... params) {}

    public static class SurfaceParams {
        public static class Builder {
            public Builder(SurfaceControl sc) {}
            public Builder withVisibility(boolean visible) { return this; }
            public Builder withAlpha(float alpha) { return this; }
            public Builder withMergeTransaction(SurfaceControl.Transaction t) { return this; }
            public Builder withWindowCrop(android.graphics.Rect crop) { return this; }
            public SurfaceParams build() { return new SurfaceParams(); }
        }
    }
}
