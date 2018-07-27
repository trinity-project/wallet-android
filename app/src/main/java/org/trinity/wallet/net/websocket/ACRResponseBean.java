package org.trinity.wallet.net.websocket;

public class ACRResponseBean extends BaseWebSocketBean {
    private String MessageType;
    private String Sender;
    private String Receiver;
    private int TxNonce;
    private String ChannelName;
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

    public static class MessageBodyBean {
        /**
         * HR : b66ad2099545a32ebd19b1f88e08e6cbf1874f68
         * R : e000d392a28142c8cd20fb7fce20b1293f6a6d0b718ea2afe54e8f236c70642c
         * Count : 0.21
         * AssetType : TNC
         * Comments : Haha
         */

        private String HR;
        private String R;
        private double Count;
        private String AssetType;
        private String Comments;

        public String getHR() {
            return HR;
        }

        public void setHR(String HR) {
            this.HR = HR;
        }

        public String getR() {
            return R;
        }

        public void setR(String R) {
            this.R = R;
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

        public String getComments() {
            return Comments;
        }

        public void setComments(String Comments) {
            this.Comments = Comments;
        }
    }
}
