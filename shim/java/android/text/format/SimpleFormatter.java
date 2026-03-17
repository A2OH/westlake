package android.text.format;

/**
 * Pure-Java String.format replacement for KitKat Dalvik compatibility.
 * Handles common format specifiers without ICU/Locale natives.
 *
 * Supports: %s, %d, %f, %.Nf, %x, %X, %o, %b, %c, %n, %%
 * Supports: width (e.g., %5d), zero-padding (e.g., %02d, %04x)
 * Supports: left-justify (e.g., %-10s)
 * Does NOT support: locale-aware grouping, currency, date/time
 */
public class SimpleFormatter {

    /**
     * Format a string with the given arguments.
     */
    public static String format(String fmt, Object... args) {
        if (fmt == null) return "null";
        if (args == null) args = new Object[0];

        // Quick check: if no % in the format string, return as-is
        boolean hasPct = false;
        for (int k = 0; k < fmt.length(); k++) {
            if (fmt.charAt(k) == '%') { hasPct = true; break; }
        }
        if (!hasPct) return fmt;

        StringBuilder sb = new StringBuilder(fmt.length() + args.length * 8);
        int argIndex = 0;
        int i = 0;
        int len = fmt.length();

        while (i < len) {
            char c = fmt.charAt(i);
            if (c != '%' || i + 1 >= len) {
                sb.append(c);
                i++;
                continue;
            }

            // We have '%' followed by something
            i++; // skip '%'

            // Handle %%
            if (fmt.charAt(i) == '%') {
                sb.append('%');
                i++;
                continue;
            }

            // Handle %n
            if (fmt.charAt(i) == 'n') {
                sb.append('\n');
                i++;
                continue;
            }

            // Parse flags
            boolean leftJustify = false;
            boolean zeroPad = false;
            boolean plusSign = false;
            boolean spaceSign = false;

            boolean parsingFlags = true;
            while (i < len && parsingFlags) {
                char fc = fmt.charAt(i);
                if (fc == '-') {
                    leftJustify = true;
                    i++;
                } else if (fc == '0') {
                    zeroPad = true;
                    i++;
                } else if (fc == '+') {
                    plusSign = true;
                    i++;
                } else if (fc == ' ') {
                    spaceSign = true;
                    i++;
                } else {
                    parsingFlags = false;
                }
            }

            // Parse width
            int width = 0;
            while (i < len && fmt.charAt(i) >= '0' && fmt.charAt(i) <= '9') {
                width = width * 10 + (fmt.charAt(i) - '0');
                i++;
            }

            // Parse precision
            int precision = -1;
            if (i < len && fmt.charAt(i) == '.') {
                i++; // skip '.'
                precision = 0;
                while (i < len && fmt.charAt(i) >= '0' && fmt.charAt(i) <= '9') {
                    precision = precision * 10 + (fmt.charAt(i) - '0');
                    i++;
                }
            }

            // Parse conversion character
            if (i >= len) {
                sb.append('%');
                break;
            }

            char conv = fmt.charAt(i);
            i++;

            String formatted;

            switch (conv) {
                case 's': {
                    Object arg = (argIndex < args.length) ? args[argIndex++] : null;
                    String s = (arg == null) ? "null" : arg.toString();
                    if (precision >= 0 && precision < s.length()) {
                        s = s.substring(0, precision);
                    }
                    formatted = s;
                    // For %s, zero-pad doesn't apply, only space padding
                    formatted = applyWidth(formatted, width, leftJustify, false);
                    sb.append(formatted);
                    break;
                }
                case 'd': {
                    Object arg = (argIndex < args.length) ? args[argIndex++] : Integer.valueOf(0);
                    long val = toLong(arg);
                    String numStr = Long.toString(val);
                    if (val >= 0 && plusSign) {
                        numStr = "+" + numStr;
                    } else if (val >= 0 && spaceSign) {
                        numStr = " " + numStr;
                    }
                    formatted = applyWidth(numStr, width, leftJustify, zeroPad);
                    sb.append(formatted);
                    break;
                }
                case 'f': {
                    Object arg = (argIndex < args.length) ? args[argIndex++] : Double.valueOf(0.0);
                    double val = toDouble(arg);
                    int prec = (precision >= 0) ? precision : 6;
                    String numStr = formatFloat(val, prec);
                    if (val >= 0.0 && !Double.isNaN(val) && !Double.isInfinite(val)) {
                        if (plusSign) {
                            numStr = "+" + numStr;
                        } else if (spaceSign) {
                            numStr = " " + numStr;
                        }
                    }
                    formatted = applyWidth(numStr, width, leftJustify, zeroPad);
                    sb.append(formatted);
                    break;
                }
                case 'x':
                case 'X': {
                    Object arg = (argIndex < args.length) ? args[argIndex++] : Integer.valueOf(0);
                    long val = toLong(arg);
                    String hexStr;
                    if (val < 0) {
                        // For negative values, use full unsigned representation
                        // matching what Java's Long.toHexString does
                        hexStr = Long.toHexString(val);
                    } else {
                        hexStr = Long.toHexString(val);
                    }
                    if (conv == 'X') {
                        hexStr = toUpperCase(hexStr);
                    }
                    formatted = applyWidth(hexStr, width, leftJustify, zeroPad);
                    sb.append(formatted);
                    break;
                }
                case 'o': {
                    Object arg = (argIndex < args.length) ? args[argIndex++] : Integer.valueOf(0);
                    long val = toLong(arg);
                    String octStr = Long.toOctalString(val);
                    formatted = applyWidth(octStr, width, leftJustify, zeroPad);
                    sb.append(formatted);
                    break;
                }
                case 'b': {
                    Object arg = (argIndex < args.length) ? args[argIndex++] : Boolean.FALSE;
                    boolean boolVal;
                    if (arg == null) {
                        boolVal = false;
                    } else if (arg instanceof Boolean) {
                        boolVal = ((Boolean) arg).booleanValue();
                    } else {
                        boolVal = true; // non-null is true per Formatter spec
                    }
                    formatted = boolVal ? "true" : "false";
                    formatted = applyWidth(formatted, width, leftJustify, false);
                    sb.append(formatted);
                    break;
                }
                case 'c': {
                    Object arg = (argIndex < args.length) ? args[argIndex++] : Character.valueOf(' ');
                    char ch;
                    if (arg instanceof Character) {
                        ch = ((Character) arg).charValue();
                    } else if (arg instanceof Number) {
                        ch = (char) ((Number) arg).intValue();
                    } else {
                        ch = ' ';
                    }
                    formatted = String.valueOf(ch);
                    formatted = applyWidth(formatted, width, leftJustify, false);
                    sb.append(formatted);
                    break;
                }
                default:
                    // Unknown conversion, output as-is
                    sb.append('%');
                    sb.append(conv);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Format a double to a string with the given number of decimal places.
     * Pure Java, no ICU/Locale needed.
     */
    private static String formatFloat(double value, int precision) {
        if (Double.isNaN(value)) return "NaN";
        if (Double.isInfinite(value)) return value > 0 ? "Infinity" : "-Infinity";

        boolean negative = value < 0;
        if (negative) value = -value;

        // Scale by 10^precision, round, then split into integer and fraction
        long factor = 1;
        for (int p = 0; p < precision; p++) factor *= 10;

        long scaled = (long) (value * factor + 0.5);
        long intPart = scaled / factor;
        long fracPart = scaled % factor;

        StringBuilder result = new StringBuilder();
        if (negative) result.append('-');
        result.append(intPart);

        if (precision > 0) {
            result.append('.');
            String frac = Long.toString(fracPart);
            // Left-pad fraction with zeros to ensure correct width
            for (int p = frac.length(); p < precision; p++) {
                result.append('0');
            }
            result.append(frac);
        }

        return result.toString();
    }

    /**
     * Apply width formatting (padding) to a formatted value.
     */
    private static String applyWidth(String value, int width, boolean leftJustify, boolean zeroPad) {
        if (width <= 0 || value.length() >= width) {
            return value;
        }

        int padLen = width - value.length();
        char padChar = zeroPad ? '0' : ' ';

        StringBuilder result = new StringBuilder(width);

        if (leftJustify) {
            result.append(value);
            for (int p = 0; p < padLen; p++) result.append(' ');
        } else {
            if (zeroPad && value.length() > 0 && (value.charAt(0) == '-' || value.charAt(0) == '+')) {
                // For zero-padding, the sign goes before the zeros
                result.append(value.charAt(0));
                for (int p = 0; p < padLen; p++) result.append('0');
                result.append(value.substring(1));
            } else {
                for (int p = 0; p < padLen; p++) result.append(padChar);
                result.append(value);
            }
        }
        return result.toString();
    }

    /**
     * Convert an Object to a long value for integer formatting.
     */
    private static long toLong(Object arg) {
        if (arg instanceof Number) {
            return ((Number) arg).longValue();
        }
        return 0L;
    }

    /**
     * Convert an Object to a double value for float formatting.
     */
    private static double toDouble(Object arg) {
        if (arg instanceof Number) {
            return ((Number) arg).doubleValue();
        }
        return 0.0;
    }

    /**
     * Convert a string to uppercase without locale dependency.
     */
    private static String toUpperCase(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 'a' && chars[i] <= 'f') {
                chars[i] = (char) (chars[i] - 'a' + 'A');
            }
        }
        return new String(chars);
    }
}
