package org.trinity.wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.trinity.util.algorithm.UUIDUtil;
import org.trinity.util.android.IMEUtil;
import org.trinity.util.android.QRCodeUtil;
import org.trinity.util.android.ToastUtil;
import org.trinity.util.android.UIStringUtil;
import org.trinity.util.convert.BigDecimalUtil;
import org.trinity.util.convert.HexUtil;
import org.trinity.util.convert.NeoSignUtil;
import org.trinity.util.convert.PaymentCodeUtil;
import org.trinity.util.convert.PrefixUtil;
import org.trinity.util.convert.TNAPUtil;
import org.trinity.util.net.JSONObjectUtil;
import org.trinity.wallet.ConfigList;
import org.trinity.wallet.R;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.entity.BillBean;
import org.trinity.wallet.entity.ChannelBean;
import org.trinity.wallet.entity.PaymentCodeBean;
import org.trinity.wallet.net.JSONRpcClient;
import org.trinity.wallet.net.WebSocketClient;
import org.trinity.wallet.net.jsonrpc.ConstructTxBean;
import org.trinity.wallet.net.jsonrpc.FunderTransactionBean;
import org.trinity.wallet.net.jsonrpc.GetBalanceBean;
import org.trinity.wallet.net.jsonrpc.HTLCTransactionBean;
import org.trinity.wallet.net.jsonrpc.RefoundTransBean;
import org.trinity.wallet.net.jsonrpc.SendrawtransactionBean;
import org.trinity.wallet.net.websocket.ACAckRouterInfoBean;
import org.trinity.wallet.net.websocket.ACFounderBean;
import org.trinity.wallet.net.websocket.ACFounderSignBean;
import org.trinity.wallet.net.websocket.ACGetRouterInfoBean;
import org.trinity.wallet.net.websocket.ACHtlcBean;
import org.trinity.wallet.net.websocket.ACHtlcSignBean;
import org.trinity.wallet.net.websocket.ACRResponseBean;
import org.trinity.wallet.net.websocket.ACRegisterChannelBean;
import org.trinity.wallet.net.websocket.ACRsmcBean;
import org.trinity.wallet.net.websocket.ACRsmcSignBean;
import org.trinity.wallet.net.websocket.ACSettleSignBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import neoutils.Neoutils;
import neoutils.Wallet;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {
    /**
     * The activity object.
     */
    @SuppressLint("StaticFieldLeak")
    protected static MainActivity instance;
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
     * The buttons & bodies of main tab.
     */
    @BindView(R.id.tabTransfer)
    ConstraintLayout tabTransfer;
    @BindView(R.id.tabSetupChannel)
    ConstraintLayout tabSetupChannel;
    @BindView(R.id.tabChannel)
    ConstraintLayout tabChannel;
    @BindView(R.id.tabBill)
    ConstraintLayout tabBill;
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
    @BindView(R.id.inputPaymentCodeAddress)
    EditText inputPaymentCodeAddress;
    @BindView(R.id.layPaymentCodeAddress)
    TextInputLayout layPaymentCodeAddress;
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
    @BindView(R.id.btnPay)
    Button btnPay;
    @BindView(R.id.btnReceive)
    Button btnReceive;
    @BindView(R.id.paymentCodeQR)
    ImageView paymentCodeQR;
    @BindView(R.id.titleSetupChannel)
    TextView titleSetupChannel;
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
    @BindView(R.id.btnSetupChannel)
    Button btnSetupChannel;
    @BindView(R.id.titleChannel)
    TextView titleChannel;
    @BindView(R.id.channelEmpty)
    TextView channelEmpty;
    @BindView(R.id.channelContainer)
    LinearLayout channelContainer;
    @BindView(R.id.titleBill)
    TextView titleBill;
    @BindView(R.id.billEmpty)
    TextView billEmpty;
    @BindView(R.id.billContainer)
    LinearLayout billContainer;
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
     * Json util.
     */
    private Gson gson;
    /**
     * Last post happen time.
     */
    private long lastPostTimeMillis;
    /**
     * Web socket pool.
     */
    @NonNull
    private List<Map<ChannelBean, WebSocket>> webSocketDesc = new ArrayList<>();

    /* ---------------------------------- ANDROID LIFE CYCLES ---------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        measureToolbar(toolbar);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(newToolbarWidth, newToolbarHeight);
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPadding(0, paddingTop, 0, 0);
        toolbar.requestLayout();

        instance = MainActivity.this;
        gson = WalletApplication.getGson();

        // Identity verify.
        initUserIdentityVerify();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        paymentCodeQR.callOnClick();
        if (resultCode == ConfigList.BACK_RESULT) {
            obtainBalance();
            return;
        }
        if (resultCode == ConfigList.SIGN_IN_RESULT) {
            refreshUITotal();
            ToastUtil.show(getBaseContext(), "Connecting block chain.\nYour balance will show in a few seconds.");
            obtainBalance();
            return;
        }
        if (resultCode == ConfigList.SIGN_OUT_RESULT) {
            refreshUITotal();
            return;
        }
        if (resultCode == ConfigList.CHANGE_PASSWORD_RESULT) {
            initUserIdentityVerify(true);
            obtainBalance();
            return;
        }
        if (resultCode == ConfigList.SCAN_RESULT) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() == null) {
                    ToastUtil.show(getBaseContext(), "QR code result empty, please make sure.");
                } else {
                    String scanResult = intentResult.getContents().trim();
                    if (scanResult.contains("@") || scanResult.contains(".") || scanResult.contains(":")) {
                        inputTNAP.setText(scanResult);
                        tab.setSelectedItemId(R.id.navigationSetupChannel);
                    }
                    // If the length of QR result is bigger than neo address(payment code always longer)
                    // and starts with "A" or "TN".
                    else if (scanResult.length() >= ConfigList.NEO_ADDRESS_LENGTH && (ConfigList.NEO_ADDRESS_FIRST.equals(scanResult.substring(0, 1)) || ConfigList.PAYMENT_CODE_FIRST.equals(scanResult.substring(0, 2)))) {
                        inputPaymentCodeAddress.setText(scanResult);
                        tab.setSelectedItemId(R.id.navigationTransfer);
                        verifyAddress(false);
                    }
                    obtainBalance();
                }
                return;
            }
        }
        obtainBalance();
    }

    /* ---------------------------------- INIT METHODS ---------------------------------- */

    private void initUserIdentityVerify() {
        initUserIdentityVerify(wApp.isFirstTime());
    }

    private void initUserIdentityVerify(boolean isFirstTime) {
        toolbar.setVisibility(View.GONE);
        cardsShell.setVisibility(View.GONE);
        tabsContainer.setVisibility(View.GONE);
        tab.setVisibility(View.GONE);
        wApp.setIdentity(false);
        userVerify.setVisibility(View.VISIBLE);

        titleUserVerify.requestFocus();
        if (isFirstTime) {
            titleUserVerify.setText("IDENTITY CONFIRM");
            infoUserVerify.setText("Please set a new password for identity verify. (It is NOT your private key.)");
            layUserVerifySure.setVisibility(View.VISIBLE);
            inputUserVerifySure.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        userIdentityCreate(inputUserVerify, inputUserVerifySure);
                        return true;
                    }
                    return false;
                }
            });
            btnUserVerify.setText(R.string.confirm);
            btnUserVerify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        userIdentityCreate(inputUserVerify, inputUserVerifySure);
                        return true;
                    }
                    return false;
                }
            });
            btnUserVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userIdentityCreate(inputUserVerify, inputUserVerifySure);
                }
            });
        } else {
            titleUserVerify.setText("IDENTITY VERIFICATION");
            infoUserVerify.setText("Please input your password for identity verify. (It is NOT your private key.)");
            layUserVerifySure.setVisibility(View.GONE);
            inputUserVerify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        userIdentityVerify(inputUserVerify);
                        return true;
                    }
                    return false;
                }
            });
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

    private void userIdentityCreate(final EditText inputUserVerify, final EditText inputUserVerifySure) {
        btnUserVerify.setClickable(false);
        IMEUtil.hideIME(btnUserVerify);

        inputUserVerify.setError(null);
        inputUserVerifySure.setError(null);
        final String newPassword = inputUserVerify.getText().toString();
        String passwordSure = inputUserVerifySure.getText().toString();
        int inLen = newPassword.length();

        if (ConfigList.USER_PASSWORD_MIN > inLen || inLen > ConfigList.USER_PASSWORD_MAX) {
            wApp.setIdentity(false);
            inputUserVerify.setError("Invalid input.");
            inputUserVerify.requestFocus();
            btnUserVerify.setClickable(true);
            return;
        }
        if (!newPassword.equals(passwordSure)) {
            inputUserVerifySure.setError("Inconsistent.");
            inputUserVerifySure.requestFocus();
            btnUserVerify.setClickable(true);
            return;
        }

        Runnable service = new Runnable() {
            @Override
            public void run() {
                wApp.iAmFirstTime(newPassword);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initUserIdentityVerify(false);
                        inputUserVerify.setText(null);
                        inputUserVerifySure.setText(null);
                        ToastUtil.show(getBaseContext(), "New password effective immediately.");
                        // Init the other.
                        endIdentityVerify();
                    }
                });
            }
        };
        runOffUiThread(service);
    }

    private void userIdentityVerify(final EditText inputUserVerify) {
        ToastUtil.show(getBaseContext(), "Verifying.");
        IMEUtil.hideIME(btnUserVerify);

        inputUserVerify.setError(null);
        final String password = inputUserVerify.getText().toString();
        int inLen = password.length();
        if (ConfigList.USER_PASSWORD_MIN > inLen || inLen > ConfigList.USER_PASSWORD_MAX) {
            wApp.setIdentity(false);
            inputUserVerify.setError("Invalid input.");
            inputUserVerify.requestFocus();
            btnUserVerify.setClickable(true);
            return;
        }

        Runnable service = new Runnable() {
            @Override
            public void run() {
                final boolean isKeyFileOpen;
                isKeyFileOpen = wApp.isKeyFileOpen(password);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isKeyFileOpen) {
                            wApp.setIdentity(false);
                            inputUserVerify.setError("Verification failure. Please make sure and try again.");
                            inputUserVerify.requestFocus();
                            btnUserVerify.setClickable(true);
                            return;
                        }
                        inputUserVerify.setText(null);
                        // Init the other.
                        endIdentityVerify();
                    }
                });
            }
        };
        runOffUiThread(service);
    }

    private void endIdentityVerify() {
        // Load the wallet via user password.
        wApp.loadGlobal();
        wApp.switchNet(wApp.getNet());
        wApp.setIdentity(true);

        // Init events of toolbar's menu button click action.
        initToolbarMenu();
        // Init events of cards' click action.
        initCards();
        // Init events of tabs' click action.
        initTabs();

        toolbar.setVisibility(View.VISIBLE);
        cardsShell.setVisibility(View.VISIBLE);
        tabsContainer.setVisibility(View.VISIBLE);
        tab.setVisibility(View.VISIBLE);

        userVerify.setVisibility(View.GONE);
        btnUserVerify.setClickable(true);

        netState.setText(getString(R.string.net_state, wApp.getNet().toUpperCase(Locale.getDefault())));

        // Init account data.
        obtainBalance();

        // Connect known channel.
        connectChannels();
    }

    private void initToolbarMenu() {
        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnMainMenu);
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
                        if (!EasyPermissions.hasPermissions(getBaseContext(), Manifest.permission.CAMERA)) {
                            break;
                        }
                        QRCodeUtil.cameraScan(MainActivity.this);
                        break;
                    case R.id.menuSwitchNet:
                        // Do nothing here now(maybe not later).
                        break;
                    case R.id.menuMainNet:
                        wApp.switchNet(ConfigList.NET_TYPE_MAIN);
                        ToastUtil.show(getBaseContext(), "Switched to " + itemTitle);
                        netState.setText(itemTitle.toString().toUpperCase(Locale.getDefault()));
                        obtainBalance();
                        refreshChannelUI();
                        refreshBillUI();
                        break;
                    case R.id.menuTestNet:
                        wApp.switchNet(ConfigList.NET_TYPE_TEST);
                        ToastUtil.show(getBaseContext(), "Switched to " + itemTitle);
                        netState.setText(itemTitle.toString().toUpperCase(Locale.getDefault()));
                        obtainBalance();
                        refreshChannelUI();
                        refreshBillUI();
                        break;
                }
                return true;
            }
        });

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                obtainBalance();
            }
        });
    }

    private void initTabs() {
        // This is tab addressMode.
        inputPaymentCodeAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    verifyAddress(true);
                    return true;
                }
                return false;
            }
        });
        inputPaymentCodeAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                boolean onFocusOut = !b;
                if (onFocusOut) {
                    verifyAddress(false);
                }
            }
        });

        RadioGroup.OnCheckedChangeListener onRadioCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checked = findViewById(i);
                String name = checked.getText().toString().trim().toUpperCase(Locale.getDefault());
                int cardIndex = 0;
                switch (name) {
                    case ConfigList.ASSET_ID_MAP_KEY_TNC:
                        cardIndex = 0;
                        break;
                    case ConfigList.ASSET_ID_MAP_KEY_NEO:
                        cardIndex = 1;
                        break;
                    case ConfigList.ASSET_ID_MAP_KEY_GAS:
                        cardIndex = 2;
                        break;
                }
                cardContainer.setCurrentItem(cardIndex, true);
            }
        };

        inputAssetsTrans.setOnCheckedChangeListener(onRadioCheckedChangeListener);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyAddress(true);
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePaymentCode();
            }
        });

        paymentCodeQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setClickable(false);
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(null);
                imageView.requestLayout();
            }
        });

        // This is tab add channel.
        inputAssets.setOnCheckedChangeListener(onRadioCheckedChangeListener);

        btnSetupChannel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptSetupChannel();
                    }
                }
        );

        // This is tab channel.
        refreshChannelUI();

        // This is tab bill.
        refreshBillUI();

        // Reset tab index.
        tab.setOnFocusChangeListener(null);
        tab.setSelectedItemId(R.id.navigationSetupChannel);
        tab.setSelectedItemId(R.id.navigationTransfer);

        // This is tab bar.
        tab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private void tabsGone() {
                tabTransfer.setVisibility(View.GONE);
                tabSetupChannel.setVisibility(View.GONE);
                tabChannel.setVisibility(View.GONE);
                tabBill.setVisibility(View.GONE);
            }

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                tabsGone();
                switch (menuItem.getItemId()) {
                    case R.id.navigationTransfer:
                        tabTransfer.setVisibility(View.VISIBLE);
                        obtainBalance();
                        return true;
                    case R.id.navigationSetupChannel:
                        tabSetupChannel.setVisibility(View.VISIBLE);
                        obtainBalance();
                        return true;
                    case R.id.navigationChannel:
                        tabChannel.setVisibility(View.VISIBLE);
                        obtainBalance();
                        return true;
                    case R.id.navigationBill:
                        tabBill.setVisibility(View.VISIBLE);
                        obtainBalance();
                        return true;
                }
                return false;
            }
        });
        tabTransfer.setVisibility(View.VISIBLE);
    }

    /* ---------------------------------- UI THREAD METHODS ---------------------------------- */

    private synchronized void refreshUITotal() {
        refreshCardUI();
        refreshChannelUI();
        refreshBillUI();
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
        ImageView cardQRAddress = view.findViewById(R.id.cardQRAddress);
        TextView cardChainBalance = view.findViewById(R.id.cardChainBalance);
        TextView cardChannelBalance = view.findViewById(R.id.cardChannelBalance);
        TextView cardAddress = view.findViewById(R.id.cardAddress);

        if (wallet == null) {
            cardAddress.setText(getString(R.string.please_sign_in));
        } else if (wallet.getAddress() != null) {
            cardQRAddress.setImageBitmap(wApp.getAddressQR());
            cardAddress.setText(wallet.getAddress());
        }

        String[] split = cardHeader.getText().toString().split(getString(R.string.card_text, ""));
        if (split.length > 0) {
            switch (split[split.length - 1]) {
                case "TNC":
                    cardChainBalance.setText(UIStringUtil.castDoubleToString(wApp.getChainTNC()));
                    cardChannelBalance.setText(UIStringUtil.castDoubleToString(wApp.getChannelTNC()));
                    break;
                case "NEO":
                    cardChainBalance.setText(UIStringUtil.castDoubleToString(wApp.getChainNEO()));
                    cardChannelBalance.setText(UIStringUtil.castDoubleToString(wApp.getChannelNEO()));
                    break;
                case "GAS":
                    cardChainBalance.setText(UIStringUtil.castDoubleToString(wApp.getChainGAS()));
                    cardChannelBalance.setText(UIStringUtil.castDoubleToString(wApp.getChannelGAS()));
                    break;
            }
        }
    }

    private void refreshChannelUI() {
        int childCount = channelContainer.getChildCount();
        if (childCount > 1) {
            channelContainer.removeViews(1, childCount - 1);
        }

        List<ChannelBean> channelDesc = currentNetChannelDesc();
        if (channelDesc.size() == 0) {
            channelEmpty.setVisibility(View.VISIBLE);
            return;
        }

        channelEmpty.setVisibility(View.GONE);
        for (ChannelBean channelBean : channelDesc) {
            addChannelView(channelBean);
        }
    }

    private void refreshBillUI() {
        String net = wApp.getNet();
        List<Map<String, BillBean>> billList = wApp.getBillList();

        int childCount = billContainer.getChildCount();
        if (childCount > 1) {
            billContainer.removeViews(1, childCount - 1);
        }

        if (billList == null) {
            billEmpty.setVisibility(View.VISIBLE);
            return;
        }

        BillBean billBean;
        int nowNetRecordCount = 0;
        billEmpty.setVisibility(View.GONE);
        for (int index = billList.size() - 1; index >= 0; index--) {
            Map<String, BillBean> billBeanWithType = billList.get(index);
            Iterator<String> typeIterator = billBeanWithType.keySet().iterator();
            if (typeIterator.hasNext() && net.equals(typeIterator.next())) {
                nowNetRecordCount++;
                billBean = billBeanWithType.get(net);
                addBillView(billBean);
            }
        }

        if (nowNetRecordCount == 0) {
            billEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void addChannelView(@NonNull final ChannelBean channelBean) {
        View channelView = View.inflate(this, R.layout.tab_channel_item, null);
        TextView channelName = channelView.findViewById(R.id.channelName);
        TextView channelDeposit = channelView.findViewById(R.id.channelDeposit);
        final TextView channelBalance = channelView.findViewById(R.id.channelBalance);
        TextView channelState = channelView.findViewById(R.id.channelState);
        ImageButton channelClose = channelView.findViewById(R.id.channelClose);
        channelName.setText(channelBean.getAlias());
        channelDeposit.setText(getString(R.string.channel_deposit, BigDecimal.valueOf(channelBean.getDeposit()).toPlainString(), channelBean.getAssetName()));
        channelBalance.setText(getString(R.string.channel_balance, BigDecimal.valueOf(channelBean.getBalance()).toPlainString(), channelBean.getAssetName()));
        channelState.setText(getString(R.string.channel_state, channelBean.getState()));
        if (ConfigList.CHANNEL_STATE_HEATING.equals(channelBean.getState()) ||
                ConfigList.CHANNEL_STATE_COOLING.equals(channelBean.getState()) ||
                ConfigList.CHANNEL_STATE_DISMANTLED.equals(channelBean.getState())) {
            channelClose.setClickable(false);
        }
        if (ConfigList.CHANNEL_STATE_CLEAR.equals(channelBean.getState())) {
            channelClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    view.setClickable(false);
                    dismantleChannel(channelBean);
                }
            });
        }

        channelContainer.addView(channelView);
        channelEmpty.setVisibility(View.GONE);
    }

    private void addBillView(@NonNull BillBean billBean) {
        View billView = View.inflate(this, R.layout.tab_bill_item, null);
        TextView billChannelName = billView.findViewById(R.id.billChannelName);
        TextView billPrice = billView.findViewById(R.id.billPrice);
        TextView billFee = billView.findViewById(R.id.billFee);
        billChannelName.setText(billBean.getChannel_Alias());
        billPrice.setText(getString(R.string.bill_price, BigDecimal.valueOf(billBean.getPrice()).toPlainString(), billBean.getAssetName()));
        billFee.setText(getString(R.string.bill_fee, BigDecimal.valueOf(billBean.getFee()).toPlainString(), billBean.getAssetName()));

        billContainer.addView(billView);
        billEmpty.setVisibility(View.GONE);
    }

    private void switchUIPaymentCodeOrAddress(boolean isPaymentCode) {
        if (isPaymentCode) {
            layAmount.setVisibility(View.GONE);
            labelAssetsTrans.setVisibility(View.GONE);
            inputAssetsTrans.setVisibility(View.GONE);
        } else {
            layAmount.setVisibility(View.VISIBLE);
            labelAssetsTrans.setVisibility(View.VISIBLE);
            inputAssetsTrans.setVisibility(View.VISIBLE);
        }
    }

    /* ---------------------------------- WEB CONNECT METHODS ---------------------------------- */

    @NonNull
    private List<ChannelBean> currentNetChannelDesc() {
        String net = wApp.getNet();
        List<Map<String, ChannelBean>> channelList = wApp.getChannelList();

        List<ChannelBean> currentDesc = new ArrayList<>();
        if (channelList != null) {
            ChannelBean channelBeanFor;
            for (int index = channelList.size() - 1; index >= 0; index--) {
                Map<String, ChannelBean> channelBeanWithType = channelList.get(index);
                Iterator<String> typeIterator = channelBeanWithType.keySet().iterator();
                if (typeIterator.hasNext() && net.equals(typeIterator.next())) {
                    channelBeanFor = channelBeanWithType.get(net);
                    currentDesc.add(channelBeanFor);
                }
            }
        }

        return currentDesc;
    }

    private void connectChannels() {
        final Wallet wallet = wApp.getWallet();

        if (wallet == null) {
            return;
        }

        final List<ChannelBean> channelDesc = currentNetChannelDesc();

        runOffUiThread(new Runnable() {
            @Override
            public void run() {

                // Recycle socket.
                if (webSocketDesc.size() != 0) {
                    List<Map<ChannelBean, WebSocket>> toBeRecycled = new ArrayList<>();
                    ChannelBean connected;
                    for (Map<ChannelBean, WebSocket> webSocketMap : webSocketDesc) {
                        Iterator<ChannelBean> keySetIterator = webSocketMap.keySet().iterator();
                        if (keySetIterator.hasNext()) {
                            connected = keySetIterator.next();
                            for (ChannelBean there : channelDesc) {
                                if (connected == there) {
                                    toBeRecycled.add(webSocketMap);
                                    break;
                                }
                            }
                        }
                    }
                    if (toBeRecycled.size() != 0) {
                        for (Map<ChannelBean, WebSocket> recycle : toBeRecycled) {
                            webSocketDesc.remove(recycle);
                        }
                    }
                }

                // Set up socket.
                if (channelDesc.size() != 0) {
                    boolean shouldNew;
                    for (ChannelBean channelBean : channelDesc) {
                        shouldNew = true;
                        for (Map<ChannelBean, WebSocket> webSocketMap : webSocketDesc) {
                            Iterator<ChannelBean> keySetIterator = webSocketMap.keySet().iterator();
                            if (keySetIterator.hasNext() && keySetIterator.next() == channelBean) {
                                shouldNew = false;
                                break;
                            }
                        }
                        if (shouldNew) {
                            WebSocket socket = connect(channelBean, wallet);
                            HashMap<ChannelBean, WebSocket> added = new HashMap<>();
                            added.put(channelBean, socket);
                            webSocketDesc.add(added);
                        }
                    }
                }
            }
        });
    }

    // TODO Inverted R transaction(in another way, receive an R transaction).
    private WebSocket connect(ChannelBean channelBean, Wallet wallet) {
        String sTNAP = channelBean.getTNAP();
        String sTNAPSpv = TNAPUtil.getTNAPSpv(sTNAP, wallet);
        WebSocketClient webSocketClient = new WebSocketClient.Builder()
                .url(TNAPUtil.getWs(sTNAPSpv))
                .build();

        return webSocketClient.connect(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
            }
        });
    }

    private synchronized void obtainBalance() {
        final Wallet wallet = wApp.getWallet();

        if (wallet == null) {
            return;
        }

        Runnable service = new Runnable() {
            @Override
            public void run() {
                JSONRpcClient client = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrl())
                        .method("getBalance")
                        .params(wallet.getAddress())
                        .id("1")
                        .build();
                final String response = client.post();

                if (response == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(getBaseContext(), "Implicit problem exists.");
                        }
                    });
                    return;
                }
                GetBalanceBean responseBean = gson.fromJson(response, GetBalanceBean.class);
                // On chain value.
                wApp.setChainTNC(Double.valueOf(responseBean.getResult().getTncBalance()));
                wApp.setChainNEO(Double.valueOf(responseBean.getResult().getNeoBalance()));
                wApp.setChainGAS(Double.valueOf(responseBean.getResult().getGasBalance()));
                // On channel value.
                List<Map<String, ChannelBean>> channelList = wApp.getChannelList();
                if (channelList != null) {
                    double chainTNC = 0;
                    double chainNEO = 0;
                    double chainGAS = 0;
                    String net = wApp.getNet();
                    String assetName;
                    ChannelBean channelBean;
                    for (Map<String, ChannelBean> channelBeanWithType : channelList) {
                        Iterator<String> typeIterator = channelBeanWithType.keySet().iterator();
                        if (typeIterator.hasNext() && net.equals(typeIterator.next())) {
                            channelBean = channelBeanWithType.get(net);
                            assetName = channelBean.getAssetName();
                            switch (assetName) {
                                case ConfigList.ASSET_ID_MAP_KEY_TNC:
                                    chainTNC += channelBean.getBalance();
                                    break;
                                case ConfigList.ASSET_ID_MAP_KEY_NEO:
                                    chainNEO += channelBean.getBalance();
                                    break;
                                case ConfigList.ASSET_ID_MAP_KEY_GAS:
                                    chainGAS += channelBean.getBalance();
                                    break;
                            }
                        }
                    }
                    wApp.setChannelTNC(chainTNC);
                    wApp.setChannelNEO(chainNEO);
                    wApp.setChannelGAS(chainGAS);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshCardUI();
                    }
                });
            }
        };

        // We have 1000ms in one second on this planet and in current physics tide.
        double postInfoHowOld = (System.currentTimeMillis() - lastPostTimeMillis) / 1000d;
        if (postInfoHowOld > ConfigList.POST_TIME_AT_LEAST) {
            runOffUiThread(service);
            // Yes, it actually longer than what config list was recorded.
            // Because post dose have it's own time space.
            lastPostTimeMillis = System.currentTimeMillis();
        }
    }

    private synchronized void generatePaymentCode() {
        inputPaymentCodeAddress.setError(null);
        inputAmount.setError(null);

        Wallet wallet = wApp.getWallet();

        if (wallet == null) {
            ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + '.');
            return;
        }

        inputPaymentCodeAddress.setText(null);

        if (layAmount.getVisibility() == View.GONE) {
            switchUIPaymentCodeOrAddress(false);
            inputAmount.requestFocus();
            return;
        }

        String amountTrim = inputAmount.getText().toString().trim();

        Double amountDouble = Double.valueOf(amountTrim);
        if (amountDouble > 10000000000000d) {
            inputAmount.setError("Deposit at most 10000000000000.");
            inputAmount.requestFocus();
            return;
        }

        RadioButton checked = findViewById(inputAssetsTrans.getCheckedRadioButtonId());
        String assetName = checked.getText().toString().trim().toUpperCase(Locale.getDefault());

        boolean isCoinInteger = !amountTrim.contains(".");
        boolean isCoinDigitsValid = !isCoinInteger && (amountTrim.length() - amountTrim.indexOf(".")) <= ConfigList.COIN_DIGITS;

        boolean isCoinAmountOK = false;

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

        List<ChannelBean> channelDesc = currentNetChannelDesc();

        if (channelDesc.size() == 0) {
            ToastUtil.show(getBaseContext(), getString(R.string.set_up_channel_first));
            tab.setSelectedItemId(R.id.navigationSetupChannel);
            return;
        }

        ChannelBean oldestChannel = channelDesc.get(channelDesc.size() - 1);

        String sTNAP = oldestChannel.getTNAP();
        String sTNAPSpv = TNAPUtil.getTNAPSpv(sTNAP, wallet);

        PaymentCodeBean paymentCodeBean = new PaymentCodeBean();
        paymentCodeBean.setTNAP(sTNAPSpv);
        paymentCodeBean.setRandomHash(UUIDUtil.getRandomLowerNoLine());
        paymentCodeBean.setAssetId(PrefixUtil.trim0x(ConfigList.ASSET_ID_MAP.get(assetName)));
        paymentCodeBean.setPrice(amountDouble);
        paymentCodeBean.setComment("Trinity wallet generate.");

        final String encode = PaymentCodeUtil.encode(paymentCodeBean);
        if (encode == null) {
            ToastUtil.show(getBaseContext(), "Current charset may not be supported.");
            return;
        }
        runOffUiThread(new Runnable() {
            private CountDownLatch latch_UI_PaymentQR_Shown = new CountDownLatch(1);

            @Override
            public void run() {
                final Bitmap paymentBitmap = QRCodeUtil.encodeAsBitmap(encode, ConfigList.QR_CODE_WIDTH, ConfigList.QR_CODE_HEIGHT);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        paymentCodeQR.setImageBitmap(paymentBitmap);
                        paymentCodeQR.requestLayout();
                        latch_UI_PaymentQR_Shown.countDown();
                    }
                });
                try {
                    latch_UI_PaymentQR_Shown.await();
                } catch (InterruptedException ignored) {
                    ToastUtil.show(getBaseContext(), "Too much works in background, please try again later.");
                }

                paymentCodeQR.post(new Runnable() {
                    @Override
                    public void run() {
                        tabsContainer.fullScroll(ScrollView.FOCUS_DOWN);
                        paymentCodeQR.setClickable(true);
                    }
                });
            }
        });
    }

    private synchronized void verifyAddress(final boolean doPay) {
        inputPaymentCodeAddress.setError(null);
        inputAmount.setError(null);
        IMEUtil.hideIME(btnPay);

        final Wallet wallet = wApp.getWallet();

        if (doPay && layAmount.getVisibility() == View.GONE) {
            ToastUtil.show(getBaseContext(), "Verifying input.");
            if (wallet == null) {
                ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + '.');
                return;
            }
        }
        obtainBalance();

        final String toAddressTrim = inputPaymentCodeAddress.getText().toString().trim();
        if ("".equals(toAddressTrim) || wallet.getAddress().equals(toAddressTrim)) {
            if (doPay) {
                inputPaymentCodeAddress.setError("Invalid input.");
                inputPaymentCodeAddress.requestFocus();
                payCodeMode(true);
            }
            return;
        }

        boolean isValid = Neoutils.validateNEOAddress(toAddressTrim);

        if (!isValid) {
            payCodeMode(doPay);
            return;
        }

        // If not address mode now, show address mode view.
        if (layAmount.getVisibility() == View.GONE) {
            switchUIPaymentCodeOrAddress(false);

            if (doPay) {
                ToastUtil.show(getBaseContext(), "Address verified.");
                inputAmount.requestFocus();
            }
            return;
        }

        // If button calls and address mode view now, pay.
        if (doPay) {
            addressMode(toAddressTrim);
        }
    }

    private void payCodeMode(boolean doPay) {
        switchUIPaymentCodeOrAddress(true);

        if (!doPay) {
            return;
        }

        final Boolean isValidPayCode = attemptHRTransaction();

        if (!isValidPayCode) {
            inputPaymentCodeAddress.setError("Invalid input.");
            inputPaymentCodeAddress.requestFocus();
        }
    }

    /**
     * @return true: Is a payment code. false: Not a payment code.
     */
    private boolean attemptHRTransaction() {
        final Wallet wallet = wApp.getWallet();
        final String net = wApp.getNet();
        final List<ChannelBean> channelDesc = currentNetChannelDesc();
        final String magic = WalletApplication.getMagic();
        final Map<String, String> assetIdMap = ConfigList.ASSET_ID_MAP;

        if (wallet == null) {
            ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + ".");
            return true;
        }

        String toAddress = inputPaymentCodeAddress.getText().toString();
        final String paymentCodeTrim = toAddress.trim();
        final PaymentCodeBean paymentCodeBean = PaymentCodeUtil.decode(paymentCodeTrim);

        if (paymentCodeBean == null) {
            return false;
        }

        if (channelDesc.size() == 0) {
            ToastUtil.show(getBaseContext(), "Please add a channel.");
            return true;
        }

        final String sTNAPPayCode = paymentCodeBean.getTNAP();

        if (!TNAPUtil.isValid(sTNAPPayCode)) {
            inputPaymentCodeAddress.setError("Invalid input.");
            inputPaymentCodeAddress.requestFocus();
            return false;
        }

        String assetNameFor = null;
        for (String assetNameInMap : assetIdMap.keySet()) {
            String add0x = PrefixUtil.add0x(paymentCodeBean.getAssetId());
            if (assetIdMap.get(assetNameInMap).equals(add0x)) {
                assetNameFor = assetNameInMap;
                break;
            }
        }

        if (assetNameFor == null) {
            ToastUtil.show(getBaseContext(), "Unsupported coin type.");
            return true;
        }

        final String assetName = assetNameFor;

        ChannelBean channelBean = null;
        for (ChannelBean channelBeanFor : channelDesc) {
            if (channelBeanFor.getTNAP().equals(sTNAPPayCode) && PrefixUtil.trim0x(assetIdMap.get(channelBeanFor.getAssetName())).equals(paymentCodeBean.getAssetId())) {
                channelBean = channelBeanFor;
                break;
            }
        }

        btnPay.setClickable(false);
        final ChannelBean channelBeanFinal = channelBean;
        runOffUiThread(new Runnable() {
            private transient double price;
            private transient int txNoncePp;
            private transient ChannelBean channelBeanR;
            private String sTNAPR;
            private String sTNAPSpvR;

            private double feeCount;
            private boolean balanceOk;
            private CountDownLatch latch_UI = new CountDownLatch(1);
            private CountDownLatch latch_H = new CountDownLatch(1);

            // H.
            private ACGetRouterInfoBean req_1;
            private ACAckRouterInfoBean resp_2;
            private JSONRpcClient rpc_3;
            private HTLCTransactionBean resp_4;
            private ACHtlcBean req_5;
            private ACHtlcSignBean resp_6;
            private ACHtlcBean resp_7;
            private ACHtlcSignBean req_8;
            private ACRResponseBean resp_9;

            // R.
            private JSONRpcClient rpc_11;
            private FunderTransactionBean resp_12;
            private ACRsmcBean req_13;
            private ACRsmcSignBean resp_14;
            private ACRsmcBean resp_15;
            private ACRsmcSignBean req_16;
            private ACRsmcBean req_17;
            private ACRsmcBean resp_18;

            @Override
            public void run() {
                if (channelBeanFinal == null) {
                    attemptHTransaction();
                } else {
                    channelBeanR = channelBeanFinal;
                    latch_H.countDown();
                }

                try {
                    latch_H.await();
                } catch (InterruptedException ignored) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(getBaseContext(), "Too much works in background, please try again later.");
                            btnPay.setClickable(true);
                        }
                    });
                    return;
                }

                if (channelBeanR != null && balanceOk) {
                    attemptRTransaction();
                }
            }

            private void attemptHTransaction() {
                final ChannelBean oldestHChannelFinal = channelDesc.get(channelDesc.size() - 1);

                oldestHChannelFinal.setTxNonce(oldestHChannelFinal.getTxNonce() + 1);

                final String sTNAPH = oldestHChannelFinal.getTNAP();
                final String sTNAPSpvH = TNAPUtil.getTNAPSpv(sTNAPH, wallet);

                WebSocketClient webSocketClient = new WebSocketClient.Builder()
                        .url(TNAPUtil.getWs(sTNAPSpvH))
                        .build();

                webSocketClient.connect(new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                        req_1 = new ACGetRouterInfoBean();
                        req_1.setSender(sTNAPSpvH);
                        req_1.setReceiver(sTNAPPayCode);
                        req_1.setMagic(magic);
                        ACGetRouterInfoBean.MessageBodyBean messageBody_1 = new ACGetRouterInfoBean.MessageBodyBean();
                        messageBody_1.setAssetType(assetName);
                        List<String> nodeList_1 = new ArrayList<>();
                        for (ChannelBean node : channelDesc) {
                            nodeList_1.add(node.getTNAP());
                        }
                        messageBody_1.setNodeList(nodeList_1);
                        req_1.setMessageBody(messageBody_1);

                        String send_1 = gson.toJson(req_1);
                        webSocket.send(send_1);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        super.onMessage(webSocket, text);
                        String messageTypeStr = JSONObjectUtil.getMessageType(text);

                        if ("HtlcFail".equals(messageTypeStr)) {
                            JsonObject htlcFail = gson.fromJson(text, JsonObject.class);
                            final String error = htlcFail.get("Error").getAsString();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String errorShow = error;

                                    if (errorShow != null && errorShow.length() > 0) {
                                        String last = errorShow.substring(errorShow.length() - 1, errorShow.length());
                                        if (!last.equals(".")) {
                                            errorShow = errorShow + ".";
                                        }
                                    } else {
                                        errorShow = "Unknown payment code info error.";
                                    }

                                    ToastUtil.show(getBaseContext(), errorShow);
                                    btnPay.setClickable(true);
                                }
                            });
                            webSocket.close(1000, null);
                            return;
                        }

                        if ("AckRouterInfo".equals(messageTypeStr)) {
                            resp_2 = gson.fromJson(text, ACAckRouterInfoBean.class);

                            String nextTNAP = resp_2.getRouterInfo().getNext();

                            if (nextTNAP == null || "".equals(nextTNAP)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.show(getBaseContext(), "No channel reaches the target trinity node.");
                                        btnPay.setClickable(true);
                                    }
                                });
                                webSocket.close(1000, null);
                                return;
                            }

                            ChannelBean nextChannel = null;
                            for (ChannelBean mayNext : channelDesc) {
                                if (mayNext.getTNAP().equals(nextTNAP)) {
                                    nextChannel = mayNext;
                                    break;
                                }
                            }
                            if (nextChannel == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.show(getBaseContext(), "No channel reaches the target trinity node.");
                                        btnPay.setClickable(true);
                                    }
                                });
                                return;
                            }

                            channelBeanR = nextChannel;

                            List<List<String>> fullPath = resp_2.getRouterInfo().getFullPath();
                            feeCount = 0d;
                            for (int index = 0; index < fullPath.size() - 1; index++) {
                                List<String> sTNAP_Fee_Pair_Thing = fullPath.get(index);
                                feeCount = BigDecimalUtil.add(feeCount, Double.valueOf(sTNAP_Fee_Pair_Thing.get(1)));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    balanceOk = checkChannelBalance();
                                    latch_UI.countDown();
                                }
                            });

                            try {
                                latch_UI.await();
                            } catch (InterruptedException ignored) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.show(getBaseContext(), "Too much works in background, please try again later.");
                                        btnPay.setClickable(true);
                                    }
                                });
                                return;
                            }
                            if (!balanceOk) {
                                return;
                            }

                            rpc_3 = new JSONRpcClient.Builder()
                                    .net(WalletApplication.getNetUrl())
                                    .method("HTLCTransaction")
                                    .params(TNAPUtil.getPublicKey(sTNAPSpvH),
                                            TNAPUtil.getPublicKey(sTNAPPayCode),
                                            String.valueOf(paymentCodeBean.getPrice()),
                                            String.valueOf(oldestHChannelFinal.getBalance()),
                                            String.valueOf(BigDecimalUtil.subtract(BigDecimalUtil.add(oldestHChannelFinal.getDeposit(), oldestHChannelFinal.getDeposit()), oldestHChannelFinal.getBalance())),
                                            paymentCodeBean.getRandomHash(),
                                            oldestHChannelFinal.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getAddressFunding(),
                                            oldestHChannelFinal.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getScriptFunding(),
                                            oldestHChannelFinal.getAssetName())
                                    .id(wallet.getAddress() + paymentCodeBean.getTNAP() + "H")
                                    .build();

                            String json_4 = rpc_3.post();

                            if (json_4 == null) {
                                webSocket.cancel();
                            }

                            resp_4 = gson.fromJson(json_4, HTLCTransactionBean.class);

                            req_5 = new ACHtlcBean();
                            req_5.setChannelName(oldestHChannelFinal.getName());
                            req_5.setSender(sTNAPSpvH);
                            req_5.setReceiver(sTNAPH);
                            req_5.setRouter(resp_2.getRouterInfo().getFullPath());
                            req_5.setNext(resp_2.getRouterInfo().getNext());
                            req_5.setTxNonce(oldestHChannelFinal.getTxNonce());
                            ACHtlcBean.MessageBodyBean messageBody_5 = gson.fromJson(gson.toJson(resp_4.getResult()), ACHtlcBean.MessageBodyBean.class);
                            messageBody_5.setAssetType(oldestHChannelFinal.getAssetName());
                            messageBody_5.setComments(paymentCodeBean.getComment());
                            messageBody_5.setCount(BigDecimalUtil.add(price, feeCount));
                            messageBody_5.setHashR(paymentCodeBean.getRandomHash());
                            messageBody_5.setRoleIndex(0);
                            req_5.setMessageBody(messageBody_5);

                            String send_5 = gson.toJson(req_5);
                            webSocket.send(send_5);
                            return;
                        }

                        if ("HtlcSign".equals(messageTypeStr)) {
                            resp_6 = gson.fromJson(text, ACHtlcSignBean.class);
                            return;
                        }

                        if ("Htlc".equals(messageTypeStr)) {
                            resp_7 = gson.fromJson(text, ACHtlcBean.class);

                            req_8 = new ACHtlcSignBean();
                            req_8.setChannelName(oldestHChannelFinal.getName());
                            req_8.setSender(sTNAPSpvH);
                            req_8.setReceiver(sTNAPH);
                            req_8.setRouter(resp_2.getRouterInfo().getFullPath());
                            req_8.setTxNonce(resp_7.getTxNonce());
                            ACHtlcSignBean.MessageBodyBean messageBody_8 = new ACHtlcSignBean.MessageBodyBean();
                            messageBody_8.setAssetType(resp_7.getMessageBody().getAssetType());
                            messageBody_8.setCount(resp_7.getMessageBody().getCount());
                            messageBody_8.setHashR(resp_7.getMessageBody().getHashR());
                            messageBody_8.setRoleIndex(resp_7.getMessageBody().getRoleIndex());
                            ACHtlcSignBean.MessageBodyBean.HCTXBean hctx_8 = new ACHtlcSignBean.MessageBodyBean.HCTXBean();
                            hctx_8.setOriginalData(gson.fromJson(gson.toJson(resp_7.getMessageBody().getHCTX()), ACHtlcSignBean.MessageBodyBean.HCTXBean.OriginalDataBean.class));
                            hctx_8.setTxDataSign(NeoSignUtil.signToHex(resp_7.getMessageBody().getHCTX().getTxData(), wallet.getPrivateKey()));
                            messageBody_8.setHCTX(hctx_8);
                            ACHtlcSignBean.MessageBodyBean.HTDTXBean htdtx_8 = new ACHtlcSignBean.MessageBodyBean.HTDTXBean();
                            htdtx_8.setOriginalData(gson.fromJson(gson.toJson(resp_7.getMessageBody().getHTDTX()), ACHtlcSignBean.MessageBodyBean.HTDTXBean.OriginalDataBeanXX.class));
                            htdtx_8.setTxDataSign(NeoSignUtil.signToHex(resp_7.getMessageBody().getHTDTX().getTxData(), wallet.getPrivateKey()));
                            messageBody_8.setHTDTX(htdtx_8);
                            ACHtlcSignBean.MessageBodyBean.RDTXBean rdtx_8 = new ACHtlcSignBean.MessageBodyBean.RDTXBean();
                            rdtx_8.setOriginalData(gson.fromJson(gson.toJson(resp_7.getMessageBody().getRDTX()), ACHtlcSignBean.MessageBodyBean.RDTXBean.OriginalDataBeanX.class));
                            rdtx_8.setTxDataSign(NeoSignUtil.signToHex(resp_7.getMessageBody().getRDTX().getTxData(), wallet.getPrivateKey()));
                            messageBody_8.setRDTX(rdtx_8);
                            req_8.setMessageBody(messageBody_8);

                            String send_8 = gson.toJson(req_8);
                            webSocket.send(send_8);
                            return;
                        }

                        if ("RResponse".equals(messageTypeStr)) {
                            resp_9 = gson.fromJson(text, ACRResponseBean.class);
                            for (ChannelBean mayR : channelDesc) {
                                if (mayR.getTNAP().equals(resp_2.getRouterInfo().getNext())) {
                                    channelBeanR = mayR;
                                    break;
                                }
                            }
                            webSocket.close(1000, null);
                            latch_H.countDown();
                        }
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                        super.onFailure(webSocket, t, response);
                    }
                });
            }

            private boolean checkChannelBalance() {
                price = paymentCodeBean.getPrice();
                String priceTrim = BigDecimal.valueOf(price).toPlainString();

                if (price > 10000000000000d) {
                    ToastUtil.show(getBaseContext(), "Payment code info error.\nPrice is 10000000000000 at most.");
                    return false;
                }

                boolean isCoinInteger = !priceTrim.contains(".");
                boolean isCoinDigitsValid = !isCoinInteger && (priceTrim.length() - priceTrim.indexOf(".")) <= ConfigList.COIN_DIGITS;

                boolean isCoinPriceOK = false;

                double priceAndFee = BigDecimalUtil.add(price, feeCount);
                BigDecimal priceAndFeeBigDecimal = BigDecimal.valueOf(priceAndFee);
                if (getString(R.string.tnc).equals(assetName)) {
                    if (isCoinInteger || isCoinDigitsValid) {
                        isCoinPriceOK = true;
                    } else {
                        ToastUtil.show(getBaseContext(), "Payment code info error.\nTNC balance is a decimal up to 8 digits.");
                    }
                } else if (getString(R.string.neo).equals(assetName)) {
                    if (isCoinInteger) {
                        isCoinPriceOK = true;
                    } else {
                        ToastUtil.show(getBaseContext(), "Payment code info error.\nNEO balance is a integer.");
                    }
                } else if (getString(R.string.gas).equals(assetName)) {
                    if (isCoinInteger || isCoinDigitsValid) {
                        isCoinPriceOK = true;
                    } else {
                        ToastUtil.show(getBaseContext(), "Payment code info error.\nGAS balance is a decimal up to 8 digits.");
                    }
                }

                if (!isCoinPriceOK) {
                    ToastUtil.show(getBaseContext(), "No channel can afford this price.");
                    btnPay.setClickable(true);
                    return false;
                }

                boolean isPriceAffordable = priceAndFeeBigDecimal.compareTo(BigDecimal.valueOf(channelBeanR.getBalance())) <= 0;
                if (!isPriceAffordable) {
                    ToastUtil.show(getBaseContext(), "No channel can afford this price and fee.");
                    btnPay.setClickable(true);
                    return false;
                }

                channelBeanR.setTxNonce(channelBeanR.getTxNonce() + 1);
                // ++ is called pp.
                txNoncePp = channelBeanR.getTxNonce();

                return true;
            }

            private void attemptRTransaction() {
                sTNAPR = channelBeanR.getTNAP();
                sTNAPSpvR = TNAPUtil.getTNAPSpv(sTNAPR, wallet);
                final double priceAndFee = BigDecimalUtil.add(price, feeCount);

                WebSocketClient webSocketClient = new WebSocketClient.Builder()
                        .url(TNAPUtil.getWs(sTNAPSpvR))
                        .build();

                webSocketClient.connect(new WebSocketListener() {
                    @Override
                    public void onOpen(final WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                        rpc_11 = new JSONRpcClient.Builder()
                                .net(WalletApplication.getNetUrl())
                                .method("FunderTransaction")
                                .params(TNAPUtil.getPublicKey(sTNAPR),
                                        TNAPUtil.getPublicKey(sTNAPSpvR),
                                        channelBeanR.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getAddressFunding(),
                                        channelBeanR.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getScriptFunding(),
                                        String.valueOf(channelBeanR.getBalance()),
                                        String.valueOf(channelBeanR.getDeposit() + channelBeanR.getDeposit() - channelBeanR.getBalance()),
                                        channelBeanR.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getTxId(),
                                        channelBeanR.getAssetName())
                                .id(channelBeanR.getName() + txNoncePp)
                                .build();

                        String json_12 = rpc_11.post();

                        if (json_12 == null) {
                            webSocket.cancel();
                            return;
                        }

                        resp_12 = gson.fromJson(json_12, FunderTransactionBean.class);

                        req_13 = new ACRsmcBean();
                        req_13.setSender(sTNAPSpvR);
                        req_13.setReceiver(sTNAPR);
                        req_13.setChannelName(channelBeanR.getName());
                        req_13.setTxNonce(txNoncePp);
                        ACRsmcBean.MessageBodyBean messageBody_13 = new ACRsmcBean.MessageBodyBean();
                        messageBody_13.setCommitment(gson.fromJson(gson.toJson(resp_12.getResult().getC_TX()), ACRsmcBean.MessageBodyBean.CommitmentBean.class));
                        messageBody_13.setRevocableDelivery(gson.fromJson(gson.toJson(resp_12.getResult().getR_TX()), ACRsmcBean.MessageBodyBean.RevocableDeliveryBean.class));
                        messageBody_13.setAssetType(channelBeanR.getAssetName());
                        messageBody_13.setValue(priceAndFee);
                        messageBody_13.setRoleIndex(0);
                        messageBody_13.setComments(paymentCodeBean.getComment());
                        req_13.setMessageBody(messageBody_13);

                        String send_13 = gson.toJson(req_13);
                        webSocket.send(send_13);
                    }

                    @Override
                    public void onMessage(final WebSocket webSocket, final String text) {
                        super.onMessage(webSocket, text);
                        String messageTypeStr = JSONObjectUtil.getMessageType(text);

                        if ("RsmcSign".equals(messageTypeStr)) {
                            resp_14 = gson.fromJson(text, ACRsmcSignBean.class);
                            return;
                        }
                        if ("Rsmc".equals(messageTypeStr)) {
                            ACRsmcBean tmp = gson.fromJson(text, ACRsmcBean.class);
                            // case BR empty: save to r15.
                            if (tmp.getMessageBody().getBreachRemedy() == null) {
                                resp_15 = tmp;

                                req_16 = new ACRsmcSignBean();
                                req_16.setChannelName(resp_15.getChannelName());
                                req_16.setSender(sTNAPSpvR);
                                req_16.setReceiver(sTNAPR);
                                req_16.setTxNonce(resp_15.getTxNonce());
                                ACRsmcSignBean.MessageBodyBean messageBody_16 = new ACRsmcSignBean.MessageBodyBean();
                                messageBody_16.setValue(resp_15.getMessageBody().getValue());
                                messageBody_16.setComments(resp_15.getMessageBody().getComments());
                                messageBody_16.setRoleIndex(resp_15.getMessageBody().getRoleIndex());
                                ACRsmcSignBean.MessageBodyBean.CommitmentBean commitment_16 = new ACRsmcSignBean.MessageBodyBean.CommitmentBean();
                                commitment_16.setOriginalData(gson.fromJson(gson.toJson(resp_15.getMessageBody().getCommitment()), ACRsmcSignBean.MessageBodyBean.CommitmentBean.OriginalDataBean.class));
                                commitment_16.setTxDataSign(NeoSignUtil.signToHex(resp_15.getMessageBody().getCommitment().getTxData(), wallet.getPrivateKey()));
                                messageBody_16.setCommitment(commitment_16);
                                ACRsmcSignBean.MessageBodyBean.RevocableDeliveryBean revocableDelivery_16 = new ACRsmcSignBean.MessageBodyBean.RevocableDeliveryBean();
                                revocableDelivery_16.setOriginalData(gson.fromJson(gson.toJson(resp_15.getMessageBody().getRevocableDelivery()), ACRsmcSignBean.MessageBodyBean.RevocableDeliveryBean.OriginalDataBeanX.class));
                                revocableDelivery_16.setTxDataSign(NeoSignUtil.signToHex(resp_15.getMessageBody().getRevocableDelivery().getTxData(), wallet.getPrivateKey()));
                                messageBody_16.setRevocableDelivery(revocableDelivery_16);
                                req_16.setMessageBody(messageBody_16);

                                String send_16 = gson.toJson(req_16);
                                webSocket.send(send_16);

                                req_17 = new ACRsmcBean();
                                req_17.setChannelName(resp_15.getChannelName());
                                req_17.setSender(sTNAPSpvR);
                                req_17.setReceiver(sTNAPR);
                                req_17.setTxNonce(resp_15.getTxNonce());
                                ACRsmcBean.MessageBodyBean messageBody_17 = new ACRsmcBean.MessageBodyBean();
                                messageBody_17.setAssetType(resp_15.getMessageBody().getAssetType());
                                messageBody_17.setValue(resp_15.getMessageBody().getValue());
                                messageBody_17.setComments(resp_15.getMessageBody().getComments());
                                messageBody_17.setRoleIndex(resp_15.getMessageBody().getRoleIndex() + 1);
                                ACRsmcBean.MessageBodyBean.BreachRemedyBean breachRemedy_17 = new ACRsmcBean.MessageBodyBean.BreachRemedyBean();
                                breachRemedy_17.setOriginalData(gson.fromJson(gson.toJson(resp_12.getResult().getBR_TX()), ACRsmcBean.MessageBodyBean.BreachRemedyBean.OriginalDataBean.class));
                                breachRemedy_17.setTxDataSign(NeoSignUtil.signToHex(resp_12.getResult().getBR_TX().getTxData(), wallet.getPrivateKey()));
                                messageBody_17.setBreachRemedy(breachRemedy_17);
                                req_17.setMessageBody(messageBody_17);

                                String send_17 = gson.toJson(req_17);
                                webSocket.send(send_17);
                            }
                            // case BR no empty save to r18.
                            else {
                                resp_18 = tmp;
                            }
                            return;
                        }
                        if ("UpdateChannel".equals(messageTypeStr)) {
                            double spvBalance = JSONObjectUtil.updateChannelGetSpvBalance(text, sTNAPSpvR, channelBeanR.getAssetName());

                            List<Map<String, BillBean>> billList = wApp.getBillList();
                            if (billList == null) {
                                billList = new ArrayList<>();
                            }
                            Map<String, BillBean> billBeanWithNetType = new HashMap<>();
                            billBeanWithNetType.put(net, new BillBean(
                                    channelBeanR,
                                    BigDecimalUtil.subtract(spvBalance, channelBeanR.getBalance()),
                                    feeCount,
                                    resp_14,
                                    resp_18,
                                    resp_6));

                            billList.add(billBeanWithNetType);
                            channelBeanR.setBalance(spvBalance);

                            wApp.setBillList(billList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    obtainBalance();
                                    refreshUITotal();
                                    tab.setSelectedItemId(R.id.navigationBill);
                                    inputPaymentCodeAddress.setText(null);
                                    inputAmount.setText(null);
                                    btnPay.setClickable(true);
                                    ToastUtil.show(getBaseContext(), "Channel payment admitted by the trinity node(not on the block chain yet).");
                                }
                            });
                            webSocket.close(1000, null);
                        }
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                        super.onFailure(webSocket, t, response);
                        t.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnPay.setClickable(true);
                                ToastUtil.show(getBaseContext(), "Implicit problem exists.");
                            }
                        });
                    }
                });
            }
        });


        return true;
    }

    private void addressMode(@NonNull String toAddressStr) {
        final String amountTrim = inputAmount.getText().toString().trim();
        if ("".equals(amountTrim)) {
            inputAmount.setError("Please input amount.");
            inputAmount.requestFocus();
            return;
        }

        Double amountDouble = Double.valueOf(amountTrim);
        if (amountDouble > 10000000000000d) {
            inputAmount.setError("Deposit at most 10000000000000.");
            inputAmount.requestFocus();
            return;
        }

        RadioButton checked = findViewById(inputAssetsTrans.getCheckedRadioButtonId());
        String assetName = checked.getText().toString().trim().toUpperCase(Locale.getDefault());

        boolean isCoinInteger = !amountTrim.contains(".");
        boolean isCoinDigitsValid = !isCoinInteger && (amountTrim.length() - amountTrim.indexOf(".")) <= ConfigList.COIN_DIGITS;

        boolean isCoinAmountOK = false;
        boolean isAmountAffordable = false;

        BigDecimal amountBigDecimal = BigDecimal.valueOf(amountDouble);
        if (getString(R.string.tnc).equals(assetName)) {
            if (isCoinInteger || isCoinDigitsValid) {
                isCoinAmountOK = true;
            } else {
                inputAmount.setError("TNC balance is a decimal up to 8 digits.");
            }
            isAmountAffordable = amountBigDecimal.compareTo(BigDecimal.valueOf(wApp.getChainTNC())) <= 0;
        } else if (getString(R.string.neo).equals(assetName)) {
            if (isCoinInteger) {
                isCoinAmountOK = true;
            } else {
                inputAmount.setError("NEO balance is a integer.");
            }
            isAmountAffordable = amountBigDecimal.compareTo(BigDecimal.valueOf(wApp.getChainNEO())) <= 0;
        } else if (getString(R.string.gas).equals(assetName)) {
            if (isCoinInteger || isCoinDigitsValid) {
                isCoinAmountOK = true;
            } else {
                inputAmount.setError("GAS balance is a decimal up to 8 digits.");
            }
            isAmountAffordable = amountBigDecimal.compareTo(BigDecimal.valueOf(wApp.getChainGAS())) <= 0;
        }

        if (!isCoinAmountOK) {
            inputAmount.requestFocus();
            return;
        }

        if (!isAmountAffordable) {
            inputAmount.setError("Balance of current asset is not enough.");
            inputAmount.requestFocus();
            return;
        }

        ToastUtil.show(getBaseContext(), "Doing addressMode.\nIt takes a very short time.");
        btnPay.setClickable(false);
        attemptPayAddress(toAddressStr, amountTrim, assetName);
    }

    private void attemptPayAddress(@NonNull final String validToAddress, @NonNull final String validAmount, @NonNull final String assetName) {
        // Send post to addressMode to address.
        final Wallet wallet = wApp.getWallet();
        if (wallet == null) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // Toast please login first.
                            ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + '.');
                            btnPay.setClickable(true);
                        }
                    }
            );
            return;
        }
        Runnable service = new Runnable() {
            @Override
            public void run() {
                String myAddress = wallet.getAddress();
                JSONRpcClient rpc_1 = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrl())
                        .method("constructTx")
                        .params(myAddress, validToAddress, validAmount, ConfigList.ASSET_ID_MAP.get(assetName))
                        .id(validToAddress + validAmount + assetName)
                        .build();
                final String json_2 = rpc_1.post();

                if (json_2 == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(getBaseContext(), "Implicit problem exists.");
                            btnPay.setClickable(true);
                        }
                    });
                    return;
                }

                final ConstructTxBean resp_2 = gson.fromJson(json_2, ConstructTxBean.class);
                String txData = resp_2.getResult().getTxData();
                String witness = resp_2.getResult().getWitness();
                String txid = resp_2.getResult().getTxid();
                if (txData == null || witness == null || txid == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(getBaseContext(), "Implicit problem exists.");
                            btnPay.setClickable(true);
                        }
                    });
                    return;
                }

                String sign = NeoSignUtil.signToHex(txData, wallet.getPrivateKey());
                String publicKeyHex = HexUtil.byteArrayToHex(wallet.getPublicKey());
                String replacedWitness = witness.replace("{signature}", sign).replace("{pubkey}", publicKeyHex);

                JSONRpcClient rpc_3 = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrlForNEO())
                        .method("sendrawtransaction")
                        .params(txData + replacedWitness)
                        .id(txid)
                        .build();
                String json_4 = rpc_3.post();

                if (json_4 == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(getBaseContext(), "Implicit problem exists.");
                            btnPay.setClickable(true);
                        }
                    });
                    return;
                }

                final SendrawtransactionBean resp_4 = gson.fromJson(json_4, SendrawtransactionBean.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!resp_4.isResult()) {
                            btnPay.setClickable(true);
                            ToastUtil.show(getBaseContext(), "Implicit problem exists.");
                            return;
                        }
                        ToastUtil.show(getBaseContext(), "Transfer succeed.\nBlock chain confirms in seconds.");
                        btnPay.setClickable(true);
                    }
                });
            }
        };
        runOffUiThread(service);
    }

    private synchronized void attemptSetupChannel() {
        inputTNAP.setError(null);
        inputDeposit.setError(null);
        inputAlias.setError(null);

        final Wallet wallet = wApp.getWallet();
        final String net = wApp.getNet();

        if (wallet == null) {
            ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + '.');
            return;
        }

        obtainBalance();

        // Check the TNAP input.
        final String sTNAPTrim = inputTNAP.getText().toString().toLowerCase().trim();
        if (!TNAPUtil.isValid(sTNAPTrim)) {
            inputTNAP.setError("Invalid input.");
            inputTNAP.requestFocus();
            return;
        }
        final String sTNAPSpv = TNAPUtil.getTNAPSpv(sTNAPTrim, wallet);

        ChannelBean channelBean;
        final List<Map<String, ChannelBean>> channelList = wApp.getChannelList();
        if (channelList != null) {
            for (Map<String, ChannelBean> channelBeanWithNetType : channelList) {
                for (String channelListNet : channelBeanWithNetType.keySet()) {
                    channelBean = channelBeanWithNetType.get(channelListNet);
                    if (net.equals(channelListNet) && sTNAPTrim.equals(channelBean.getTNAP())) {
                        inputTNAP.setError("Already have one channel on this trinity node.");
                        inputTNAP.requestFocus();
                        return;
                    }
                }
            }
        }

        // Check the deposit input.
        final String depositTrim = inputDeposit.getText().toString().trim();

        if ("".equals(depositTrim)) {
            inputDeposit.setError("Please input deposit.");
            inputDeposit.requestFocus();
            return;
        }
        Double depositDouble = Double.valueOf(depositTrim);
        if (depositDouble < 1d) {
            inputDeposit.setError("Deposit at least 1 when add channel.");
            inputDeposit.requestFocus();
            return;
        }

        if (depositDouble > 10000000000000d) {
            inputDeposit.setError("Deposit at most 10000000000000.");
            inputDeposit.requestFocus();
            return;
        }

        int radioButtonId = inputAssets.getCheckedRadioButtonId();
        RadioButton radioChecked = MainActivity.this.findViewById(radioButtonId);
        final String assetName = radioChecked.getText().toString().trim();
        boolean isCoinInteger = !depositTrim.contains(".");
        boolean isCoinDigitsValid = !isCoinInteger && (depositTrim.length() - 1 - depositTrim.indexOf(".")) <= ConfigList.COIN_DIGITS;
        boolean isCoinAmountOK = false;
        BigDecimal depositBigDecimal = BigDecimal.valueOf(depositDouble);

        boolean isDepositAffordable = false;

        if (getString(R.string.tnc).equals(assetName)) {
            if (isCoinInteger || isCoinDigitsValid) {
                isCoinAmountOK = true;
            } else {
                inputDeposit.setError("TNC balance is a decimal up to 8 digits.");
            }
            isDepositAffordable = depositBigDecimal.compareTo(BigDecimal.valueOf(wApp.getChainTNC())) <= 0;
        } else if (getString(R.string.neo).equals(assetName)) {
            if (isCoinInteger) {
                isCoinAmountOK = true;
            } else {
                inputDeposit.setError("NEO balance is a integer.");
            }
            isDepositAffordable = depositBigDecimal.compareTo(BigDecimal.valueOf(wApp.getChainNEO())) <= 0;
        } else if (getString(R.string.gas).equals(assetName)) {
            if (isCoinInteger || isCoinDigitsValid) {
                isCoinAmountOK = true;
            } else {
                inputDeposit.setError("GAS balance is a decimal up to 8 digits.");
            }
            isDepositAffordable = depositBigDecimal.compareTo(BigDecimal.valueOf(wApp.getChainGAS())) <= 0;
        }

        if (!isCoinAmountOK) {
            inputDeposit.requestFocus();
            return;
        }

        if (!isDepositAffordable) {
            inputDeposit.setError("Balance of current asset is not enough.");
            inputDeposit.requestFocus();
            return;
        }

        final String aliasTrim = inputAlias.getText().toString().trim();

        if ("".equals(aliasTrim)) {
            inputAlias.setError("Alias is required.");
            inputAlias.requestFocus();
            return;
        }

        if (aliasTrim.length() > 75) {
            inputAlias.setError("Alias is too long(more than 75).");
            inputAlias.requestFocus();
            return;
        }

        ToastUtil.show(getBaseContext(), "Doing add channel.\nIt takes a very short time.");

        final WebSocketClient webSocketClient = new WebSocketClient.Builder()
                .url(TNAPUtil.getWs(sTNAPSpv))
                .build();

        webSocketClient.connect(new WebSocketListener() {
            private ChannelBean channelBeanHeating;

            private ACRegisterChannelBean req_1;
            private ACFounderBean resp_2;
            private ACFounderSignBean req_3;
            private JSONRpcClient rpc_4;
            private FunderTransactionBean resp_5;
            private ACFounderBean req_6;
            private ACFounderSignBean resp_7;
            private JSONRpcClient rpc_8;
            private SendrawtransactionBean resp_9;

            @Override
            public void onOpen(final WebSocket webSocket, final Response response) {
                super.onOpen(webSocket, response);
                Runnable service = new Runnable() {
                    @Override
                    public void run() {
                        req_1 = new ACRegisterChannelBean();
                        req_1.setSender(sTNAPSpv);
                        req_1.setReceiver(sTNAPTrim);
                        req_1.setMagic(WalletApplication.getMagic());
                        req_1.setChannelName(wallet.getAddress() + UUIDUtil.getRandomLowerNoLine());
                        ACRegisterChannelBean.MessageBodyBean messageBody = new ACRegisterChannelBean.MessageBodyBean();
                        messageBody.setAssetType(assetName);
                        messageBody.setDeposit(Double.parseDouble(depositTrim));
                        req_1.setMessageBody(messageBody);
                        String text = gson.toJson(req_1);
                        webSocket.send(text);
                    }
                };
                runOffUiThread(service);
            }

            @Override
            public void onMessage(final WebSocket webSocket, final String text) {
                super.onMessage(webSocket, text);
                Runnable service = new Runnable() {
                    @Override
                    public void run() {
                        String messageTypeStr = JSONObjectUtil.getMessageType(text);
                        if ("RegisterChannelFail".equals(messageTypeStr)) {
                            webSocket.cancel();
                            return;
                        }
                        if ("FounderFail".equals(messageTypeStr)) {
                            webSocket.cancel();
                            return;
                        }
                        if ("Founder".equals(messageTypeStr)) {
                            resp_2 = gson.fromJson(text, ACFounderBean.class);
                            req_3 = new ACFounderSignBean();
                            req_3.setSender(sTNAPSpv);
                            req_3.setReceiver(sTNAPTrim);
                            req_3.setChannelName(resp_2.getChannelName());
                            req_3.setTxNonce(resp_2.getTxNonce());
                            ACFounderSignBean.MessageBodyBean messageBody3 = new ACFounderSignBean.MessageBodyBean();
                            ACFounderSignBean.MessageBodyBean.FounderBean founder3 = new ACFounderSignBean.MessageBodyBean.FounderBean();
                            founder3.setTxDataSign(NeoSignUtil.signToHex(resp_2.getMessageBody().getFounder().getTxData(), wallet.getPrivateKey()));
                            founder3.setOriginalData(gson.fromJson(gson.toJson(resp_2.getMessageBody().getFounder()), ACFounderSignBean.MessageBodyBean.FounderBean.OriginalDataBean.class));
                            messageBody3.setFounder(founder3);
                            ACFounderSignBean.MessageBodyBean.CommitmentBean commitment3 = new ACFounderSignBean.MessageBodyBean.CommitmentBean();
                            commitment3.setTxDataSign(NeoSignUtil.signToHex(resp_2.getMessageBody().getCommitment().getTxData(), wallet.getPrivateKey()));
                            commitment3.setOriginalData(gson.fromJson(gson.toJson(resp_2.getMessageBody().getCommitment()), ACFounderSignBean.MessageBodyBean.CommitmentBean.OriginalDataBeanX.class));
                            messageBody3.setCommitment(commitment3);
                            ACFounderSignBean.MessageBodyBean.RevocableDeliveryBean revocableDelivery3 = new ACFounderSignBean.MessageBodyBean.RevocableDeliveryBean();
                            revocableDelivery3.setTxDataSign(NeoSignUtil.signToHex(resp_2.getMessageBody().getRevocableDelivery().getTxData(), wallet.getPrivateKey()));
                            revocableDelivery3.setOriginalData(gson.fromJson(gson.toJson(resp_2.getMessageBody().getRevocableDelivery()), ACFounderSignBean.MessageBodyBean.RevocableDeliveryBean.OriginalDataBeanXX.class));
                            messageBody3.setRevocableDelivery(revocableDelivery3);
                            messageBody3.setAssetType(resp_2.getMessageBody().getAssetType());
                            messageBody3.setDeposit(resp_2.getMessageBody().getDeposit());
                            messageBody3.setRoleIndex(resp_2.getMessageBody().getRoleIndex());
                            req_3.setMessageBody(messageBody3);
                            String send3 = gson.toJson(req_3);
                            webSocket.send(send3);

                            rpc_4 = new JSONRpcClient.Builder()
                                    .net(WalletApplication.getNetUrl())
                                    .method("FunderTransaction")
                                    .params(TNAPUtil.getPublicKey(sTNAPSpv),
                                            TNAPUtil.getPublicKey(sTNAPTrim),
                                            resp_2.getMessageBody().getFounder().getAddressFunding(),
                                            resp_2.getMessageBody().getFounder().getScriptFunding(),
                                            String.valueOf(resp_2.getMessageBody().getDeposit()),
                                            String.valueOf(resp_2.getMessageBody().getDeposit()),
                                            resp_2.getMessageBody().getFounder().getTxId(),
                                            ConfigList.ASSET_ID_MAP.get(resp_2.getMessageBody().getAssetType().trim().toUpperCase(Locale.getDefault())))
                                    .id(wallet.getAddress() + UUIDUtil.getRandomLowerNoLine())
                                    .build();

                            String json_5 = rpc_4.post();
                            if (json_5 == null) {
                                webSocket.cancel();
                                return;
                            }
                            resp_5 = gson.fromJson(json_5, FunderTransactionBean.class);

                            req_6 = new ACFounderBean();
                            req_6.setChannelName(resp_2.getChannelName());
                            req_6.setSender(sTNAPSpv);
                            req_6.setReceiver(sTNAPTrim);
                            req_6.setTxNonce(resp_2.getTxNonce());
                            ACFounderBean.MessageBodyBean messageBody4 = new ACFounderBean.MessageBodyBean();
                            messageBody4.setAssetType(resp_2.getMessageBody().getAssetType());
                            messageBody4.setDeposit(resp_2.getMessageBody().getDeposit());
                            messageBody4.setRoleIndex(resp_2.getMessageBody().getRoleIndex() + 1);
                            messageBody4.setFounder(resp_2.getMessageBody().getFounder());
                            messageBody4.setCommitment(gson.fromJson(gson.toJson(resp_5.getResult().getC_TX()), ACFounderBean.MessageBodyBean.CommitmentBean.class));
                            messageBody4.setRevocableDelivery(gson.fromJson(gson.toJson(resp_5.getResult().getR_TX()), ACFounderBean.MessageBodyBean.RevocableDeliveryBean.class));
                            req_6.setMessageBody(messageBody4);
                            String send6 = gson.toJson(req_6);
                            webSocket.send(send6);

                            return;
                        }

                        if ("FounderSign".equals(messageTypeStr)) {
                            resp_7 = gson.fromJson(text, ACFounderSignBean.class);
                            rpc_8 = new JSONRpcClient.Builder()
                                    .net(WalletApplication.getNetUrlForNEO())
                                    .method("sendrawtransaction")
                                    .params(resp_7.getMessageBody().getFounder().getOriginalData().getTxData() + resp_7.getMessageBody().getFounder().getOriginalData().getWitness().replace("{signOther}", resp_7.getMessageBody().getFounder().getTxDataSign()).replace("{signSelf}", req_3.getMessageBody().getFounder().getTxDataSign()))
                                    .id(wallet.getAddress() + UUIDUtil.getRandomLowerNoLine())
                                    .build();

                            String json_9 = rpc_8.post();
                            if (json_9 == null) {
                                webSocket.cancel();
                                return;
                            }
                            resp_9 = gson.fromJson(json_9, SendrawtransactionBean.class);
                            Boolean setChannelResult = resp_9.isResult();

                            // Set up channel success.
                            if (setChannelResult) {
                                channelBeanHeating = new ChannelBean(
                                        resp_7.getChannelName(),
                                        sTNAPTrim,
                                        resp_7.getTxNonce(),
                                        aliasTrim,
                                        resp_7.getMessageBody().getDeposit(),
                                        resp_7.getMessageBody().getDeposit(),
                                        resp_7.getMessageBody().getAssetType(),
                                        ConfigList.CHANNEL_STATE_HEATING,
                                        resp_7);
                                Map<String, ChannelBean> channelBeanWithNetType = new HashMap<>();
                                channelBeanWithNetType.put(net, channelBeanHeating);
                                List<Map<String, ChannelBean>> wAppChannelList = wApp.getChannelList();
                                if (wAppChannelList == null) {
                                    wAppChannelList = new ArrayList<>();
                                }
                                wAppChannelList.add(channelBeanWithNetType);
                                wApp.setChannelList(wAppChannelList);
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshChannelUI();
                                                tab.setSelectedItemId(R.id.navigationChannel);
                                                inputTNAP.setText(null);
                                                inputDeposit.setText(null);
                                                inputAlias.setText(null);
                                                btnSetupChannel.setClickable(true);
                                                ToastUtil.show(getBaseContext(), "Channel \"" + aliasTrim + "\" was found.\nNew channel will preheat for a few seconds before it can use.");
                                            }
                                        }
                                );
                            }
                            // Set up channel fail.
                            else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnSetupChannel.setClickable(true);
                                    }
                                });
                                webSocket.cancel();
                            }
                            return;
                        }
                        if ("AddChannel".equals(messageTypeStr)) {
                            channelBeanHeating.setState(ConfigList.CHANNEL_STATE_CLEAR);
                            wApp.saveData();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshUITotal();
                                    ToastUtil.show(getBaseContext(), "Channel \"" + aliasTrim + "\" was clear.");
                                }
                            });
                            webSocket.close(1000, null);
                        }
                    }
                };
                runOffUiThread(service);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                t.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSetupChannel.setClickable(true);
                        ToastUtil.show(getBaseContext(), "Implicit problem exists.");
                    }
                });
            }
        });
    }

    private void dismantleChannel(final ChannelBean channelBean) {
        final Wallet wallet = wApp.getWallet();
        final String net = wApp.getNet();
        final String sTNAP = channelBean.getTNAP();
        final String sTNAPSpv = TNAPUtil.getTNAPSpv(sTNAP, wallet);
        WebSocketClient webSocketClient = new WebSocketClient.Builder()
                .url(TNAPUtil.getWs(sTNAPSpv))
                .build();

        webSocketClient.connect(new WebSocketListener() {
            private Map<String, ChannelBean> channelToBeRemoved;

            private JSONRpcClient rpc_1;
            private RefoundTransBean resp_2;
            private String req_3;
            private ACSettleSignBean resp_4;
            private JSONRpcClient rpc_5;
            private SendrawtransactionBean resp_6;

            @Override
            public void onOpen(final WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                runOffUiThread(new Runnable() {
                    @Override
                    public void run() {
                        channelBean.setTxNonce(channelBean.getTxNonce() + 1);

                        List<Map<String, ChannelBean>> channelList = wApp.getChannelList();
                        ChannelBean channelBeanFor;
                        for (Map<String, ChannelBean> channelBeanWithNetType : channelList) {
                            Iterator<String> keySetIterator = channelBeanWithNetType.keySet().iterator();
                            if (keySetIterator.hasNext() && net.equals(keySetIterator.next())) {
                                channelBeanFor = channelBeanWithNetType.get(net);
                                if (channelBean.getName().equals(channelBeanFor.getName())) {
                                    channelToBeRemoved = channelBeanWithNetType;
                                    break;
                                }
                            }
                        }

                        if (channelToBeRemoved == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webSocket.cancel();
                                }
                            });
                            return;
                        }

                        rpc_1 = new JSONRpcClient.Builder()
                                .net(WalletApplication.getNetUrl())
                                .method("RefoundTrans")
                                .params(channelBean.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getAddressFunding(),
                                        String.valueOf(channelBean.getBalance()),
                                        String.valueOf(BigDecimalUtil.subtract(BigDecimalUtil.add(channelBean.getDeposit(), channelBean.getDeposit()), channelBean.getBalance())),
                                        TNAPUtil.getPublicKey(sTNAPSpv),
                                        TNAPUtil.getPublicKey(sTNAP),
                                        channelBean.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getScriptFunding(),
                                        channelBean.getAssetName())
                                .id(channelBean.getName() + channelBean.getTxNonce())
                                .build();

                        String json_2 = rpc_1.post();

                        if (json_2 == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webSocket.cancel();
                                }
                            });
                            return;
                        }

                        resp_2 = gson.fromJson(json_2, RefoundTransBean.class);

                        req_3 = JSONObjectUtil.settleJSONMaker(wallet, channelBean, resp_2);

                        String send_3 = req_3;
                        webSocket.send(send_3);

                        channelToBeRemoved.get(net).setState(ConfigList.CHANNEL_STATE_COOLING);
                        wApp.saveData();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshUITotal();
                                ToastUtil.show(getBaseContext(), "Channel \"" + channelBean.getAlias() + "\" is cooling down.");
                            }
                        });
                    }
                });
            }

            @Override
            public void onMessage(final WebSocket webSocket, final String text) {
                super.onMessage(webSocket, text);
                runOffUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String messageTypeStr = JSONObjectUtil.getMessageType(text);

                        if ("SettleSign".equals(messageTypeStr)) {
                            resp_4 = gson.fromJson(text, ACSettleSignBean.class);
                            rpc_5 = new JSONRpcClient.Builder()
                                    .net(WalletApplication.getNetUrlForNEO())
                                    .method("sendrawtransaction")
                                    .params(resp_4.getMessageBody().getSettlement().getOriginalData().getTxData() + resp_4.getMessageBody().getSettlement().getOriginalData().getWitness().replace("{signSelf}", NeoSignUtil.signToHex(resp_4.getMessageBody().getSettlement().getOriginalData().getTxData(), wallet.getPrivateKey())).replace("{signOther}", resp_4.getMessageBody().getSettlement().getTxDataSign()))
                                    .id(channelBean.getName() + channelBean.getTxNonce() + "DISMANTLE")
                                    .build();

                            String json_6 = rpc_5.post();

                            if (json_6 == null) {
                                webSocket.cancel();
                                return;
                            }

                            resp_6 = gson.fromJson(json_6, SendrawtransactionBean.class);

                            boolean dismantleResult = resp_6.isResult();
                            if (!dismantleResult) {
                                webSocket.cancel();
                                return;
                            }

                            return;
                        }

                        if ("DeleteChannel".equals(messageTypeStr)) {
                            // No such need recent. Just delete it, not mark it.
                            // channelToBeRemoved.get(net).setState(ConfigList.CHANNEL_STATE_DISMANTLED);
                            wApp.getChannelList().remove(channelToBeRemoved);
                            wApp.saveData();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    obtainBalance();
                                    refreshUITotal();
                                    ToastUtil.show(getBaseContext(), "Channel \"" + channelBean.getAlias() + "\" was dismantled.");
                                }
                            });
                            webSocket.close(1000, null);
                        }
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                runOffUiThread(new Runnable() {
                    @Override
                    public void run() {
                        channelToBeRemoved.get(net).setState(ConfigList.CHANNEL_STATE_CLEAR);
                        wApp.saveData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshUITotal();
                                ToastUtil.show(getBaseContext(), "Implicit problem exists.");
                            }
                        });
                    }
                });
            }
        });
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
            return ConfigList.COIN_TYPE_ACCOUNT;
        }
    }
}
