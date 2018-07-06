package org.trinity.util;

public final class HexUtil {
    /**
     * Convert byte array to hex string.
     *
     * @param bytes Byte array
     * @return Hex string
     */
    public static String byteArrayToHex(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String aHex;
        for (byte aByte : bytes) {
            aHex = Integer.toHexString(aByte & 0xFF);
            if (aHex.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(aHex);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte array.
     *
     * @param hex Hex string
     * @return Byte array
     */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || "".equals(hex)) {
            return null;
        }
        int hexLen = hex.length();
        if (hexLen % 2 == 1) {
            hexLen++;
            hex = '0' + hex;
        }
        byte[] bytes = new byte[(hexLen / 2)];
        for (int i = 0; i < hexLen; i += 2) {
            bytes[i / 2] = hexToByte(hex.substring(i, i + 2));
        }
        return bytes;
    }

    /**
     * Convert hex to byte.
     *
     * @param hex Hex string
     * @return Byte
     */
    private static byte hexToByte(String hex) {
        return (byte) Integer.parseInt(hex, 16);
    }
}
