package libcore.icu;

import java.util.Locale;

/** Stub for AOSP libcore LocaleData used by NumberPicker. */
public class LocaleData {
    public char zeroDigit = '0';
    public String[] amPm = {"AM", "PM"};
    public String narrowAm = "a";
    public String narrowPm = "p";
    public String timeFormat_hm = "h:mm a";
    public String timeFormat_Hm = "HH:mm";

    public LocaleData() {}

    public static LocaleData get(Locale locale) {
        return new LocaleData();
    }
}
