package android.os;

import java.util.PriorityQueue;

/**
 * Single-threaded message queue for the engine runtime.
 * Messages are sorted by delivery time (when). poll() returns the next ready message.
 */
public final class MessageQueue {
    private final PriorityQueue<Message> mQueue = new PriorityQueue<>(11,
            (a, b) -> Long.compare(a.when, b.when));
    private boolean mQuitting;

    public MessageQueue() {}

    /** Enqueue a message at a specific delivery time. */
    boolean enqueueMessage(Message msg, long when) {
        if (mQuitting) return false;
        msg.when = when;
        mQueue.add(msg);
        return true;
    }

    /** Return the next message that's ready (when <= now), or null if none ready. */
    Message next() {
        if (mQuitting) return null;
        Message msg = mQueue.peek();
        if (msg != null && msg.when <= SystemClock.uptimeMillis()) {
            return mQueue.poll();
        }
        return null;
    }

    /** Drain and dispatch all ready messages. Returns count dispatched. */
    int drainReady() {
        int count = 0;
        Message msg;
        while ((msg = next()) != null) {
            if (msg.target != null) msg.target.dispatchMessage(msg);
            msg.recycle();
            count++;
        }
        return count;
    }

    /** Drain ALL messages regardless of time (for testing/shutdown). */
    int drainAll() {
        int count = 0;
        Message msg;
        while ((msg = mQueue.poll()) != null) {
            if (msg.target != null) msg.target.dispatchMessage(msg);
            msg.recycle();
            count++;
        }
        return count;
    }

    public boolean hasMessages() { return !mQueue.isEmpty(); }
    public int size() { return mQueue.size(); }

    void quit(boolean safe) {
        mQuitting = true;
        mQueue.clear();
    }

    // Stub API methods for compatibility
    public void addIdleHandler(Object p0) {}
    public void addOnFileDescriptorEventListener(Object p0, Object p1, Object p2) {}
    public boolean isIdle() { return mQueue.isEmpty(); }
    public void removeIdleHandler(Object p0) {}
    public void removeOnFileDescriptorEventListener(Object p0) {}
}
