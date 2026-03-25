package com.example.apklauncher;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Material Design Icons via the Material Icons TTF font.
 * Unicode codepoints from https://fonts.google.com/icons
 */
public class MaterialIcons {
    private static Typeface iconFont;

    // Common Material Icons (unicode codepoints)
    public static final String HOME = "\uE88A";
    public static final String PHONE = "\uE0CD";
    public static final String CHAT = "\uE0B7";
    public static final String CAMERA = "\uE3AF";
    public static final String SETTINGS = "\uE8B8";
    public static final String SEARCH = "\uE8B6";
    public static final String ARROW_BACK = "\uE5C4";
    public static final String RESTAURANT = "\uE56C";
    public static final String CALCULATE = "\uEA5F";
    public static final String CALENDAR = "\uE878";
    public static final String ALARM = "\uE855";
    public static final String FOLDER = "\uE2C7";
    public static final String PERSON = "\uE7FD";
    public static final String GROUP = "\uE7EF";
    public static final String FAVORITE = "\uE87D";
    public static final String SHARE = "\uE80D";
    public static final String ADD = "\uE145";
    public static final String DELETE = "\uE872";
    public static final String EDIT = "\uE3C9";
    public static final String STAR = "\uE838";
    public static final String CHECK = "\uE5CA";
    public static final String CLOSE = "\uE5CD";
    public static final String MENU = "\uE5D2";
    public static final String MORE_VERT = "\uE5D4";
    public static final String SEND = "\uE163";
    public static final String SHOPPING_CART = "\uE8CC";
    public static final String NOTIFICATIONS = "\uE7F4";
    public static final String APPS = "\uE5C3";
    public static final String PLAY_ARROW = "\uE037";
    public static final String THUMB_UP = "\uE8DC";
    public static final String VOICEMAIL = "\uE0D9";
    public static final String CALL_MADE = "\uE0B2";
    public static final String CALL_RECEIVED = "\uE0B5";
    public static final String CALL_MISSED = "\uE0B4";
    public static final String BACKSPACE = "\uE14A";
    public static final String HISTORY = "\uE889";
    public static final String CONTACTS = "\uE0BA";
    public static final String DIALPAD = "\uE0DC";
    public static final String WEB = "\uE894";
    public static final String PHOTO = "\uE410";
    public static final String VIDEOCAM = "\uE04B";
    public static final String KEYBOARD = "\uE312";
    public static final String MIC = "\uE029";
    public static final String VOLUME_UP = "\uE050";
    public static final String PAUSE = "\uE034";
    public static final String ADD_CALL = "\uE0E8";
    public static final String STORE = "\uE8D1";
    public static final String TV = "\uE333";

    public static void init(Context ctx) {
        try {
            iconFont = Typeface.createFromAsset(ctx.getAssets(), "material_icons.ttf");
            System.out.println("[MaterialIcons] Font loaded: " + (iconFont != null));
        } catch (Exception e) {
            System.out.println("[MaterialIcons] Font load failed: " + e);
            // Try alternative path
            try {
                java.io.File fontFile = new java.io.File(ctx.getCacheDir(), "material_icons.ttf");
                if (!fontFile.exists()) {
                    java.io.InputStream is = ctx.getAssets().open("material_icons.ttf");
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(fontFile);
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = is.read(buf)) > 0) fos.write(buf, 0, n);
                    fos.close();
                    is.close();
                }
                iconFont = Typeface.createFromFile(fontFile);
                System.out.println("[MaterialIcons] Font loaded from cache: " + (iconFont != null));
            } catch (Exception e2) {
                System.out.println("[MaterialIcons] Cache font load also failed: " + e2);
                iconFont = Typeface.DEFAULT;
            }
        }
    }

    public static Typeface getFont() {
        return iconFont != null ? iconFont : Typeface.DEFAULT;
    }

    /** Create a TextView displaying a Material Icon */
    public static TextView icon(Context ctx, String codepoint, int sizeSp, int color) {
        TextView tv = new TextView(ctx);
        tv.setText(codepoint);
        tv.setTextSize(sizeSp);
        tv.setTextColor(color);
        tv.setTypeface(getFont());
        tv.setGravity(android.view.Gravity.CENTER);
        return tv;
    }
}
