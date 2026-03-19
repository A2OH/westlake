package android.media;

/**
 * Stub for android.media.SubtitleTrack used by VideoView.
 */
public abstract class SubtitleTrack {
    public SubtitleTrack(MediaFormat format) {}

    public interface RenderingWidget {
        void setOnChangedListener(OnChangedListener listener);
        void setSize(int width, int height);
        void setVisible(boolean visible);
        void draw(android.graphics.Canvas c);

        interface OnChangedListener {
            void onChanged(RenderingWidget widget);
        }

        void onAttachedToWindow();
        void onDetachedFromWindow();
    }
}
