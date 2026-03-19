package android.widget;

import android.content.Context;
import android.util.AttributeSet;

public class MediaController extends FrameLayout {
    public MediaController(Context context) { super(context); }
    public MediaController(Context context, AttributeSet attrs) { super(context, attrs); }
    public MediaController(Context context, boolean useFastForward) { super(context); }

    public interface MediaPlayerControl {
        void start();
        void pause();
        int getDuration();
        int getCurrentPosition();
        void seekTo(int pos);
        boolean isPlaying();
        int getBufferPercentage();
        boolean canPause();
        boolean canSeekBackward();
        boolean canSeekForward();
        int getAudioSessionId();
    }

    public void hide() {}
    public boolean isShowing() { return false; }
    public void setEnabled(boolean enabled) { super.setEnabled(enabled); }
    public void setAnchorView(android.view.View view) {}
    public void setMediaPlayer(MediaPlayerControl player) {}
    public void setPrevNextListeners(android.view.View.OnClickListener next, android.view.View.OnClickListener prev) {}
    public void show() {}
    public void show(int timeout) {}
}
