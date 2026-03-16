package android.os;

/**
 * Looper — message loop with MessageQueue for the engine runtime.
 *
 * In single-threaded engine mode, messages are queued and dispatched
 * via pumpMessages() / flushAll() rather than blocking in loop().
 */
public final class Looper {

    private static final Looper sMainLooper = new Looper(Thread.currentThread());

    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>() {
        @Override
        protected Looper initialValue() {
            return new Looper(Thread.currentThread());
        }
    };

    private final Thread mThread;
    private final MessageQueue mQueue;

    private Looper(Thread thread) {
        mThread = thread;
        mQueue = new MessageQueue();
    }

    /** Return the main-thread Looper. Always non-null. */
    public static Looper getMainLooper() {
        return sMainLooper;
    }

    /** Return the Looper for the calling thread. Returns main looper for the main thread. */
    public static Looper myLooper() {
        if (Thread.currentThread() == sMainLooper.mThread) {
            return sMainLooper;
        }
        return sThreadLocal.get();
    }

    /** No-op — present so code that calls Looper.prepare() compiles. */
    public static void prepare() {
        // no-op
    }

    /** No-op — present for API compatibility. */
    public static void loop() {
        // no-op
    }

    /** Return this Looper's MessageQueue. */
    public MessageQueue getQueue() {
        return mQueue;
    }

    /** Return the thread this Looper is attached to. */
    public Thread getThread() {
        return mThread;
    }

    /** Return true if this is the main-thread Looper. */
    public boolean isCurrentThread() {
        return mThread == Thread.currentThread();
    }

    /** Process all ready messages. Call from the engine's main loop. */
    public static int pumpMessages() {
        Looper main = getMainLooper();
        if (main != null) return main.mQueue.drainReady();
        return 0;
    }

    /** Process ALL queued messages regardless of time (for testing). */
    public static int flushAll() {
        Looper main = getMainLooper();
        if (main != null) return main.mQueue.drainAll();
        return 0;
    }

    @Override
    public String toString() {
        return "Looper{thread=" + mThread.getName() + "}";
    }
}
