package android.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Backing implementation for SharedPreferences interface.
 * Pure Java HashMap-backed, no JNI needed.
 */
public class SharedPreferencesImpl implements SharedPreferences {
    private static final Map<String, SharedPreferencesImpl> sInstances = new HashMap<>();

    private final String name;
    private final Map<String, Object> store = new HashMap<>();
    private final List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();

    SharedPreferencesImpl(String name) {
        this.name = name;
        // Pre-populate McDonald's config to prevent NPE in HomeHelper.setAppParameter()
        if (name != null && name.contains("mcd")) {
            seedMcDonaldsDefaults();
        }
    }

    private void seedMcDonaldsDefaults() {
        // Minimal market config JSON that HomeHelper.setAppParameter() expects
        store.put("SELECTED_CONFIG", "{\"marketId\":\"US\",\"country\":\"US\",\"language\":\"en\",\"currencyCode\":\"USD\",\"currencySymbol\":\"$\",\"justFlip.splitEnvironmentId\":\"474f8810-15c1-11ee-b490-4afd76afc5e7\",\"justFlip.splitApiKey\":\"offline\",\"baseUrl\":\"https://us-prod.api.mcd.com/exp/v1/\",\"tokenUrl\":\"https://us-prod.api.mcd.com/v1/security/auth/token\",\"configMap\":{\"justFlip.splitEnvironmentId\":\"474f8810-15c1-11ee-b490-4afd76afc5e7\"}}");
        store.put("SELECTED_MARKET_ID", "US");
        store.put("SELECTED_COUNTRY", "US");
        store.put("SELECTED_LANGUAGE", "en");
        store.put("DEBUG_OVERRIDE_MARKET_ID", "US");
        store.put("IS_FIRST_LAUNCH", "false");
        store.put("APP_VERSION", "7.0.0");
        store.put("HAS_ACCEPTED_TERMS", "true");
        store.put("PUSH_ENABLED", "false");
    }

    public static synchronized SharedPreferencesImpl getInstance(String name) {
        SharedPreferencesImpl sp = sInstances.get(name);
        if (sp == null) {
            sp = new SharedPreferencesImpl(name);
            sInstances.put(name, sp);
        }
        return sp;
    }

    @Override public Map<String, ?> getAll() {
        synchronized (store) { return new HashMap<>(store); }
    }
    @Override public String getString(String key, String defValue) {
        synchronized (store) {
            Object v = store.get(key);
            String r = (v instanceof String) ? (String) v : defValue;
            return r;
        }
    }
    @Override public int getInt(String key, int defValue) {
        synchronized (store) { Object v = store.get(key); return (v instanceof Integer) ? (Integer) v : defValue; }
    }
    @Override public long getLong(String key, long defValue) {
        synchronized (store) { Object v = store.get(key); return (v instanceof Long) ? (Long) v : defValue; }
    }
    @Override public float getFloat(String key, float defValue) {
        synchronized (store) { Object v = store.get(key); return (v instanceof Float) ? (Float) v : defValue; }
    }
    @Override public boolean getBoolean(String key, boolean defValue) {
        synchronized (store) { Object v = store.get(key); return (v instanceof Boolean) ? (Boolean) v : defValue; }
    }
    @Override public Set<String> getStringSet(String key, Set<String> defValues) {
        synchronized (store) { Object v = store.get(key); return (v instanceof Set) ? new HashSet<>((Set<String>) v) : defValues; }
    }
    @Override public boolean contains(String key) {
        synchronized (store) { return store.containsKey(key); }
    }
    @Override public SharedPreferences.Editor edit() {
        return new EditorImpl(this);
    }
    @Override public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener l) {
        if (l != null) synchronized (listeners) { if (!listeners.contains(l)) listeners.add(l); }
    }
    @Override public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener l) {
        synchronized (listeners) { listeners.remove(l); }
    }

    void notifyListeners(Set<String> keys) {
        List<OnSharedPreferenceChangeListener> snap;
        synchronized (listeners) { if (listeners.isEmpty()) return; snap = new ArrayList<>(listeners); }
        for (String k : keys) for (OnSharedPreferenceChangeListener l : snap) l.onSharedPreferenceChanged(this, k);
    }

    static class EditorImpl implements SharedPreferences.Editor {
        private final SharedPreferencesImpl prefs;
        private final Map<String, Object> pending = new HashMap<>();
        private final Set<String> removals = new HashSet<>();
        private boolean clearReq = false;

        EditorImpl(SharedPreferencesImpl prefs) { this.prefs = prefs; }

        @Override public Editor putString(String k, String v) { if (v==null) return remove(k); pending.put(k,v); removals.remove(k); return this; }
        @Override public Editor putInt(String k, int v) { pending.put(k, v); removals.remove(k); return this; }
        @Override public Editor putLong(String k, long v) { pending.put(k, v); removals.remove(k); return this; }
        @Override public Editor putFloat(String k, float v) { pending.put(k, v); removals.remove(k); return this; }
        @Override public Editor putBoolean(String k, boolean v) { pending.put(k, v); removals.remove(k); return this; }
        @Override public Editor putStringSet(String k, Set<String> v) { if (v==null) return remove(k); pending.put(k, new HashSet<>(v)); removals.remove(k); return this; }
        @Override public Editor remove(String k) { removals.add(k); pending.remove(k); return this; }
        @Override public Editor clear() { clearReq = true; pending.clear(); removals.clear(); return this; }
        @Override public boolean commit() { Set<String> c = flush(); prefs.notifyListeners(c); return true; }
        @Override public void apply() { Set<String> c = flush(); prefs.notifyListeners(c); }

        private Set<String> flush() {
            Set<String> changed = new HashSet<>();
            synchronized (prefs.store) {
                if (clearReq) { changed.addAll(prefs.store.keySet()); prefs.store.clear(); }
                for (String k : removals) if (prefs.store.containsKey(k)) { changed.add(k); prefs.store.remove(k); }
                for (Map.Entry<String, Object> e : pending.entrySet()) {
                    Object old = prefs.store.get(e.getKey());
                    if (old == null || !old.equals(e.getValue())) changed.add(e.getKey());
                    prefs.store.put(e.getKey(), e.getValue());
                }
            }
            pending.clear(); removals.clear(); clearReq = false;
            return changed;
        }
    }
}
