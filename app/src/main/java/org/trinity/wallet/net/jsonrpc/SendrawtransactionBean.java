package org.trinity.wallet.net.jsonrpc;

public class SendrawtransactionBean extends BaseJSONRpcBean {
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
