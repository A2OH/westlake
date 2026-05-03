package android.os;

/**
 * Android-compatible Process shim. Uses Java Runtime for process info.
 * Compatible with Java 8 compilation target.
 */
public class Process {
    public static final int FIRST_APPLICATION_UID = 10000;
    public static final int LAST_APPLICATION_UID = 19999;
    public static final int SYSTEM_UID = 1000;
    public static final int PHONE_UID = 1001;
    public static final int BLUETOOTH_UID = 1002;
    public static final int WIFI_UID = 1010;
    public static final int INVALID_UID = -1;

    public static final int SIGNAL_QUIT = 3;
    public static final int SIGNAL_KILL = 9;
    public static final int SIGNAL_USR1 = 10;

    public static final int THREAD_PRIORITY_DEFAULT = 0;
    public static final int THREAD_PRIORITY_LOWEST = 19;
    public static final int THREAD_PRIORITY_BACKGROUND = 10;
    public static final int THREAD_PRIORITY_FOREGROUND = -2;
    public static final int THREAD_PRIORITY_DISPLAY = -4;
    public static final int THREAD_PRIORITY_URGENT_DISPLAY = -8;
    public static final int THREAD_PRIORITY_AUDIO = -16;
    public static final int THREAD_PRIORITY_URGENT_AUDIO = -19;

    /** Cached PID, resolved once via /proc/self or fallback. */
    private static int sCachedPid = -1;

    /**
     * Returns the PID of this process. Uses Java 8 compatible approach.
     */
    public static int myPid() {
        if (sCachedPid == -1) {
            sCachedPid = resolvePid();
        }
        return sCachedPid;
    }

    private static int resolvePid() {
        // Android does not expose java.lang.management. Prefer /proc when
        // available, but keep a stable fallback for non-Linux hosts such as OHOS.
        java.io.FileInputStream in = null;
        try {
            in = new java.io.FileInputStream("/proc/self/stat");
            byte[] buf = new byte[32];
            int len = in.read(buf);
            int pid = 0;
            for (int i = 0; i < len; i++) {
                int ch = buf[i] & 0xff;
                if (ch < '0' || ch > '9') {
                    break;
                }
                pid = pid * 10 + (ch - '0');
            }
            if (pid > 0) {
                return pid;
            }
        } catch (Exception e) {
            // fall through
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception ignored) {
            }
        }
        return 1000;
    }

    public static int myTid() {
        return (int) Thread.currentThread().getId();
    }

    public static int myUid() {
        return FIRST_APPLICATION_UID; // mock app UID
    }

    public static UserHandle myUserHandle() {
        return new UserHandle(0);
    }

    public static void setThreadPriority(int priority) {
        // Map Android priority to Java priority (rough mapping)
        int javaPriority;
        if (priority <= THREAD_PRIORITY_URGENT_AUDIO) javaPriority = Thread.MAX_PRIORITY;
        else if (priority <= THREAD_PRIORITY_FOREGROUND) javaPriority = Thread.NORM_PRIORITY + 2;
        else if (priority == THREAD_PRIORITY_DEFAULT) javaPriority = Thread.NORM_PRIORITY;
        else javaPriority = Thread.MIN_PRIORITY;
        Thread.currentThread().setPriority(Math.max(Thread.MIN_PRIORITY, Math.min(Thread.MAX_PRIORITY, javaPriority)));
    }

    public static void setThreadPriority(int tid, int priority) {
        // Only support current thread
        if (tid == myTid()) {
            setThreadPriority(priority);
        }
    }

    public static int getThreadPriority(int tid) {
        return THREAD_PRIORITY_DEFAULT; // stub
    }

    public static boolean supportsProcesses() {
        return true;
    }

    public static void killProcess(int pid) {
        // Java 8 has no portable way to kill by PID; log and no-op
        System.out.println("[Process] killProcess(" + pid + ") - stub");
    }

    public static void sendSignal(int pid, int signal) {
        if (signal == SIGNAL_KILL) {
            killProcess(pid);
        }
    }

    public static long getElapsedCpuTime() {
        return SystemClock.elapsedRealtime();
    }

    public static boolean isApplicationUid(int uid) {
        return uid >= FIRST_APPLICATION_UID && uid <= LAST_APPLICATION_UID;
    }

    public static boolean isIsolated() {
        return false;
    }
}
