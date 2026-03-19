package android.content;

import android.database.Cursor;
import android.net.Uri;

/** Stub for android.content.AsyncQueryHandler. */
public class AsyncQueryHandler {
    public AsyncQueryHandler() {}
    public AsyncQueryHandler(ContentResolver cr) {}

    public void startQuery(int token, Object cookie, Uri uri,
            String[] projection, String selection, String[] selectionArgs,
            String orderBy) {}

    public void cancelOperation(int token) {}

    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {}
    protected void onInsertComplete(int token, Object cookie, Uri uri) {}
    protected void onUpdateComplete(int token, Object cookie, int result) {}
    protected void onDeleteComplete(int token, Object cookie, int result) {}

    public void startInsert(int token, Object cookie, Uri uri, ContentValues initialValues) {}
    public void startUpdate(int token, Object cookie, Uri uri, ContentValues values,
            String selection, String[] selectionArgs) {}
    public void startDelete(int token, Object cookie, Uri uri,
            String selection, String[] selectionArgs) {}
}
