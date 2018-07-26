package org.trinity.wallet.net;

import android.support.annotation.NonNull;

import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient extends AbstractClient {
    private Request request;

    WebSocketClient(WebSocketClient.Builder builder) {
        request = new Request.Builder()
                .url(builder.url)
                .build();
    }

    public WebSocket connect(WebSocketListener listener) {
        return client.newWebSocket(request, listener);
    }

    public static final class Builder {
        String url;

        public Builder() {
        }

        public WebSocketClient.Builder url(@NonNull String url) {
            this.url = url;
            return this;
        }

        public WebSocketClient build() {
            return new WebSocketClient(this);
        }
    }
}
