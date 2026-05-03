package android.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.net.Uri;

/**
 * Android-compatible IntentFilter shim.
 *
 * Structured description of Intent values to be matched. Used when
 * registering BroadcastReceiver instances via Context.registerReceiver().
 *
 * Pure Java stub — no OH bridge calls needed.
 */
public class IntentFilter {
    public static final int NO_MATCH_TYPE = -1;
    public static final int NO_MATCH_DATA = -2;
    public static final int NO_MATCH_ACTION = -3;
    public static final int NO_MATCH_CATEGORY = -4;
    public static final int MATCH_CATEGORY_EMPTY = 0x0100000;

    private final List<String> mActions    = new ArrayList<>();
    private final List<String> mCategories = new ArrayList<>();
    private final List<String> mDataSchemes = new ArrayList<>();
    private final List<String> mDataTypes  = new ArrayList<>();

    private int mPriority = 0;

    // ── Constructors ──

    /** Create an empty IntentFilter. */
    public IntentFilter() {}

    /**
     * Create an IntentFilter that matches a single action.
     * @param action the action to match (e.g. Intent.ACTION_VIEW)
     */
    public IntentFilter(String action) {
        addAction(action);
    }

    /**
     * Create an IntentFilter that matches a single action and a single
     * data MIME type.
     * @param action   the action to match
     * @param dataType MIME type to match
     */
    public IntentFilter(String action, String dataType) {
        addAction(action);
        mDataTypes.add(dataType);
    }

    // ── Actions ──

    /** Add an action to match against. */
    public void addAction(String action) {
        if (action != null && !mActions.contains(action)) {
            mActions.add(action);
        }
    }

    /** @return true if this filter includes the given action */
    public boolean hasAction(String action) {
        return mActions.contains(action);
    }

    /**
     * Match an action against this filter.
     * @return true if the filter has no actions (matches all) or contains the given action
     */
    public boolean matchAction(String action) {
        if (mActions.isEmpty()) return true;
        return mActions.contains(action);
    }

    /** Return the number of actions in this filter. */
    public int countActions() {
        return mActions.size();
    }

    /** Return the action at the given index. */
    public String getAction(int index) {
        return mActions.get(index);
    }

    /** @return an Iterator over the actions in this filter */
    public Iterator<String> actionsIterator() {
        return mActions.iterator();
    }

    // ── Categories ──

    /** Add a category to match against. */
    public void addCategory(String category) {
        if (category != null && !mCategories.contains(category)) {
            mCategories.add(category);
        }
    }

    /** @return true if this filter includes the given category */
    public boolean hasCategory(String category) {
        return mCategories.contains(category);
    }

    /** Return the number of categories in this filter. */
    public int countCategories() {
        return mCategories.size();
    }

    /** Return the category at the given index. */
    public String getCategory(int index) {
        return mCategories.get(index);
    }

    /** @return an Iterator over the categories in this filter */
    public Iterator<String> categoriesIterator() {
        return mCategories.iterator();
    }

    // ── Data schemes ──

    /**
     * Add a URI scheme (e.g. "http", "content") that this filter matches.
     */
    public void addDataScheme(String scheme) {
        if (scheme != null && !mDataSchemes.contains(scheme)) {
            mDataSchemes.add(scheme);
        }
    }

    /** @return true if this filter includes the given data scheme */
    public boolean hasDataScheme(String scheme) {
        return mDataSchemes.contains(scheme);
    }

    /** Return the number of data schemes in this filter. */
    public int countDataSchemes() {
        return mDataSchemes.size();
    }

    /** Return the data scheme at the given index. */
    public String getDataScheme(int index) {
        return mDataSchemes.get(index);
    }

    /** @return an Iterator over the data schemes in this filter */
    public Iterator<String> schemesIterator() {
        return mDataSchemes.iterator();
    }

    // ── Data types ──

    /**
     * Add a MIME data type to match.
     * @param type MIME type (e.g. "image/jpeg", "text/*")
     */
    public void addDataType(String type) {
        if (type != null && !mDataTypes.contains(type)) {
            mDataTypes.add(type);
        }
    }

    /** @return true if this filter includes the given MIME data type */
    public boolean hasDataType(String type) {
        return mDataTypes.contains(type);
    }

    /** Return the number of data types in this filter. */
    public int countDataTypes() {
        return mDataTypes.size();
    }

    /** @return an Iterator over the data types in this filter */
    public Iterator<String> typesIterator() {
        return mDataTypes.iterator();
    }

    // ── Priority ──

    /**
     * Specify the priority of this filter. Receivers with higher priority
     * are called before those with lower priority.
     */
    public void setPriority(int priority) {
        mPriority = priority;
    }

    /** @return the priority of this filter */
    public int getPriority() {
        return mPriority;
    }

    // ── Matching ──

    /**
     * Test whether this filter matches the given Intent action, returning true
     * if it does.
     *
     * Mirrors the real Android IntentFilter.match() logic for action-only
     * matching. More complex URI/type matching is not implemented in this stub.
     *
     * @param action the action from the Intent
     * @return true if this filter matches the action (or has no action constraints)
     */
    public boolean match(String action) {
        return matchAction(action);
    }

    public int match(String action, String type, String scheme, Uri data,
            Set<String> categories, String logTag) {
        if (!matchAction(action)) {
            return NO_MATCH_ACTION;
        }
        if (!matchDataType(type)) {
            return NO_MATCH_TYPE;
        }
        String dataScheme = scheme;
        if (dataScheme == null && data != null) {
            dataScheme = data.getScheme();
        }
        if (!matchDataScheme(dataScheme)) {
            return NO_MATCH_DATA;
        }
        if (!matchCategories(categories)) {
            return NO_MATCH_CATEGORY;
        }
        return MATCH_CATEGORY_EMPTY + mPriority;
    }

    private boolean matchDataType(String type) {
        if (mDataTypes.isEmpty() || type == null) {
            return true;
        }
        for (String filterType : mDataTypes) {
            if (filterType == null) {
                continue;
            }
            if (filterType.equals(type)) {
                return true;
            }
            if (filterType.endsWith("/*")) {
                int slash = type.indexOf('/');
                String prefix = slash >= 0 ? type.substring(0, slash + 1) : type + "/";
                if (filterType.regionMatches(0, prefix, 0, prefix.length())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchDataScheme(String scheme) {
        if (mDataSchemes.isEmpty() || scheme == null) {
            return true;
        }
        return mDataSchemes.contains(scheme);
    }

    private boolean matchCategories(Set<String> categories) {
        if (mCategories.isEmpty() || categories == null || categories.isEmpty()) {
            return true;
        }
        for (String category : categories) {
            if (category != null && !mCategories.contains(category)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "IntentFilter{actions=" + mActions
                + ", categories=" + mCategories
                + ", schemes=" + mDataSchemes + "}";
    }
}
