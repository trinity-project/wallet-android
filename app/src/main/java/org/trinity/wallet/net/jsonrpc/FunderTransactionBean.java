package org.trinity.wallet.net.jsonrpc;

public class FunderTransactionBean extends BaseJSONRpcBean {
    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private BRTXBean BR_TX;
        private CTXBean C_TX;
        private RTXBean R_TX;

        public BRTXBean getBR_TX() {
            return BR_TX;
        }

        public void setBR_TX(BRTXBean BR_TX) {
            this.BR_TX = BR_TX;
        }

        public CTXBean getC_TX() {
            return C_TX;
        }

        public void setC_TX(CTXBean C_TX) {
            this.C_TX = C_TX;
        }

        public RTXBean getR_TX() {
            return R_TX;
        }

        public void setR_TX(RTXBean R_TX) {
            this.R_TX = R_TX;
        }

        public static class BRTXBean {
            private String txData;
            private String txId;
            private String witness;

            public String getTxData() {
                return txData;
            }

            public void setTxData(String txData) {
                this.txData = txData;
            }

            public String getTxId() {
                return txId;
            }

            public void setTxId(String txId) {
                this.txId = txId;
            }

            public String getWitness() {
                return witness;
            }

            public void setWitness(String witness) {
                this.witness = witness;
            }
        }

        public static class CTXBean {
            private String addressRSMC;
            private String scriptRSMC;
            private String txData;
            private String txId;
            private String witness;

            public String getAddressRSMC() {
                return addressRSMC;
            }

            public void setAddressRSMC(String addressRSMC) {
                this.addressRSMC = addressRSMC;
            }

            public String getScriptRSMC() {
                return scriptRSMC;
            }

            public void setScriptRSMC(String scriptRSMC) {
                this.scriptRSMC = scriptRSMC;
            }

            public String getTxData() {
                return txData;
            }

            public void setTxData(String txData) {
                this.txData = txData;
            }

            public String getTxId() {
                return txId;
            }

            public void setTxId(String txId) {
                this.txId = txId;
            }

            public String getWitness() {
                return witness;
            }

            public void setWitness(String witness) {
                this.witness = witness;
            }
        }

        public static class RTXBean {
            private String txData;
            private String txId;
            private String witness;

            public String getTxData() {
                return txData;
            }

            public void setTxData(String txData) {
                this.txData = txData;
            }

            public String getTxId() {
                return txId;
            }

            public void setTxId(String txId) {
                this.txId = txId;
            }

            public String getWitness() {
                return witness;
            }

            public void setWitness(String witness) {
                this.witness = witness;
            }
        }
    }
}
