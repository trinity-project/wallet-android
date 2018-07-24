package org.trinity.util.convert;

import android.support.annotation.NonNull;

import org.trinity.wallet.ConfigList;

import neoutils.Wallet;

/**
 * A TNAP must be "public key" + "@" + "ip:port".
 */
public class TNAPUtil {
    public static boolean isValid(@NonNull String sTNAP) {
        if ("".equals(sTNAP) || !sTNAP.contains("@") || !sTNAP.contains(".") || !sTNAP.contains(":")) {
            return false;
        }

        String[] sTNAPSplit = sTNAP.split("@");
        if (sTNAPSplit.length != 2) {
            return false;
        }

        String publicKey = sTNAPSplit[0];
        if (!publicKey.matches(ConfigList.REGEX_NEO_PUBLIC_KEY)) {
            return false;
        }

        String ipPort = sTNAPSplit[1];
        if (!ipPort.matches(ConfigList.REGEX_IP_PORT)) {
            return false;
        }

        return true;
    }

    public static String getPublicKey(@NonNull String sTNAP) {
        return sTNAP.substring(0, sTNAP.indexOf("@"));
    }

    public static String getTNAPSpv(@NonNull String sTNAP, @NonNull Wallet wallet) {
        return HexUtil.byteArrayToHex(wallet.getPublicKey()) + sTNAP.substring(sTNAP.indexOf("@"), sTNAP.indexOf(":") + 1) + ConfigList.GATEWAY_SPV_PORT;
    }

    public static String getIpPort(@NonNull String sTNAP) {
        return sTNAP.substring(sTNAP.indexOf("@") + 1);
    }

    public static String getHttp(@NonNull String sTNAP) {
        return ConfigList.HTTP_URL_PREFIX + getIpPort(sTNAP);
    }

    public static String getWs(@NonNull String sTNAP) {
        return ConfigList.WS_URL_PREFIX + getIpPort(sTNAP);
    }
}
