package org.trinity.wallet.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.securepreferences.SecurePreferences;

import org.trinity.wallet.WalletApplication;

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * The WalletApplication object.
     */
    public static WalletApplication wApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wApp = WalletApplication.getInstance();
    }
}
