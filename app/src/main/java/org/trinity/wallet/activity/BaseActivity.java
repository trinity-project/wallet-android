package org.trinity.wallet.activity;

import android.support.v7.app.AppCompatActivity;

import org.trinity.wallet.WalletApplication;

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Get the wallet application.
     *
     * @return Wallet application.
     */
    public final WalletApplication getWalletApplication() {
        return (WalletApplication) getApplication();
    }
}
