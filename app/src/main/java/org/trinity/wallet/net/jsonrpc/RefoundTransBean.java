package org.trinity.wallet.net.jsonrpc;

public class RefoundTransBean {
    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String txData;
        private String txid;
        private String witness;

        public String getTxData() {
            return txData;
        }

        public void setTxData(String txData) {
            this.txData = txData;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getWitness() {
            return witness;
        }

        public void setWitness(String witness) {
            this.witness = witness;
        }
    }
}
