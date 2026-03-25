package android.database.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import java.lang.reflect.Method;

/**
 * Bridges our shim SQLiteDatabase to the phone's real SQLiteDatabase when running
 * on a real Android phone. Uses the parent classloader (framework) via reflection.
 *
 * On OHOS/headless: returns null, falls back to in-memory shim.
 * On phone: delegates to real android.database.sqlite.SQLiteDatabase.
 */
public class SQLiteBridge {

    private static boolean phoneDetected;
    private static boolean initialized;
    private static Class<?> realDbClass;
    private static Class<?> realCursorFactoryClass;

    static {
        try {
            // Check if we're on a real Android phone
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            Object instance = host.getField("instance").get(null);
            phoneDetected = (instance != null);
        } catch (Exception e) {
            phoneDetected = false;
        }
    }

    public static boolean isOnPhone() {
        return phoneDetected;
    }

    /**
     * Open or create a real SQLite database via the framework.
     * Returns the framework's SQLiteDatabase object, or null if not on phone.
     */
    public static Object openRealDatabase(String path) {
        if (!phoneDetected) return null;
        try {
            ensureInit();
            // Call android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(path, null)
            Method m = realDbClass.getMethod("openOrCreateDatabase", String.class, realCursorFactoryClass);
            return m.invoke(null, path, null);
        } catch (Exception e) {
            System.out.println("[SQLiteBridge] openRealDatabase failed: " + e);
            return null;
        }
    }

    public static void execSQL(Object realDb, String sql) {
        if (realDb == null) return;
        try {
            Method m = realDb.getClass().getMethod("execSQL", String.class);
            m.invoke(realDb, sql);
        } catch (Exception e) {
            System.out.println("[SQLiteBridge] execSQL failed: " + e);
        }
    }

    public static void execSQL(Object realDb, String sql, Object[] bindArgs) {
        if (realDb == null) return;
        try {
            Method m = realDb.getClass().getMethod("execSQL", String.class, Object[].class);
            m.invoke(realDb, sql, bindArgs);
        } catch (Exception e) {
            System.out.println("[SQLiteBridge] execSQL(args) failed: " + e);
        }
    }

    public static long insert(Object realDb, String table, String nullColumnHack, ContentValues values) {
        if (realDb == null) return -1;
        try {
            // ContentValues from our shim may not be compatible with framework's ContentValues
            // Create a framework ContentValues via reflection
            Class<?> cvClass = realDb.getClass().getClassLoader().loadClass("android.content.ContentValues");
            Object realCv = cvClass.newInstance();
            Method putString = cvClass.getMethod("put", String.class, String.class);
            Method putInt = cvClass.getMethod("put", String.class, Integer.class);
            Method putLong = cvClass.getMethod("put", String.class, Long.class);
            Method putDouble = cvClass.getMethod("put", String.class, Double.class);

            // Copy values
            for (String key : values.keySet()) {
                Object val = values.get(key);
                if (val == null) {
                    cvClass.getMethod("putNull", String.class).invoke(realCv, key);
                } else if (val instanceof String) {
                    putString.invoke(realCv, key, (String) val);
                } else if (val instanceof Integer) {
                    putInt.invoke(realCv, key, (Integer) val);
                } else if (val instanceof Long) {
                    putLong.invoke(realCv, key, (Long) val);
                } else if (val instanceof Double) {
                    putDouble.invoke(realCv, key, (Double) val);
                } else if (val instanceof Float) {
                    cvClass.getMethod("put", String.class, Float.class).invoke(realCv, key, (Float) val);
                } else {
                    putString.invoke(realCv, key, val.toString());
                }
            }

            Method m = realDb.getClass().getMethod("insert", String.class, String.class, cvClass);
            return (Long) m.invoke(realDb, table, nullColumnHack, realCv);
        } catch (Exception e) {
            System.out.println("[SQLiteBridge] insert failed: " + e);
            return -1;
        }
    }

