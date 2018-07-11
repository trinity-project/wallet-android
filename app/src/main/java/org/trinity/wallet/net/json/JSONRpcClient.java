package org.trinity.wallet.net.json;

import com.alibaba.fastjson.JSON;

import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.json.bean.RequestBean;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Callback;

public final class JSONRpcClient extends AbstractJSONClient {

    private String url;
    private String json;

    JSONRpcClient(Builder builder) {
        this.url = WalletApplication.getNetUrl();
        this.json = JSON.toJSONString(builder.requestBean);
    }

    public void post(Callback callback) throws IOException {
        post(url, json, callback);
    }

    public static final class Builder {
        RequestBean requestBean;
        String method;
        String[] params;

        public Builder() {
            requestBean = new RequestBean();
        }

        public Builder method(String method) {
            requestBean.setMethod(method);
            this.method = method;
            return this;
        }

        public Builder params(String... params) {
            requestBean.setParams(Arrays.asList(params));
            this.params = params;
            return this;
        }

        public JSONRpcClient build() {
            return new JSONRpcClient(this);
        }
    }
}
