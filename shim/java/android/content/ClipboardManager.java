package android.content;

import java.util.ArrayList;
import java.util.List;

public class ClipboardManager {

    private ClipData mPrimaryClip;
    private final List<OnPrimaryClipChangedListener> mListeners = new ArrayList<>();

    /** Listener interface matching AOSP. */
    public interface OnPrimaryClipChangedListener {
        void onPrimaryClipChanged();
    }

    public ClipboardManager() {}

    public void setPrimaryClip(ClipData clip) {
        mPrimaryClip = clip;
        for (OnPrimaryClipChangedListener l : mListeners) {
            l.onPrimaryClipChanged();
        }
    }

    // Keep old Object-param overload for binary compat
    public void setPrimaryClip(Object p0) {
        if (p0 instanceof ClipData) {
            setPrimaryClip((ClipData) p0);
        }
    }

    public ClipData getPrimaryClip() {
        return mPrimaryClip;
    }

    public ClipDescription getPrimaryClipDescription() {
        return mPrimaryClip != null ? mPrimaryClip.getDescription() : null;
    }

    public boolean hasPrimaryClip() {
        return mPrimaryClip != null;
    }

    public void clearPrimaryClip() {
        mPrimaryClip = null;
    }

    public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        mListeners.remove(listener);
    }

    // Keep old Object-param overloads for binary compat
    public void addPrimaryClipChangedListener(Object p0) {
        if (p0 instanceof OnPrimaryClipChangedListener) {
            addPrimaryClipChangedListener((OnPrimaryClipChangedListener) p0);
        }
    }
    public void removePrimaryClipChangedListener(Object p0) {
        if (p0 instanceof OnPrimaryClipChangedListener) {
            removePrimaryClipChangedListener((OnPrimaryClipChangedListener) p0);
        }
    }
}
