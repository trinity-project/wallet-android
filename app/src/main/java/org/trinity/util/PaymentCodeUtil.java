package org.trinity.util;

import org.trinity.util.algorithm.Base58Util;
import org.trinity.wallet.entity.PaymentCodeBean;

public class PaymentCodeUtil {
    public static PaymentCodeBean decode(String paymentCode){
        byte[] decode = Base58Util.decode(paymentCode.substring(2));
        String decodeStr = new String(decode);
        String[] split = decodeStr.split("&");
        PaymentCodeBean paymentCodeBean = new PaymentCodeBean();
        return null;
    }
}
