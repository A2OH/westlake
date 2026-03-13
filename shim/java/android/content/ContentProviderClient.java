package android.content;

import android.database.Cursor;
import android.net.Uri;
import java.io.Closeable;

/**
 * Android-compatible ContentProviderClient shim. Stub — returns empty results.
 */
public class ContentProviderClient implements Closeable {
    private boolean mClosed;
    private final String mAuthority;

    public ContentProviderClient(String authority) {
        mAuthority = authority;
    }

    public Cursor query(Uri url, String[] projection, String selection,
                       String[] selectionArgs, String sortOrder) {
        return null; // stub
    }

    public Uri insert(Uri url, ContentValues values) {
        return url; // stub
    }

    public int update(Uri url, ContentValues values, String selection, String[] selectionArgs) {
        return 0; // stub
    }

    public int delete(Uri url, String selection, String[] selectionArgs) {
        return 0; // stub
    }

    public boolean release() {
        mClosed = true;
        return true;
    }

    @Override
    public void close() {
        release();
    }
}
