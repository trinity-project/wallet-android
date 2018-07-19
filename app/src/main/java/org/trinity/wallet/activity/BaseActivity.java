package org.trinity.wallet.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.trinity.wallet.WalletApplication;

import java.lang.reflect.Field;

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * The WalletApplication object.
     */
    public static WalletApplication wApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                @SuppressLint("PrivateApi")
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field mSemiTransparentStatusBarColor = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                mSemiTransparentStatusBarColor.setAccessible(true);
                mSemiTransparentStatusBarColor.setInt(getWindow().getDecorView(), Color.TRANSPARENT);
            } catch (Exception ignored) {
            }
        }

        wApp = WalletApplication.getInstance();
    }
}
