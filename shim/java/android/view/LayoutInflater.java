package android.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * LayoutInflater — basic stub implementation for MiniServer.
 *
 * Since we don't have a resource system yet, inflate() returns stub views:
 * - A FrameLayout container for any resource ID
 * - The View gets the resource ID stored for later identification
 *
 * This is enough for apps that call setContentView(R.layout.xxx) to get
 * a non-null View tree that can hold child views.
 */
public class LayoutInflater {
    private Context mContext;

    public LayoutInflater(Context context) {
        mContext = context;
    }

    public LayoutInflater(LayoutInflater original, Context newContext) {
        mContext = newContext;
    }

    /**
     * Get a LayoutInflater from a Context.
     * This is the standard way apps obtain a LayoutInflater.
     */
    public static LayoutInflater from(Context context) {
        if (context == null) return new LayoutInflater(context);
        Object svc = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (svc instanceof LayoutInflater) {
            return (LayoutInflater) svc;
        }
        return new LayoutInflater(context);
    }

    public Context getContext() { return mContext; }

    public LayoutInflater cloneInContext(Context newContext) {
        return new LayoutInflater(this, newContext);
    }

    /**
     * Inflate a layout resource. Since we don't have real resources,
     * returns a stub FrameLayout with the resource ID set.
     */
    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    /**
     * Inflate a layout resource with attachToRoot control.
     */
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        // Create a stub FrameLayout as the inflated view
        View view = new FrameLayout(mContext);
        view.setId(resource);

        if (root != null && attachToRoot) {
            root.addView(view);
            return root;
        }
        return view;
    }

    public View createView(String name, String prefix, AttributeSet attrs) {
        try {
            String fullName = (prefix != null) ? prefix + name : name;
            Class<?> cls = Class.forName(fullName);
            return (View) cls.getConstructor(Context.class).newInstance(mContext);
        } catch (Exception e) {
            return null;
        }
    }

    public View onCreateView(String name, AttributeSet attrs) { return null; }
    public View onCreateView(View parent, String name, AttributeSet attrs) { return null; }

    public Object getFactory() { return null; }
    public Object getFactory2() { return null; }
    public Filter getFilter() { return null; }
    public void setFactory(Object factory) {}
    public void setFactory2(Object factory) {}
    public void setFilter(Filter filter) {}
}
