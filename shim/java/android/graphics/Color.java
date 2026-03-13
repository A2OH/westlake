package android.graphics;

public class Color {
    public Color() {}

    public static final int BLACK       = 0xFF000000;
    public static final int DKGRAY      = 0xFF444444;
    public static final int GRAY        = 0xFF888888;
    public static final int LTGRAY      = 0xFFCCCCCC;
    public static final int WHITE       = 0xFFFFFFFF;
    public static final int RED         = 0xFFFF0000;
    public static final int GREEN       = 0xFF00FF00;
    public static final int BLUE        = 0xFF0000FF;
    public static final int YELLOW      = 0xFFFFFF00;
    public static final int CYAN        = 0xFF00FFFF;
    public static final int MAGENTA     = 0xFFFF00FF;
    public static final int TRANSPARENT = 0x00000000;

    public static int alpha(int color) { return (color >>> 24); }
    public static int red(int color)   { return (color >> 16) & 0xFF; }
    public static int green(int color) { return (color >> 8) & 0xFF; }
    public static int blue(int color)  { return color & 0xFF; }

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int rgb(int red, int green, int blue) {
        return argb(255, red, green, blue);
    }

    public static int parseColor(String colorString) {
        if (colorString == null || colorString.isEmpty()) {
            throw new IllegalArgumentException("Unknown color: " + colorString);
        }
        if (colorString.charAt(0) == '#') {
            String hex = colorString.substring(1);
            long color;
            if (hex.length() == 6) {
                color = Long.parseLong(hex, 16) | 0xFF000000L;
            } else if (hex.length() == 8) {
                color = Long.parseLong(hex, 16);
            } else {
                throw new IllegalArgumentException("Unknown color: " + colorString);
            }
            return (int) color;
        }
        switch (colorString.toLowerCase()) {
            case "black":       return BLACK;
            case "darkgray":
            case "dkgray":      return DKGRAY;
            case "gray":        return GRAY;
            case "lightgray":
            case "ltgray":      return LTGRAY;
            case "white":       return WHITE;
            case "red":         return RED;
            case "green":       return GREEN;
            case "blue":        return BLUE;
            case "yellow":      return YELLOW;
            case "cyan":        return CYAN;
            case "magenta":     return MAGENTA;
            case "transparent": return TRANSPARENT;
            default:
                throw new IllegalArgumentException("Unknown color: " + colorString);
        }
    }

    // Float-returning variants for compat
    public float alpha() { return 0f; }
    public float blue() { return 0f; }
    public float green() { return 0f; }
    public float red() { return 0f; }

    public static void RGBToHSV(int red, int green, int blue, float[] hsv) {}
    public static void colorToHSV(int color, float[] hsv) {}
    public float getComponent(Object p0, Object p1) { return 0f; }
    public Object getModel() { return null; }
    public static boolean isInColorSpace(Object p0, Object p1) { return false; }
    public boolean isSrgb() { return false; }
    public static boolean isSrgb(Object p0) { return false; }
    public boolean isWideGamut() { return false; }
    public static boolean isWideGamut(Object p0) { return false; }
    public float luminance() { return 0f; }
    public static float luminance(Object p0) { return 0f; }
}
