package org.trinity.util.convert;

public class PrefixUtil {
    public static String add0x(String hex) {
        return "0x" + hex;
    }

    public static String trim0x(String hex0x) {
        if (hex0x.length() <= 2) {
            return hex0x;
        } else {
            return hex0x.substring(2);
        }
    }
}
