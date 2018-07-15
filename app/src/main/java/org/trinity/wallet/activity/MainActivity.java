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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.alibaba.fastjson.JSON;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.trinity.util.HexUtil;
import org.trinity.util.JSONRpcErrorUtil;
import org.trinity.util.android.QRCodeUtil;
import org.trinity.util.android.ToastUtil;
import org.trinity.util.android.UIStringUtil;
import org.trinity.wallet.ConfigList;
import org.trinity.wallet.R;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.net.JSONRpcClient;
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
     * The frame of a single card view.
     */
    @BindView(R.id.cardsShell)
    CoordinatorLayout cardsShell;
    /**
     * The container of a single card.
     * <p>
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.cardContainer)
    ViewPager cardContainer;
    /**
     * The adapter of the card group.
     * <p>
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter pagerAdapter;
    /**
     * The navigation bar of main tab.
     */
    @BindView(R.id.tab)
    BottomNavigationView tab;
    /**
     * The bodies of main tab.
     */
    @BindView(R.id.tabTransfer)
    ConstraintLayout tabTransfer;
    @BindView(R.id.tabAddChannel)
    ConstraintLayout tabAddChannel;
    @BindView(R.id.tabChannelList)
    ConstraintLayout tabChannelList;
    @BindView(R.id.tabRecord)
    ConstraintLayout tabRecord;
    /**
     * The butter knife inject components.
     */
    @BindView(R.id.netState)
    TextView netState;
    @BindView(R.id.btnMainMenu)
    Button btnMainMenu;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.titleTransfer)
    TextView titleTransfer;
    @BindView(R.id.inputTransferTo)
    EditText inputTransferTo;
    @BindView(R.id.layTransferTo)
    TextInputLayout layTransferTo;
    @BindView(R.id.inputAmount)
    EditText inputAmount;
    @BindView(R.id.layAmount)
    TextInputLayout layAmount;
    @BindView(R.id.labelAssetsTrans)
    TextView labelAssetsTrans;
    @BindView(R.id.rdoItmTNCTrans)
    RadioButton rdoItmTNCTrans;
    @BindView(R.id.rdoItmNEOTrans)
    RadioButton rdoItmNEOTrans;
    @BindView(R.id.rdoItmGASTrans)
    RadioButton rdoItmGASTrans;
    @BindView(R.id.inputAssetsTrans)
    RadioGroup inputAssetsTrans;
    @BindView(R.id.btnTransferTo)
    Button btnTransferTo;
    @BindView(R.id.titleAddChannel)
    TextView titleAddChannel;
    @BindView(R.id.inputTNAP)
    EditText inputTNAP;
    @BindView(R.id.layTNAP)
    TextInputLayout layTNAP;
    @BindView(R.id.inputDeposit)
    EditText inputDeposit;
    @BindView(R.id.layDeposit)
    TextInputLayout layDeposit;
    @BindView(R.id.labelAssets)
    TextView labelAssets;
    @BindView(R.id.rdoItmTNC)
    RadioButton rdoItmTNC;
    @BindView(R.id.rdoItmNEO)
    RadioButton rdoItmNEO;
    @BindView(R.id.rdoItmGAS)
    RadioButton rdoItmGAS;
    @BindView(R.id.inputAssets)
    RadioGroup inputAssets;
    @BindView(R.id.inputAlias)
    EditText inputAlias;
    @BindView(R.id.layAlias)
    TextInputLayout layAlias;
    @BindView(R.id.titleChannelList)
    TextView titleChannelList;
    @BindView(R.id.channelListEmpty)
    TextView channelListEmpty;
    @BindView(R.id.channelContainer)
    LinearLayout channelContainer;
    @BindView(R.id.titleRecord)
    TextView titleRecord;
    @BindView(R.id.recordEmpty)
    TextView recordEmpty;
    @BindView(R.id.recordContainer)
    LinearLayout recordContainer;
    @BindView(R.id.tabsContainer)
    ScrollView tabsContainer;
    @BindView(R.id.titleUserVerify)
    TextView titleUserVerify;
    @BindView(R.id.infoUserVerify)
    TextView infoUserVerify;
    @BindView(R.id.inputUserVerify)
    EditText inputUserVerify;
    @BindView(R.id.layUserVerify)
    TextInputLayout layUserVerify;
    @BindView(R.id.inputUserVerifySure)
    EditText inputUserVerifySure;
    @BindView(R.id.layUserVerifySure)
    TextInputLayout layUserVerifySure;
    @BindView(R.id.btnUserVerify)
    Button btnUserVerify;
    @BindView(R.id.userVerify)
    LinearLayout userVerify;
    @BindView(R.id.mainContainer)
    ConstraintLayout mainContainer;
    /**
     * The total number of cards.
     */
    private int cardAccount = ConfigList.COIN_TYPE_ACCOUNT;
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
        toolbar.setVisibility(View.GONE);
        cardsShell.setVisibility(View.GONE);
        tabsContainer.setVisibility(View.GONE);
        tab.setVisibility(View.GONE);
        wApp.setIsIdentity(false);
        userVerify.setVisibility(View.VISIBLE);

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

    private void userIdentitySet(final EditText inputUserVerify, final EditText inputUserVerifySure) {
        btnUserVerify.setClickable(false);
        verifyIdentityHideIME();

        inputUserVerify.setError(null);
        inputUserVerifySure.setError(null);
        final String password = inputUserVerify.getText().toString();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                wApp.iAmNotFirstTime(password);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wApp.setPasswordOnRAM(password);
                        initUserIdentityVerify(false);

                        inputUserVerify.setText(null);
                        inputUserVerifySure.setText(null);
                        ToastUtil.show(getBaseContext(), "New password effective immediately.");

                        endIdentityVerify();
                    }
                });
            }
        }, "MainActivity::userIdentityVerify").start();
    }

    private void userIdentityVerify(final EditText inputUserVerify) {
        ToastUtil.show(getBaseContext(), "Verifying.");
        verifyIdentityHideIME();
        inputUserVerify.setError(null);
        final String password = inputUserVerify.getText().toString();
        int inLen = password.length();
        if (ConfigList.USER_PASSWORD_MIN > inLen || inLen > ConfigList.USER_PASSWORD_MAX) {
            wApp.setIsIdentity(false);
            inputUserVerify.setError("Invalid input.");
            inputUserVerify.requestFocus();
            btnUserVerify.setClickable(true);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean isKeyFileOpen = wApp.isKeyFileOpen(password);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isKeyFileOpen) {
                            wApp.setIsIdentity(false);
                            inputUserVerify.setError("Verification failure. Please make sure and try again.");
                            inputUserVerify.requestFocus();
                            btnUserVerify.setClickable(true);
                            return;
                        }

                        wApp.setPasswordOnRAM(password);
                        inputUserVerify.setText(null);
                        ToastUtil.show(getBaseContext(), "Switched to main net.");

                        endIdentityVerify();
                    }
                });
            }
        }, "MainActivity::userIdentityVerify").start();
    }

    private void verifyIdentityHideIME() {
        InputMethodManager imm = (InputMethodManager) btnUserVerify.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(btnUserVerify.getApplicationWindowToken(), 0);
        }
    }

    private void endIdentityVerify() {
        toolbar.setVisibility(View.VISIBLE);
        cardsShell.setVisibility(View.VISIBLE);
        tabsContainer.setVisibility(View.VISIBLE);
        tab.setVisibility(View.VISIBLE);
        wApp.setIsIdentity(true);

        userVerify.setVisibility(View.GONE);
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
                String scanResult = intentResult.getContents();
                inputTransferTo.setText(scanResult);
                attemptTransfer(false);
            }
            return;
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
        btnMainMenu.setOnClickListener(new View.OnClickListener() {
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

        inputTransferTo.setError(null);
        inputAmount.setError(null);

        if (isButtonCall && layAmount.getVisibility() == View.GONE) {
            // Toast please login first.
            ToastUtil.show(getBaseContext(), "Verifying input.");

            if (wallet == null) {
                // Toast please login first.
                ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');

                return;
            }
        }
        final String toAddressStr = inputTransferTo.getText().toString().trim();
        if ("".equals(toAddressStr) || wallet.getAddress().equals(toAddressStr)) {
            if (isButtonCall) {
                // Invalid text error.
                inputTransferTo.setError("Invalid input.");
                inputTransferTo.requestFocus();
                layAmount.setVisibility(View.GONE);
                labelAssetsTrans.setVisibility(View.GONE);
                inputAssetsTrans.setVisibility(View.GONE);

            }
            return;
        }

        boolean isLooksLikeAnAddress = inputTransferTo.length() == ConfigList.NEO_ADDRESS_LENGTH && ConfigList.NEO_ADDRESS_FIRST.equals(toAddressStr.substring(0, 1));
        // Send post to verify to address.
        if (isLooksLikeAnAddress) {
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
                                        if (responseBean.getResult().isIsvalid()) {
                                            runOnUiThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Show transfer window.
                                                            if (layAmount.getVisibility() == View.GONE) {
                                                                layAmount.setVisibility(View.VISIBLE);
                                                                labelAssetsTrans.setVisibility(View.VISIBLE);
                                                                inputAssetsTrans.setVisibility(View.VISIBLE);
                                                                ToastUtil.show(getBaseContext(), "Address verified.");
                                                            } else if (isButtonCall) {
                                                                final String amountTrim = inputAmount.getText().toString().trim();
                                                                if ("".equals(amountTrim)) {
                                                                    inputAmount.setError("Please input amount.");
                                                                    inputAmount.requestFocus();
                                                                    return;
                                                                }
                                                                int radioButtonId = inputAssetsTrans.getCheckedRadioButtonId();
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
                                                                        inputAmount.setError("TNC balance is a decimal up to 8 digits.");
                                                                    }
                                                                } else if (getString(R.string.neo).equals(assetName)) {
                                                                    if (isCoinInteger) {
                                                                        isCoinAmountOK = true;
                                                                    } else {
                                                                        inputAmount.setError("NEO balance is a integer.");
                                                                    }
                                                                } else if (getString(R.string.gas).equals(assetName)) {
                                                                    if (isCoinInteger || isCoinDigitsValid) {
                                                                        isCoinAmountOK = true;
                                                                    } else {
                                                                        inputAmount.setError("GAS balance is a decimal up to 8 digits.");
                                                                    }
                                                                }

                                                                if (!isCoinAmountOK) {
                                                                    inputAmount.requestFocus();
                                                                    return;
                                                                }

                                                                boolean isAmountEnough = amountBigDecimal.compareTo(wApp.getChainTNC()) <= 0;

                                                                if (!isAmountEnough) {
                                                                    inputAmount.setError("Balance of current asset is not enough.");
                                                                    inputAmount.requestFocus();
                                                                    return;
                                                                }

                                                                btnTransferTo.setClickable(false);
                                                                ToastUtil.show(getBaseContext(), "Doing transfer.\nIt takes a very short time.");
                                                                postConstructTx(toAddressStr, amountTrim, assetName);
                                                            }
                                                        }
                                                    }
                                            );
                                        } else if (isButtonCall) {
                                            // not address
                                            verifyPaymentCode(isButtonCall);
                                        }
                                    }
                                }
                            }
                    );
                }
            }, "MainActivity::isValidAddress").start();
        } else {
            // not address
            verifyPaymentCode(isButtonCall);
        }
    }

    private void verifyPaymentCode(boolean isButtonCall) {
        layAmount.setVisibility(View.GONE);
        labelAssetsTrans.setVisibility(View.GONE);
        inputAssetsTrans.setVisibility(View.GONE);

        if (!isButtonCall) {
            return;
        }

        final boolean isPaymentCode = false;
        // TODO Do something to know it is a payment code.

        if (isPaymentCode) {
            // TODO Gateway trade.
        } else {
            // All invalid.

            // Invalid text error.
            inputTransferTo.setError("Invalid input.");
            inputTransferTo.requestFocus();

        }
    }

    private void postConstructTx(final String validToAddress, final String validAmount, final String assetName) {
        // Send post to transfer to address.
        final Wallet wallet = wApp.getWallet();
        if (wallet == null) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // Toast please login first.
                            ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
                            btnTransferTo.setClickable(true);
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
                                        btnTransferTo.setClickable(true);
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
                                                btnTransferTo.setClickable(true);
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
        if (wallet == null) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // Toast please login first.
                            ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
                            btnTransferTo.setClickable(true);
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
                                    btnTransferTo.setClickable(true);
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
                                        btnTransferTo.setClickable(true);
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
                                                btnTransferTo.setClickable(true);
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
                                    btnTransferTo.setClickable(true);
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
        inputTransferTo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptTransfer(true);
                    return true;
                }
                return false;
            }
        });
        inputTransferTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                boolean focusOut = !b;
                if (focusOut) {
                    attemptTransfer(false);
                }
            }
        });
        btnTransferTo.setOnClickListener(new View.OnClickListener() {
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
        TextView cardChainBalance = view.findViewById(R.id.cardChainBalance);
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
                    cardChainBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChainTNC()));
                    channelBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChannelTNC()));
                    break;
                case "NEO":
                    cardChainBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChainNEO()));
                    channelBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChannelNEO()));
                    break;
                case "GAS":
                    cardChainBalance.setText(UIStringUtil.bigDecimalToString(wApp.getChainGAS()));
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
