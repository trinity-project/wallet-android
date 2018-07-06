package org.trinity.util.net;

import org.json.JSONObject;

public final class JSONRpcClient extends AbstractJSONRpcClient {
    private JSONObject jsonObject;
    private Method method;
    private String[] params;
    private IJSONRpcClientCallback callback;

    public JSONRpcClient(Builder builder) {
        this.jsonObject = builder.jsonObject;
        this.method = builder.method;
        this.params = builder.params;
        this.callback = builder.callback;
    }

    public enum Method {

    }

    public static final class Builder {
        JSONObject jsonObject;
        Method method;
        String[] params;
        IJSONRpcClientCallback callback;

        public Builder() {
            jsonObject = new JSONObject();
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder params(String... params) {
            this.params = params;
            return this;
        }

        public Builder callback(IJSONRpcClientCallback callback) {
            this.callback = callback;
            return this;
        }

        public JSONRpcClient build() {
            return new JSONRpcClient(this);
        }
    }
}
