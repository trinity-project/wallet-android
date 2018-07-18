package org.trinity.wallet;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.trinity.util.EncryptUtil;
import org.trinity.util.HexUtil;
import org.trinity.util.NeoSignUtil;
import org.trinity.util.UUIDUtil;
import org.trinity.util.WebSocketMessageTypeUtil;
import org.trinity.util.android.ToastUtil;
import org.trinity.wallet.activity.MainActivity;
import org.trinity.wallet.net.JSONRpcClient;
import org.trinity.wallet.net.WebSocketClient;
import org.trinity.wallet.net.jsonrpc.FunderTransactionBean;
import org.trinity.wallet.net.jsonrpc.RequestJSONRpcBean;
import org.trinity.wallet.net.jsonrpc.SendrawtransactionBean;
import org.trinity.wallet.net.websocket.ACFounderBean;
import org.trinity.wallet.net.websocket.ACFounderSignBean;
import org.trinity.wallet.net.websocket.ACRegisterChannelBean;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import neoutils.Neoutils;
import neoutils.Wallet;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static org.junit.Assert.assertEquals;
import static org.trinity.wallet.activity.BaseActivity.wApp;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("org.trinity.wallet", appContext.getPackageName());
    }

    @Test
    public void testNEOWallet() {
        try {
            Wallet wallet = Neoutils.newWallet();
            final byte[] privateKey = wallet.getPrivateKey();
            final byte[] publicKey = wallet.getPublicKey();
            final byte[] hashedSignature = wallet.getHashedSignature();
            final String address = wallet.getAddress();
            final String wif = wallet.getWIF();
            final String sign = "TachitutetoHyahyuhyo.";
            final String sign16 = HexUtil.byteArrayToHex(sign.getBytes());
            final byte[] signed = Neoutils.sign(sign.getBytes(), HexUtil.byteArrayToHex(privateKey));
            System.out.println("\n" +
                    "|------------------------------------Made------------------------------------~" + "\n" +
                    "| priKey: " + HexUtil.byteArrayToHex(privateKey) + "\n" +
                    "| pubKey: " + HexUtil.byteArrayToHex(publicKey) + "\n" +
                    "| hashSn: " + HexUtil.byteArrayToHex(hashedSignature) + "\n" +
                    "| WIF   : " + wif + "\n" +
                    "| add   : " + address + "\n" +
                    "| Sign  : " + sign + "\n" +
                    "| Sign16: " + sign16 + "\n" +
                    "| Signed: " + HexUtil.byteArrayToHex(signed) + "\n" +
                    "|------------------------------------Made------------------------------------~" + "\n"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void attemptAddChannel() throws Exception {
        WalletApplication wApp = new WalletApplication();
        wApp.switchNet(ConfigList.NET_TYPE_TEST);

        final Wallet wallet = Neoutils.newWallet();

        final Gson gson = new Gson();

        String address = wallet.getAddress();
        System.out.println(address);

        final String publicKeyHex = HexUtil.byteArrayToHex(wallet.getPublicKey());

        final String sTNAPTrim = "02be2cd38375dae1f5541deb2ca6525c7fdf123b3553346e922d3a8c12b5a22771@47.97.223.57:8089";
        final String depositTrim = "2";
        final String assetName = "TNC";

        final String[] sTNAPSplit = sTNAPTrim.split("@");
        final String sTNAP_PublicKey = sTNAPSplit[0];
        final String sTNAP_IpPort = sTNAPSplit[1];
        final String sTNAP_IpPort8766 = sTNAP_IpPort.split(":")[0] + ":8766";
        final String sTNAP8766 = (publicKeyHex + "@" + sTNAP_IpPort8766).toLowerCase();

        final WebSocketClient webSocketClient = new WebSocketClient.Builder()
                .url("ws://" + sTNAP_IpPort8766)
                .build();


        webSocketClient.connect(new WebSocketListener() {
            boolean iAmSender = true;

            ACRegisterChannelBean req_1;
            ACFounderBean resp_2;
            ACFounderSignBean req_3;
            JSONRpcClient client_4;
            FunderTransactionBean resp_5;
            ACFounderBean req_6;
            ACFounderSignBean resp_7;
            JSONRpcClient client_8;
            SendrawtransactionBean resp_9;

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                req_1 = new ACRegisterChannelBean();
                req_1.setSender(sTNAP8766);
                req_1.setReceiver(sTNAPTrim);
                req_1.setMagic(WalletApplication.getMagic());
                req_1.setChannelName(wallet.getAddress() + UUIDUtil.getRandomLowerNoLine());
                ACRegisterChannelBean.MessageBodyBean messageBody = new ACRegisterChannelBean.MessageBodyBean();
                messageBody.setAssetType(assetName);
                messageBody.setDeposit(Double.parseDouble(depositTrim));
                req_1.setMessageBody(messageBody);
                String text = gson.toJson(req_1);
                webSocket.send(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

                String messageTypeStr = WebSocketMessageTypeUtil.getMessageType(text);

                if (iAmSender) {
                    if ("Founder".equals(messageTypeStr)) {
                        resp_2 = gson.fromJson(text, ACFounderBean.class);
                        req_3 = new ACFounderSignBean();
                        req_3.setSender(sTNAP8766);
                        req_3.setReceiver(sTNAPTrim);
                        req_3.setChannelName(resp_2.getChannelName());
                        req_3.setTxNonce(resp_2.getTxNonce());
                        ACFounderSignBean.MessageBodyBean messageBody3 = new ACFounderSignBean.MessageBodyBean();
                        ACFounderSignBean.MessageBodyBean.FounderBean founder3 = new ACFounderSignBean.MessageBodyBean.FounderBean();
                        founder3.setTxDataSign(NeoSignUtil.signToHex(resp_2.getMessageBody().getFounder().getTxData(), wallet.getPrivateKey()));
                        founder3.setOriginalData(gson.fromJson(gson.toJson(resp_2.getMessageBody().getFounder()), ACFounderSignBean.MessageBodyBean.FounderBean.OriginalDataBean.class));
                        messageBody3.setFounder(founder3);
                        ACFounderSignBean.MessageBodyBean.CommitmentBean commitment3 = new ACFounderSignBean.MessageBodyBean.CommitmentBean();
                        commitment3.setTxDataSign(NeoSignUtil.signToHex(resp_2.getMessageBody().getCommitment().getTxData(), wallet.getPrivateKey()));
                        commitment3.setOriginalData(gson.fromJson(gson.toJson(resp_2.getMessageBody().getCommitment()), ACFounderSignBean.MessageBodyBean.CommitmentBean.OriginalDataBeanX.class));
                        messageBody3.setCommitment(commitment3);
                        ACFounderSignBean.MessageBodyBean.RevocableDeliveryBean revocableDelivery3 = new ACFounderSignBean.MessageBodyBean.RevocableDeliveryBean();
                        revocableDelivery3.setTxDataSign(NeoSignUtil.signToHex(resp_2.getMessageBody().getRevocableDelivery().getTxData(), wallet.getPrivateKey()));
                        revocableDelivery3.setOriginalData(gson.fromJson(gson.toJson(resp_2.getMessageBody().getRevocableDelivery()), ACFounderSignBean.MessageBodyBean.RevocableDeliveryBean.OriginalDataBeanXX.class));
                        messageBody3.setRevocableDelivery(revocableDelivery3);
                        messageBody3.setAssetType(resp_2.getMessageBody().getAssetType());
                        messageBody3.setDeposit(resp_2.getMessageBody().getDeposit());
                        messageBody3.setRoleIndex(resp_2.getMessageBody().getRoleIndex());
                        req_3.setMessageBody(messageBody3);
                        String send3 = gson.toJson(req_3);
                        webSocket.send(send3);

                        client_4 = new JSONRpcClient.Builder()
                                .net(WalletApplication.getNetUrl())
                                .method("FunderTransaction")
                                .params(publicKeyHex,
                                        sTNAP_PublicKey,
                                        resp_2.getMessageBody().getFounder().getAddressFunding(),
                                        resp_2.getMessageBody().getFounder().getScriptFunding(),
                                        String.valueOf(resp_2.getMessageBody().getDeposit()),
                                        String.valueOf(resp_2.getMessageBody().getDeposit()),
                                        resp_2.getMessageBody().getFounder().getTxId(),
                                        ConfigList.ASSET_ID_MAP.get(resp_2.getMessageBody().getAssetType().trim().toUpperCase()))
                                .id(wallet.getAddress() + UUIDUtil.getRandomLowerNoLine())
                                .build();

                        String json_5 = client_4.post();

                        if (json_5 == null) {
                            webSocket.cancel();
                            return;
                        }

                        resp_5 = gson.fromJson(json_5, FunderTransactionBean.class);

                        req_6 = new ACFounderBean();
                        req_6.setChannelName(resp_2.getChannelName());
                        req_6.setSender(sTNAP8766);
                        req_6.setReceiver(sTNAPTrim);
                        req_6.setTxNonce(resp_2.getTxNonce());
                        ACFounderBean.MessageBodyBean messageBody4 = new ACFounderBean.MessageBodyBean();
                        messageBody4.setAssetType(resp_2.getMessageBody().getAssetType());
                        messageBody4.setDeposit(resp_2.getMessageBody().getDeposit());
                        messageBody4.setRoleIndex(resp_2.getMessageBody().getRoleIndex() + 1);
                        messageBody4.setFounder(resp_2.getMessageBody().getFounder());
                        messageBody4.setCommitment(gson.fromJson(gson.toJson(resp_5.getResult().getC_TX()), ACFounderBean.MessageBodyBean.CommitmentBean.class));
                        messageBody4.setRevocableDelivery(gson.fromJson(gson.toJson(resp_5.getResult().getR_TX()), ACFounderBean.MessageBodyBean.RevocableDeliveryBean.class));
                        req_6.setMessageBody(messageBody4);

                        String send6 = gson.toJson(req_6);
                        webSocket.send(send6);

                        return;
                    }

                    if ("FounderSign".equals(messageTypeStr)) {
                        resp_7 = gson.fromJson(text, ACFounderSignBean.class);
                        client_8 = new JSONRpcClient.Builder()
                                .net(WalletApplication.getNetUrlForNEO())
                                .method("sendrawtransaction")
                                .params(resp_7.getMessageBody().getFounder().getOriginalData().getTxData() + resp_7.getMessageBody().getFounder().getOriginalData().getWitness().replace("{signOther}", resp_7.getMessageBody().getFounder().getTxDataSign()).replace("{signSelf}", req_3.getMessageBody().getFounder().getTxDataSign()))
                                .id(wallet.getAddress() + UUIDUtil.getRandomLowerNoLine())
                                .build();

                        String json_9 = client_8.post();

                        if (json_9 == null) {
                            webSocket.cancel();
                            return;
                        }

                        resp_9 = gson.fromJson(json_9, SendrawtransactionBean.class);

                        {
                            Gson gsonP = new GsonBuilder().setPrettyPrinting().create();
                            // Test code.
                            System.out.println(gsonP.toJson(req_1));
                            System.out.println(gsonP.toJson(resp_2));
                            System.out.println(gsonP.toJson(req_3));
                            System.out.println(gsonP.toJson(gson.fromJson(client_4.getJson(), RequestJSONRpcBean.class)));
                            System.out.println(gsonP.toJson(resp_5));
                            System.out.println(gsonP.toJson(req_6));
                            System.out.println(gsonP.toJson(resp_7));
                            System.out.println(gsonP.toJson(gson.fromJson(client_8.getJson(), RequestJSONRpcBean.class)));
                            System.out.println(gsonP.toJson(resp_9));
                        }

                        boolean result = resp_9.isResult();
                        if (result) {
                            webSocket.cancel();
                            latch.countDown();
                            return;
                        }

                        if (!result) {
                            iAmSender = false;
                            // TODO mirror add channel.
                        }
                    }
                    return;
                }

                if (!iAmSender) {
                    if (text.contains("\"RegisterChannel\"")) {

                    }

                    if (text.contains("\"FounderSign\"")) {

                    }

                    if (text.contains("\"Founder\"")) {

                    }
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                t.printStackTrace();
                latch.countDown();
            }
        });

        latch.await();
    }
}

