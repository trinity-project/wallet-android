package org.trinity.wallet.net.websocket;

import java.util.List;

public class ACHtlcBean extends BaseWebSocketBean {
    private String MessageType = "Htlc";
    private String Sender;
    private String Receiver;
    private int TxNonce;
    private String ChannelName;
    private String Next;
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

    public String getNext() {
        return Next;
    }

    public void setNext(String Next) {
        this.Next = Next;
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
        private HEDTXBean HEDTX;
        private HERDTXBean HERDTX;
        private HETXBean HETX;
        private HTDTXBean HTDTX;
        private HTRDTXBean HTRDTX;
        private HTTXBean HTTX;
        private RDTXBean RDTX;
        private int RoleIndex;
        private String Comments;
        private double Count;
        private String AssetType;
        private String HashR;

        public HCTXBean getHCTX() {
            return HCTX;
        }

        public void setHCTX(HCTXBean HCTX) {
            this.HCTX = HCTX;
        }

        public HEDTXBean getHEDTX() {
            return HEDTX;
        }

        public void setHEDTX(HEDTXBean HEDTX) {
            this.HEDTX = HEDTX;
        }

        public HERDTXBean getHERDTX() {
            return HERDTX;
        }

        public void setHERDTX(HERDTXBean HERDTX) {
            this.HERDTX = HERDTX;
        }

        public HETXBean getHETX() {
            return HETX;
        }

        public void setHETX(HETXBean HETX) {
            this.HETX = HETX;
        }

        public HTDTXBean getHTDTX() {
            return HTDTX;
        }

        public void setHTDTX(HTDTXBean HTDTX) {
            this.HTDTX = HTDTX;
        }

        public HTRDTXBean getHTRDTX() {
            return HTRDTX;
        }

        public void setHTRDTX(HTRDTXBean HTRDTX) {
            this.HTRDTX = HTRDTX;
        }

        public HTTXBean getHTTX() {
            return HTTX;
        }

        public void setHTTX(HTTXBean HTTX) {
            this.HTTX = HTTX;
        }

        public RDTXBean getRDTX() {
            return RDTX;
        }

        public void setRDTX(RDTXBean RDTX) {
            this.RDTX = RDTX;
        }

        public int getRoleIndex() {
            return RoleIndex;
        }

        public void setRoleIndex(int RoleIndex) {
            this.RoleIndex = RoleIndex;
        }

        public String getComments() {
            return Comments;
        }

        public void setComments(String Comments) {
            this.Comments = Comments;
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

        public static class HEDTXBean {
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

        public static class HERDTXBean {
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

        public static class HETXBean {
            private String RSMCscript;
            private String addressRSMC;
            private String txData;
            private String txId;
            private String witness;

            public String getRSMCscript() {
                return RSMCscript;
            }

            public void setRSMCscript(String RSMCscript) {
                this.RSMCscript = RSMCscript;
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

        public static class HTDTXBean {
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

        public static class HTRDTXBean {
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

        public static class HTTXBean {
            private String RSMCscript;
            private String addressRSMC;
            private String txData;
            private String txId;
            private String witness;

            public String getRSMCscript() {
                return RSMCscript;
            }

            public void setRSMCscript(String RSMCscript) {
                this.RSMCscript = RSMCscript;
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

        public static class RDTXBean {
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
