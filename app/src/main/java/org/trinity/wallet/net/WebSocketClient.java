package org.trinity.wallet.net;

import org.trinity.wallet.net.jsonrpc.RequestJSONRpcBean;

import java.util.Arrays;

import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient extends AbstractClient {
    private WebSocket stepsWebSocket;
    private Request request;

    WebSocketClient(WebSocketClient.Builder builder) {
        request = new Request.Builder()
                .url(builder.netUrl)
                .build();
    }

    public void connect(WebSocketListener listener) {
        stepsWebSocket = client.newWebSocket(request, listener);
    }

    public void finish() {
        stepsWebSocket.cancel();
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
