package org.trinity.wallet.net.json;

import com.alibaba.fastjson.JSON;

import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.json.bean.RequestBean;

import java.util.Arrays;

import okhttp3.Callback;

public class JSONRpcClient extends AbstractJSONClient {

    private String url;
    private String json;

    JSONRpcClient(Builder builder) {
        this.url = builder.netUrl;
        this.json = JSON.toJSONString(builder.requestBean);
        if (url == null || "".equals(url.trim())) {
            url = WalletApplication.getNetUrl();
        }
    }

    public void post(Callback callback) {
        post(url, json, callback);
    }

    public static final class Builder {
        String netUrl;
        RequestBean requestBean;

        public Builder() {
            requestBean = new RequestBean();
            requestBean.setId("1");
        }

        public Builder net(String netUrl) {
            this.netUrl = netUrl;
            return this;
        }

        public Builder method(String method) {
            requestBean.setMethod(method);
            return this;
        }

        public Builder params(String... params) {
            requestBean.setParams(Arrays.asList(params));
            return this;
        }

        public Builder id(String id) {
            requestBean.setId(id);
            return this;
        }

        public JSONRpcClient build() {
            return new JSONRpcClient(this);
        }
    }
}
