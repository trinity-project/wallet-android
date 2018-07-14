package org.trinity.wallet.net.jsonrpc;

public class ErrorBean extends BaseJSONRpcBean {
    private Object error;

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
