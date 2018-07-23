package org.trinity.util;

public class PrefixUtil {
    public static String addOx(String hex) {
        return "0x" + hex;
    }

    public static String trimOx(String hex0x) {
        if (hex0x.length() <= 2) {
            return hex0x;
        } else {
            return hex0x.substring(2);
        }
    }
}
