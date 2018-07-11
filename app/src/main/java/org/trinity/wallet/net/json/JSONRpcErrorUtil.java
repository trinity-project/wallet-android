package org.trinity.wallet.net.json;

import com.alibaba.fastjson.JSON;

import org.trinity.wallet.net.json.bean.ErrorBean;

public final class JSONRpcErrorUtil {
    public static boolean hasError(String json) {
        ErrorBean errorBean = JSON.parseObject(json, ErrorBean.class);
        return errorBean.getError() != null;
    }
}
