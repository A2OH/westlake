package android.media;

/**
 * Android-compatible MediaSyncEvent shim. Stub for media sync events.
 */
public class MediaSyncEvent {
    public static final int SYNC_EVENT_NONE = 0;
    public static final int SYNC_EVENT_PRESENTATION_COMPLETE = 1;

    private final int mType;
    private int mAudioSessionId;

    private MediaSyncEvent(int type) {
        mType = type;
    }

    public static MediaSyncEvent createEvent(int eventType) {
        return new MediaSyncEvent(eventType);
    }

    public MediaSyncEvent setAudioSessionId(int audioSessionId) {
        mAudioSessionId = audioSessionId;
        return this;
    }

    public int getType() { return mType; }
    public int getAudioSessionId() { return mAudioSessionId; }
}
