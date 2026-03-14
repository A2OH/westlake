package android.graphics;

/**
 * Shim: android.graphics.Insets
 * Pure Java — immutable inset values (left, top, right, bottom).
 */
public final class Insets {

    public static final Insets NONE = new Insets(0, 0, 0, 0);

    public final int left;
    public final int top;
    public final int right;
    public final int bottom;

    private Insets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Insets() {
        this(0, 0, 0, 0);
    }

    public static Insets of(int left, int top, int right, int bottom) {
        if (left == 0 && top == 0 && right == 0 && bottom == 0) return NONE;
        return new Insets(left, top, right, bottom);
    }

    public static Insets of(Rect r) {
        if (r == null) return NONE;
        return of(r.left, r.top, r.right, r.bottom);
    }

    public static Insets add(Insets a, Insets b) {
        return of(a.left + b.left, a.top + b.top, a.right + b.right, a.bottom + b.bottom);
    }

    public static Insets subtract(Insets a, Insets b) {
        return of(a.left - b.left, a.top - b.top, a.right - b.right, a.bottom - b.bottom);
    }

    public static Insets max(Insets a, Insets b) {
        return of(Math.max(a.left, b.left), Math.max(a.top, b.top),
                  Math.max(a.right, b.right), Math.max(a.bottom, b.bottom));
    }

    public static Insets min(Insets a, Insets b) {
        return of(Math.min(a.left, b.left), Math.min(a.top, b.top),
                  Math.min(a.right, b.right), Math.min(a.bottom, b.bottom));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insets)) return false;
        Insets i = (Insets) o;
        return left == i.left && top == i.top && right == i.right && bottom == i.bottom;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + top;
        result = 31 * result + right;
        result = 31 * result + bottom;
        return result;
    }

    @Override
    public String toString() {
        return "Insets{left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom + "}";
    }

    public int describeContents() { return 0; }
    public void writeToParcel(android.os.Parcel dest, int flags) {}
}
