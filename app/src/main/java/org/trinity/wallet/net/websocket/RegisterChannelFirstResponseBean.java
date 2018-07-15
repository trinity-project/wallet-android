package org.trinity.wallet.net.websocket;

public class RegisterChannelFirstResponseBean extends BaseWebSocketBean {
    private String ChannelName;
    private String Comments = null;
    private MessageBody MessageBody;

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String ChannelName) {
        this.ChannelName = ChannelName;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public MessageBody getMessageBody() {
        return MessageBody;
    }

    public void setMessageBody(MessageBody MessageBody) {
        this.MessageBody = MessageBody;
    }

    public static class MessageBody {
        private String AssetType;
        private Commitment Commitment;
        private Founder Founder;
        private RevocableDelivery RevocableDelivery;
        private String MessageType;
        private String Receiver;
        private String Sender;
        private String TxNonce;

        public String getAssetType() {
            return AssetType;
        }

        public void setAssetType(String AssetType) {
            this.AssetType = AssetType;
        }

        public Commitment getCommitment() {
            return Commitment;
        }

        public void setCommitment(Commitment Commitment) {
            this.Commitment = Commitment;
        }

        public Founder getFounder() {
            return Founder;
        }

        public void setFounder(Founder Founder) {
            this.Founder = Founder;
        }

        public RevocableDelivery getRevocableDelivery() {
            return RevocableDelivery;
        }

        public void setRevocableDelivery(RevocableDelivery RevocableDelivery) {
            this.RevocableDelivery = RevocableDelivery;
        }

        public String getMessageType() {
            return MessageType;
        }

        public void setMessageType(String MessageType) {
            this.MessageType = MessageType;
        }

        public String getReceiver() {
            return Receiver;
        }

        public void setReceiver(String Receiver) {
            this.Receiver = Receiver;
        }

        public String getSender() {
            return Sender;
        }

        public void setSender(String Sender) {
            this.Sender = Sender;
        }

        public String getTxNonce() {
            return TxNonce;
        }

        public void setTxNonce(String TxNonce) {
            this.TxNonce = TxNonce;
        }

        public static class Commitment {
            private String addressRSMC;
            private String scriptRSMC;
            private String txData;
            private String txId;
            private String witness;
            private String Deposit;

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

            public String getDeposit() {
                return Deposit;
            }

            public void setDeposit(String Deposit) {
                this.Deposit = Deposit;
            }
        }

        public static class Founder {
            private String addressFunding;
            private String scriptFunding;
            private String txData;
            private String txId;
            private String witness;

            public String getAddressFunding() {
                return addressFunding;
            }

            public void setAddressFunding(String addressFunding) {
                this.addressFunding = addressFunding;
            }

            public String getScriptFunding() {
                return scriptFunding;
            }

            public void setScriptFunding(String scriptFunding) {
                this.scriptFunding = scriptFunding;
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

        public static class RevocableDelivery {
            private String txData;
            private String txId;
            private String witness;
            private String RoleIndex;

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

            public String getRoleIndex() {
                return RoleIndex;
            }

            public void setRoleIndex(String RoleIndex) {
                this.RoleIndex = RoleIndex;
            }
        }
    }
}
