package android.media;

/**
 * Stub for MediaTimeProvider used by VideoView/SubtitleController.
 */
public interface MediaTimeProvider {
    long NO_TIME = -1;

    interface OnMediaTimeListener {
        void onTimedEvent(long currentTimeUs);
        void onStopRequest();
        void onSeek(long currentTimeUs);
    }

    void scheduleUpdate(OnMediaTimeListener listener);
    void cancelNotifications(OnMediaTimeListener listener);
    long getCurrentTimeUs(boolean precise, boolean mono);
}
