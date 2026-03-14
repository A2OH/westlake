package android.graphics;

/**
 * Shim: android.graphics.Region
 * Pure Java — simple rectangular region implementation.
 */
public class Region {

    public enum Op {
        DIFFERENCE, INTERSECT, UNION, XOR, REVERSE_DIFFERENCE, REPLACE
    }

    private final Rect mBounds = new Rect();
    private boolean mEmpty = true;

    public Region() {}

    public Region(Region region) {
        if (region != null && !region.mEmpty) {
            mBounds.set(region.mBounds);
            mEmpty = false;
        }
    }

    public Region(Rect r) {
        if (r != null && !r.isEmpty()) {
            mBounds.set(r);
            mEmpty = false;
        }
    }

    public Region(int left, int top, int right, int bottom) {
        if (left < right && top < bottom) {
            mBounds.set(left, top, right, bottom);
            mEmpty = false;
        }
    }

    public void setEmpty() {
        mBounds.set(0, 0, 0, 0);
        mEmpty = true;
    }

    public boolean set(Region region) {
        if (region == null || region.mEmpty) {
            setEmpty();
            return false;
        }
        mBounds.set(region.mBounds);
        mEmpty = false;
        return true;
    }

    public boolean set(Rect r) {
        if (r == null || r.isEmpty()) {
            setEmpty();
            return false;
        }
        mBounds.set(r);
        mEmpty = false;
        return true;
    }

    public boolean set(int left, int top, int right, int bottom) {
        if (left >= right || top >= bottom) {
            setEmpty();
            return false;
        }
        mBounds.set(left, top, right, bottom);
        mEmpty = false;
        return true;
    }

    public boolean isEmpty() { return mEmpty; }
    public boolean isRect() { return !mEmpty; }
    public boolean isComplex() { return false; }

    public Rect getBounds() { return new Rect(mBounds); }

    public boolean getBounds(Rect r) {
        if (r != null) r.set(mBounds);
        return !mEmpty;
    }

    public boolean getBoundaryPath(Path path) {
        return !mEmpty;
    }

    public boolean contains(int x, int y) {
        return !mEmpty && mBounds.contains(x, y);
    }

    public boolean quickContains(Rect r) {
        return !mEmpty && r != null && mBounds.contains(r.left, r.top, r.right, r.bottom);
    }

    public boolean quickContains(int left, int top, int right, int bottom) {
        return !mEmpty && mBounds.contains(left, top, right, bottom);
    }

    public boolean quickReject(Rect r) {
        if (mEmpty || r == null) return true;
        return r.left >= mBounds.right || r.right <= mBounds.left
            || r.top >= mBounds.bottom || r.bottom <= mBounds.top;
    }

    public boolean quickReject(int left, int top, int right, int bottom) {
        if (mEmpty) return true;
        return left >= mBounds.right || right <= mBounds.left
            || top >= mBounds.bottom || bottom <= mBounds.top;
    }

    public boolean quickReject(Region rgn) {
        if (mEmpty || rgn == null || rgn.mEmpty) return true;
        return rgn.mBounds.left >= mBounds.right || rgn.mBounds.right <= mBounds.left
            || rgn.mBounds.top >= mBounds.bottom || rgn.mBounds.bottom <= mBounds.top;
    }

    public void translate(int dx, int dy) {
        mBounds.offset(dx, dy);
    }

    public void translate(int dx, int dy, Region dst) {
        if (dst != null) {
            dst.set(this);
            dst.translate(dx, dy);
        }
    }

    public boolean union(Rect r) {
        if (r == null || r.isEmpty()) return false;
        if (mEmpty) {
            mBounds.set(r);
            mEmpty = false;
        } else {
            mBounds.union(r);
        }
        return true;
    }

    public boolean op(Rect r, Op op) {
        if (r == null) return false;
        switch (op) {
            case REPLACE: return set(r);
            case INTERSECT:
                if (mEmpty) return false;
                return mBounds.intersect(r);
            case UNION: return union(r);
            default: return false;
        }
    }

    public boolean op(int left, int top, int right, int bottom, Op op) {
        return op(new Rect(left, top, right, bottom), op);
    }

    public boolean op(Region region, Op op) {
        if (region == null) return false;
        return op(region.mBounds, op);
    }

    public boolean op(Rect rect, Region region, Op op) {
        set(rect);
        return op(region, op);
    }

    public boolean op(Region region1, Region region2, Op op) {
        set(region1);
        return op(region2, op);
    }

    public boolean setPath(Path path, Region clip) {
        // Approximate: use clip bounds
        if (clip != null && !clip.mEmpty) {
            mBounds.set(clip.mBounds);
            mEmpty = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region)) return false;
        Region r = (Region) o;
        if (mEmpty && r.mEmpty) return true;
        return !mEmpty && !r.mEmpty && mBounds.equals(r.mBounds);
    }

    @Override
    public String toString() {
        return mEmpty ? "Region(empty)" : "Region(" + mBounds + ")";
    }

    public int describeContents() { return 0; }
    public void writeToParcel(android.os.Parcel dest, int flags) {}
}
