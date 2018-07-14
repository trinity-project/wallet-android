package org.trinity.wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.trinity.util.HexUtil;
import org.trinity.util.android.QRCodeUtil;
import org.trinity.util.android.ToastUtil;
import org.trinity.util.android.UIStringUtil;
import org.trinity.wallet.ConfigList;
import org.trinity.wallet.R;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.JSONRpcClient;
import org.trinity.wallet.net.JSONRpcErrorUtil;
import org.trinity.wallet.net.jsonrpc.ConstructTxBean;
import org.trinity.wallet.net.jsonrpc.GetBalanceBean;
import org.trinity.wallet.net.jsonrpc.SendrawtransactionBean;
import org.trinity.wallet.net.jsonrpc.ValidateaddressBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import neoutils.Neoutils;
import neoutils.Wallet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {
    /**
     * The activity object.
     */
    @SuppressLint("StaticFieldLeak")
    public static MainActivity instance;
    /**
     * The user verify button.
     */
    @BindView(R.id.btnUserVerify)
    public Button btnUserVerify;
    /**
     * The menu button.
     */
    @BindView(R.id.btnMainMenu)
    public Button mainMenu;
    /**
     * The net state text.
     */
    @BindView(R.id.netState)
    public TextView netState;
    /**
     * The frame of a single card view.
     */
    @BindView(R.id.cardsShell)
    public CoordinatorLayout cardsShell;
    /**
     * The total number of cards.
     */
    public int cardAccount = ConfigList.COIN_TYPE_ACCOUNT;
    /**
     * The container of a single card.
     * <p>
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.cardContainer)
    public ViewPager cardContainer;
    /**
     * The adapter of the card group.
     * <p>
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public SectionsPagerAdapter pagerAdapter;
    /**
     * The navigation bar of main tab.
     */
    @BindView(R.id.tab)
    public BottomNavigationView tab;
    /**
     * The bodies of main tab.
     */
    @BindView(R.id.tabTransfer)
    public ConstraintLayout tabTransfer;
    @BindView(R.id.tabAddChannel)
    public ConstraintLayout tabAddChannel;
    @BindView(R.id.tabChannelList)
    public ConstraintLayout tabChannelList;
    @BindView(R.id.tabRecord)
    public ConstraintLayout tabRecord;
    /**
     * The lock for post requests witch gets balance.
     */
    private transient int retryTimesNow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        instance = MainActivity.this;

        // Init events of toolbar's menu button click action.
        initToolbarMenu();
        // Init events of cards' click action.
        initCards();
        // Init events of tabs' click action.
        initTabs();

        // Identity verify.
        initUserIdentityVerify();
    }

    private void initUserIdentityVerify() {
        initUserIdentityVerify(wApp.isFirstTime());
    }

    private void initUserIdentityVerify(boolean isFirstTime) {
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        findViewById(R.id.cardsShell).setVisibility(View.GONE);
        findViewById(R.id.tabsContainer).setVisibility(View.GONE);
        tab.setVisibility(View.GONE);
        wApp.setIsIdentity(false);
        findViewById(R.id.userVerify).setVisibility(View.VISIBLE);

        final EditText inputUserVerify = findViewById(R.id.inputUserVerify);
        final TextInputLayout layUserVerifySure = findViewById(R.id.layUserVerifySure);
        final EditText inputUserVerifySure = findViewById(R.id.inputUserVerifySure);
        final TextView titleUserVerify = findViewById(R.id.titleUserVerify);
        final TextView infoUserVerify = findViewById(R.id.infoUserVerify);

        titleUserVerify.requestFocus();
        if (isFirstTime) {
            titleUserVerify.setText("IDENTITY CONFIRM");
            infoUserVerify.setText("Please set a new password for identity verify. (It is NOT your private key.)");
            layUserVerifySure.setVisibility(View.VISIBLE);
            btnUserVerify.setText(R.string.confirm);
            btnUserVerify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        userIdentitySet(inputUserVerify, inputUserVerifySure);
                        return true;
                    }
                    return false;
                }
            });
            btnUserVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userIdentitySet(inputUserVerify, inputUserVerifySure);
                }
            });
        } else {
            titleUserVerify.setText("IDENTITY VERIFICATION");
            infoUserVerify.setText("Please input your password for identity verify. (It is NOT your private key.)");
            layUserVerifySure.setVisibility(View.GONE);
            btnUserVerify.setText(R.string.verify);
            btnUserVerify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        userIdentityVerify(inputUserVerify);
                        return true;
                    }
                    return false;
                }
            });
            btnUserVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userIdentityVerify(inputUserVerify);
                }
            });
        }
    }

    private void userIdentitySet(EditText inputUserVerify, EditText inputUserVerifySure) {
        btnUserVerify.setClickable(false);

        inputUserVerify.setError(null);
        inputUserVerifySure.setError(null);
        String password = inputUserVerify.getText().toString();
        String passwordSure = inputUserVerifySure.getText().toString();
        int inLen = password.length();

        if (ConfigList.USER_PASSWORD_MIN > inLen || inLen > ConfigList.USER_PASSWORD_MAX) {
            wApp.setIsIdentity(false);
            inputUserVerify.setError("Invalid input.");
            inputUserVerify.requestFocus();
            btnUserVerify.setClickable(true);
            return;
        }
        if (!password.equals(passwordSure)) {
            inputUserVerifySure.setError("Inconsistent.");
            inputUserVerifySure.requestFocus();
            btnUserVerify.setClickable(true);
            return;
        }

        wApp.iAmNotFirstTime(password);
        wApp.setPasswordOnRAM(password);
        initUserIdentityVerify(false);

        inputUserVerify.setText(null);
        inputUserVerifySure.setText(null);
        ToastUtil.show(getBaseContext(), "New password effective immediately.");

        endIdentityVerify();
    }

    private void userIdentityVerify(EditText inputUserVerify) {
        inputUserVerify.setError(null);
        String password = inputUserVerify.getText().toString();
        int inLen = password.length();
        if (ConfigList.USER_PASSWORD_MIN > inLen || inLen > ConfigList.USER_PASSWORD_MAX) {
            wApp.setIsIdentity(false);
            inputUserVerify.setError("Invalid input.");
            inputUserVerify.requestFocus();
            btnUserVerify.setClickable(true);
            return;
        }

        if (!wApp.isKeyFileOpen(password)) {
            wApp.setIsIdentity(false);
            inputUserVerify.setError("Verification failure. Please make sure and try again.");
            inputUserVerify.requestFocus();
            btnUserVerify.setClickable(true);
            return;
        }

        wApp.setPasswordOnRAM(password);

        inputUserVerify.setText(null);
        ToastUtil.show(getBaseContext(), "Identity verified.\nSwitched to main net.");

        endIdentityVerify();
    }

    private void endIdentityVerify() {
        InputMethodManager imm = (InputMethodManager) btnUserVerify.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(btnUserVerify.getApplicationWindowToken(), 0);
        }
        findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        findViewById(R.id.cardsShell).setVisibility(View.VISIBLE);
        findViewById(R.id.tabsContainer).setVisibility(View.VISIBLE);
        tab.setVisibility(View.VISIBLE);
        wApp.setIsIdentity(true);

        findViewById(R.id.userVerify).setVisibility(View.GONE);
        btnUserVerify.setClickable(true);

        // Load the wallet via user password.
        wApp.load();
        // Init account data.
        postGetBalance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConfigList.SIGN_IN_RESULT) {
            refreshCardUI();
            ToastUtil.show(getBaseContext(), "Connecting block chain.\nYour balance will show in a few seconds.");
            postGetBalance();
            // Save the wallet via user password.
            wApp.save();
            return;
        }
        if (resultCode == ConfigList.SIGN_OUT_RESULT) {
            refreshCardUI();
            // Save the wallet via user password.
            wApp.save();
            return;
        }
        if (resultCode == ConfigList.CHANGE_PASSWORD_RESULT) {
            initUserIdentityVerify(true);
            return;
        }
