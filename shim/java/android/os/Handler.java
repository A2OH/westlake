package android.os;
import android.util.Printer;

public class Handler {

    public interface Callback {
        boolean handleMessage(Message msg);
    }

    private Looper mLooper;
    private Callback mCallback;

    public Handler() {
        mLooper = Looper.myLooper();
    }

    public Handler(Looper looper) {
        mLooper = looper;
    }

    public Handler(Callback callback) {
        mLooper = Looper.myLooper();
        mCallback = callback;
    }

    public Handler(Looper looper, Callback callback) {
        mLooper = looper;
        mCallback = callback;
    }

    // Keep old constructor for source compat with code passing Object
    public Handler(Looper looper, Object callback) {
        mLooper = looper;
        if (callback instanceof Callback) mCallback = (Callback) callback;
    }

    public Looper getLooper() { return mLooper; }

    public void dispatchMessage(Message msg) {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) return;
        }
        handleMessage(msg);
    }

    public void dump(Printer p0, String p1) {}

    public void handleMessage(Message msg) {}

    public boolean hasCallbacks(Runnable p0) { return false; }
    public boolean hasMessages(int p0) { return false; }
    public boolean hasMessages(int p0, Object p1) { return false; }

    public boolean post(Runnable r) {
        if (r != null) r.run();
        return true;
    }

    public boolean postAtFrontOfQueue(Runnable p0) { return false; }
    public boolean postAtTime(Runnable p0, long p1) { return false; }
    public boolean postAtTime(Runnable p0, Object p1, long p2) { return false; }
    public boolean postDelayed(Runnable p0, long p1) { return false; }
    public boolean postDelayed(Runnable p0, Object p1, long p2) { return false; }
    public void removeCallbacks(Runnable p0) {}
    public void removeCallbacks(Runnable p0, Object p1) {}
    public void removeCallbacksAndMessages(Object p0) {}
    public void removeMessages(int p0) {}
    public void removeMessages(int p0, Object p1) {}

    public boolean sendEmptyMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.target = this;
        return sendMessage(msg);
    }

    public boolean sendEmptyMessageAtTime(int p0, long p1) { return false; }
    public boolean sendEmptyMessageDelayed(int p0, long p1) { return false; }

    public boolean sendMessage(Message msg) {
        if (msg != null) {
            dispatchMessage(msg);
        }
        return true;
    }

    public boolean sendMessageAtFrontOfQueue(Message p0) { return false; }
    public boolean sendMessageAtTime(Message p0, long p1) { return false; }
    public boolean sendMessageDelayed(Message p0, long p1) { return false; }
}
