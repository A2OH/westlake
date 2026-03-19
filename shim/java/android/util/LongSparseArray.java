package android.util;

/**
 * Stub for android.util.LongSparseArray<E>.
 */
@SuppressWarnings("unchecked")
public class LongSparseArray<E> implements Cloneable {
    private final java.util.HashMap<Long, E> map = new java.util.HashMap<>();

    public LongSparseArray() {}
    public LongSparseArray(int initialCapacity) {}

    public void append(long key, E value) { map.put(key, value); }
    public void clear() { map.clear(); }
    @Override
    public LongSparseArray<E> clone() {
        LongSparseArray<E> c = new LongSparseArray<>();
        c.map.putAll(map);
        return c;
    }
    public void delete(long key) { map.remove(key); }
    public E get(long key) { return map.get(key); }
    public E get(long key, E valueIfKeyNotFound) {
        E v = map.get(key);
        return v != null ? v : valueIfKeyNotFound;
    }
    public int indexOfKey(long key) {
        int i = 0;
        for (Long k : map.keySet()) {
            if (k == key) return i;
            i++;
        }
        return -1;
    }
    public int indexOfValue(E value) {
        int i = 0;
        for (E v : map.values()) {
            if (v == value) return i;
            i++;
        }
        return -1;
    }
    public long keyAt(int index) {
        int i = 0;
        for (Long k : map.keySet()) {
            if (i == index) return k;
            i++;
        }
        return 0;
    }
    public void put(long key, E value) { map.put(key, value); }
    public void remove(long key) { map.remove(key); }
    public void removeAt(int index) {
        long key = keyAt(index);
        map.remove(key);
    }
    public void setValueAt(int index, E value) {
        long key = keyAt(index);
        map.put(key, value);
    }
    public int size() { return map.size(); }
    public E valueAt(int index) {
        int i = 0;
        for (E v : map.values()) {
            if (i == index) return v;
            i++;
        }
        return null;
    }
}