//        if (resultCode == ConfigList.SCAN_RESULT) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                ToastUtil.show(getBaseContext(), "QR code result empty, please make sure.");
            } else {
                ToastUtil.show(getBaseContext(), "QR code scanned, analyzing content.");
                String scanResult = intentResult.getContents();
                // TODO handle scan result.
                ToastUtil.show(getBaseContext(), scanResult);
            }
        }
//            return;
//        }
        postGetBalance();
    }

    private synchronized void postGetBalance() {
        // TODO on channel value.
        new Thread(new Runnable() {
            @Override
            public void run() {
                Wallet wallet = wApp.getWallet();

                if (wallet == null) {
                    return;
                }
                JSONRpcClient client = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrl())
                        .method("getBalance")
                        .params(wallet.getAddress())
                        .id("1")
                        .build();
                client.post(
                        new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                                // jsonRpcErrorOccur(true);
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                            }
                                        }
                                );
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                final GetBalanceBean responseBean;
                                ResponseBody responseBody = response.body();
                                if (responseBody != null) {
                                    String body = responseBody.string();
                                    if (MainActivity.this.jsonRpcErrorOccur(body)) {
                                        return;
                                    }
                                    responseBean = JSON.parseObject(body, GetBalanceBean.class);
                                    wApp.setChainTNC(BigDecimal.valueOf(Double.valueOf(responseBean.getResult().getTncBalance())));
                                    wApp.setChainNEO(BigDecimal.valueOf(Double.valueOf(responseBean.getResult().getNeoBalance())));
                                    wApp.setChainGAS(BigDecimal.valueOf(Double.valueOf(responseBean.getResult().getGasBalance())));
                                    runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    refreshCardUI();
                                                }
                                            }
                                    );
                                }
                            }
                        }
                );
            }
        }, "MainActivity::postGetBalance").start();
    }

    private boolean jsonRpcErrorOccur(String body) {
        return jsonRpcErrorOccur(JSONRpcErrorUtil.hasError(body));
    }

    private boolean jsonRpcErrorOccur(boolean hasError) {
        synchronized (this) {
            boolean triedForTimes = retryTimesNow == ConfigList.RETRY_TIMES;
            if (!hasError || triedForTimes) {
                retryTimesNow = 0;
                if (triedForTimes) {
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                }
                            }
                    );
                }
            } else {
                retryTimesNow++;
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                postGetBalance();
                            }
                        }
                );
            }
            return hasError;
        }
    }

    private void initToolbarMenu() {
        netState.setText(getString(R.string.main_menu_main_net).toUpperCase(Locale.getDefault()));
        mainMenu.setOnClickListener(new View.OnClickListener() {
            private PopupMenu popupMenu;

            @Override
            public void onClick(View view) {
                popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        CharSequence itemTitle = item.getTitle();
                        int itemId = item.getItemId();
                        switch (itemId) {
                            case R.id.menuAccount:
                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivityForResult(intent, 0);
                                break;
                            case R.id.menuScan:
                                // Request camera permission.
                                EasyPermissions.requestPermissions(
                                        MainActivity.this,
                                        "Camera access permission is required for QR code scanning.",
                                        1,
                                        Manifest.permission.CAMERA);
                                QRCodeUtil.cameraScan(MainActivity.this);
                                break;
                            case R.id.menuSwitchNet:
                                // Do nothing here now(maybe not later).
                                break;
                            case R.id.menuMainNet:
                                wApp.switchNet(ConfigList.NET_TYPE_MAIN);
                                ToastUtil.show(getBaseContext(), "Switched to " + itemTitle);
                                netState.setText(itemTitle.toString().toUpperCase(Locale.getDefault()));
                                postGetBalance();
                                break;
                            case R.id.menuTestNet:
                                wApp.switchNet(ConfigList.NET_TYPE_TEST);
                                ToastUtil.show(getBaseContext(), "Switched to " + itemTitle);
                                netState.setText(itemTitle.toString().toUpperCase(Locale.getDefault()));
                                postGetBalance();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void initCards() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        cardContainer.setAdapter(pagerAdapter);

        cardContainer.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                refreshCardUI();
                postGetBalance();
            }
        });
    }

    private synchronized void attemptTransfer(final boolean isButtonCall) {
        // Wallet
        final Wallet wallet = wApp.getWallet();
        final EditText toAddress = findViewById(R.id.inputTransferTo);
        final TextInputLayout amountLabel = findViewById(R.id.layAmount);
        final EditText amountText = findViewById(R.id.inputAmount);
        final TextView assetLabel = findViewById(R.id.labelAssetsTrans);
        final RadioGroup assetRadioGroup = findViewById(R.id.inputAssetsTrans);
        final Button btnTransfer = findViewById(R.id.btnTransferTo);

        if (isButtonCall && amountLabel.getVisibility() == View.GONE) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // Toast please login first.
                            ToastUtil.show(getBaseContext(), "Verifying input.");
                        }
                    }
            );

            toAddress.setError(null);
            amountText.setError(null);

            if (wallet == null) {
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                // Toast please login first.
                                ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
                            }
                        }
                );
                return;
            }
        }
        final String toAddressStr = toAddress.getText().toString().trim();
        if ("".equals(toAddressStr) || isButtonCall && wallet.getAddress().equals(toAddressStr)) {
            if (isButtonCall) {
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                // Invalid text error.
                                toAddress.setError("Invalid input.");
                                toAddress.requestFocus();
                                amountLabel.setVisibility(View.GONE);
                                assetLabel.setVisibility(View.GONE);
                                assetRadioGroup.setVisibility(View.GONE);
                            }
                        }
                );
            }
            return;
        }
        if (toAddress.length() != ConfigList.NEO_ADDRESS_LENGTH || !ConfigList.NEO_ADDRESS_FIRST.equals(toAddressStr.substring(0, 1))) {
            if (isButtonCall) {
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                // Invalid text error.
                                toAddress.setError("Invalid input.");
                                toAddress.requestFocus();
                                amountLabel.setVisibility(View.GONE);
                                assetLabel.setVisibility(View.GONE);
                                assetRadioGroup.setVisibility(View.GONE);
                            }
                        }
                );
            }
            return;
        }
        // Send post to verify to address.
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONRpcClient client = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrlForNEO())
                        .method("validateaddress")
                        .params(toAddressStr)
                        .id(toAddressStr)
                        .build();
                client.post(
                        new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                            }
                                        }
                                );
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                final ValidateaddressBean responseBean;
                                ResponseBody responseBody = response.body();
                                if (responseBody != null) {
                                    String body = responseBody.string();
                                    responseBean = JSON.parseObject(body, ValidateaddressBean.class);
                                    // The User input is a neo address.
                                    if (responseBean.getResult().isIsvalid()) runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Show transfer window.
                                                    if (amountLabel.getVisibility() == View.GONE) {
                                                        amountLabel.setVisibility(View.VISIBLE);
                                                        assetLabel.setVisibility(View.VISIBLE);
                                                        assetRadioGroup.setVisibility(View.VISIBLE);
                                                        ToastUtil.show(getBaseContext(), "Address verified.");
                                                    } else if (isButtonCall) {
                                                        final String amountTrim = amountText.getText().toString().trim();
                                                        if ("".equals(amountTrim)) {
                                                            amountText.setError("Please input amount.");
                                                            amountText.requestFocus();
                                                            return;
                                                        }
                                                        int radioButtonId = assetRadioGroup.getCheckedRadioButtonId();
                                                        RadioButton radioChecked = MainActivity.this.findViewById(radioButtonId);
                                                        String assetName = radioChecked.getText().toString().trim();
                                                        boolean isCoinInteger = !amountTrim.contains(".");
                                                        boolean isCoinDigitsValid = !isCoinInteger && (amountTrim.length() - amountTrim.indexOf(".")) <= ConfigList.COIN_DIGITS;
                                                        boolean isCoinAmountOK = false;
                                                        BigDecimal amountBigDecimal = BigDecimal.valueOf(Double.valueOf(amountTrim));

                                                        if (getString(R.string.tnc).equals(assetName)) {
                                                            if (isCoinInteger || isCoinDigitsValid) {
                                                                isCoinAmountOK = true;
                                                            } else {
                                                                amountText.setError("TNC balance is a decimal up to 8 digits.");
                                                            }
                                                        } else if (getString(R.string.neo).equals(assetName)) {
                                                            if (isCoinInteger) {
                                                                isCoinAmountOK = true;
                                                            } else {
                                                                amountText.setError("NEO balance is a integer.");
                                                            }
                                                        } else if (getString(R.string.gas).equals(assetName)) {
                                                            if (isCoinInteger || isCoinDigitsValid) {
                                                                isCoinAmountOK = true;
                                                            } else {
                                                                amountText.setError("GAS balance is a decimal up to 8 digits.");
                                                            }
                                                        }

                                                        if (!isCoinAmountOK) {
                                                            amountText.requestFocus();
                                                            return;
                                                        }

                                                        boolean isAmountEnough = amountBigDecimal.compareTo(wApp.getChainTNC()) <= 0;

                                                        if (!isAmountEnough) {
                                                            amountText.setError("Balance of current asset is not enough.");
                                                            amountText.requestFocus();
                                                            return;
                                                        }

                                                        btnTransfer.setClickable(false);
                                                        ToastUtil.show(getBaseContext(), "Doing transfer.\nIt takes a very short time.");
                                                        postConstructTx(toAddressStr, amountTrim, assetName);
                                                    }
                                                }
                                            }
                                    );
                                    else if (isButtonCall) {
                                        // TODO Do something to know it is a gateway.
                                        if (true) {
                                            // TODO Gateway trade.
                                            runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            amountLabel.setVisibility(View.GONE);
                                                            assetLabel.setVisibility(View.GONE);
                                                            assetRadioGroup.setVisibility(View.GONE);
                                                        }
                                                    }
                                            );
                                        } else {
                                            // All invalid.
                                            runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Invalid text error.
                                                            toAddress.setError("Invalid input.");
                                                            toAddress.requestFocus();
                                                            amountLabel.setVisibility(View.GONE);
                                                            assetLabel.setVisibility(View.GONE);
                                                            assetRadioGroup.setVisibility(View.GONE);
                                                        }
                                                    }
                                            );
                                        }
                                    }
                                }
                            }
                        }
                );
            }
        }, "MainActivity::isValidAddress").start();
    }

    private void postConstructTx(final String validToAddress, final String validAmount, final String assetName) {
        // Send post to transfer to address.
        final Wallet wallet = wApp.getWallet();
        final Button btnTransfer = findViewById(R.id.btnTransferTo);
        if (wallet == null) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // Toast please login first.
                            ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
                            btnTransfer.setClickable(true);
                        }
                    }
            );
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String myAddress = wallet.getAddress();
                JSONRpcClient client = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrl())
                        .method("constructTx")
                        .params(myAddress, validToAddress, validAmount, ConfigList.ASSET_ID_MAP.get(assetName))
                        .id(validToAddress + validAmount + assetName)
                        .build();
                client.post(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                        btnTransfer.setClickable(true);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body != null) {
                            String respJson = body.string();
                            boolean hasError = JSONRpcErrorUtil.hasError(respJson);
                            if (hasError) {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                // Toast please login first.
                                                ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                                btnTransfer.setClickable(true);
                                            }
                                        }
                                );
                                return;
                            }
                            final ConstructTxBean bean = JSON.parseObject(respJson, ConstructTxBean.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    postSendrawtransaction(bean.getResult().getTxData(), bean.getResult().getTxid(), bean.getResult().getWitness());
                                }
                            });
                        }
                    }
                });
            }
        }, "MainActivity::postConstructTx").start();
    }

    private void postSendrawtransaction(final String txData, final String txid, final String witness) {
        // Send post to transfer to address.
        final Wallet wallet = wApp.getWallet();
        final Button btnTransfer = findViewById(R.id.btnTransferTo);
        if (wallet == null) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // Toast please login first.
                            ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
                            btnTransfer.setClickable(true);
                        }
                    }
            );
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] sign;
                try {
                    sign = Neoutils.sign(HexUtil.hexToByteArray(txData), HexUtil.byteArrayToHex(wallet.getPrivateKey()));
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // Toast please login first.
                                    ToastUtil.show(getBaseContext(), "Failed on signature.\nPlease try again later or contact the administrator.");
                                    btnTransfer.setClickable(true);
                                }
                            }
                    );
                    return;
                }
                String sign16 = HexUtil.byteArrayToHex(sign);
                String publicKey16 = HexUtil.byteArrayToHex(wallet.getPublicKey());
                String replacedWitness = witness.replace("{signature}", sign16).replace("{pubkey}", publicKey16);
                JSONRpcClient client = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrlForNEO())
                        .method("sendrawtransaction")
                        .params(txData + replacedWitness)
                        .id(txid)
                        .build();
                client.post(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                        btnTransfer.setClickable(true);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body != null) {
                            String respJson = body.string();
                            boolean hasError = JSONRpcErrorUtil.hasError(respJson);
                            if (hasError) {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                                btnTransfer.setClickable(true);
                                            }
                                        }
                                );
                                return;
                            }
                            final SendrawtransactionBean bean = JSON.parseObject(respJson, SendrawtransactionBean.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (bean.isResult()) {
                                        ToastUtil.show(getBaseContext(), "Transfer succeed.\nChain confirms in seconds.");
                                        postGetBalance();
                                    } else {
                                        ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                    }
                                    btnTransfer.setClickable(true);
                                }
                            });
                        }
                    }
                });
            }
        }, "MainActivity::postSendrawtransaction").start();
    }

    private void initTabs() {
        // This is tab transfer.
        final EditText toAddress = findViewById(R.id.inputTransferTo);
        final Button btnTransfer = findViewById(R.id.btnTransferTo);
        toAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptTransfer(true);
                    return true;
                }
                return false;
            }
        });
        toAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                boolean focusOut = !b;
                if (focusOut) {
                    attemptTransfer(false);
                }
            }
        });
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptTransfer(true);
            }
        });

        // This is tab add channel.

        // This is tab channel list.

        // This is tab record.

        tab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                letTabsBeGone();
                switch (menuItem.getItemId()) {
                    case R.id.navigationTransfer:
                        tabTransfer.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigationAddChannel:
                        tabAddChannel.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigationChannelList:
                        tabChannelList.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigationRecord:
                        tabRecord.setVisibility(View.VISIBLE);
                        return true;
                }
                return false;
            }
        });
        tabTransfer.setVisibility(View.VISIBLE);
    }

    private void letTabsBeGone() {
        tabTransfer.setVisibility(View.GONE);
        tabAddChannel.setVisibility(View.GONE);
        tabChannelList.setVisibility(View.GONE);
        tabRecord.setVisibility(View.GONE);
    }

    private synchronized void refreshCardUI() {
        View card;
        for (int cardIndex = 0; cardIndex < cardContainer.getChildCount(); cardIndex++) {
            card = cardContainer.getChildAt(cardIndex);
            refreshCardUI(card);
        }
    }

    private synchronized void refreshCardUI(View view) {
        Wallet wallet = wApp.getWallet();
        TextView cardHeader = view.findViewById(R.id.cardHeader);
        TextView chainBalance = view.findViewById(R.id.cardChainBalance);
        TextView channelBalance = view.findViewById(R.id.cardChannelBalance);
        TextView cardAddress = view.findViewById(R.id.cardAddress);

        if (wallet == null) {
            cardAddress.setText(getString(R.string.please_login));
        } else if (wallet.getAddress() != null) {
            cardAddress.setText(wallet.getAddress());
        }

        String[] split = cardHeader.getText().toString().split("WALLET INFO\n");
        if (split.length > 0) {
            switch (split[split.length - 1]) {
                case "TNC":
                    chainBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChainTNC()));
                    channelBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChannelTNC()));
                    break;
                case "NEO":
                    chainBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChainNEO()));
                    channelBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChannelNEO()));
                    break;
                case "GAS":
                    chainBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChainGAS()));
                    channelBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChannelGAS()));
                    break;
            }
        }
    }

    /* ---------------------------------- INNER CLASSES ---------------------------------- */

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String CARD_NO = "card_no";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int cardNo) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(CARD_NO, cardNo);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_card, container, false);
            ImageView imageView = rootView.findViewById(R.id.cardIcon);
            TextView textView = rootView.findViewById(R.id.cardHeader);
            View cardColorFooter = rootView.findViewById(R.id.cardColorFooter);

            int cardNo = 0;
            if (getArguments() != null) {
                cardNo = getArguments().getInt(CARD_NO);
            }
            switch (cardNo) {
                case 1:
                    imageView.setImageResource(R.mipmap.ic_tnc);
                    textView.setText(getString(R.string.card_text, getString(R.string.tnc)));
                    cardColorFooter.setBackgroundResource(R.drawable.shape_corner_down_tnc);
                    break;
                case 2:
                    imageView.setImageResource(R.mipmap.ic_neo);
                    textView.setText(getString(R.string.card_text, getString(R.string.neo)));
                    cardColorFooter.setBackgroundResource(R.drawable.shape_corner_down_neo);
                    break;
                case 3:
                    imageView.setImageResource(R.mipmap.ic_gas);
                    textView.setText(getString(R.string.card_text, getString(R.string.gas)));
                    cardColorFooter.setBackgroundResource(R.drawable.shape_corner_down_gas);
                    break;
            }
            instance.refreshCardUI(rootView);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show how many total pages.
            return cardAccount;
        }
    }
}
