# WS2: MiniServer — Complete Remaining Engine Infrastructure

## Mission

Finish the MiniServer engine so a real APK can launch headlessly: manifest auto-registration, Handler/Looper message queue, Resources stub, ContentResolver wiring, and full MiniServer.loadApk() pipeline.

## Current State (as of ad8e25c)

### Implemented (DO NOT REWRITE — build on these)

| Component | File | Status |
|-----------|------|--------|
| MiniServer | `shim/java/android/app/MiniServer.java` | Singleton, wires AM+SM+PM, init/shutdown |
| MiniActivityManager | `shim/java/android/app/MiniActivityManager.java` | Full lifecycle, back stack, startActivityForResult |
| MiniServiceManager | `shim/java/android/app/MiniServiceManager.java` | start/stop/bind/unbind with proper state |
| MiniPackageManager | `shim/java/android/content/pm/MiniPackageManager.java` | Activity/service registration, intent resolution |
| SystemServiceRegistry | `shim/java/android/app/SystemServiceRegistry.java` | 15 services registered |
| LayoutInflater | `shim/java/android/view/LayoutInflater.java` | Stub inflation, from(Context) |
| Context | `shim/java/android/content/Context.java` | getSystemService, startActivity, start/stopService, bind/unbindService |
| Activity | `shim/java/android/app/Activity.java` | Full lifecycle + setContentView + findViewById |
| Window | `shim/java/android/view/Window.java` | DecorView, setContentView, findViewById |
| View tree | `shim/java/android/view/View.java`, `ViewGroup.java` | mParent tracking, recursive findViewById, addView/removeView |
| ApkLoader | `shim/java/android/app/ApkLoader.java` | ZIP extraction, DEX discovery |
| BinaryXmlParser | `shim/java/android/app/BinaryXmlParser.java` | AXML manifest parsing |
| ApkInfo | `shim/java/android/app/ApkInfo.java` | Parsed manifest data class |

### Test baseline: 758 pass, 5 pre-existing failures

## What Needs To Be Done

### Task 1: Wire ApkLoader → MiniServer.loadApk()

**Goal:** `MiniServer.loadApk("/path/to/app.apk")` parses the manifest and registers everything.

File: `shim/java/android/app/MiniServer.java`

Add:
```java
/**
 * Load an APK: extract DEX files, parse manifest, register activities/services.
 * After this, startActivity() with the launcher intent will work.
 */
public ApkInfo loadApk(String apkPath) throws java.io.IOException {
    ApkInfo info = ApkLoader.load(apkPath);

    // Update package info
    mPackageName = info.packageName;
    mApplication.setPackageName(info.packageName);

    // Register all activities from manifest
    for (String activityName : info.activities) {
        // Create a default intent filter for explicit intents
        mPackageManager.addActivity(activityName, null);
    }

    // Register launcher activity with MAIN/LAUNCHER filter
    if (info.launcherActivity != null) {
        android.content.IntentFilter launcherFilter = new android.content.IntentFilter(
                android.content.Intent.ACTION_MAIN);
        launcherFilter.addCategory(android.content.Intent.CATEGORY_LAUNCHER);
        mPackageManager.addActivity(info.launcherActivity, launcherFilter);
    }

    // Register services
    for (String serviceName : info.services) {
        mPackageManager.addService(serviceName, null);
    }

    return info;
}

/**
 * Load APK and launch the main activity.
 */
public void loadAndLaunch(String apkPath) throws java.io.IOException {
    ApkInfo info = loadApk(apkPath);
    if (info.launcherActivity != null) {
        startActivity(info.launcherActivity);
    } else if (!info.activities.isEmpty()) {
        startActivity(info.activities.get(0));
    }
}
```

### Task 2: Implement Handler/Looper Message Queue

**Goal:** `Handler.post(Runnable)` and `Handler.sendMessageDelayed()` work correctly for single-threaded engine execution.

Currently Handler runs everything synchronously (`post()` calls `r.run()` immediately). This breaks apps that depend on message ordering or deferred execution.

#### 2a. Implement MessageQueue

File: `shim/java/android/os/MessageQueue.java` (new or rewrite existing stub)

```java
package android.os;

import java.util.PriorityQueue;

/**
 * Single-threaded message queue for the engine runtime.
 * Messages are sorted by delivery time (when). poll() returns the next ready message.
 */
public class MessageQueue {
    private final PriorityQueue<Message> mQueue = new PriorityQueue<>(11,
            (a, b) -> Long.compare(a.when, b.when));
    private boolean mQuitting;

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
            msg.target.dispatchMessage(msg);
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

    boolean hasMessages() { return !mQueue.isEmpty(); }
    int size() { return mQueue.size(); }

    void quit(boolean safe) {
        mQuitting = true;
        mQueue.clear();
    }
}
```

