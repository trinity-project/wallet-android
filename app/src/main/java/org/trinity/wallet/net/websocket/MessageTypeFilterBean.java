package org.trinity.wallet.net.websocket;

public class MessageTypeFilterBean extends BaseWebSocketBean {
    private String MessageType;

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }
}
