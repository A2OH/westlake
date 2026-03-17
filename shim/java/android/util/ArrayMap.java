package android.util;

import java.util.*;

/**
 * Shim: android.util.ArrayMap
 * A Map implementation backed by a HashMap internally.
 * Also provides indexed access (keyAt/valueAt) via parallel ArrayList.
 * Pure Java — no Android or OHBridge dependencies.
 */
@SuppressWarnings("unchecked")
public final class ArrayMap<K, V> implements Map<K, V> {

    private final HashMap<K, V> mMap;
    private final ArrayList<K> mKeys;

    public ArrayMap() {
        mMap = new HashMap<>();
        mKeys = new ArrayList<>();
    }

    public ArrayMap(int capacity) {
        mMap = new HashMap<>(capacity);
        mKeys = new ArrayList<>(capacity);
    }

    public ArrayMap(ArrayMap<K, V> map) {
        this();
        if (map != null) putAll(map);
    }

    // ── Map interface ─────────────────────────────────────────────────────

    @Override public int size() { return mMap.size(); }
    @Override public boolean isEmpty() { return mMap.isEmpty(); }
    @Override public boolean containsKey(Object key) { return mMap.containsKey(key); }
    @Override public boolean containsValue(Object value) { return mMap.containsValue(value); }
    @Override public V get(Object key) { return mMap.get(key); }

    @Override
    public V put(K key, V value) {
        V old = mMap.put(key, value);
        if (old == null && !mMap.containsKey(key)) {
            // shouldn't happen, but guard
        }
        if (!mKeys.contains(key)) {
            mKeys.add(key);
        }
        return old;
    }

    @Override
    public V remove(Object key) {
        V old = mMap.remove(key);
        mKeys.remove(key);
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        mMap.clear();
        mKeys.clear();
    }

    @Override public Set<K> keySet() { return mMap.keySet(); }
    @Override public Collection<V> values() { return mMap.values(); }
    @Override public Set<Entry<K, V>> entrySet() { return mMap.entrySet(); }

    // ── Indexed access (ArrayMap-specific) ────────────────────────────────

    public K keyAt(int index) {
        return mKeys.get(index);
    }

    public V valueAt(int index) {
        return mMap.get(mKeys.get(index));
    }

    public V setValueAt(int index, V value) {
        K key = mKeys.get(index);
        return mMap.put(key, value);
    }

    public int indexOfKey(Object key) {
        return mKeys.indexOf(key);
    }

    public int indexOfValue(Object value) {
        for (int i = 0; i < mKeys.size(); i++) {
            V v = mMap.get(mKeys.get(i));
            if (Objects.equals(v, value)) return i;
        }
        return -1;
    }

    public V removeAt(int index) {
        K key = mKeys.remove(index);
        return mMap.remove(key);
    }

    public void ensureCapacity(int minimumCapacity) {
        mKeys.ensureCapacity(minimumCapacity);
    }

    public boolean containsAll(Collection<?> collection) {
        return mMap.keySet().containsAll(collection);
    }

    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        for (Object key : collection) {
            if (mMap.containsKey(key)) {
                remove(key);
                changed = true;
            }
        }
        return changed;
    }

    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<K> it = mKeys.iterator();
        while (it.hasNext()) {
            K key = it.next();
            if (!collection.contains(key)) {
                it.remove();
                mMap.remove(key);
                changed = true;
            }
        }
        return changed;
    }

    // ── Object overrides ──────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Map) return mMap.equals(o);
        return false;
    }

    @Override
    public int hashCode() {
        return mMap.hashCode();
    }

    @Override
    public String toString() {
        return mMap.toString();
    }
}