    /**
     * Query and wrap the result in our shim Cursor.
     */
    public static Cursor query(Object realDb, String table, String[] columns, String selection,
                                String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (realDb == null) return null;
        try {
            Method m = realDb.getClass().getMethod("query",
                String.class, String[].class, String.class, String[].class,
                String.class, String.class, String.class);
            Object realCursor = m.invoke(realDb, table, columns, selection, selectionArgs, groupBy, having, orderBy);
            if (realCursor == null) return null;
            return new CursorBridge(realCursor);
        } catch (Exception e) {
            System.out.println("[SQLiteBridge] query failed: " + e);
            return null;
        }
    }

    public static Cursor rawQuery(Object realDb, String sql, String[] selectionArgs) {
        if (realDb == null) return null;
        try {
            Method m = realDb.getClass().getMethod("rawQuery", String.class, String[].class);
            Object realCursor = m.invoke(realDb, sql, selectionArgs);
            if (realCursor == null) return null;
            return new CursorBridge(realCursor);
        } catch (Exception e) {
            System.out.println("[SQLiteBridge] rawQuery failed: " + e);
            return null;
        }
    }

    public static int delete(Object realDb, String table, String whereClause, String[] whereArgs) {
        if (realDb == null) return 0;
        try {
            Method m = realDb.getClass().getMethod("delete", String.class, String.class, String[].class);
            return (Integer) m.invoke(realDb, table, whereClause, whereArgs);
        } catch (Exception e) {
            System.out.println("[SQLiteBridge] delete failed: " + e);
            return 0;
        }
    }

    public static int update(Object realDb, String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (realDb == null) return 0;
        try {
            Class<?> cvClass = realDb.getClass().getClassLoader().loadClass("android.content.ContentValues");
            Object realCv = cvClass.newInstance();
            Method putString = cvClass.getMethod("put", String.class, String.class);

            for (String key : values.keySet()) {
                Object val = values.get(key);
                if (val != null) putString.invoke(realCv, key, val.toString());
            }

            Method m = realDb.getClass().getMethod("update", String.class, cvClass, String.class, String[].class);
            return (Integer) m.invoke(realDb, table, realCv, whereClause, whereArgs);
        } catch (Exception e) {
            System.out.println("[SQLiteBridge] update failed: " + e);
            return 0;
        }
    }

    public static void close(Object realDb) {
        if (realDb == null) return;
        try {
            realDb.getClass().getMethod("close").invoke(realDb);
        } catch (Exception e) {}
    }

    public static void beginTransaction(Object realDb) {
        if (realDb == null) return;
        try {
            realDb.getClass().getMethod("beginTransaction").invoke(realDb);
        } catch (Exception e) {}
    }

    public static void setTransactionSuccessful(Object realDb) {
        if (realDb == null) return;
        try {
            realDb.getClass().getMethod("setTransactionSuccessful").invoke(realDb);
        } catch (Exception e) {}
    }

    public static void endTransaction(Object realDb) {
        if (realDb == null) return;
        try {
            realDb.getClass().getMethod("endTransaction").invoke(realDb);
        } catch (Exception e) {}
    }

    public static int getVersion(Object realDb) {
        if (realDb == null) return 0;
        try {
            return (Integer) realDb.getClass().getMethod("getVersion").invoke(realDb);
        } catch (Exception e) { return 0; }
    }

    public static void setVersion(Object realDb, int version) {
        if (realDb == null) return;
        try {
            realDb.getClass().getMethod("setVersion", int.class).invoke(realDb, version);
        } catch (Exception e) {}
    }

    private static void ensureInit() throws Exception {
        if (!initialized) {
            // Load the REAL framework SQLiteDatabase from parent classloader
            ClassLoader parent = SQLiteBridge.class.getClassLoader().getParent();
            if (parent == null) parent = ClassLoader.getSystemClassLoader();
            realDbClass = parent.loadClass("android.database.sqlite.SQLiteDatabase");
            try {
                realCursorFactoryClass = parent.loadClass("android.database.sqlite.SQLiteDatabase$CursorFactory");
            } catch (ClassNotFoundException e) {
                // Some versions use different inner class name
                realCursorFactoryClass = null;
            }
            initialized = true;
            System.out.println("[SQLiteBridge] Initialized with real framework SQLiteDatabase");
        }
    }
}
