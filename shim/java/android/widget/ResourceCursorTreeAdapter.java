package android.widget;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

public class ResourceCursorTreeAdapter extends CursorTreeAdapter {
    public ResourceCursorTreeAdapter(Context p0, Cursor p1, int p2, int p3, int p4, int p5) { super(p1, p0); }
    public ResourceCursorTreeAdapter(Context p0, Cursor p1, int p2, int p3, int p4) { super(p1, p0); }
    public ResourceCursorTreeAdapter(Context p0, Cursor p1, int p2, int p3) { super(p1, p0); }

    protected Cursor getChildrenCursor(Cursor groupCursor) { return null; }
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {}
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {}
    public View newChildView(Context p0, Cursor p1, boolean p2, ViewGroup p3) { return null; }
    public View newGroupView(Context p0, Cursor p1, boolean p2, ViewGroup p3) { return null; }
}
