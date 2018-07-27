package org.trinity.wallet.net.websocket;

import java.util.List;

public class ACHtlcSignBean extends BaseWebSocketBean {
    private String MessageType = "HtlcSign";
    private String Sender;
    private String Receiver;
    private int TxNonce;
    private String ChannelName;
    private MessageBodyBean MessageBody;
    private List<List<String>> Router;

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

    public int getTxNonce() {
        return TxNonce;
    }

    public void setTxNonce(int TxNonce) {
        this.TxNonce = TxNonce;
    }

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String ChannelName) {
        this.ChannelName = ChannelName;
    }

    public MessageBodyBean getMessageBody() {
        return MessageBody;
    }

    public void setMessageBody(MessageBodyBean MessageBody) {
        this.MessageBody = MessageBody;
    }

    public List<List<String>> getRouter() {
        return Router;
    }

    public void setRouter(List<List<String>> Router) {
        this.Router = Router;
    }

    public static class MessageBodyBean {
        private HCTXBean HCTX;
        private RDTXBean RDTX;
        private HTDTXBean HTDTX;
        private int RoleIndex;
        private double Count;
        private String AssetType;
        private String HashR;

        public HCTXBean getHCTX() {
            return HCTX;
        }

        public void setHCTX(HCTXBean HCTX) {
            this.HCTX = HCTX;
        }

        public RDTXBean getRDTX() {
            return RDTX;
        }

        public void setRDTX(RDTXBean RDTX) {
            this.RDTX = RDTX;
        }

        public HTDTXBean getHTDTX() {
            return HTDTX;
        }

        public void setHTDTX(HTDTXBean HTDTX) {
            this.HTDTX = HTDTX;
        }

        public int getRoleIndex() {
            return RoleIndex;
        }

        public void setRoleIndex(int RoleIndex) {
            this.RoleIndex = RoleIndex;
        }

        public double getCount() {
            return Count;
        }

        public void setCount(double Count) {
            this.Count = Count;
        }

        public String getAssetType() {
            return AssetType;
        }

        public void setAssetType(String AssetType) {
            this.AssetType = AssetType;
        }

        public String getHashR() {
            return HashR;
        }

        public void setHashR(String HashR) {
            this.HashR = HashR;
        }

        public static class HCTXBean {
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
                private String HTLCscript;
                private String RSMCscript;
                private String addressHTLC;
                private String addressRSMC;
                private String txData;
                private String txId;
                private String witness;

                public String getHTLCscript() {
                    return HTLCscript;
                }

                public void setHTLCscript(String HTLCscript) {
                    this.HTLCscript = HTLCscript;
                }

                public String getRSMCscript() {
                    return RSMCscript;
                }

                public void setRSMCscript(String RSMCscript) {
                    this.RSMCscript = RSMCscript;
                }

                public String getAddressHTLC() {
                    return addressHTLC;
                }

                public void setAddressHTLC(String addressHTLC) {
                    this.addressHTLC = addressHTLC;
                }

                public String getAddressRSMC() {
                    return addressRSMC;
                }

                public void setAddressRSMC(String addressRSMC) {
                    this.addressRSMC = addressRSMC;
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
        }

        public static class RDTXBean {
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

        public static class HTDTXBean {
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
