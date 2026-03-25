package android.database.sqlite;

import android.database.Cursor;
import java.lang.reflect.Method;

/**
 * Wraps the phone's real framework Cursor object and exposes it through our shim Cursor interface.
 * Uses reflection to call methods on the framework Cursor across the classloader boundary.
 */
public class CursorBridge implements Cursor {

    private final Object realCursor;
    private Method mMoveToFirst, mMoveToNext, mMoveToPrevious, mMoveToPosition, mMoveToLast;
    private Method mGetCount, mGetColumnCount, mGetColumnIndex, mGetColumnName, mGetColumnNames;
    private Method mGetString, mGetInt, mGetLong, mGetFloat, mGetDouble, mGetBlob;
    private Method mIsNull, mIsAfterLast, mIsBeforeFirst, mIsFirst, mIsLast;
    private Method mGetPosition, mClose, mIsClosed;

    public CursorBridge(Object realCursor) {
        this.realCursor = realCursor;
        try {
            Class<?> c = realCursor.getClass();
            mMoveToFirst = c.getMethod("moveToFirst");
            mMoveToNext = c.getMethod("moveToNext");
            mMoveToPrevious = c.getMethod("moveToPrevious");
            mMoveToPosition = c.getMethod("moveToPosition", int.class);
            mMoveToLast = c.getMethod("moveToLast");
            mGetCount = c.getMethod("getCount");
            mGetColumnCount = c.getMethod("getColumnCount");
            mGetColumnIndex = c.getMethod("getColumnIndex", String.class);
            mGetColumnName = c.getMethod("getColumnName", int.class);
            mGetColumnNames = c.getMethod("getColumnNames");
            mGetString = c.getMethod("getString", int.class);
            mGetInt = c.getMethod("getInt", int.class);
            mGetLong = c.getMethod("getLong", int.class);
            mGetFloat = c.getMethod("getFloat", int.class);
            mGetDouble = c.getMethod("getDouble", int.class);
            mGetBlob = c.getMethod("getBlob", int.class);
            mIsNull = c.getMethod("isNull", int.class);
            mIsAfterLast = c.getMethod("isAfterLast");
            mIsBeforeFirst = c.getMethod("isBeforeFirst");
            mIsFirst = c.getMethod("isFirst");
            mIsLast = c.getMethod("isLast");
            mGetPosition = c.getMethod("getPosition");
            mClose = c.getMethod("close");
            mIsClosed = c.getMethod("isClosed");
        } catch (Exception e) {
            System.out.println("[CursorBridge] Method lookup failed: " + e);
        }
    }

    private Object call(Method m, Object... args) {
        try { return m.invoke(realCursor, args); }
        catch (Exception e) { return null; }
    }

    public boolean moveToFirst() { return (Boolean) call(mMoveToFirst); }
    public boolean moveToNext() { return (Boolean) call(mMoveToNext); }
    public boolean moveToPrevious() { return (Boolean) call(mMoveToPrevious); }
    public boolean moveToPosition(int position) { return (Boolean) call(mMoveToPosition, position); }
    public boolean moveToLast() { return (Boolean) call(mMoveToLast); }
    public int getCount() { return (Integer) call(mGetCount); }
    public int getColumnCount() { return (Integer) call(mGetColumnCount); }
    public int getColumnIndex(String columnName) { return (Integer) call(mGetColumnIndex, columnName); }
    public String getColumnName(int columnIndex) { return (String) call(mGetColumnName, columnIndex); }
    public String[] getColumnNames() { return (String[]) call(mGetColumnNames); }
    public String getString(int columnIndex) { return (String) call(mGetString, columnIndex); }
    public int getInt(int columnIndex) { return (Integer) call(mGetInt, columnIndex); }
    public long getLong(int columnIndex) { return (Long) call(mGetLong, columnIndex); }
    public float getFloat(int columnIndex) { return (Float) call(mGetFloat, columnIndex); }
    public double getDouble(int columnIndex) { return (Double) call(mGetDouble, columnIndex); }
    public byte[] getBlob(int columnIndex) { return (byte[]) call(mGetBlob, columnIndex); }
    public boolean isNull(int columnIndex) { return (Boolean) call(mIsNull, columnIndex); }
    public boolean isAfterLast() { return (Boolean) call(mIsAfterLast); }
    public boolean isBeforeFirst() { return (Boolean) call(mIsBeforeFirst); }
    public boolean isFirst() { return (Boolean) call(mIsFirst); }
    public boolean isLast() { return (Boolean) call(mIsLast); }
    public int getPosition() { return (Integer) call(mGetPosition); }
    public void close() { call(mClose); }
    public boolean isClosed() { return (Boolean) call(mIsClosed); }

    public int getColumnIndexOrThrow(String columnName) {
        int idx = getColumnIndex(columnName);
        if (idx < 0) throw new IllegalArgumentException("column '" + columnName + "' does not exist");
        return idx;
    }

    // Stubs for less common methods
    public boolean move(int offset) { return moveToPosition(getPosition() + offset); }
    public int getType(int columnIndex) { return 3; /* FIELD_TYPE_STRING */ }
    public void setNotificationUri(android.content.ContentResolver cr, android.net.Uri uri) {}
    public void setNotificationUris(android.content.ContentResolver cr, java.util.List<Object> uris) {}
    public android.net.Uri getNotificationUri() { return null; }
    public android.os.Bundle getExtras() { return new android.os.Bundle(); }
    public android.os.Bundle respond(android.os.Bundle extras) { return new android.os.Bundle(); }
    public boolean requery() { return false; }
    public void deactivate() {}
    public void registerContentObserver(android.database.ContentObserver observer) {}
    public void unregisterContentObserver(android.database.ContentObserver observer) {}
    public void registerDataSetObserver(android.database.DataSetObserver observer) {}
    public void unregisterDataSetObserver(android.database.DataSetObserver observer) {}
    public void copyStringToBuffer(int columnIndex, android.database.CharArrayBuffer buffer) {}
    public boolean getWantsAllOnMoveCalls() { return false; }
    public short getShort(int columnIndex) { return (short) getInt(columnIndex); }
    public void setExtras(android.os.Bundle extras) {}
}
