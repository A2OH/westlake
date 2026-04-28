package com.westlake.mcdprofile;

import com.westlake.engine.WestlakeLauncher;

final class McdProfileLog {
    private McdProfileLog() {}

    static void mark(String name, String detail) {
        try {
            WestlakeLauncher.appendCutoffCanaryMarker(
                    "MCD_PROFILE_" + name + " " + (detail == null ? "" : detail));
        } catch (Throwable ignored) {
        }
    }

    static String token(String value) {
        if (value == null || value.length() == 0) {
            return "empty";
        }
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')
                    || c == '.' || c == '-' || c == '_') {
                out.append(c);
            } else {
                out.append('_');
            }
        }
        return out.length() > 96 ? out.substring(0, 96) : out.toString();
    }
}
