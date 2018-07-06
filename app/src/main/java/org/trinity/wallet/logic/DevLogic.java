package org.trinity.wallet.logic;

import org.trinity.util.HexUtil;

import neoutils.Neoutils;
import neoutils.Wallet;

public class DevLogic {
    public void testNeoutil(final IDevCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    callback.invoke(privateKey, publicKey, hashedSignature, wif, address, sign, sign16, signed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
