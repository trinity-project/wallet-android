package org.trinity.wallet.net.websocket;

public class RegisterChannelFirstRequestBean extends BaseWebSocketBean {
    private String MessageType = "RegisterChannel";
    private String Sender;
    private String Receiver;
    private String ChannelName;
    private MessageBody MessageBody;

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

    public MessageBody getMessageBody() {
        return MessageBody;
    }

    public void setMessageBody(MessageBody MessageBody) {
        this.MessageBody = MessageBody;
    }

    public static class MessageBody {
        private String AssetType;
        private String Deposit;

        public String getAssetType() {
            return AssetType;
        }

        public void setAssetType(String AssetType) {
            this.AssetType = AssetType;
        }

        public String getDeposit() {
            return Deposit;
        }

        public void setDeposit(String Deposit) {
            this.Deposit = Deposit;
        }
    }
}