#### 2b. Wire Handler to MessageQueue

File: `shim/java/android/os/Handler.java`

Replace synchronous execution with queue-based:

```java
public boolean post(Runnable r) {
    return sendMessageDelayed(Message.obtain(this, r), 0);
}

public boolean postDelayed(Runnable r, long delayMillis) {
    return sendMessageDelayed(Message.obtain(this, r), delayMillis);
}

public boolean sendMessage(Message msg) {
    return sendMessageDelayed(msg, 0);
}

public boolean sendMessageDelayed(Message msg, long delayMillis) {
    msg.target = this;
    long when = SystemClock.uptimeMillis() + delayMillis;
    Looper looper = mLooper != null ? mLooper : Looper.getMainLooper();
    return looper.getQueue().enqueueMessage(msg, when);
}

public void dispatchMessage(Message msg) {
    if (msg.callback != null) {
        msg.callback.run();
    } else if (mCallback != null) {
        mCallback.handleMessage(msg);
    } else {
        handleMessage(msg);
    }
}

public void removeCallbacks(Runnable r) {
    // Remove matching messages from queue
}
```

#### 2c. Wire Looper

File: `shim/java/android/os/Looper.java`

```java
private MessageQueue mQueue;

private Looper() {
    mQueue = new MessageQueue();
}

public MessageQueue getQueue() { return mQueue; }

/** Process all ready messages. Call this from the engine's main loop. */
public static int pumpMessages() {
    Looper main = getMainLooper();
    if (main != null) return main.mQueue.drainReady();
    return 0;
}

/** Process ALL queued messages (for testing). */
public static int flushAll() {
    Looper main = getMainLooper();
    if (main != null) return main.mQueue.drainAll();
    return 0;
}
```

#### 2d. Wire Message

File: `shim/java/android/os/Message.java`

Ensure Message has:
```java
Handler target;
Runnable callback;
long when;
int what;
int arg1, arg2;
Object obj;
Bundle data;

public static Message obtain(Handler h, Runnable r) {
    Message m = obtain();
    m.target = h;
    m.callback = r;
    return m;
}
```

### Task 3: Resources Stub

**Goal:** `context.getResources()` returns a non-null Resources that won't crash apps.

File: `shim/java/android/content/res/Resources.java` (modify existing stub)

Key methods to implement:
```java
// Display metrics — return reasonable defaults
public android.util.DisplayMetrics getDisplayMetrics() {
    android.util.DisplayMetrics dm = new android.util.DisplayMetrics();
    dm.density = 2.0f;        // xhdpi
    dm.densityDpi = 320;
    dm.widthPixels = 1080;
    dm.heightPixels = 1920;
    dm.scaledDensity = 2.0f;
    dm.xdpi = 320f;
    dm.ydpi = 320f;
    return dm;
}

// Configuration
public android.content.res.Configuration getConfiguration() {
    return new android.content.res.Configuration();
}

// String resources — return the key as fallback
public String getString(int resId) {
    return "res:0x" + Integer.toHexString(resId);
}

// Dimension resources — return 0
public float getDimension(int resId) { return 0f; }
public int getDimensionPixelSize(int resId) { return 0; }

// Color resources
public int getColor(int resId) { return 0xFF000000; }
```

Wire Context:
```java
// In Context.java
private Resources mResources;
public Resources getResources() {
    if (mResources == null) mResources = new Resources();
    return mResources;
}
```

### Task 4: ContentResolver Wiring

**Goal:** `context.getContentResolver()` returns a functional ContentResolver that routes to ContentProviders.

File: `shim/java/android/content/ContentResolver.java` (modify existing stub)

```java
// Route query/insert/update/delete to registered ContentProviders via MiniServer
public Cursor query(Uri uri, String[] projection, String selection,
                    String[] selectionArgs, String sortOrder) {
    ContentProvider provider = acquireProvider(uri);
    if (provider != null) {
        return provider.query(uri, projection, selection, selectionArgs, sortOrder);
    }
    return null;
}

public Uri insert(Uri uri, ContentValues values) {
    ContentProvider provider = acquireProvider(uri);
    if (provider != null) return provider.insert(uri, values);
    return null;
}

// acquireProvider resolves authority → ContentProvider via MiniPackageManager
private ContentProvider acquireProvider(Uri uri) {
    if (uri == null) return null;
    String authority = uri.getAuthority();
    // Look up in MiniPackageManager's provider registry
    return MiniServer.get().getPackageManager().resolveProvider(authority);
}
```

