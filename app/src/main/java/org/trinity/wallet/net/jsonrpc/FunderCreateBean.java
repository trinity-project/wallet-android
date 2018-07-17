package org.trinity.wallet.net.jsonrpc;

public class FunderCreateBean extends BaseJSONRpcBean {
    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private FounderBean Founder;
        private CTXBean C_TX;
        private RTXBean R_TX;

        public FounderBean getFounder() {
            return Founder;
        }

        public void setFounder(FounderBean Founder) {
            this.Founder = Founder;
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

        public static class FounderBean {
            private String txData;
            private String addressFunding;
            private String txId;
            private String scriptFunding;
            private String witness;

            public String getTxData() {
                return txData;
            }

            public void setTxData(String txData) {
                this.txData = txData;
            }

            public String getAddressFunding() {
                return addressFunding;
            }

            public void setAddressFunding(String addressFunding) {
                this.addressFunding = addressFunding;
            }

            public String getTxId() {
                return txId;
            }

            public void setTxId(String txId) {
                this.txId = txId;
            }

            public String getScriptFunding() {
                return scriptFunding;
            }

            public void setScriptFunding(String scriptFunding) {
                this.scriptFunding = scriptFunding;
            }

            public String getWitness() {
                return witness;
            }

            public void setWitness(String witness) {
                this.witness = witness;
            }
        }

        public static class CTXBean {
            private String txData;
            private String addressRSMC;
            private String scriptRSMC;
            private String txId;
            private String witness;

            public String getTxData() {
                return txData;
            }

            public void setTxData(String txData) {
                this.txData = txData;
            }

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
