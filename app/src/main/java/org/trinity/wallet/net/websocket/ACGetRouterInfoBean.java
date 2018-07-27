package org.trinity.wallet.net.websocket;

import java.util.List;

public class ACGetRouterInfoBean extends BaseWebSocketBean {
    private String MessageType = "GetRouterInfo";
    private String Sender;
    private String Receiver;
    private String Magic;
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
        // A gateway api fix about java array.
        private String type = "string";
        private List<String> NodeList;

        public String getAssetType() {
            return AssetType;
        }

        public void setAssetType(String AssetType) {
            this.AssetType = AssetType;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getNodeList() {
            return NodeList;
        }

        public void setNodeList(List<String> NodeList) {
            this.NodeList = NodeList;
        }
    }
}
