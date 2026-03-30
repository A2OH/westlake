package android.content.res;
import java.util.Locale;

public class Configuration {
    public static final int ORIENTATION_UNDEFINED  = 0;
    public static final int ORIENTATION_PORTRAIT   = 1;
    public static final int ORIENTATION_LANDSCAPE  = 2;

    public int             orientation   = ORIENTATION_PORTRAIT;
    public int             screenWidthDp  = 240;
    public int             screenHeightDp = 400;
    public int             densityDpi     = 320;
    public java.util.Locale locale        = java.util.Locale.getDefault();
    public int             uiMode         = 0;

    public static final int UI_MODE_TYPE_MASK = 0x0f;
    public static final int UI_MODE_TYPE_UNDEFINED = 0;
    public static final int UI_MODE_TYPE_NORMAL = 1;
    public static final int UI_MODE_TYPE_DESK = 2;
    public static final int UI_MODE_TYPE_CAR = 3;
    public static final int UI_MODE_TYPE_TELEVISION = 4;
    public static final int UI_MODE_TYPE_APPLIANCE = 5;
    public static final int UI_MODE_TYPE_WATCH = 6;
    public static final int UI_MODE_TYPE_VR_HEADSET = 7;

    public static final int UI_MODE_NIGHT_MASK = 0x30;
    public static final int UI_MODE_NIGHT_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_NO = 0x10;
    public static final int UI_MODE_NIGHT_YES = 0x20;

    public static final int SCREENLAYOUT_SIZE_MASK = 0x0f;
    public static final int SCREENLAYOUT_SIZE_UNDEFINED = 0;
    public static final int SCREENLAYOUT_SIZE_SMALL = 1;
    public static final int SCREENLAYOUT_SIZE_NORMAL = 2;
    public static final int SCREENLAYOUT_SIZE_LARGE = 3;
    public static final int SCREENLAYOUT_SIZE_XLARGE = 4;
    public int screenLayout = 0;
    public float fontScale = 1.0f;

    public Configuration() {}

    public Configuration(Configuration o) {
        this.orientation = o.orientation;
        this.screenWidthDp = o.screenWidthDp;
        this.screenHeightDp = o.screenHeightDp;
        this.densityDpi = o.densityDpi;
        this.locale = o.locale;
        this.uiMode = o.uiMode;
        this.screenLayout = o.screenLayout;
        this.fontScale = o.fontScale;
    }

    public int smallestScreenWidthDp = 320;
    public int navigation = 0;
    public int touchscreen = 0;
    public int keyboard = 0;
    public int keyboardHidden = 0;
    public int hardKeyboardHidden = 0;
    public int navigationHidden = 0;
    public int mnc = 0;
    public int mcc = 0;
    public int colorMode = 0;
    public int screenLayoutLong = 0;

    public boolean isScreenRound() { return false; }

    public android.os.LocaleList getLocales() {
        return new android.os.LocaleList(locale != null ? locale : java.util.Locale.getDefault());
    }

    public void setLocales(android.os.LocaleList locales) {
        if (locales != null && locales.size() > 0) {
            locale = locales.get(0);
        }
    }

    public void setToDefaults() {
        fontScale = 1.0f;
        orientation = ORIENTATION_UNDEFINED;
        uiMode = 0;
        locale = java.util.Locale.getDefault();
    }

    public int diff(Configuration delta) { return 0; }
}
