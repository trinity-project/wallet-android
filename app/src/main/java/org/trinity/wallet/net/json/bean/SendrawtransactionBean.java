package org.trinity.wallet.net.json.bean;

public class SendrawtransactionBean {
    private String id;
    private String jsonrpc;
    private String result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String isResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
