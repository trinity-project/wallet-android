package org.trinity.wallet.net.json.bean;

import java.math.BigDecimal;

public class GetBalanceBean {
    private String id;
    private String jsonrpc;
    private ResultBean result;

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
