import android.app.AlertDialog;
import android.app.MiniServer;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SimpleMenu;

import com.example.superapp.TestAsyncTask;
import com.example.superapp.TestProvider;
import com.example.superapp.TestReceiver;
import com.example.superapp.TestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * SuperApp test runner -- stress-tests Android shim APIs.
 * 30+ checks across Handler, AsyncTask, BroadcastReceiver,
 * Service, ContentProvider, Notification, Menu, Timer, Clipboard.
 */
public class SuperAppRunner {

    static int passed = 0;
    static int failed = 0;

    static void check(String name, boolean ok) {
        if (ok) {
            System.out.println("  [PASS] " + name);
            passed++;
        } else {
            System.out.println("  [FAIL] " + name);
            failed++;
        }
    }

    static void section(String name) {
        System.out.println();
        System.out.println("=== " + name + " ===");
    }

    // ---------------------------------------------------------------
    // 1. Handler / Looper / MessageQueue
    // ---------------------------------------------------------------
    static void testHandlerLooper() {
        section("Handler / Looper / MessageQueue");

        // Looper basics
        Looper mainLooper = Looper.getMainLooper();
        check("Looper.getMainLooper() non-null", mainLooper != null);

        Looper myLooper = Looper.myLooper();
        check("Looper.myLooper() non-null", myLooper != null);

        check("Main looper has MessageQueue",
                mainLooper != null && mainLooper.getQueue() != null);

        // Handler with Looper
        Handler handler = new Handler(mainLooper);
        check("Handler(Looper) creates handler", handler != null);
        check("Handler.getLooper() returns same looper",
                handler.getLooper() == mainLooper);

        // Post a Runnable
        final boolean[] runnableRan = {false};
        boolean posted = handler.post(new Runnable() {
            public void run() { runnableRan[0] = true; }
        });
        check("handler.post(Runnable) returns true", posted);

        // Flush to dispatch
        int dispatched = Looper.flushAll();
        check("Looper.flushAll() dispatched >= 1 message", dispatched >= 1);
        check("Posted Runnable was executed", runnableRan[0]);

        // Send Message with what/arg1/arg2/obj
        final int[] receivedWhat = {-1};
        final int[] receivedArg1 = {-1};
        final int[] receivedArg2 = {-1};
        final Object[] receivedObj = {null};
        Handler msgHandler = new Handler(mainLooper) {
            public void handleMessage(Message msg) {
                receivedWhat[0] = msg.what;
                receivedArg1[0] = msg.arg1;
                receivedArg2[0] = msg.arg2;
                receivedObj[0] = msg.obj;
            }
        };
        Message msg = Message.obtain(msgHandler, 42, 10, 20, "payload");
        msgHandler.sendMessage(msg);
        Looper.flushAll();
        check("Message.what received correctly", receivedWhat[0] == 42);
        check("Message.arg1 received correctly", receivedArg1[0] == 10);
        check("Message.arg2 received correctly", receivedArg2[0] == 20);
        check("Message.obj received correctly", "payload".equals(receivedObj[0]));

        // sendEmptyMessage
        final int[] emptyWhat = {-1};
        Handler emptyHandler = new Handler(mainLooper) {
            public void handleMessage(Message msg) {
                emptyWhat[0] = msg.what;
            }
        };
        emptyHandler.sendEmptyMessage(99);
        Looper.flushAll();
        check("sendEmptyMessage dispatched", emptyWhat[0] == 99);

        // sendDelayed -- verify it queues (won't fire until drained)
        final boolean[] delayedRan = {false};
        handler.postDelayed(new Runnable() {
            public void run() { delayedRan[0] = true; }
        }, 100000); // 100 seconds in the future
        int pumpCount = Looper.pumpMessages(); // only pump ready messages
        check("postDelayed does not fire immediately (pumpMessages)",
                !delayedRan[0]);
        // Now flush all regardless of time
        Looper.flushAll();
        check("postDelayed fires on flushAll", delayedRan[0]);

        // Handler.Callback pattern
        final int[] callbackWhat = {-1};
        Handler.Callback cb = new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                callbackWhat[0] = msg.what;
                return true;
            }
        };
        Handler cbHandler = new Handler(mainLooper, cb);
        cbHandler.sendEmptyMessage(77);
        Looper.flushAll();
        check("Handler.Callback received message", callbackWhat[0] == 77);
    }

    // ---------------------------------------------------------------
    // 2. HandlerThread
    // ---------------------------------------------------------------
    static void testHandlerThread() {
        section("HandlerThread");

        HandlerThread ht = new HandlerThread("test-thread");
        check("HandlerThread created", ht != null);
        check("HandlerThread name correct", "test-thread".equals(ht.getName()));

        ht.start();
        ht.waitUntilReady();
        check("HandlerThread started and ready", ht.isAlive());

        boolean quit = ht.quit();
        check("HandlerThread.quit() returns true", quit);

        // Wait for it to actually stop
        try { ht.join(1000); } catch (InterruptedException e) { /* ignore */ }
        check("HandlerThread stopped after quit", !ht.isAlive());
    }

    // ---------------------------------------------------------------
    // 3. AsyncTask
    // ---------------------------------------------------------------
    static void testAsyncTask() {
        section("AsyncTask");

        TestAsyncTask.reset();

        TestAsyncTask task = new TestAsyncTask();
        check("AsyncTask status is PENDING",
                task.getStatus() == AsyncTask.Status.PENDING);

        task.execute("hello");

        // AsyncTask runs on a background thread, wait a bit
        try { Thread.sleep(500); } catch (InterruptedException e) { /* ignore */ }

        check("onPreExecute was called", TestAsyncTask.sPreExecuteCalled);
        check("doInBackground was called", TestAsyncTask.sDoInBackgroundCalled);
        check("Input parameter received", "hello".equals(TestAsyncTask.sInput));
        check("onPostExecute was called", TestAsyncTask.sPostExecuteCalled);
        check("Result correct", "result-hello".equals(TestAsyncTask.sResult));
        check("Status is FINISHED",
                task.getStatus() == AsyncTask.Status.FINISHED);

        // Execution order: onPreExecute first
        check("onPreExecute ran first",
                TestAsyncTask.sExecutionOrder.size() > 0
                && "onPreExecute".equals(TestAsyncTask.sExecutionOrder.get(0)));

        // publishProgress was called
        check("onProgressUpdate was called",
                TestAsyncTask.sProgressValues.size() >= 1);
        if (TestAsyncTask.sProgressValues.size() >= 2) {
            check("Progress value 50 received",
                    TestAsyncTask.sProgressValues.get(0).intValue() == 50);
            check("Progress value 100 received",
                    TestAsyncTask.sProgressValues.get(1).intValue() == 100);
        }

        // Cannot execute twice
        boolean threw = false;
        try {
            task.execute("again");
        } catch (IllegalStateException e) {
            threw = true;
        }
        check("AsyncTask throws on second execute()", threw);
    }

    // ---------------------------------------------------------------
    // 4. BroadcastReceiver
    // ---------------------------------------------------------------
    static void testBroadcastReceiver() {
        section("BroadcastReceiver");

        TestReceiver.reset();
        TestReceiver receiver = new TestReceiver();

        // Direct call test (since Context.sendBroadcast is a stub)
        Intent intent = new Intent("com.example.TEST_ACTION");
        intent.putExtra("key", "value");

        // Test that onReceive works when called directly
        Context ctx = new Context();
        receiver.onReceive(ctx, intent);
        check("onReceive was called", TestReceiver.sReceived);
        check("onReceive got context", TestReceiver.sLastContext != null);
        check("onReceive got intent", TestReceiver.sLastIntent != null);
        check("onReceive intent is correct object",
                TestReceiver.sLastIntent == intent);

        // IntentFilter matching
        IntentFilter filter = new IntentFilter("com.example.TEST_ACTION");
        check("IntentFilter matches action",
                filter.match("com.example.TEST_ACTION"));
        check("IntentFilter does not match wrong action",
                !filter.match("com.example.OTHER_ACTION"));

        // Context.sendBroadcast is a no-op stub -- test that it at least doesn't crash
        boolean sendOk = true;
        try {
            ctx.sendBroadcast(intent);
        } catch (Exception e) {
            sendOk = false;
        }
        check("Context.sendBroadcast() does not crash", sendOk);

        // Context.registerReceiver is missing from Context (only in ContextWrapper)
        // This is a gap we want to discover
        boolean hasRegisterReceiver = false;
        try {
            java.lang.reflect.Method m = Context.class.getMethod(
                    "registerReceiver", BroadcastReceiver.class, IntentFilter.class);
            hasRegisterReceiver = (m != null);
        } catch (NoSuchMethodException e) {
            hasRegisterReceiver = false;
        }
        check("Context has registerReceiver(BroadcastReceiver, IntentFilter)",
                hasRegisterReceiver);
    }

    // ---------------------------------------------------------------
    // 5. Service lifecycle
    // ---------------------------------------------------------------
    static void testServiceLifecycle() {
        section("Service lifecycle");

        TestService.reset();

        // Register the service class with MiniServer's package manager
        MiniServer server = MiniServer.get();
        server.getPackageManager().addService("com.example.superapp.TestService");

        // Create intent
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                server.getPackageName(), "com.example.superapp.TestService"));

        // Start service
        Context ctx = new Context();
        ComponentName cn = ctx.startService(intent);
        check("startService returns ComponentName", cn != null);
        check("Service.onCreate was called", TestService.sCreated);
        check("Service.onStartCommand was called", TestService.sStartCommandCount == 1);
        check("Service received startId", TestService.sLastStartId > 0);

        // Start again (should call onStartCommand again, not onCreate again)
        TestService.sCreated = false; // Reset to detect duplicate onCreate
        ctx.startService(intent);
        check("Second startService increments count",
                TestService.sStartCommandCount == 2);

        // Stop service
        boolean stopped = ctx.stopService(intent);
        check("stopService returns true", stopped);
        check("Service.onDestroy was called", TestService.sDestroyed);
    }

    // ---------------------------------------------------------------
    // 6. ContentProvider (via ContentResolver)
    // ---------------------------------------------------------------
    static void testContentProvider() {
        section("ContentProvider");

        TestProvider.reset();

        // Register provider
        String authority = "com.example.superapp.provider";
        TestProvider provider = new TestProvider();
        MiniServer.get().getPackageManager().addProvider(authority, provider);
        provider.attachInfo(new Context(), new ContentProvider.ProviderInfo(authority));

        check("ContentProvider.onCreate was called", TestProvider.sOnCreateCalled);

        // Get ContentResolver
        Context ctx = new Context();
        ContentResolver resolver = ctx.getContentResolver();
        check("ContentResolver non-null", resolver != null);

        Uri baseUri = Uri.parse("content://" + authority + "/items");

        // Insert
        ContentValues cv = new ContentValues();
        cv.put("name", "item1");
        cv.put("value", "val1");
        Uri insertedUri = resolver.insert(baseUri, cv);
        check("insert() returns Uri", insertedUri != null);
        if (insertedUri != null) {
            check("insert() Uri contains id",
                    insertedUri.toString().endsWith("/1"));
        }

        // Insert second row
        ContentValues cv2 = new ContentValues();
        cv2.put("name", "item2");
        cv2.put("value", "val2");
        resolver.insert(baseUri, cv2);

        // Query
        Cursor cursor = resolver.query(baseUri, null, null, null, null);
        check("query() returns Cursor", cursor != null);
        if (cursor != null) {
            int count = cursor.getCount();
            check("query() returns 2 rows", count == 2);

            boolean moved = cursor.moveToFirst();
            check("cursor.moveToFirst() works", moved);
            if (moved) {
                int nameIdx = cursor.getColumnIndex("name");
                check("cursor has 'name' column", nameIdx >= 0);
                if (nameIdx >= 0) {
                    String name = cursor.getString(nameIdx);
                    check("first row name is 'item1'", "item1".equals(name));
                }
            }
            cursor.close();
        }

        // Update
        ContentValues updateVals = new ContentValues();
        updateVals.put("value", "updated");
        int updateCount = resolver.update(baseUri, updateVals,
                "name=?", new String[]{"item1"});
        check("update() returns 1", updateCount == 1);

        // Delete
        int deleteCount = resolver.delete(baseUri,
                "name=?", new String[]{"item2"});
        check("delete() returns 1", deleteCount == 1);

        // Verify after delete
        Cursor cursor2 = resolver.query(baseUri, null, null, null, null);
        if (cursor2 != null) {
            check("After delete, 1 row remains", cursor2.getCount() == 1);
            cursor2.close();
        }

        // getType
        String type = provider.getType(baseUri);
        check("getType() returns MIME type", type != null);
    }

    // ---------------------------------------------------------------
    // 7. AlertDialog.Builder
    // ---------------------------------------------------------------
    static void testAlertDialog() {
        section("AlertDialog.Builder");

        // AlertDialog is mostly a stub -- let's see what works
        AlertDialog dialog = new AlertDialog();
        check("AlertDialog created", dialog != null);

        // Test setMessage doesn't crash
        boolean ok = true;
        try {
            dialog.setMessage("Test message");
        } catch (Exception e) {
            ok = false;
        }
        check("AlertDialog.setMessage() does not crash", ok);

        // Check if Builder exists -- it doesn't in the current shim
        boolean hasBuilder = false;
        try {
            Class<?> builderClass = Class.forName("android.app.AlertDialog$Builder");
            hasBuilder = true;
        } catch (ClassNotFoundException e) {
            hasBuilder = false;
        }
        check("AlertDialog.Builder class exists", hasBuilder);
    }

    // ---------------------------------------------------------------
    // 8. Notification.Builder
    // ---------------------------------------------------------------
    static void testNotification() {
        section("Notification.Builder");

        Context ctx = new Context();
        Notification.Builder builder = new Notification.Builder(ctx, "test-channel");
        check("Notification.Builder created", builder != null);

        builder.setContentTitle("Test Title");
        builder.setContentText("Test Body");
        builder.setSmallIcon(0x7f080001);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        builder.setCategory(Notification.CATEGORY_MESSAGE);

        Notification notif = builder.build();
        check("Notification.build() returns object", notif != null);
        // shimTitle/shimText are package-private -- test via extras
        check("Notification extras has EXTRA_TITLE in extras",
                notif.extras != null
                && "Test Title".equals(notif.extras.getString(Notification.EXTRA_TITLE)));
        check("Notification extras has EXTRA_TEXT in extras",
                notif.extras != null
                && "Test Body".equals(notif.extras.getString(Notification.EXTRA_TEXT)));
        check("Notification channel set", "test-channel".equals(notif.getChannelId()));
        check("Notification icon set", notif.icon == 0x7f080001);
        check("Notification priority set", notif.priority == Notification.PRIORITY_HIGH);
        check("Notification category set",
                Notification.CATEGORY_MESSAGE.equals(notif.category));
        check("Notification FLAG_AUTO_CANCEL set",
                (notif.flags & Notification.FLAG_AUTO_CANCEL) != 0);

        // Notification.clone test
        Notification cloned = notif.clone();
        check("Notification.clone() works",
                cloned != null && cloned.getChannelId() != null
                && cloned.getChannelId().equals(notif.getChannelId()));
    }

    // ---------------------------------------------------------------
    // 9. Menu / MenuItem
    // ---------------------------------------------------------------
    static void testMenu() {
        section("Menu / MenuItem");

        SimpleMenu menu = new SimpleMenu();
        check("SimpleMenu created", menu != null);
        check("Empty menu size is 0", menu.size() == 0);

        MenuItem item1 = menu.add(0, 101, 0, "Copy");
        check("add() returns MenuItem", item1 != null);
        check("MenuItem id correct", item1.getItemId() == 101);
        check("MenuItem title correct", "Copy".equals(item1.getTitle().toString()));

        MenuItem item2 = menu.add(0, 102, 1, "Paste");
        MenuItem item3 = menu.add(0, 103, 2, "Delete");
        check("Menu size after 3 adds", menu.size() == 3);

        // findItem
        MenuItem found = menu.findItem(102);
        check("findItem by id", found != null && "Paste".equals(found.getTitle().toString()));

        // findItem for missing
        MenuItem missing = menu.findItem(999);
        check("findItem returns null for missing", missing == null);

        // removeItem
        menu.removeItem(102);
        check("removeItem reduces size", menu.size() == 2);
        check("Removed item not found", menu.findItem(102) == null);

        // setTitle on MenuItem
        item1.setTitle("Cut");
        check("MenuItem.setTitle works", "Cut".equals(item1.getTitle().toString()));

        // clear
        menu.clear();
        check("Menu.clear() empties menu", menu.size() == 0);
    }

    // ---------------------------------------------------------------
    // 10. Timer / TimerTask
    // ---------------------------------------------------------------
    static void testTimer() {
        section("Timer / TimerTask");

        final boolean[] timerFired = {false};
        Timer timer = new Timer("test-timer", true);
        timer.schedule(new TimerTask() {
            public void run() { timerFired[0] = true; }
        }, 50);

        // Wait for it
        try { Thread.sleep(200); } catch (InterruptedException e) { /* ignore */ }
        check("TimerTask fired", timerFired[0]);

        timer.cancel();
        check("Timer.cancel() does not crash", true);
    }

    // ---------------------------------------------------------------
    // 11. Clipboard
    // ---------------------------------------------------------------
    static void testClipboard() {
        section("Clipboard");

        ClipboardManager cm = new ClipboardManager();
        check("ClipboardManager created", cm != null);
        check("Initially no primary clip", !cm.hasPrimaryClip());

        // Set plain text
        ClipData clip = ClipData.newPlainText("label", "Hello Clipboard");
        cm.setPrimaryClip(clip);
        check("hasPrimaryClip after set", cm.hasPrimaryClip());

        ClipData retrieved = cm.getPrimaryClip();
        check("getPrimaryClip non-null", retrieved != null);
        if (retrieved != null) {
            check("Clip item count is 1", retrieved.getItemCount() == 1);
            CharSequence text = retrieved.getItemAt(0).getText();
            check("Clip text matches", "Hello Clipboard".equals(text.toString()));
        }

        // Listener
        final boolean[] listenerFired = {false};
        ClipboardManager.OnPrimaryClipChangedListener listener =
                new ClipboardManager.OnPrimaryClipChangedListener() {
            public void onPrimaryClipChanged() { listenerFired[0] = true; }
        };
        cm.addPrimaryClipChangedListener(listener);
        cm.setPrimaryClip(ClipData.newPlainText("label2", "Second clip"));
        check("Clipboard listener fired on change", listenerFired[0]);

        // Clear
        cm.clearPrimaryClip();
        check("clearPrimaryClip works", !cm.hasPrimaryClip());
    }

    // ---------------------------------------------------------------
    // 12. Message pool and recycling
    // ---------------------------------------------------------------
    static void testMessagePool() {
        section("Message pool");

        Message m1 = Message.obtain();
        check("Message.obtain() returns object", m1 != null);
        m1.what = 123;
        m1.arg1 = 456;
        m1.arg2 = 789;
        m1.obj = "test";

        m1.recycle();
        check("Message.recycle() resets what", m1.what == 0);
        check("Message.recycle() resets arg1", m1.arg1 == 0);
        check("Message.recycle() resets obj", m1.obj == null);

        // Obtain should reuse recycled message
        Message m2 = Message.obtain();
        check("Message.obtain() reuses recycled (same object)", m2 == m1);

        // Obtain with handler
        Handler h = new Handler(Looper.getMainLooper());
        m2.recycle();
        Message m3 = Message.obtain(h, 55, 1, 2, "data");
        check("Message.obtain(handler,what,arg1,arg2,obj) sets what", m3.what == 55);
        check("Message.obtain sets arg1", m3.arg1 == 1);
        check("Message.obtain sets arg2", m3.arg2 == 2);
        check("Message.obtain sets obj", "data".equals(m3.obj));
        check("Message.obtain sets target", m3.target == h);
        m3.recycle();
    }

    // ---------------------------------------------------------------
    // Main
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("SuperApp Test Runner");
        System.out.println("====================");

        // Initialize MiniServer
        MiniServer.init("com.example.superapp");

        testHandlerLooper();
        testHandlerThread();
        testAsyncTask();
        testBroadcastReceiver();
        testServiceLifecycle();
        testContentProvider();
        testAlertDialog();
        testNotification();
        testMenu();
        testTimer();
        testClipboard();
        testMessagePool();

        System.out.println();
        System.out.println("====================");
        System.out.println("SuperApp results: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
