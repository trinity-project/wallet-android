package org.trinity.wallet.net.websocket;

import java.util.List;

public class ACAckRouterInfoBean extends BaseWebSocketBean {
    private String MessageType = "AckRouterInfo";
    private RouterInfoBean RouterInfo;

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public RouterInfoBean getRouterInfo() {
        return RouterInfo;
    }

    public void setRouterInfo(RouterInfoBean RouterInfo) {
        this.RouterInfo = RouterInfo;
    }

    public static class RouterInfoBean {
        private String Next;
        private List<List<String>> FullPath;

        public String getNext() {
            return Next;
        }

        public void setNext(String Next) {
            this.Next = Next;
        }

        public List<List<String>> getFullPath() {
            return FullPath;
        }

        public void setFullPath(List<List<String>> FullPath) {
            this.FullPath = FullPath;
        }
    }
}
