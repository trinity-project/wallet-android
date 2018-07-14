package org.trinity.wallet.net.jsonrpc;

import java.util.List;

public final class RequestJSONRpcBean extends BaseJSONRpcBean {
    private String method;
    private List<String> params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}
