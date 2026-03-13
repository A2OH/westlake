package android.content;
import android.content.pm.PathPermission;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class ContentProvider implements ComponentCallbacks2 {

    private Context mContext;

    public ContentProvider() {}

    /** Inner class for provider metadata. */
    public static class ProviderInfo {
        public String authority;
        public ProviderInfo() {}
        public ProviderInfo(String authority) { this.authority = authority; }
    }

    public void attachInfo(Context context, Object info) {
        mContext = context;
    }

    public Context getContext() { return mContext; }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        int count = 0;
        if (values != null) {
            for (ContentValues v : values) {
                if (insert(uri, v) != null) count++;
            }
        }
        return count;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null; }
    public Uri insert(Uri uri, ContentValues values) { return null; }
    public String getType(Uri uri) { return null; }
    public Bundle call(String method, String arg, Bundle extras) { return null; }

    public int delete(Uri p0, String p1, String[] p2) { return 0; }
    public int delete(Uri p0, Bundle p1) { return 0; }
    public void dump(FileDescriptor p0, PrintWriter p1, String[] p2) {}
    public boolean isTemporary() { return false; }
    public void onCallingPackageChanged() {}
    public void onConfigurationChanged(Configuration p0) {}
    public boolean onCreate() { return false; }
    public void onLowMemory() {}
    public void onTrimMemory(int p0) {}
    public boolean refresh(Uri p0, Bundle p1, CancellationSignal p2) { return false; }
    public void restoreCallingIdentity(Object p0) {}
    public void setPathPermissions(PathPermission[] p0) {}
    public void setReadPermission(String p0) {}
    public void setWritePermission(String p0) {}
    public void shutdown() {}
    public int update(Uri p0, ContentValues p1, String p2, String[] p3) { return 0; }
    public int update(Uri p0, ContentValues p1, Bundle p2) { return 0; }
}
