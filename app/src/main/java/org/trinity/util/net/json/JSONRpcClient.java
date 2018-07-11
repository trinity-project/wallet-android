package org.trinity.util.net.json;

import com.alibaba.fastjson.JSON;

import org.trinity.util.net.json.bean.RequestBean;

import java.io.IOException;
import java.util.Arrays;

public final class JSONRpcClient extends AbstractJSONClient {
    public static final String MAIN_NET_URL = "http://47.93.214.2:21332";
    public static final String TEST_NET_URL = "http://47.254.64.251:21332";

    private String url = MAIN_NET_URL;
    private String json;

    JSONRpcClient(Builder builder) {
        this.json = JSON.toJSONString(builder.requestBean);
    }

    public String post() throws IOException {
        return post(url, json);
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
