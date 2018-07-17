package org.trinity.util;

import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.jsonrpc.ErrorBean;

public final class JSONRpcErrorUtil {
    public static boolean hasError(String json) {
        ErrorBean errorBean = WalletApplication.getGson().fromJson(json, ErrorBean.class);
        return errorBean.getError() != null;
    }
}
