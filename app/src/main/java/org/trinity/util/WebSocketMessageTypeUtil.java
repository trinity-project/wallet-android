package org.trinity.util;

import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.websocket.MessageTypeFilterBean;

public final class WebSocketMessageTypeUtil {
    public static String getMessageType(String text) {
        return WalletApplication.getGson().fromJson(text, MessageTypeFilterBean.class).getMessageType();
    }
}
