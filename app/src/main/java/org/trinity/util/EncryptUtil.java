package org.trinity.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {
    /**
     * MD5 encrypt.
     *
     * @param src Source string.
     * @return Encrypted code.
     */
    public static String encodeMd5(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return HexUtil.byteArrayToHex(md5.digest(src.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
            return null;
        }
    }
}
