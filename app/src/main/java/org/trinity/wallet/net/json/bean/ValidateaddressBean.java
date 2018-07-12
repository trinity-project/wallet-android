package org.trinity.wallet.net.json.bean;

public class ValidateaddressBean {
    private String jsonrpc;

    private String id;

    private Result result;

    public String getJsonrpc() {
        return this.jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