### Task 5: Application Class Wiring

**Goal:** App's custom Application subclass gets instantiated and receives callbacks.

File: `shim/java/android/app/MiniServer.java`

In `loadApk()`, after parsing manifest:
```java
// If manifest declares <application android:name=".MyApp">, instantiate it
if (info.applicationClassName != null) {
    try {
        Class<?> appCls = Class.forName(info.applicationClassName);
        mApplication = (Application) appCls.newInstance();
    } catch (Exception e) {
        mApplication = new Application(); // fallback
    }
}
mApplication.setPackageName(info.packageName);
mApplication.onCreate();
```

Add `applicationClassName` to `ApkInfo.java`:
```java
public String applicationClassName;
```

Wire in `BinaryXmlParser.java` — parse `<application android:name="...">`.

### Task 6: Tests

Add to `test-apps/02-headless-cli/src/HeadlessTest.java`:

```java
static void testApkLoaderIntegration() {
    section("ApkLoader → MiniServer integration");

    // Create a test APK with binary manifest
    // ... (create ZIP with AXML manifest + dummy DEX)

    // Load it via MiniServer
    MiniServer.init("com.test");
    MiniServer server = MiniServer.get();
    // server.loadApk(testApk.getAbsolutePath());

    // Verify activities registered
    // Verify launcher detected
    // Verify DEX paths available
}

static void testHandlerMessageQueue() {
    section("Handler / MessageQueue");

    android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

    final int[] counter = {0};
    handler.post(() -> counter[0]++);
    handler.post(() -> counter[0] += 10);

    // Messages should be queued, not executed yet
    // Pump the message queue
    android.os.Looper.pumpMessages();

    check("handler posted 2 runnables", counter[0] == 11);

    // Delayed messages
    handler.postDelayed(() -> counter[0] += 100, 0);
    android.os.Looper.flushAll();
    check("delayed message delivered", counter[0] == 111);
}

static void testResources() {
    section("Resources stub");

    android.content.Context ctx = new android.content.Context();
    android.content.res.Resources res = ctx.getResources();
    check("getResources non-null", res != null);

    android.util.DisplayMetrics dm = res.getDisplayMetrics();
    check("displayMetrics non-null", dm != null);
    check("density > 0", dm.density > 0);
    check("widthPixels > 0", dm.widthPixels > 0);
}
```

## Execution Order

1. **Task 1** (ApkLoader → MiniServer.loadApk) — 15 min, highest impact
2. **Task 2** (Handler/Looper/MessageQueue) — 30 min, unblocks many apps
3. **Task 3** (Resources stub) — 10 min, prevents NPEs
4. **Task 4** (ContentResolver wiring) — 15 min, enables data access
5. **Task 5** (Application class) — 10 min, enables custom app init
6. **Task 6** (Tests) — 15 min

## Verification

```bash
cd test-apps && ./run-local-tests.sh headless
# Must: compile cleanly, new tests pass, existing 758 tests don't regress
# Target: 800+ tests passing after all tasks
```

## Files to Modify

| File | Action |
|------|--------|
| `shim/java/android/app/MiniServer.java` | Add loadApk(), loadAndLaunch() |
| `shim/java/android/app/ApkInfo.java` | Add applicationClassName field |
| `shim/java/android/app/BinaryXmlParser.java` | Parse <application android:name> |
| `shim/java/android/os/MessageQueue.java` | Rewrite: priority queue with time-based delivery |
| `shim/java/android/os/Handler.java` | Wire to MessageQueue instead of synchronous |
| `shim/java/android/os/Looper.java` | Add getQueue(), pumpMessages(), flushAll() |
| `shim/java/android/os/Message.java` | Add target, callback, when fields |
| `shim/java/android/content/res/Resources.java` | Implement getDisplayMetrics, getConfiguration, getString |
| `shim/java/android/content/Context.java` | Wire getResources() |
| `shim/java/android/content/ContentResolver.java` | Route query/insert to ContentProviders |
| `test-apps/02-headless-cli/src/HeadlessTest.java` | Add integration tests |

## Key Rules

- **Don't break existing tests**: 758 pass — no regressions
- **Build on existing code**: MiniServer, MiniActivityManager, etc. are solid — extend, don't rewrite
- **Match AOSP signatures**: apps compiled against real Android SDK depend on exact method signatures
- **Pure Java**: No JNI, no OHBridge for WS2 tasks — everything is in-process Java
- **Self-validating**: every new feature must include tests proving it works
- **Read existing files FIRST before modifying**: understand what's already there
