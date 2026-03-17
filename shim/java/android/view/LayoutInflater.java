package android.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;

/**
 * LayoutInflater — inflates layout resources into View trees.
 *
 * Supports three inflation strategies (tried in order):
 * 1. Programmatic layout registry — apps register ViewFactory builders for resource IDs
 * 2. Binary layout XML from APK — parses AXML format layout files
 * 3. Fallback — returns an empty FrameLayout with the resource ID set
 */
public class LayoutInflater {
    private Context mContext;

    // ── Programmatic layout registry ──────────────────────────────────────
    /**
     * Factory interface for programmatic layout creation.
     * Apps can register View tree builders for specific resource IDs,
     * enabling programmatic UI construction via LayoutInflater.inflate().
     */
    public interface ViewFactory {
        View createView(Context context, ViewGroup parent);
    }

    private static HashMap sLayoutRegistry = new HashMap();

    /**
     * Register a programmatic layout builder for the given resource ID.
     * When inflate() is called with this resource ID, the factory will be
     * invoked instead of parsing binary XML.
     */
    public static void registerLayout(int resourceId, ViewFactory factory) {
        sLayoutRegistry.put(Integer.valueOf(resourceId), factory);
    }

    /**
     * Unregister a previously registered layout factory.
     */
    public static void unregisterLayout(int resourceId) {
        sLayoutRegistry.remove(Integer.valueOf(resourceId));
    }

    /**
     * Check if a programmatic layout is registered for the given resource ID.
     */
    public static boolean hasRegisteredLayout(int resourceId) {
        return sLayoutRegistry.containsKey(Integer.valueOf(resourceId));
    }

    // ── View tag-to-class mapping ─────────────────────────────────────────
    private static final HashMap sTagClassMap = new HashMap();
    static {
        // android.view.* classes
        sTagClassMap.put("View", "android.view.View");
        sTagClassMap.put("ViewGroup", "android.view.ViewGroup");
        sTagClassMap.put("SurfaceView", "android.view.SurfaceView");
        sTagClassMap.put("TextureView", "android.view.TextureView");
        // android.widget.* classes
        sTagClassMap.put("LinearLayout", "android.widget.LinearLayout");
        sTagClassMap.put("RelativeLayout", "android.widget.FrameLayout"); // approximate
        sTagClassMap.put("FrameLayout", "android.widget.FrameLayout");
        sTagClassMap.put("ScrollView", "android.widget.ScrollView");
        sTagClassMap.put("TextView", "android.widget.TextView");
        sTagClassMap.put("Button", "android.widget.Button");
        sTagClassMap.put("EditText", "android.widget.EditText");
        sTagClassMap.put("ImageView", "android.widget.ImageView");
        sTagClassMap.put("CheckBox", "android.widget.CheckBox");
        sTagClassMap.put("RadioButton", "android.widget.RadioButton");
        sTagClassMap.put("Switch", "android.widget.Switch");
        sTagClassMap.put("SeekBar", "android.widget.SeekBar");
        sTagClassMap.put("ProgressBar", "android.widget.ProgressBar");
        sTagClassMap.put("ListView", "android.widget.ListView");
        sTagClassMap.put("Spinner", "android.widget.Spinner");
        sTagClassMap.put("WebView", "android.webkit.WebView");
        // Placeholder tags (no-op)
        sTagClassMap.put("include", null);
        sTagClassMap.put("merge", null);
        sTagClassMap.put("fragment", null);
        sTagClassMap.put("requestFocus", null);
    }

    /**
     * Create a View from an XML tag name.
     * Handles fully-qualified class names ("com.example.MyView"),
     * short Android names ("LinearLayout" -> android.widget.LinearLayout),
     * and special tags ("include", "merge", "fragment" -> null/placeholder).
     */
    public View createViewFromTag(String tagName) {
        if (tagName == null) return null;

        // Special tags that don't produce views
        if ("include".equals(tagName) || "merge".equals(tagName)
                || "fragment".equals(tagName) || "requestFocus".equals(tagName)) {
            return null;
        }

        String fullName = null;

        // Check if it's a fully-qualified name (contains a dot)
        if (tagName.contains(".")) {
            fullName = tagName;
        } else {
            // Look up in the tag map
            Object mapped = sTagClassMap.get(tagName);
            if (mapped != null) {
                fullName = (String) mapped;
            } else {
                // Default: try android.widget.* then android.view.*
                fullName = "android.widget." + tagName;
            }
        }

        // Try to instantiate the view class
        try {
            Class cls = Class.forName(fullName);
            // Try no-arg constructor first
            return (View) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Try Context constructor
            try {
                Class cls = Class.forName(fullName);
                return (View) cls.getDeclaredConstructor(Context.class).newInstance(mContext);
            } catch (Exception e2) {
                // For non-widget short names, try android.view.*
                if (!tagName.contains(".") && fullName.startsWith("android.widget.")) {
                    try {
                        Class cls = Class.forName("android.view." + tagName);
                        return (View) cls.getDeclaredConstructor().newInstance();
                    } catch (Exception e3) {
                        // fall through
                    }
                }
            }
        }
        return null;
    }

