package org.trinity.util.net;

import android.support.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.trinity.util.convert.BigDecimalUtil;
import org.trinity.util.convert.TNAPUtil;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.entity.ChannelBean;
import org.trinity.wallet.net.jsonrpc.RefoundTransBean;

import neoutils.Wallet;

public class JSONObjectUtil {
    public static double updateChannelGetSpvBalance(String respJson, String sTNAPSpv, String assetName) {
        return WalletApplication.getGson().fromJson(respJson, JsonObject.class)
                .getAsJsonObject("MessageBody")
                .getAsJsonObject("Balance")
                .getAsJsonObject(sTNAPSpv)
                .get(assetName)
                .getAsDouble();
    }

    public static String settleBeanMaker(@NonNull Wallet wallet, @NonNull ChannelBean channel, @NonNull RefoundTransBean resp) {
        ACSettleBean req = new ACSettleBean();

        String sTNAP = channel.getTNAP();
        String sTNAPSpv = TNAPUtil.getTNAPSpv(sTNAP, wallet);

        req.setChannelName(channel.getName());
        req.setSender(sTNAPSpv);
        req.setReceiver(sTNAP);
        req.setTxNonce(channel.getTxNonce());
        ACSettleBean.MessageBodyBean messageBody = new ACSettleBean.MessageBodyBean();
        ACSettleBean.MessageBodyBean.SettlementBean settlement = new ACSettleBean.MessageBodyBean.SettlementBean();
        settlement.setTxData(resp.getResult().getTxData());
        settlement.setTxId(resp.getResult().getTxid());
        settlement.setWitness(resp.getResult().getWitness());
        JsonObject balance = new JsonObject();
        JsonObject spvPublicKey = new JsonObject();
        spvPublicKey.addProperty(channel.getAssetName(), channel.getBalance());
        balance.add(TNAPUtil.getPublicKey(sTNAPSpv), spvPublicKey);
        JsonObject nodePublicKey = new JsonObject();
        nodePublicKey.addProperty(channel.getAssetName(), BigDecimalUtil.subtract(BigDecimalUtil.add(channel.getDeposit(), channel.getDeposit()), channel.getBalance()));
        balance.add(TNAPUtil.getPublicKey(sTNAP), nodePublicKey);
        messageBody.setBalance(balance);
        req.setMessageBody(messageBody);

        return WalletApplication.getGson().toJson(req);
    }

    private static class ACSettleBean {
        private String MessageType = "Settle";
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
            private SettlementBean Settlement;
            /**
             * Like this.
             * <p>
             * "SPV public key": {
             * "Asset type": 20.00000002
             * },
             * "Trinity node public key": {
             * "Asset type": 20.00000002
             * }
             */
            private JsonElement Balance;

            public SettlementBean getSettlement() {
                return Settlement;
            }

            public void setSettlement(SettlementBean Settlement) {
                this.Settlement = Settlement;
            }

            public JsonElement getBalance() {
                return Balance;
            }

            public void setBalance(JsonElement Balance) {
                this.Balance = Balance;
            }

            public static class SettlementBean {
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
