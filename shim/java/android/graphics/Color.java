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

    public static void RGBToHSV(int r, int g, int b, float[] hsv) {
        float rf = r / 255f, gf = g / 255f, bf = b / 255f;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;
        // Hue
        if (delta == 0f) hsv[0] = 0f;
        else if (max == rf) hsv[0] = 60f * (((gf - bf) / delta) % 6f);
        else if (max == gf) hsv[0] = 60f * (((bf - rf) / delta) + 2f);
        else hsv[0] = 60f * (((rf - gf) / delta) + 4f);
        if (hsv[0] < 0f) hsv[0] += 360f;
        // Saturation
        hsv[1] = max == 0f ? 0f : delta / max;
        // Value
        hsv[2] = max;
    }

    public static void colorToHSV(int color, float[] hsv) {
        RGBToHSV(red(color), green(color), blue(color), hsv);
    }

    public static int HSVToColor(float[] hsv) {
        return HSVToColor(255, hsv);
    }

    public static int HSVToColor(int alpha, float[] hsv) {
        float h = hsv[0], s = hsv[1], v = hsv[2];
        float c = v * s;
        float x = c * (1f - Math.abs((h / 60f) % 2f - 1f));
        float m = v - c;
        float r, g, b;
        if (h < 60) { r = c; g = x; b = 0; }
        else if (h < 120) { r = x; g = c; b = 0; }
        else if (h < 180) { r = 0; g = c; b = x; }
        else if (h < 240) { r = 0; g = x; b = c; }
        else if (h < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }
        return argb(alpha, Math.round((r + m) * 255), Math.round((g + m) * 255), Math.round((b + m) * 255));
    }

    public static float luminance(int color) {
        double r = linearize(red(color) / 255.0);
        double g = linearize(green(color) / 255.0);
        double b = linearize(blue(color) / 255.0);
        return (float) (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }

    private static double linearize(double srgb) {
        return srgb <= 0.04045 ? srgb / 12.92 : Math.pow((srgb + 0.055) / 1.055, 2.4);
    }

    public float getComponent(Object p0, Object p1) { return 0f; }
    public Object getModel() { return null; }
    public static boolean isInColorSpace(Object p0, Object p1) { return false; }
    public boolean isSrgb() { return true; }
    public static boolean isSrgb(Object p0) { return true; }
    public boolean isWideGamut() { return false; }
    public static boolean isWideGamut(Object p0) { return false; }
    public float luminance() { return 0f; }
}
