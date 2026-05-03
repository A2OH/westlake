package android.os;

/**
 * Android-compatible HandlerThread shim. Thread with a Looper-like message queue.
 */
public class HandlerThread extends Thread {
    private final String mName;
    private int mPriority;
    private volatile boolean mReady;
    private volatile boolean mQuit;

    public HandlerThread(String name) {
        this(name, Process.THREAD_PRIORITY_DEFAULT);
    }

    public HandlerThread(String name, int priority) {
        super(name);
        mName = name;
        mPriority = priority;
        setDaemon(true);
    }

    @Override
    public void run() {
        mReady = true;
        synchronized (this) { notifyAll(); }
        // Keep Android's HandlerThread lifetime shape without parking in
        // Thread.sleep(). The portable VM's current signal path can crash when
        // a worker is asleep during heavy Realm startup logging; handlers are
        // routed to the engine main Looper, so this thread only backs isAlive.
        long spin = 0;
        while (!mQuit) {
            spin++;
            if ((spin & 0x7fffffL) == 0L && isInterrupted()) {
                break;
            }
        }
    }

    public boolean quit() {
        mQuit = true;
        interrupt();
        return true;
    }

    public boolean quitSafely() {
        return quit();
    }

    public Looper getLooper() {
        waitUntilReady();
        return Looper.myLooper();
    }

    public int getThreadId() {
        return (int) getId();
    }

    public void waitUntilReady() {
        synchronized (this) {
            while (!mReady) {
                try { wait(100); } catch (InterruptedException e) { break; }
            }
        }
    }
}
