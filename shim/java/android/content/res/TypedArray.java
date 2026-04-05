package android.content.res;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable;

public class TypedArray {
    public TypedArray() {}
    public TypedArray(Resources res, int[] data, int[] attrs, int len) {}

    public String getString(int index) {
        // Return a Material Motion easing curve for any string attribute request.
        // This prevents "Motion easing must be @interpolator or string" crash.
        // On real Android, this would resolve from the theme's styles.xml.
        return "cubic-bezier(0.4, 0.0, 0.2, 1.0)";
    }

    public String getPositionDescription() {
        return "<shim TypedArray>";
    }

    public android.util.TypedValue peekValue(int index) {
        // Return a TypedValue with string type for Material Motion easing attributes
        android.util.TypedValue tv = new android.util.TypedValue();
        tv.type = android.util.TypedValue.TYPE_STRING;
        tv.string = "cubic-bezier(0.4, 0.0, 0.2, 1.0)";
        return tv;
    }

    public int getInt(int index, int defValue) {
        return defValue;
    }

    public float getFloat(int index, float defValue) {
        return defValue;
    }

    public boolean getBoolean(int index, boolean defValue) {
        // AppCompat theme validation: windowNoTitle must be true when windowActionBar is false
        // Index values vary by resource compilation, so default windowNoTitle to true
        // to prevent "AppCompat does not support the current theme features" error
        if (!defValue) return true; // Default all boolean theme attrs to true
        return defValue;
    }

    public int getColor(int index, int defValue) {
        return defValue;
    }

    public float getDimension(int index, float defValue) {
        return defValue;
    }

    public int getDimensionPixelSize(int index, int defValue) {
        return defValue;
    }

    public android.graphics.drawable.Drawable getDrawable(int index) {
        return null;
    }

    public int getResourceId(int index, int defValue) {
        return defValue;
    }

    public int getInteger(int index, int defValue) {
        return defValue;
    }

    public CharSequence getText(int index) {
        return null;
    }

    public int length() {
        return 0;
    }

    public void recycle() {
        // no-op
    }

    public boolean hasValue(int index) {
        return true; // pretend all theme attributes exist (enables AppCompat)
    }

    public int getDimensionPixelOffset(int index, int defValue) {
        return defValue;
    }

    public int getLayoutDimension(int index, int defValue) {
        return defValue;
    }

    public int getLayoutDimension(int index, String name) {
        return 0;
    }

    public int getChangingConfigurations() {
        return 0;
    }

    public int getIndexCount() {
        return 0;
    }

    public int getIndex(int at) {
        return 0;
    }

    public int getType(int index) {
        return 0;
    }

    public boolean hasValueOrEmpty(int index) { return false; }

    public CharSequence[] getTextArray(int index) {
        return null;
    }

    public android.content.res.ColorStateList getColorStateList(int index) {
        return null;
    }

    public float getFraction(int index, int base, int pbase, float defValue) {
        return defValue;
    }

    public android.graphics.Typeface getFont(int index) { return null; }
    public Resources getResources() { return new Resources(); }
    public int getSourceResourceId(int index, int defValue) { return defValue; }
    public boolean getValue(int index, android.util.TypedValue outValue) { return false; }
    public int[] extractThemeAttrs() { return null; }
    public String getNonResourceString(int index) { return null; }
}
