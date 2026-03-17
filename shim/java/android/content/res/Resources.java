package android.content.res;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import java.util.HashMap;

/**
 * Shim: android.content.res.Resources — Phase 1
 *
 * Returns sensible defaults for all resource lookups so apps calling
 * getResources().getString() etc. don't crash.  If a ResourceTable has
 * been loaded (from resources.arsc), real values are returned instead.
 *
 * Phase 2 hook: registerStringResource / registerColorResource allow
 * programmatic injection of resource values.
 */
public class Resources {

    public static class NotFoundException extends RuntimeException {
        public NotFoundException() { super(); }
        public NotFoundException(String message) { super(message); }
        public NotFoundException(String message, Throwable cause) { super(message, cause); }
    }

    /** Minimal Theme stub so getColor(int, Theme) / getDrawable(int, Theme) compile. */
    public static class Theme {
        public Theme() {}
    }

    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private final Configuration  mConfiguration  = new Configuration();
    private ResourceTable mTable;

    /** Phase 2 registry: manually registered resources keyed by resource ID. */
    private final HashMap<Integer, Object> mRegistry = new HashMap<Integer, Object>();

    public Resources() {}

    // ── ResourceTable integration ────────────────────────────────────────────

    /** Load a parsed ResourceTable for resource ID resolution. */
    public void loadResourceTable(ResourceTable table) {
        mTable = table;
    }

    /** Get the loaded ResourceTable, or null if none loaded. */
    public ResourceTable getResourceTable() {
        return mTable;
    }

    // ── Phase 2 registration hooks ───────────────────────────────────────────

    /** Register a string resource for the given ID (overrides defaults and ResourceTable). */
    public void registerStringResource(int id, String value) {
        mRegistry.put(Integer.valueOf(id), value);
    }

    /** Register a color resource for the given ID (overrides defaults and ResourceTable). */
    public void registerColorResource(int id, int color) {
        mRegistry.put(Integer.valueOf(id), Integer.valueOf(color));
    }

    /** Register an integer resource for the given ID. */
    public void registerIntegerResource(int id, int value) {
        mRegistry.put(Integer.valueOf(id), Integer.valueOf(value));
    }

    // ── String resources ─────────────────────────────────────────────────────

    public String getString(int id) {
        // Check registry first
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof String) return (String) reg;
        // Then ResourceTable
        if (mTable != null) {
            String s = mTable.getString(id);
            if (s != null) return s;
        }
        return "string_" + id;
    }

    public String getString(int id, Object... formatArgs) {
        // Simple substitution: replace %s, %d, %f etc. with arg.toString()
        // Cannot use String.format (JNI native on KitKat Dalvik)
        String template = getString(id);
        if (formatArgs == null || formatArgs.length == 0) return template;
        StringBuilder sb = new StringBuilder();
        int argIdx = 0;
        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);
            if (c == '%' && i + 1 < template.length() && argIdx < formatArgs.length) {
                char next = template.charAt(i + 1);
                if (next == '%') {
                    sb.append('%');
                    i++;
                } else {
                    // Skip format specifier chars until we hit the conversion char
                    int j = i + 1;
                    while (j < template.length() && "0123456789.-+ #,(".indexOf(template.charAt(j)) >= 0) j++;
                    if (j < template.length()) {
                        sb.append(formatArgs[argIdx] != null ? formatArgs[argIdx].toString() : "null");
                        argIdx++;
                        i = j; // skip the conversion char
                    } else {
                        sb.append(c);
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public CharSequence getText(int id) {
        return getString(id);
    }

    public String[] getStringArray(int id) {
        return new String[0];
    }

    // ── Color resources ──────────────────────────────────────────────────────

    public int getColor(int id) {
        // Check registry
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        // Then ResourceTable
        if (mTable != null) {
            return mTable.getInteger(id, 0xFF000000);
        }
        return 0xFF000000;
    }

    public int getColor(int id, Theme theme) {
        return getColor(id);
    }

    public ColorStateList getColorStateList(int id) {
        return ColorStateList.valueOf(getColor(id));
    }

    public ColorStateList getColorStateList(int id, Theme theme) {
        return getColorStateList(id);
    }

    // ── Integer / boolean resources ──────────────────────────────────────────

    public int getInteger(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    public boolean getBoolean(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue() != 0;
        if (mTable != null) {
            return mTable.getInteger(id, 0) != 0;
        }
        return false;
    }

    // ── Dimension resources ──────────────────────────────────────────────────

    public float getDimension(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return (float) ((Integer) reg).intValue();
        if (mTable != null) {
            return (float) mTable.getInteger(id, 0);
        }
        return 0f;
    }

    public int getDimensionPixelSize(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    public int getDimensionPixelOffset(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    // ── Drawable resources ───────────────────────────────────────────────────

    public Drawable getDrawable(int id) {
        return new ColorDrawable(0xFFCCCCCC);
    }

    public Drawable getDrawable(int id, Theme theme) {
        return getDrawable(id);
    }

    // ── Layout resources ─────────────────────────────────────────────────────

    public XmlResourceParser getLayout(int id) {
        return null; // B9 will handle this
    }

    // ── Array resources ──────────────────────────────────────────────────────

    public int[] getIntArray(int id) {
        return new int[0];
    }

    // ── Resource ID lookup ───────────────────────────────────────────────────

    public int getIdentifier(String name, String defType, String defPackage) {
        return 0;
    }

    /**
     * Get a resource name by ID (e.g., "string/app_name").
     * Returns a synthetic name if no ResourceTable is loaded.
     */
    public String getResourceName(int id) {
        if (mTable != null) {
            String name = mTable.getResourceName(id);
            if (name != null) return name;
        }
        return "res/0x" + Integer.toHexString(id);
    }

    public String getResourceEntryName(int id) {
        if (mTable != null) {
            String name = mTable.getResourceName(id);
            if (name != null) {
                int slash = name.indexOf('/');
                if (slash >= 0 && slash + 1 < name.length()) {
                    return name.substring(slash + 1);
                }
                return name;
            }
        }
        return "entry_" + Integer.toHexString(id);
    }

    public String getResourceTypeName(int id) {
        if (mTable != null) {
            String name = mTable.getResourceName(id);
            if (name != null) {
                int slash = name.indexOf('/');
                if (slash > 0) {
                    return name.substring(0, slash);
                }
            }
        }
        // Derive type from the TT byte of the resource ID: 0xPPTTEEEE
        int typeId = (id >> 16) & 0xFF;
        switch (typeId) {
            case 0x01: return "attr";
            case 0x02: return "drawable";
            case 0x03: return "layout";
            case 0x04: return "anim";
            case 0x05: return "xml";
            case 0x06: return "raw";
            case 0x07: return "array";
            case 0x08: return "string";
            case 0x09: return "bool";
            case 0x0a: return "integer";
            case 0x0b: return "color";
            case 0x0c: return "dimen";
            case 0x0d: return "style";
            case 0x0e: return "id";
            default:   return "unknown";
        }
    }

    // ── Display / Configuration ──────────────────────────────────────────────

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }
}
