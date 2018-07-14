package org.trinity.wallet.net.jsonrpc;

public class GetBalanceBean extends BaseJSONRpcBean {
    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String gasBalance;
        private String neoBalance;
        private String tncBalance;

        public String getGasBalance() {
            return gasBalance;
        }

        public void setGasBalance(String gasBalance) {
            this.gasBalance = gasBalance;
        }

        public String getNeoBalance() {
            return neoBalance;
        }

        public void setNeoBalance(String neoBalance) {
            this.neoBalance = neoBalance;
        }

        public String getTncBalance() {
            return tncBalance;
        }

        public void setTncBalance(String tncBalance) {
            this.tncBalance = tncBalance;
        }
    }
}
