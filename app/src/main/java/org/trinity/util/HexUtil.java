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
        String hexLocal = hex;
        if (hexLocal == null || "".equals(hexLocal)) {
            return null;
        }
        if (hexLocal.toLowerCase().contains("0x")) {
            hexLocal = hexLocal.substring(2);
        }
        int hexLen = hexLocal.length();
        if (hexLen % 2 == 1) {
            hexLen++;
            hexLocal = '0' + hexLocal;
        }
        byte[] bytes = new byte[(hexLen / 2)];
        for (int i = 0; i < hexLen; i += 2) {
            bytes[i / 2] = hexToByte(hexLocal.substring(i, i + 2));
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
