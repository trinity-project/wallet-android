package org.trinity.util.android;

import android.app.Activity;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.trinity.wallet.activity.ScanActivity;

public class QRCodeUtil {
    public static Bitmap encodeAsBitmap(String src, int width, int height) {
        Bitmap bitmap = null;
        BitMatrix result;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(src, BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException iae) {
            return null;
        }

        return bitmap;
    }

    public static void cameraScan(Activity activity) {
        new IntentIntegrator(activity)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class)
                .initiateScan();
    }
}
