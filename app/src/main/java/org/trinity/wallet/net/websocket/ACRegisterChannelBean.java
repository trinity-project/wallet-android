package org.trinity.wallet.net.websocket;

import org.trinity.wallet.net.websocket.BaseWebSocketBean;

public class ACRegisterChannelBean extends BaseWebSocketBean {
    private String MessageType = "RegisterChannel";
    private String Sender;
    private String Receiver;
    private String ChannelName;
    private String Magic;
    private MessageBodyBean MessageBody;

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
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

    public String getMagic() {
        return Magic;
    }

    public void setMagic(String Magic) {
        this.Magic = Magic;
    }

    public MessageBodyBean getMessageBody() {
        return MessageBody;
    }

    public void setMessageBody(MessageBodyBean MessageBody) {
        this.MessageBody = MessageBody;
    }

    public static class MessageBodyBean {
        private String AssetType;
        private double Deposit;

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
    }
}
