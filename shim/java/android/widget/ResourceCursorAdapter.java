package android.widget;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

public class ResourceCursorAdapter extends CursorAdapter {
    public ResourceCursorAdapter(Context p0, int p1, Cursor p2, boolean p3) { super(p0, p2, p3); }
    public ResourceCursorAdapter(Context p0, int p1, Cursor p2, int p3) { super(p0, p2, p3 != 0); }

    public void bindView(View view, Context context, Cursor cursor) {}
    public View newView(Context p0, Cursor p1, ViewGroup p2) { return null; }
    public void setDropDownViewResource(int p0) {}
    public void setViewResource(int p0) {}
}
