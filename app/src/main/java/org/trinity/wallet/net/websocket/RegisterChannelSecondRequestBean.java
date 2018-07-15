package org.trinity.wallet.net.websocket;

public class RegisterChannelSecondRequestBean extends BaseWebSocketBean {
    private String ChannelName;
    private MessageBody MessageBody;
    private String MessageType;
    private String Receiver;
    private String Sender;
    private String TxNonce;

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String ChannelName) {
        this.ChannelName = ChannelName;
    }

    public MessageBody getMessageBody() {
        return MessageBody;
    }

    public void setMessageBody(MessageBody MessageBody) {
        this.MessageBody = MessageBody;
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

    public static class MessageBody {
        private String AssetType;
        private Commitment Commitment;
        private String Deposit;
        private Founder Founder;
        private RevocableDelivery RevocableDelivery;
        private String RoleIndex;

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

        public String getDeposit() {
            return Deposit;
        }

        public void setDeposit(String Deposit) {
            this.Deposit = Deposit;
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

        public String getRoleIndex() {
            return RoleIndex;
        }

        public void setRoleIndex(String RoleIndex) {
            this.RoleIndex = RoleIndex;
        }

        public static class Commitment {
            private String originalData;
            private String txDataSign;

            public String getOriginalData() {
                return originalData;
            }

            public void setOriginalData(String originalData) {
                this.originalData = originalData;
            }

            public String getTxDataSign() {
                return txDataSign;
            }

            public void setTxDataSign(String txDataSign) {
                this.txDataSign = txDataSign;
            }
        }

        public static class Founder {
            private String originalData;
            private String txDataSign;

            public String getOriginalData() {
                return originalData;
            }

            public void setOriginalData(String originalData) {
                this.originalData = originalData;
            }

            public String getTxDataSign() {
                return txDataSign;
            }

            public void setTxDataSign(String txDataSign) {
                this.txDataSign = txDataSign;
            }
        }

        public static class RevocableDelivery {
            private String originalData;
            private String txDataSign;

            public String getOriginalData() {
                return originalData;
            }

            public void setOriginalData(String originalData) {
                this.originalData = originalData;
            }

            public String getTxDataSign() {
                return txDataSign;
            }

            public void setTxDataSign(String txDataSign) {
                this.txDataSign = txDataSign;
            }
        }
    }
}