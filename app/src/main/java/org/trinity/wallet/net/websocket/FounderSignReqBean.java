package org.trinity.wallet.net.websocket;

public class FounderSignReqBean extends BaseWebSocketBean {
    private String MessageType;
    private String Sender;
    private String Receiver;
    private String ChannelName;
    private int TxNonce;
    private MessageBodyBean MessageBody;

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String Sender) {
        this.Sender = Sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String Receiver) {
        this.Receiver = Receiver;
    }

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String ChannelName) {
        this.ChannelName = ChannelName;
    }

    public int getTxNonce() {
        return TxNonce;
    }

    public void setTxNonce(int TxNonce) {
        this.TxNonce = TxNonce;
    }

    public MessageBodyBean getMessageBody() {
        return MessageBody;
    }

    public void setMessageBody(MessageBodyBean MessageBody) {
        this.MessageBody = MessageBody;
    }

    public static class MessageBodyBean {
        private FounderBean Founder;
        private CommitmentBean Commitment;
        private RevocableDeliveryBean RevocableDelivery;
        private String AssetType;
        private double Deposit;
        private int RoleIndex;

        public FounderBean getFounder() {
            return Founder;
        }

        public void setFounder(FounderBean Founder) {
            this.Founder = Founder;
        }

        public CommitmentBean getCommitment() {
            return Commitment;
        }

        public void setCommitment(CommitmentBean Commitment) {
            this.Commitment = Commitment;
        }

        public RevocableDeliveryBean getRevocableDelivery() {
            return RevocableDelivery;
        }

        public void setRevocableDelivery(RevocableDeliveryBean RevocableDelivery) {
            this.RevocableDelivery = RevocableDelivery;
        }

        public String getAssetType() {
            return AssetType;
        }

        public void setAssetType(String AssetType) {
            this.AssetType = AssetType;
        }

        public double getDeposit() {
            return Deposit;
        }

        public void setDeposit(double Deposit) {
            this.Deposit = Deposit;
        }

        public int getRoleIndex() {
            return RoleIndex;
        }

        public void setRoleIndex(int RoleIndex) {
            this.RoleIndex = RoleIndex;
        }

        public static class FounderBean {
            private String txDataSign;
            private OriginalDataBean originalData;

            public String getTxDataSign() {
                return txDataSign;
            }

            public void setTxDataSign(String txDataSign) {
                this.txDataSign = txDataSign;
            }

            public OriginalDataBean getOriginalData() {
                return originalData;
            }

            public void setOriginalData(OriginalDataBean originalData) {
                this.originalData = originalData;
            }

            public static class OriginalDataBean {
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
        }

        public static class CommitmentBean {
            private String txDataSign;
            private OriginalDataBeanX originalData;

            public String getTxDataSign() {
                return txDataSign;
            }

            public void setTxDataSign(String txDataSign) {
                this.txDataSign = txDataSign;
            }

            public OriginalDataBeanX getOriginalData() {
                return originalData;
            }

            public void setOriginalData(OriginalDataBeanX originalData) {
                this.originalData = originalData;
            }

            public static class OriginalDataBeanX {
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
        }

        public static class RevocableDeliveryBean {
            private String txDataSign;
            private OriginalDataBeanXX originalData;

            public String getTxDataSign() {
                return txDataSign;
            }

            public void setTxDataSign(String txDataSign) {
                this.txDataSign = txDataSign;
            }

            public OriginalDataBeanXX getOriginalData() {
                return originalData;
            }

            public void setOriginalData(OriginalDataBeanXX originalData) {
                this.originalData = originalData;
            }

            public static class OriginalDataBeanXX {
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
}