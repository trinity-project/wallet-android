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

import org.trinity.util.BigDecimalUtil;
import org.trinity.util.HexUtil;
import org.trinity.util.NeoSignUtil;
import org.trinity.util.PaymentCodeUtil;
import org.trinity.util.PrefixUtil;
import org.trinity.util.TNAPUtil;
import org.trinity.util.WebSocketMessageTypeUtil;
import org.trinity.util.algorithm.UUIDUtil;
import org.trinity.util.android.QRCodeUtil;
import org.trinity.util.android.ToastUtil;
import org.trinity.util.android.UIStringUtil;
import org.trinity.wallet.ConfigList;
import org.trinity.wallet.R;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.entity.ChannelBean;
import org.trinity.wallet.entity.PaymentCodeBean;
import org.trinity.wallet.entity.RecordBean;
import org.trinity.wallet.net.JSONRpcClient;
import org.trinity.wallet.net.WebSocketClient;
import org.trinity.wallet.net.jsonrpc.ConstructTxBean;
import org.trinity.wallet.net.jsonrpc.FunderTransactionBean;
import org.trinity.wallet.net.jsonrpc.GetBalanceBean;
import org.trinity.wallet.net.jsonrpc.SendrawtransactionBean;
import org.trinity.wallet.net.jsonrpc.ValidateaddressBean;
import org.trinity.wallet.net.websocket.ACFounderBean;
import org.trinity.wallet.net.websocket.ACFounderSignBean;
import org.trinity.wallet.net.websocket.ACRegisterChannelBean;
import org.trinity.wallet.net.websocket.ACRsmcBean;
import org.trinity.wallet.net.websocket.ACRsmcSignBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import neoutils.Wallet;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import pub.devrel.easypermissions.EasyPermissions;

