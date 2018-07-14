package org.trinity.wallet.net;

import com.alibaba.fastjson.JSON;

import org.trinity.wallet.ConfigList;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.jsonrpc.RequestJSONRpcBean;

import java.util.Arrays;

import okhttp3.MediaType;

public class WebSocketClient extends AbstractClient {
    private static final MediaType MEDIA_TYPE = MediaType.parse(ConfigList.CLIENT_MEDIA_TYPE);
    private String url;
    private String json;

    WebSocketClient(WebSocketClient.Builder builder) {
        this.url = builder.netUrl;
        this.json = JSON.toJSONString(builder.requestJSONRpcBean);
        if (url == null || "".equals(url.trim())) {
            url = WalletApplication.getNetUrl();
        }
    }

    public static final class Builder {
        String netUrl;
        RequestJSONRpcBean requestJSONRpcBean;

        public Builder() {
            requestJSONRpcBean = new RequestJSONRpcBean();
            requestJSONRpcBean.setJsonrpc("2.0");
            requestJSONRpcBean.setId("1");
        }

        public WebSocketClient.Builder net(String netUrl) {
            this.netUrl = netUrl;
            return this;
        }

        public WebSocketClient.Builder jsonrpc(String jsonrpc) {
            requestJSONRpcBean.setJsonrpc(jsonrpc);
            return this;
        }

        public WebSocketClient.Builder method(String method) {
            requestJSONRpcBean.setMethod(method);
            return this;
        }

        public WebSocketClient.Builder params(String... params) {
            requestJSONRpcBean.setParams(Arrays.asList(params));
            return this;
        }

        public WebSocketClient.Builder id(String id) {
            requestJSONRpcBean.setId(id);
            return this;
        }

        public WebSocketClient build() {
            return new WebSocketClient(this);
        }
    }
}
