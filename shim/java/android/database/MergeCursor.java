package android.database;

/**
 * Shim: android.database.MergeCursor
 * Pure Java — concatenates multiple Cursors into one.
 */
public class MergeCursor extends AbstractCursor {

    private final Cursor[] cursors;
    private int activeCursorIndex = -1;
    private Cursor activeCursor;
    private int totalCount = -1;
    private int position = -1;

    public MergeCursor(Cursor[] cursors) {
        this.cursors = cursors != null ? cursors : new Cursor[0];
    }

    @Override
    public int getCount() {
        if (totalCount < 0) {
            totalCount = 0;
            for (Cursor c : cursors) {
                if (c != null) totalCount += c.getCount();
            }
        }
        return totalCount;
    }

    @Override
    public String[] getColumnNames() {
        for (Cursor c : cursors) {
            if (c != null) return c.getColumnNames();
        }
        return new String[0];
    }

    @Override
    public int getColumnCount() {
        for (Cursor c : cursors) {
            if (c != null) return c.getColumnCount();
        }
        return 0;
    }

    @Override
    public int getColumnIndex(String columnName) {
        for (Cursor c : cursors) {
            if (c != null) return c.getColumnIndex(columnName);
        }
        return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) {
        for (Cursor c : cursors) {
            if (c != null) return c.getColumnIndexOrThrow(columnName);
        }
        throw new IllegalArgumentException("column '" + columnName + "' does not exist");
    }

    @Override
    public String getColumnName(int columnIndex) {
        for (Cursor c : cursors) {
            if (c != null) return c.getColumnName(columnIndex);
        }
        return null;
    }

    private boolean updateActiveCursor(int pos) {
        if (pos < 0 || pos >= getCount()) {
            activeCursorIndex = -1;
            activeCursor = null;
            position = pos < 0 ? -1 : getCount();
            return false;
        }
        position = pos;
        int remaining = pos;
        for (int i = 0; i < cursors.length; i++) {
            if (cursors[i] == null) continue;
            int count = cursors[i].getCount();
            if (remaining < count) {
                activeCursorIndex = i;
                activeCursor = cursors[i];
                activeCursor.moveToPosition(remaining);
                return true;
            }
            remaining -= count;
        }
        activeCursorIndex = -1;
        activeCursor = null;
        return false;
    }

    @Override public int getPosition() { return position; }
    @Override public boolean moveToPosition(int pos) { return updateActiveCursor(pos); }
    @Override public boolean moveToFirst() { return moveToPosition(0); }
    @Override public boolean moveToLast() { return moveToPosition(getCount() - 1); }
    @Override public boolean moveToNext() { return moveToPosition(position + 1); }
    @Override public boolean moveToPrevious() { return moveToPosition(position - 1); }
    @Override public boolean move(int offset) { return moveToPosition(position + offset); }

    @Override public boolean isBeforeFirst() { return getCount() > 0 && position == -1; }
    @Override public boolean isAfterLast() { return getCount() > 0 && position == getCount(); }
    @Override public boolean isFirst() { return position == 0 && getCount() > 0; }
    @Override public boolean isLast() { return position == getCount() - 1 && getCount() > 0; }

    private void checkActive() {
        if (activeCursor == null) throw new CursorIndexOutOfBoundsException(position, getCount());
    }

    @Override public String getString(int column) { checkActive(); return activeCursor.getString(column); }
    @Override public int getInt(int column) { checkActive(); return activeCursor.getInt(column); }
    @Override public long getLong(int column) { checkActive(); return activeCursor.getLong(column); }
    @Override public float getFloat(int column) { checkActive(); return activeCursor.getFloat(column); }
    @Override public double getDouble(int column) { checkActive(); return activeCursor.getDouble(column); }
    @Override public short getShort(int column) { checkActive(); return activeCursor.getShort(column); }
    @Override public boolean isNull(int column) { checkActive(); return activeCursor.isNull(column); }
    @Override public byte[] getBlob(int column) { checkActive(); return activeCursor.getBlob(column); }
    @Override public int getType(int column) { checkActive(); return activeCursor.getType(column); }

    @Override
    public void close() {
        for (Cursor c : cursors) { if (c != null) c.close(); }
    }
}
