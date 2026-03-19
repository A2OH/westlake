package android.media;

import android.content.Context;

/**
 * Stub for android.media.SubtitleController used by VideoView.
 */
public class SubtitleController {
    public SubtitleController(Context context, MediaTimeProvider timeProvider, Listener listener) {}

    public interface Anchor {
        void setSubtitleWidget(SubtitleTrack.RenderingWidget renderingWidget);
        android.os.Looper getSubtitleLooper();
    }

    public interface Listener {
        void onSubtitleTrackSelected(SubtitleTrack track);
    }

    public void registerRenderer(Object renderer) {}
    public void setAnchor(Anchor anchor) {}
    public void reset() {}
    public void show() {}
    public void hide() {}
}
