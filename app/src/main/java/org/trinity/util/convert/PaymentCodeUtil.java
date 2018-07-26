package org.trinity.util.convert;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.trinity.util.algorithm.Base58Util;
import org.trinity.wallet.entity.PaymentCodeBean;

import java.math.BigDecimal;

public class PaymentCodeUtil {
    private static final String splitSign = "&";

    @Nullable
    public static PaymentCodeBean decode(String paymentCode) {
        byte[] decode;
        try {
            decode = Base58Util.decode(paymentCode.substring(2));
        } catch (Exception e) {
            return null;
        }
        String decodeStr = new String(decode);
        String[] split = decodeStr.split(splitSign);
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
        paymentCodeBean.setTNAP(split[0]);
        paymentCodeBean.setRandomHash(split[1]);
        paymentCodeBean.setAssetId(split[2]);
        paymentCodeBean.setPrice(split_3);
        paymentCodeBean.setComment(split[4]);
        return paymentCodeBean;
    }

    @Nullable
    public static String encode(@NonNull PaymentCodeBean paymentCodeBean) {
        String[] unSplit = {
                paymentCodeBean.getTNAP(),
                paymentCodeBean.getRandomHash(),
                paymentCodeBean.getAssetId(),
                BigDecimal.valueOf(paymentCodeBean.getPrice()).toPlainString(),
                paymentCodeBean.getComment()};

        StringBuilder stringBuilder = new StringBuilder();
        for (String unit : unSplit) {
            stringBuilder.append(unit).append(splitSign);
        }
        try {
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(splitSign));
        } catch (Exception ignored) {
            return null;
        }
        String encodeStr = stringBuilder.toString();
        String paymentCode;
        try {
            paymentCode = "TN" + Base58Util.encode(encodeStr.getBytes("US-ASCII"));
        } catch (Exception ignored) {
            return null;
        }

        return paymentCode;
    }
}
