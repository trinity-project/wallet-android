package org.trinity.wallet.net.json.bean;

public class GetBalanceBean {
    private int id;
    private String jsonrpc;
    private ResultBean result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        private double gasBalance;
        private int neoBalance;
        private int tncBalance;

        public double getGasBalance() {
            return gasBalance;
        }

        public void setGasBalance(double gasBalance) {
            this.gasBalance = gasBalance;
        }

        public int getNeoBalance() {
            return neoBalance;
        }

        public void setNeoBalance(int neoBalance) {
            this.neoBalance = neoBalance;
        }

        public int getTncBalance() {
            return tncBalance;
        }

        public void setTncBalance(int tncBalance) {
            this.tncBalance = tncBalance;
        }
    }
}
