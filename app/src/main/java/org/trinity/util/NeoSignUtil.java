package org.trinity.util;

import neoutils.Neoutils;

public final class NeoSignUtil {
    public static byte[] sign(String srcHex, byte[] keyBytes) {
        return sign(HexUtil.hexToByteArray(srcHex), HexUtil.byteArrayToHex(keyBytes));
    }

    public static byte[] sign(String srcHex, String keyHex) {
        return sign(HexUtil.hexToByteArray(srcHex), keyHex);
    }

    public static byte[] sign(byte[] srcBytes, byte[] keyBytes) {
        return sign(srcBytes, HexUtil.byteArrayToHex(keyBytes));
    }

    public static byte[] sign(byte[] srcBytes, String keyHex) {
        try {
            return Neoutils.sign(srcBytes, keyHex);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String signToHex(String srcHex, byte[] keyBytes) {
        return HexUtil.byteArrayToHex(sign(srcHex, keyBytes));
    }

    public static String signToHex(String srcHex, String keyHex) {
        return HexUtil.byteArrayToHex(sign(srcHex, keyHex));
    }

    public static String signToHex(byte[] srcBytes, byte[] keyBytes) {
        return HexUtil.byteArrayToHex(sign(srcBytes, keyBytes));
    }

    public static String signToHex(byte[] srcBytes, String keyHex) {
        return HexUtil.byteArrayToHex(sign(srcBytes, keyHex));
    }
}