    // ── Constructor and factory ───────────────────────────────────────────

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
            LayoutInflater base = (LayoutInflater) svc;
            // Return a clone bound to the caller's context (matches AOSP behavior)
            return base.cloneInContext(context);
        }
        return new LayoutInflater(context);
    }

    public Context getContext() { return mContext; }

    public LayoutInflater cloneInContext(Context newContext) {
        return new LayoutInflater(this, newContext);
    }

    /**
     * Inflate a layout resource.
     */
    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    /**
     * Inflate a layout resource with attachToRoot control.
     *
     * Tries four strategies in order:
     * 1. Programmatic layout registry (ViewFactory)
     * 2. Registered layout bytes via Resources.getLayoutBytes() (BinaryLayoutParser)
     * 3. Binary layout XML from APK via ResourceTable (BinaryLayoutParser)
     * 4. Fallback: empty FrameLayout with resource ID
     */
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        View view = null;

        // 1. Check programmatic layout registry first
        Object factoryObj = sLayoutRegistry.get(Integer.valueOf(resource));
        if (factoryObj != null) {
            ViewFactory factory = (ViewFactory) factoryObj;
            view = factory.createView(mContext, root);
            if (view != null) {
                if (attachToRoot && root != null) {
                    root.addView(view);
                    return root;
                }
                return view;
            }
        }

        // 2. Try to inflate from registered layout bytes
        if (view == null && mContext != null) {
            android.content.res.Resources res = mContext.getResources();
            if (res != null) {
                byte[] layoutBytes = res.getLayoutBytes(resource);
                if (layoutBytes != null && layoutBytes.length > 0) {
                    try {
                        BinaryLayoutParser parser = new BinaryLayoutParser(mContext);
                        view = parser.parse(layoutBytes);
                    } catch (Exception e) {
                        // parse failed, fall through to next strategy
                    }
                }
            }
        }

        // 3. Try to inflate from the real binary layout XML via ResourceTable
        if (view == null && mContext != null) {
            android.content.res.Resources res = mContext.getResources();
            if (res != null) {
                android.content.res.ResourceTable table = res.getResourceTable();
                if (table != null) {
                    String layoutFile = table.getLayoutFileName(resource);
                    if (layoutFile != null) {
                        byte[] xmlData = loadLayoutXml(layoutFile);
                        if (xmlData != null && xmlData.length > 0) {
                            BinaryLayoutParser parser = new BinaryLayoutParser(mContext);
                            view = parser.parse(xmlData);
                        }
                    }
                }
            }
        }

        // 4. Fallback: stub FrameLayout
        if (view == null) {
            view = new FrameLayout(mContext);
        }
        view.setId(resource);

        if (root != null && attachToRoot) {
            root.addView(view);
            return root;
        }
        return view;
    }

    /**
     * Load binary layout XML bytes from the extracted APK res/ directory.
     */
    private byte[] loadLayoutXml(String layoutPath) {
        // Try to find the file in the APK's extracted directory
        try {
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null) {
                String resDir = null;
                android.app.ApkInfo info = server.getApkInfo();
                if (info != null) resDir = info.resDir;
                if (resDir == null) resDir = info != null ? info.extractDir : null;

                if (resDir != null) {
                    java.io.File xmlFile = new java.io.File(resDir, layoutPath);
                    if (!xmlFile.exists()) {
                        // Try without res/ prefix
                        xmlFile = new java.io.File(resDir, layoutPath.startsWith("res/") ? layoutPath.substring(4) : layoutPath);
                    }
                    if (xmlFile.exists()) {
                        return readFile(xmlFile);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private static byte[] readFile(java.io.File file) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            int offset = 0;
            while (offset < data.length) {
                int n = fis.read(data, offset, data.length - offset);
                if (n < 0) break;
                offset += n;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
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
