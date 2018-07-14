package org.trinity.wallet.net.jsonrpc;

public class ValidateaddressBean extends BaseJSONRpcBean {
    private Result result;

    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        private String address;

        private boolean isvalid;

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isIsvalid() {
            return isvalid;
        }

        public void setIsvalid(boolean isvalid) {
            this.isvalid = isvalid;
        }
    }
}
