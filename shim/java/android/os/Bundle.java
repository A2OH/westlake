package android.os;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;
import java.io.Serializable;
import java.util.ArrayList;

public final class Bundle extends BaseBundle implements Cloneable, Parcelable {
    public static final int EMPTY = 0;

    public Bundle() { super(); }
    public Bundle(ClassLoader p0) { super(); }
    public Bundle(int capacity) { super(capacity); }
    public Bundle(Bundle other) { super(other); }
    public Bundle(PersistableBundle p0) { super(); }

    public Object clone() {
        return new Bundle(this);
    }
    public Bundle deepCopy() { return new Bundle(this); }
    public int describeContents() { return 0; }
    public byte getByte(String key) { return getByte(key, (byte) 0); }
    public Byte getByte(String key, byte def) { Object v = map.get(key); return v instanceof Byte ? (Byte) v : (v instanceof Number ? ((Number) v).byteValue() : def); }
    public char getChar(String key) { return getChar(key, '\0'); }
    public char getChar(String key, char def) { Object v = map.get(key); return v instanceof Character ? (Character) v : def; }
    public CharSequence getCharSequence(String key) { Object v = map.get(key); return v instanceof CharSequence ? (CharSequence) v : null; }
    public CharSequence getCharSequence(String key, CharSequence def) { Object v = map.get(key); return v instanceof CharSequence ? (CharSequence) v : def; }
    public ClassLoader getClassLoader() { return null; }
    public float getFloat(String key) { return getFloat(key, 0f); }
    public float getFloat(String key, float def) { Object v = map.get(key); return v instanceof Number ? ((Number) v).floatValue() : def; }
    public short getShort(String key) { return getShort(key, (short) 0); }
    public short getShort(String key, short def) { Object v = map.get(key); return v instanceof Number ? ((Number) v).shortValue() : def; }
    public Parcelable getParcelable(String key) { Object v = map.get(key); return v instanceof Parcelable ? (Parcelable) v : null; }
    public Serializable getSerializable(String key) { Object v = map.get(key); return v instanceof Serializable ? (Serializable) v : null; }
    public Bundle getBundle(String key) { Object v = map.get(key); return v instanceof Bundle ? (Bundle) v : null; }
    public byte[] getByteArray(String key) { Object v = map.get(key); return v instanceof byte[] ? (byte[]) v : null; }
    public char[] getCharArray(String key) { Object v = map.get(key); return v instanceof char[] ? (char[]) v : null; }
    public float[] getFloatArray(String key) { Object v = map.get(key); return v instanceof float[] ? (float[]) v : null; }
    public short[] getShortArray(String key) { Object v = map.get(key); return v instanceof short[] ? (short[]) v : null; }
    public boolean hasFileDescriptors() { return false; }
    public void putAll(Bundle other) {
        if (other != null) map.putAll(other.map);
    }
    public void putBinder(String key, IBinder val) { map.put(key, val); }
    public void putBundle(String key, Bundle val) { map.put(key, val); }
    public void putByte(String key, byte val) { map.put(key, val); }
    public void putByteArray(String key, byte[] val) { map.put(key, val); }
    public void putChar(String key, char val) { map.put(key, val); }
    public void putCharArray(String key, char[] val) { map.put(key, val); }
    public void putCharSequence(String key, CharSequence val) { map.put(key, val); }
    public void putCharSequenceArray(String key, CharSequence[] val) { map.put(key, val); }
    public void putCharSequenceArrayList(String key, java.util.ArrayList<Object> val) { map.put(key, val); }
    public void putFloat(String key, float val) { map.put(key, val); }
    public void putFloatArray(String key, float[] val) { map.put(key, val); }
    public void putIntegerArrayList(String key, java.util.ArrayList<Object> val) { map.put(key, val); }
    public void putParcelable(String key, Parcelable val) { map.put(key, val); }
    public void putParcelableArray(String key, Parcelable[] val) { map.put(key, val); }
    public void putParcelableArrayList(String key, java.util.ArrayList<Object> val) { map.put(key, val); }
    public void putSerializable(String key, Serializable val) { map.put(key, val); }
    public void putShort(String key, short val) { map.put(key, val); }
    public void putShortArray(String key, short[] val) { map.put(key, val); }
    public void putSize(String key, Size val) { map.put(key, val); }
    public void putSizeF(String key, SizeF val) { map.put(key, val); }
    public void putSparseParcelableArray(String key, Object val) { map.put(key, val); }
    public void putStringArrayList(String key, java.util.ArrayList<Object> val) { map.put(key, val); }
    public void putNull(String key) { map.put(key, null); }
    public void readFromParcel(Parcel p0) {}
    public void setClassLoader(ClassLoader p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
