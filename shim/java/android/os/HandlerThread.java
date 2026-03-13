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
        while (!mQuit) {
            try { Thread.sleep(10); } catch (InterruptedException e) { break; }
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
