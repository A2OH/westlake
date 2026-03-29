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
    public int fontScale = 1;

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

    public boolean isScreenRound() { return false; }
}
