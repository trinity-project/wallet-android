package org.trinity.wallet;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.trinity.util.HexUtil;

import neoutils.Neoutils;
import neoutils.Wallet;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
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
                    "| hashSn : " + HexUtil.byteArrayToHex(hashedSignature) + "\n" +
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
}
