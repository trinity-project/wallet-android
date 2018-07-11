package org.trinity.util.net.json.bean;

import java.util.ArrayList;
import java.util.List;

public final class RequestBean {
    private String jsonrpc = "2.0";
    private String method;
    private List<String> params = new ArrayList<>();
    private int id = 1;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
