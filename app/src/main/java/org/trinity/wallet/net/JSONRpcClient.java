package org.trinity.wallet.net;

import com.alibaba.fastjson.JSON;

import org.trinity.wallet.ConfigList;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.jsonrpc.RequestJSONRpcBean;

import java.util.Arrays;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class JSONRpcClient extends AbstractClient {

    private static final MediaType MEDIA_TYPE = MediaType.parse(ConfigList.CLIENT_MEDIA_TYPE);
    private String url;
    private String json;

    JSONRpcClient(Builder builder) {
        this.url = builder.netUrl;
        this.json = JSON.toJSONString(builder.requestJSONRpcBean);
        if (url == null || "".equals(url.trim())) {
            url = WalletApplication.getNetUrl();
        }
    }

    public void post(Callback callback) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static final class Builder {
        String netUrl;
        RequestJSONRpcBean requestJSONRpcBean;

        public Builder() {
            requestJSONRpcBean = new RequestJSONRpcBean();
            requestJSONRpcBean.setJsonrpc("2.0");
            requestJSONRpcBean.setId("1");
        }

        public Builder net(String netUrl) {
            this.netUrl = netUrl;
            return this;
        }

        public Builder jsonrpc(String jsonrpc) {
            requestJSONRpcBean.setJsonrpc(jsonrpc);
            return this;
        }

        public Builder method(String method) {
            requestJSONRpcBean.setMethod(method);
            return this;
        }

        public Builder params(String... params) {
            requestJSONRpcBean.setParams(Arrays.asList(params));
            return this;
        }

        public Builder id(String id) {
            requestJSONRpcBean.setId(id);
            return this;
        }

        public JSONRpcClient build() {
            return new JSONRpcClient(this);
        }
    }
}
