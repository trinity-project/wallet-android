package org.trinity.wallet.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.trinity.util.android.ToastUtil;
import org.trinity.wallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends BaseActivity implements DecoratedBarcodeView.TorchListener {
    @BindView(R.id.btnSwitchLight)
    Button switchLight;
    @BindView(R.id.btnFromGallery)
    Button fromGallery;
    @BindView(R.id.scanPlugin)
    DecoratedBarcodeView scanPlugin;

    private CaptureManager captureManager;
    private boolean isLightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        scanPlugin.setStatusText("");
        scanPlugin.setTorchListener(this);
        if (!canFlash()) {
            switchLight.setVisibility(View.GONE);
        }

        // Init capture.
        captureManager = new CaptureManager(this, scanPlugin);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return scanPlugin.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTorchOn() {
        ToastUtil.show(getBaseContext(), "Torch on");
        isLightOn = true;
    }

    @Override
    public void onTorchOff() {
        ToastUtil.show(getBaseContext(), "Torch off");
        isLightOn = false;
    }

    private boolean canFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @OnClick(R.id.btnSwitchLight)
    public void toggleLight(View view) {
        if (isLightOn) {
            scanPlugin.setTorchOff();
        } else {
            scanPlugin.setTorchOn();
        }
    }

    @OnClick(R.id.btnFromGallery)
    public void fromGallery(View view) {
        // TODO scan from gallery.
        ToastUtil.show(getBaseContext(), "Coming soon.");
    }
}
