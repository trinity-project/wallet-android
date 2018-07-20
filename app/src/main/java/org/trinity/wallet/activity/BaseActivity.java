package org.trinity.wallet.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toolbar;

import org.trinity.wallet.R;
import org.trinity.wallet.WalletApplication;

import java.lang.reflect.Field;

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * The WalletApplication object.
     */
    public static WalletApplication wApp;

    /**
     * Toolbar UI params.
     */
    protected static int paddingTop;

    protected int newToolbarWidth;
    protected int newToolbarHeight;

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

    protected void measureToolbar(Toolbar toolbar) {
        int status_bar_height = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        paddingTop = this.getResources().getDimensionPixelOffset(status_bar_height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        toolbar.measure(measuredWidth, measuredHeight);
        newToolbarWidth = -1;
        newToolbarHeight = toolbar.getMeasuredHeight() + paddingTop;
    }
}