// TODO Set error and toast should not do immediately, set them into activity toast List and set error map.
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
    @BindView(R.id.btnAddChannel)
    Button btnAddChannel;
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
     * Use this to know channel needs to be re drawn.
     */
    private List<Map<String, ChannelBean>> channelListInActivity;
    /**
     * Use this to know record needs to be re drawn.
     */
    private List<Map<String, RecordBean>> recordListInActivity;
    /**
     * Json util.
     */
    private Gson gson;

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
        if (resultCode == ConfigList.BACK_RESULT) {
            refreshCardUI();
            postGetBalance();
            refreshChannelListUI();
            return;
        }
        if (resultCode == ConfigList.SIGN_IN_RESULT) {
            refreshCardUI();
            refreshChannelListUI();
            refreshRecordUI();
            ToastUtil.show(getBaseContext(), "Connecting block chain.\nYour balance will show in a few seconds.");
            postGetBalance();
            return;
        }
        if (resultCode == ConfigList.SIGN_OUT_RESULT) {
            refreshCardUI();
            refreshChannelListUI();
            refreshRecordUI();
            return;
        }
        if (resultCode == ConfigList.CHANGE_PASSWORD_RESULT) {
            initUserIdentityVerify(true);
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
                        tab.setSelectedItemId(R.id.navigationAddChannel);
                        postGetBalance();
                        refreshUITotal();
                    }
                    // If the length of QR result is bigger than neo address(payment code always longer)
                    // and starts with "A" or "TN".
                    else if (scanResult.length() >= ConfigList.NEO_ADDRESS_LENGTH && (ConfigList.NEO_ADDRESS_FIRST.equals(scanResult.substring(0, 1)) || ConfigList.PAYMENT_CODE_FIRST.equals(scanResult.substring(0, 2)))) {
                        inputTransferTo.setText(scanResult);
                        tab.setSelectedItemId(R.id.navigationTransfer);
                        verifyAddress(false);
                    }
                }
                return;
            }
        }
        postGetBalance();
        refreshUITotal();
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
        verifyIdentityHideIME();

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
        verifyIdentityHideIME();
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

    private void verifyIdentityHideIME() {
        InputMethodManager imm = (InputMethodManager) btnUserVerify.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(btnUserVerify.getApplicationWindowToken(), 0);
        }
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
        postGetBalance();
        refreshChannelListUI();
        refreshRecordUI();
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
                        postGetBalance();
                        refreshChannelListUI();
                        refreshRecordUI();
                        break;
                    case R.id.menuTestNet:
                        wApp.switchNet(ConfigList.NET_TYPE_TEST);
                        ToastUtil.show(getBaseContext(), "Switched to " + itemTitle);
                        netState.setText(itemTitle.toString().toUpperCase(Locale.getDefault()));
                        postGetBalance();
                        refreshChannelListUI();
                        refreshRecordUI();
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
                refreshCardUI();
                postGetBalance();
            }
        });
    }

    private void initTabs() {
        // This is tab addressMode.
        inputTransferTo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    verifyAddress(true);
                    return true;
                }
                return false;
            }
        });
        inputTransferTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        btnTransferTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyAddress(true);
            }
        });

        // This is tab add channel.
        inputAssets.setOnCheckedChangeListener(onRadioCheckedChangeListener);

        btnAddChannel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptAddChannel();
                    }
                }
        );

        // This is tab channel list.
        refreshChannelListUI();

        // This is tab record.
        refreshRecordUI();

        // Reset tab index.
        tab.setOnFocusChangeListener(null);
        tab.setSelectedItemId(R.id.navigationAddChannel);
        tab.setSelectedItemId(R.id.navigationTransfer);

        // This is tab bar.
        tab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                tabsGone();
                switch (menuItem.getItemId()) {
                    case R.id.navigationTransfer:
                        tabTransfer.setVisibility(View.VISIBLE);
                        postGetBalance();
                        return true;
                    case R.id.navigationAddChannel:
                        tabAddChannel.setVisibility(View.VISIBLE);
                        postGetBalance();
                        return true;
                    case R.id.navigationChannelList:
                        tabChannelList.setVisibility(View.VISIBLE);
                        postGetBalance();
                        return true;
                    case R.id.navigationRecord:
                        tabRecord.setVisibility(View.VISIBLE);
                        postGetBalance();
                        return true;
                }
                return false;
            }
        });
        tabTransfer.setVisibility(View.VISIBLE);
    }

    private void tabsGone() {
        tabTransfer.setVisibility(View.GONE);
        tabAddChannel.setVisibility(View.GONE);
        tabChannelList.setVisibility(View.GONE);
        tabRecord.setVisibility(View.GONE);
    }

    /* ---------------------------------- UI THREAD METHODS ---------------------------------- */

    // TODO Spread the refreshUITotal() api.
    private synchronized void refreshUITotal() {
        refreshCardUI();
        refreshChannelListUI();
        refreshRecordUI();
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

    private void refreshChannelListUI() {
        String net = wApp.getNet();
        List<Map<String, ChannelBean>> channelList = wApp.getChannelList();

        int childCount = channelContainer.getChildCount();
        if (childCount > 1) {
            channelContainer.removeViews(1, childCount - 1);
        }

        if (channelList == null) {
            channelListInActivity = null;
            channelListEmpty.setVisibility(View.VISIBLE);
            return;
        }

        channelListInActivity = channelList;

        ChannelBean channelBean;
        int nowNetChannelCount = 0;
        channelListEmpty.setVisibility(View.GONE);
        for (int index = channelListInActivity.size() - 1; index >= 0; index--) {
            Map<String, ChannelBean> channelBeanWithType = channelListInActivity.get(index);
            Iterator<String> typeIterator = channelBeanWithType.keySet().iterator();
            if (typeIterator.hasNext() && net.equals(typeIterator.next())) {
                nowNetChannelCount++;
                channelBean = channelBeanWithType.get(net);
                addChannelView(channelBean);
            }
        }

        if (nowNetChannelCount == 0) {
            channelListEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void refreshRecordUI() {
        String net = wApp.getNet();
        List<Map<String, RecordBean>> recordList = wApp.getRecordList();

        int childCount = recordContainer.getChildCount();
        if (childCount > 1) {
            recordContainer.removeViews(1, childCount - 1);
        }

        if (recordList == null) {
            recordListInActivity = null;
            recordEmpty.setVisibility(View.VISIBLE);
            return;
        }

        recordListInActivity = recordList;

        RecordBean recordBean;
        int nowNetRecordCount = 0;
        recordEmpty.setVisibility(View.GONE);
        for (int index = recordListInActivity.size() - 1; index >= 0; index--) {
            Map<String, RecordBean> recordBeanWithType = recordListInActivity.get(index);
            Iterator<String> typeIterator = recordBeanWithType.keySet().iterator();
            if (typeIterator.hasNext() && net.equals(typeIterator.next())) {
                nowNetRecordCount++;
                recordBean = recordBeanWithType.get(net);
                addRecordView(recordBean);
            }
        }

        if (nowNetRecordCount == 0) {
            recordEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void addChannelView(@NonNull ChannelBean channelBean) {
        View channelView = View.inflate(this, R.layout.tab_channel_list_item, null);
        TextView channelName = channelView.findViewById(R.id.channelName);
        TextView channelDeposit = channelView.findViewById(R.id.channelDeposit);
        TextView channelBalance = channelView.findViewById(R.id.channelBalance);
        TextView channelState = channelView.findViewById(R.id.channelState);
        ImageButton channelClose = channelView.findViewById(R.id.channelClose);
        channelName.setText(channelBean.getAlias());
        channelDeposit.setText(getString(R.string.channel_deposit, BigDecimal.valueOf(channelBean.getDeposit()).toPlainString(), channelBean.getAssetName()));
        channelBalance.setText(getString(R.string.channel_balance, BigDecimal.valueOf(channelBean.getBalance()).toPlainString(), channelBean.getAssetName()));
        channelState.setText(getString(R.string.channel_state, channelBean.getState()));
        channelClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO close channel.
            }
        });
        channelContainer.addView(channelView);
        channelListEmpty.setVisibility(View.GONE);
    }

    private void addRecordView(@NonNull RecordBean recordBean) {
        View recordView = View.inflate(this, R.layout.tab_record_item, null);
        TextView recordChannelName = recordView.findViewById(R.id.recordChannelName);
        TextView recordPrice = recordView.findViewById(R.id.recordPrice);
        TextView recordFee = recordView.findViewById(R.id.recordFee);
        recordChannelName.setText(recordBean.getChannel_Alias());
        recordPrice.setText(getString(R.string.record_price, BigDecimal.valueOf(recordBean.getPrice()).toPlainString(), recordBean.getAssetName()));
        recordFee.setText(getString(R.string.record_fee, BigDecimal.valueOf(recordBean.getFee()).toPlainString(), recordBean.getAssetName()));

        recordContainer.addView(recordView);
        recordEmpty.setVisibility(View.GONE);
    }


    /* ---------------------------------- WEB CONNECT METHODS ---------------------------------- */

    // TODO Post get balance should not happen twice in 0.2 seconds.
    // TODO Always decoupling the i/o and UI works. Such as postGetBalance() should not have a refreshUITotal() in it, refreshUITotal() is called by postGetBalance()'s father method.
    private synchronized void postGetBalance() {
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
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      if (response == null) {
                                          ToastUtil.show(getBaseContext(), "Implicit problem happened.");
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
                                      refreshUITotal();
                                  }
                              }
                );
            }
        };
        runOffUiThread(service);
    }

    // TODO Split the verify and transfer function. Just verify whether it is an address or a payment code, then distribute the neo pay trinity node pay or error checked result.
    private synchronized void verifyAddress(final boolean doPay) {
        inputTransferTo.setError(null);
        inputAmount.setError(null);
        verifyAddressHideIME();

        // Wallet.
        final Wallet wallet = wApp.getWallet();

        if (doPay && layAmount.getVisibility() == View.GONE) {
            ToastUtil.show(getBaseContext(), "Verifying input.");
            if (wallet == null) {
                ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + '.');
                return;
            }
        }
        postGetBalance();

        final String toAddressTrim = inputTransferTo.getText().toString().trim();
        if ("".equals(toAddressTrim) || wallet.getAddress().equals(toAddressTrim)) {
            if (doPay) {
                // Invalid text error.
                inputTransferTo.setError("Invalid input.");
                inputTransferTo.requestFocus();
                payCodeMode(true);
            }
            return;
        }

        boolean isLooksLikeAnAddress = inputTransferTo.length() == ConfigList.NEO_ADDRESS_LENGTH && ConfigList.NEO_ADDRESS_FIRST.equals(toAddressTrim.substring(0, 1));

        if (!isLooksLikeAnAddress) {
            payCodeMode(doPay);
            return;
        }

        // Send post to verify to address.
        Runnable service = new Runnable() {
            @Override
            public void run() {
                JSONRpcClient client = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrlForNEO())
                        .method("validateaddress")
                        .params(toAddressTrim)
                        .id(toAddressTrim + UUIDUtil.getRandomLowerNoLine())
                        .build();
                final String response = client.post();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            ToastUtil.show(getBaseContext(), "Implicit problem happened.");
                            return;
                        }

                        final ValidateaddressBean responseBean;
                        responseBean = gson.fromJson(response, ValidateaddressBean.class);
                        // The User input is a neo address.
                        boolean isValid = responseBean.getResult().isIsvalid();

                        if (!isValid) {
                            if (doPay) {
                                payCodeMode(true);
                            }
                            return;
                        }

                        // Show addressMode window.
                        if (layAmount.getVisibility() == View.GONE) {
                            layAmount.setVisibility(View.VISIBLE);
                            labelAssetsTrans.setVisibility(View.VISIBLE);
                            inputAssetsTrans.setVisibility(View.VISIBLE);
                            if (doPay) {
                                ToastUtil.show(getBaseContext(), "Address verified.");
                                inputAmount.requestFocus();
                            }
                            return;
                        }

                        if (doPay) {
                            addressMode(toAddressTrim);
                        }
                    }
                });
            }
        };
        runOffUiThread(service);
    }

    private void verifyAddressHideIME() {
        InputMethodManager imm = (InputMethodManager) btnTransferTo.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(btnTransferTo.getApplicationWindowToken(), 0);
        }
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

        int checkedRadioId = inputAssetsTrans.getCheckedRadioButtonId();
        RadioButton radioChecked = MainActivity.this.findViewById(checkedRadioId);
        String assetName = radioChecked.getText().toString().trim();

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
        btnTransferTo.setClickable(false);
        postConstructTx(toAddressStr, amountTrim, assetName);
    }

    private void payCodeMode(boolean doPay) {
        layAmount.setVisibility(View.GONE);
        labelAssetsTrans.setVisibility(View.GONE);
        inputAssetsTrans.setVisibility(View.GONE);

        if (!doPay) {
            return;
        }

        final Boolean isValidPayCode = attemptRTransaction();
        // TODO try payment code.

        if (isValidPayCode != null && !isValidPayCode) {
            inputTransferTo.setError("Invalid input.");
            inputTransferTo.requestFocus();
        }
    }

    // TODO Inverted R transaction.
    private Boolean attemptRTransaction() {
        final Wallet wallet = wApp.getWallet();
        if (wallet == null) {
            ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + ".");
            return null;
        }
        String toAddress = inputTransferTo.getText().toString();
        final String paymentCodeTrim = toAddress.trim();
        final PaymentCodeBean paymentCodeBean = PaymentCodeUtil.decode(paymentCodeTrim);
        if (paymentCodeBean == null) {
            return false;
        }
        List<Map<String, ChannelBean>> channelList = wApp.getChannelList();
        final String net = wApp.getNet();
        if (channelList == null) {
            ToastUtil.show(getBaseContext(), "Please add a channel.");
            return null;
        }
        ChannelBean channelBean = null;
        ChannelBean channelBeanFor;
        final String sTNAP = paymentCodeBean.getsTNAP();
        if (!TNAPUtil.isValid(sTNAP)) {
            inputTransferTo.setError("Invalid input.");
            inputTransferTo.requestFocus();
            return false;
        }
        final String sTNAPSpv = TNAPUtil.getTNAPSpv(sTNAP, wallet);

        for (Map<String, ChannelBean> channelBeanWithNetType : channelList) {
            Iterator<String> keySetIterator = channelBeanWithNetType.keySet().iterator();
            if (keySetIterator.hasNext() && net.equals(keySetIterator.next())) {
                channelBeanFor = channelBeanWithNetType.get(net);
                if (channelBeanFor.getTNAP().equals(sTNAP) && PrefixUtil.trimOx(ConfigList.ASSET_ID_MAP.get(channelBeanFor.getAssetName())).equals(paymentCodeBean.getAssetId())) {
                    channelBean = channelBeanFor;
                    break;
                }
            }
        }
        if (channelBean == null) {
            // TODO H transaction.
        }

        // TODO channel balance enough.

        channelBean.setTxNonce(channelBean.getTxNonce() + 1);
        final int txNoncePp = channelBean.getTxNonce();
        final ChannelBean channelBeanFinal = channelBean;
        Runnable service = new Runnable() {
            private JSONRpcClient rpc_1;
            private FunderTransactionBean resp_2;
            private ACRsmcBean req_3;
            private ACRsmcSignBean resp_4;
            private ACRsmcBean resp_5;
            private ACRsmcSignBean req_6;
            private ACRsmcBean req_7;
            private ACRsmcBean resp_8;

            @Override
            public void run() {
                rpc_1 = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrl())
                        .method("FunderTransaction")
                        .params(TNAPUtil.getPublicKey(sTNAP),
                                TNAPUtil.getPublicKey(sTNAPSpv),
                                channelBeanFinal.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getAddressFunding(),
                                channelBeanFinal.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getScriptFunding(),
                                String.valueOf(channelBeanFinal.getBalance()),
                                String.valueOf(channelBeanFinal.getDeposit() + channelBeanFinal.getDeposit() - channelBeanFinal.getBalance()),
                                channelBeanFinal.getFounderSign_HeSigned().getMessageBody().getFounder().getOriginalData().getTxId(),
                                channelBeanFinal.getAssetName())
                        .id(channelBeanFinal.getName() + txNoncePp)
                        .build();

                String json_2 = rpc_1.post();

                if (json_2 == null) {
                    return;
                }

                resp_2 = gson.fromJson(json_2, FunderTransactionBean.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WebSocketClient webSocketClient = new WebSocketClient.Builder()
                                .url(TNAPUtil.getWs(sTNAPSpv))
                                .build();
                        webSocketClient.connect(new WebSocketListener() {
                            @Override
                            public void onOpen(final WebSocket webSocket, Response response) {
                                super.onOpen(webSocket, response);
                                runOffUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        req_3 = new ACRsmcBean();
                                        req_3.setSender(sTNAPSpv);
                                        req_3.setReceiver(sTNAP);
                                        req_3.setChannelName(channelBeanFinal.getName());
                                        req_3.setTxNonce(txNoncePp);
                                        ACRsmcBean.MessageBodyBean messageBody_3 = new ACRsmcBean.MessageBodyBean();
                                        messageBody_3.setCommitment(gson.fromJson(gson.toJson(resp_2.getResult().getC_TX()), ACRsmcBean.MessageBodyBean.CommitmentBean.class));
                                        messageBody_3.setRevocableDelivery(gson.fromJson(gson.toJson(resp_2.getResult().getR_TX()), ACRsmcBean.MessageBodyBean.RevocableDeliveryBean.class));
                                        messageBody_3.setAssetType(channelBeanFinal.getAssetName());
                                        messageBody_3.setValue(paymentCodeBean.getPrice());
                                        messageBody_3.setRoleIndex(0);
                                        messageBody_3.setComments(paymentCodeBean.getComment());
                                        req_3.setMessageBody(messageBody_3);

                                        String send_3 = gson.toJson(req_3);
                                        webSocket.send(send_3);
                                    }
                                });
                            }

                            @Override
                            public void onMessage(final WebSocket webSocket, final String text) {
                                super.onMessage(webSocket, text);
                                runOffUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String messageTypeStr = WebSocketMessageTypeUtil.getMessageType(text);
                                        if ("RsmcSign".equals(messageTypeStr)) {
                                            resp_4 = gson.fromJson(text, ACRsmcSignBean.class);
                                        }
                                        if ("Rsmc".equals(messageTypeStr)) {
                                            ACRsmcBean tmp = gson.fromJson(text, ACRsmcBean.class);
                                            // case BR empty: save to r5.
                                            if (tmp.getMessageBody().getBreachRemedy() == null) {
                                                resp_5 = tmp;
                                                req_6 = new ACRsmcSignBean();
                                                req_6.setChannelName(resp_5.getChannelName());
                                                req_6.setSender(sTNAPSpv);
                                                req_6.setReceiver(sTNAP);
                                                req_6.setTxNonce(resp_5.getTxNonce());
                                                ACRsmcSignBean.MessageBodyBean messageBody_6 = new ACRsmcSignBean.MessageBodyBean();
                                                messageBody_6.setValue(resp_5.getMessageBody().getValue());
                                                messageBody_6.setComments(resp_5.getMessageBody().getComments());
                                                messageBody_6.setRoleIndex(resp_5.getMessageBody().getRoleIndex());
                                                ACRsmcSignBean.MessageBodyBean.CommitmentBean commitment_6 = new ACRsmcSignBean.MessageBodyBean.CommitmentBean();
                                                commitment_6.setOriginalData(gson.fromJson(gson.toJson(resp_5.getMessageBody().getCommitment()), ACRsmcSignBean.MessageBodyBean.CommitmentBean.OriginalDataBean.class));
                                                commitment_6.setTxDataSign(NeoSignUtil.signToHex(resp_5.getMessageBody().getCommitment().getTxData(), wallet.getPrivateKey()));
                                                messageBody_6.setCommitment(commitment_6);
                                                ACRsmcSignBean.MessageBodyBean.RevocableDeliveryBean revocableDelivery_6 = new ACRsmcSignBean.MessageBodyBean.RevocableDeliveryBean();
                                                revocableDelivery_6.setOriginalData(gson.fromJson(gson.toJson(resp_5.getMessageBody().getRevocableDelivery()), ACRsmcSignBean.MessageBodyBean.RevocableDeliveryBean.OriginalDataBeanX.class));
                                                revocableDelivery_6.setTxDataSign(NeoSignUtil.signToHex(resp_5.getMessageBody().getRevocableDelivery().getTxData(), wallet.getPrivateKey()));
                                                messageBody_6.setRevocableDelivery(revocableDelivery_6);
                                                req_6.setMessageBody(messageBody_6);

                                                String send_6 = gson.toJson(req_6);
                                                webSocket.send(send_6);

                                                req_7 = new ACRsmcBean();
                                                req_7.setChannelName(resp_5.getChannelName());
                                                req_7.setSender(sTNAPSpv);
                                                req_7.setReceiver(sTNAP);
                                                req_7.setTxNonce(resp_5.getTxNonce());
                                                ACRsmcBean.MessageBodyBean messageBody_7 = new ACRsmcBean.MessageBodyBean();
                                                messageBody_7.setAssetType(resp_5.getMessageBody().getAssetType());
                                                messageBody_7.setValue(resp_5.getMessageBody().getValue());
                                                messageBody_7.setComments(resp_5.getMessageBody().getComments());
                                                messageBody_7.setRoleIndex(resp_5.getMessageBody().getRoleIndex() + 1);
                                                ACRsmcBean.MessageBodyBean.BreachRemedyBean breachRemedy_7 = new ACRsmcBean.MessageBodyBean.BreachRemedyBean();
                                                breachRemedy_7.setOriginalData(gson.fromJson(gson.toJson(resp_2.getResult().getBR_TX()), ACRsmcBean.MessageBodyBean.BreachRemedyBean.OriginalDataBean.class));
                                                breachRemedy_7.setTxDataSign(NeoSignUtil.signToHex(resp_2.getResult().getBR_TX().getTxData(), wallet.getPrivateKey()));
                                                messageBody_7.setBreachRemedy(breachRemedy_7);
                                                req_7.setMessageBody(messageBody_7);

                                                String send_7 = gson.toJson(req_7);
                                                webSocket.send(send_7);
                                            }
                                            // case BR no empty(or r5 not null) save to r8.
                                            if (resp_5 != null) {
                                                resp_8 = tmp;
                                            }
                                        }
                                        if ("UpdateChannel".equals(messageTypeStr)) {
                                            double spvBalance = gson.fromJson(text, JsonObject.class)
                                                    .getAsJsonObject("MessageBody")
                                                    .getAsJsonObject("Balance")
                                                    .getAsJsonObject(sTNAPSpv)
                                                    .get(channelBeanFinal.getAssetName())
                                                    .getAsDouble();
                                            List<Map<String, RecordBean>> recordList = wApp.getRecordList();
                                            if (recordList == null) {
                                                recordList = new ArrayList<>();
                                            }
                                            Map<String, RecordBean> recordBeanWithNetType = new HashMap<>();
                                            recordBeanWithNetType.put(net, new RecordBean(
                                                    channelBeanFinal,
                                                    BigDecimalUtil.subtract(spvBalance, channelBeanFinal.getBalance()),
                                                    0d,
                                                    resp_4,
                                                    resp_8));
                                            recordList.add(recordBeanWithNetType);
                                            channelBeanFinal.setBalance(spvBalance);
                                            wApp.setRecordList(recordList);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    postGetBalance();
                                                    refreshUITotal();
                                                    inputTransferTo.setText(null);
                                                    inputAmount.setText(null);
                                                    btnTransferTo.setClickable(true);
                                                    tab.setSelectedItemId(R.id.navigationRecord);
                                                    ToastUtil.show(getBaseContext(), "Channel payment admitted by the trinity node(not on the block chain yet).");
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
                                t.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.show(getBaseContext(), "Implicit problem happened.");
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };
        runOffUiThread(service);

        // TODO the other one's public key to address.
        return true;
    }

    // TODO Inline the address transfer procedure, or some others, just like what the add channel method do.
    private void postConstructTx(@NonNull final String validToAddress, @NonNull final String validAmount, @NonNull final String assetName) {
        // Send post to addressMode to address.
        final Wallet wallet = wApp.getWallet();
        if (wallet == null) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // Toast please login first.
                            ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + '.');
                            btnTransferTo.setClickable(true);
                        }
                    }
            );
            return;
        }
        Runnable service = new Runnable() {
            @Override
            public void run() {
                String myAddress = wallet.getAddress();
                JSONRpcClient client = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrl())
                        .method("constructTx")
                        .params(myAddress, validToAddress, validAmount, ConfigList.ASSET_ID_MAP.get(assetName))
                        .id(validToAddress + validAmount + assetName)
                        .build();
                final String response = client.post();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            ToastUtil.show(getBaseContext(), "Implicit problem happened.");
                            btnTransferTo.setClickable(true);
                            return;
                        }

                        final ConstructTxBean bean = gson.fromJson(response, ConstructTxBean.class);
                        String txData = bean.getResult().getTxData();
                        String witness = bean.getResult().getWitness();
                        String txid = bean.getResult().getTxid();
                        if (txData == null || witness == null || txid == null) {
                            ToastUtil.show(getBaseContext(), "Implicit problem happened.");
                            btnTransferTo.setClickable(true);
                            return;
                        }
                        postSendrawtransaction(txData, witness, txid);
                    }
                });
            }
        };
        runOffUiThread(service);
    }

    private void postSendrawtransaction(@NonNull final String txData, @NonNull final String witness, @NonNull final String txid) {
        // Send post to addressMode to address.
        final Wallet wallet = wApp.getWallet();
        if (wallet == null) {
            // Toast please login first.
            ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + '.');
            btnTransferTo.setClickable(true);
            return;
        }
        Runnable service = new Runnable() {
            @Override
            public void run() {
                String sign = NeoSignUtil.signToHex(txData, wallet.getPrivateKey());
                String publicKeyHex = HexUtil.byteArrayToHex(wallet.getPublicKey());
                String replacedWitness = witness.replace("{signature}", sign).replace("{pubkey}", publicKeyHex);
                JSONRpcClient client = new JSONRpcClient.Builder()
                        .net(WalletApplication.getNetUrlForNEO())
                        .method("sendrawtransaction")
                        .params(txData + replacedWitness)
                        .id(txid)
                        .build();
                final String response = client.post();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            ToastUtil.show(getBaseContext(), "Implicit problem happened.");
                            btnTransferTo.setClickable(true);
                            return;
                        }

                        SendrawtransactionBean bean = gson.fromJson(response, SendrawtransactionBean.class);
                        if (!bean.isResult()) {
                            ToastUtil.show(getBaseContext(), "Implicit problem happened.");
                            btnTransferTo.setClickable(true);
                            return;
                        }

                        ToastUtil.show(getBaseContext(), "Transfer succeed.\nBlock chain confirms in seconds.");
                        postGetBalance();
                        btnTransferTo.setClickable(true);
                    }
                });
            }
        };
        runOffUiThread(service);
    }

    private void attemptAddChannel() {
        inputTNAP.setError(null);
        inputDeposit.setError(null);
        inputAlias.setError(null);

        final Wallet wallet = wApp.getWallet();
        final String net = wApp.getNet();

        if (wallet == null) {
            ToastUtil.show(getBaseContext(), getString(R.string.please_sign_in) + '.');
            return;
        }

        postGetBalance();

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
                        String messageTypeStr = WebSocketMessageTypeUtil.getMessageType(text);
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
                                    .params(TNAPUtil.getPublicKey(sTNAPTrim),
                                            TNAPUtil.getPublicKey(sTNAPSpv),
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
                            Boolean addChannelResult = resp_9.isResult();

                            // Add channel success.
                            if (addChannelResult) {
                                channelBeanHeating = new ChannelBean(
                                        resp_7.getChannelName(),
                                        sTNAPTrim,
                                        resp_7.getTxNonce(),
                                        aliasTrim,
                                        resp_7.getMessageBody().getDeposit(),
                                        resp_7.getMessageBody().getDeposit(),
                                        resp_7.getMessageBody().getAssetType(),
                                        ConfigList.CHANNEL_STATUS_HEATING,
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
                                                refreshChannelListUI();
                                                tab.setSelectedItemId(R.id.navigationChannelList);
                                                inputTNAP.setText(null);
                                                inputDeposit.setText(null);
                                                inputAlias.setText(null);
                                                btnAddChannel.setClickable(true);
                                                ToastUtil.show(getBaseContext(), "Channel \"" + aliasTrim + "\" found.");
                                            }
                                        }
                                );
                            }
                            // Add channel fail.
                            else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnAddChannel.setClickable(true);
                                    }
                                });
                                webSocket.cancel();
                            }
                        }
                        if ("AddChannel".equals(messageTypeStr)) {
                            channelBeanHeating.setState(ConfigList.CHANNEL_STATUS_CLEAR);
                            // TODO Sockets always connects when app on.
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
                        btnAddChannel.setClickable(true);
                        ToastUtil.show(getBaseContext(), "Implicit problem happened.");
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
