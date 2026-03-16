package android.content.res;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class Resources {

    public static class NotFoundException extends RuntimeException {
        public NotFoundException() { super(); }
        public NotFoundException(String message) { super(message); }
        public NotFoundException(String message, Throwable cause) { super(message, cause); }
    }

    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private final Configuration  mConfiguration  = new Configuration();
    private ResourceTable mTable;

    /** Load a parsed ResourceTable for resource ID resolution. */
    public void loadResourceTable(ResourceTable table) {
        mTable = table;
    }

    /** Get the loaded ResourceTable, or null if none loaded. */
    public ResourceTable getResourceTable() {
        return mTable;
    }

    public String getString(int id) {
        if (mTable != null) {
            String s = mTable.getString(id);
            if (s != null) return s;
        }
        return "string_" + id;
    }

    public String getString(int id, Object... formatArgs) {
        return String.format(getString(id), formatArgs);
    }

    public CharSequence getText(int id) {
        return getString(id);
    }

    public int getInteger(int id) {
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    public boolean getBoolean(int id) {
        if (mTable != null) {
            return mTable.getInteger(id, 0) != 0;
        }
        return false;
    }

    public float getDimension(int id) {
        if (mTable != null) {
            return (float) mTable.getInteger(id, 0);
        }
        return 0f;
    }

    public int getColor(int id) {
        if (mTable != null) {
            return mTable.getInteger(id, 0xFF000000);
        }
        return 0xFF000000;
    }

    public Drawable getDrawable(int id) {
        return null;
    }

    public int getDimensionPixelSize(int id) {
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    public String[] getStringArray(int id) {
        return new String[0];
    }

    public int[] getIntArray(int id) {
        return new int[0];
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }

    /**
     * Get a resource name by ID (e.g., "string/app_name").
     * Returns null if not found or no ResourceTable loaded.
     */
    public String getResourceName(int id) {
        if (mTable != null) {
            return mTable.getResourceName(id);
        }
        return null;
    }
}
