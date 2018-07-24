package org.trinity.util;

import android.support.annotation.Nullable;

import org.trinity.util.algorithm.Base58Util;
import org.trinity.wallet.entity.PaymentCodeBean;

public class PaymentCodeUtil {
    @Nullable
    public static PaymentCodeBean decode(String paymentCode) {
        byte[] decode;
        try {
            decode = Base58Util.decode(paymentCode.substring(2));
        } catch (Exception e) {
            return null;
        }
        String decodeStr = new String(decode);
        String[] split = decodeStr.split("&");
        if (split.length != 5) {
            return null;
        }
        double split_3;
        try {
            split_3 = Double.parseDouble(split[3]);
        } catch (NumberFormatException ignored) {
            return null;
        }
        PaymentCodeBean paymentCodeBean = new PaymentCodeBean();
        paymentCodeBean.setsTNAP(split[0]);
        paymentCodeBean.setRandomHash(split[1]);
        paymentCodeBean.setAssetId(split[2]);
        paymentCodeBean.setPrice(split_3);
        paymentCodeBean.setComment(split[4]);
        return paymentCodeBean;
    }
}
