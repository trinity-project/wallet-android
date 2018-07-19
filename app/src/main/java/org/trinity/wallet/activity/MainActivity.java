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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.trinity.util.HexUtil;
import org.trinity.util.NeoSignUtil;
import org.trinity.util.UUIDUtil;
import org.trinity.util.WebSocketMessageTypeUtil;
import org.trinity.util.android.QRCodeUtil;
import org.trinity.util.android.ToastUtil;
import org.trinity.util.android.UIStringUtil;
import org.trinity.wallet.ConfigList;
import org.trinity.wallet.R;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.entity.ChannelBean;
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
     * The lock for post requests witch gets balance.
     */
    private transient int retryTimesNow = 0;
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
        instance = MainActivity.this;
        gson = WalletApplication.getGson();

        // Identity verify.
        initUserIdentityVerify();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConfigList.SIGN_IN_RESULT) {
            refreshCardUI();
            ToastUtil.show(getBaseContext(), "Connecting block chain.\nYour balance will show in a few seconds.");
            postGetBalance();
            return;
        }
        if (resultCode == ConfigList.SIGN_OUT_RESULT) {
            refreshCardUI();
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
                // TODO handle the cam result analyze the result's type.
                inputTransferTo.setText(scanResult);
                verifyAddress(false);
            }
            return;
        }
//            return;
//        }
        postGetBalance();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                wApp.iAmNotFirstTime(newPassword);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            wApp.setIdentity(false);
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
                            wApp.setIdentity(false);
                            inputUserVerify.setError("Verification failure. Please make sure and try again.");
                            inputUserVerify.requestFocus();
                            btnUserVerify.setClickable(true);
                            return;
                        }

                        inputUserVerify.setText(null);

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
        wApp.setIdentity(true);

        userVerify.setVisibility(View.GONE);
        btnUserVerify.setClickable(true);

        // Load the wallet via user password.
        wApp.loadGlobal();
        wApp.switchNet(wApp.getNet());
        netState.setText(getString(R.string.net_state, wApp.getNet().toUpperCase(Locale.getDefault())));

        // Init account data.
        postGetBalance();
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

    private void initTabs() {
        // This is tab transfer.
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

        // TODO This is tab record.

        // This is tab bar.
        tab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                letTabsBecomeGone();
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

    private void letTabsBecomeGone() {
        tabTransfer.setVisibility(View.GONE);
        tabAddChannel.setVisibility(View.GONE);
        tabChannelList.setVisibility(View.GONE);
        tabRecord.setVisibility(View.GONE);
    }

    /* ---------------------------------- UI THREAD METHODS ---------------------------------- */

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

    private void refreshChannelListUI() {
        List<Map<String, ChannelBean>> channelList = wApp.getChannelList();
        if (channelList == null) {
            addChannelView(wApp.getNet(), null);
        } else {
            String net = wApp.getNet();
            ChannelBean channelBean;
            for (Map<String, ChannelBean> channelBeanWithType : channelList) {
                Iterator<String> typeIterator = channelBeanWithType.keySet().iterator();
                if (typeIterator.hasNext() && net.equals(typeIterator.next())) {
                    channelBean = channelBeanWithType.get(net);
                    addChannelView(net, channelBean);
                }
            }
        }
    }

    /* ---------------------------------- WEB CONNECT METHODS ---------------------------------- */

    private synchronized void postGetBalance() {
        final Wallet wallet = wApp.getWallet();

        if (wallet == null) {
            ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
            return;
        }

        new Thread(new Runnable() {
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
                                          ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                          return;
                                      }

                                      GetBalanceBean responseBean = gson.fromJson(response, GetBalanceBean.class);

                                      wApp.setChainTNC(BigDecimal.valueOf(Double.valueOf(responseBean.getResult().getTncBalance())));
                                      wApp.setChainNEO(BigDecimal.valueOf(Double.valueOf(responseBean.getResult().getNeoBalance())));
                                      wApp.setChainGAS(BigDecimal.valueOf(Double.valueOf(responseBean.getResult().getGasBalance())));

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

                                          wApp.setChannelTNC(BigDecimal.valueOf(chainTNC));
                                          wApp.setChannelNEO(BigDecimal.valueOf(chainNEO));
                                          wApp.setChannelGAS(BigDecimal.valueOf(chainGAS));
                                      }

                                      refreshCardUI();
                                  }
                              }
                );
            }
        }, "MainActivity::postGetBalance").start();
    }

    private boolean postGetBalanceShouldRetry(boolean keepTry) {
        synchronized (this) {
            if (!keepTry) {
                retryTimesNow = 0;
                return false;
            }

            if (retryTimesNow == ConfigList.RETRY_TIMES) {
                retryTimesNow = 0;
                ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                return false;
            }

            retryTimesNow++;
            postGetBalance();
            return true;
        }
    }

    private synchronized void verifyAddress(final boolean doPay) {
        // Wallet
        final Wallet wallet = wApp.getWallet();

        inputTransferTo.setError(null);
        inputAmount.setError(null);

        if (doPay && layAmount.getVisibility() == View.GONE) {
            ToastUtil.show(getBaseContext(), "Verifying input.");

            if (wallet == null) {
                ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
                return;
            }

            postGetBalance();
        }

        final String toAddressStr = inputTransferTo.getText().toString().trim();
        if ("".equals(toAddressStr) || wallet.getAddress().equals(toAddressStr)) {
            if (doPay) {
                // Invalid text error.
                inputTransferTo.setError("Invalid input.");
                inputTransferTo.requestFocus();
                attemptPayCode(true);
            }
            return;
        }

        boolean isLooksLikeAnAddress = inputTransferTo.length() == ConfigList.NEO_ADDRESS_LENGTH && ConfigList.NEO_ADDRESS_FIRST.equals(toAddressStr.substring(0, 1));

        if (!isLooksLikeAnAddress) {
            attemptPayCode(doPay);
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
                        .id(toAddressStr + UUIDUtil.getRandomLowerNoLine())
                        .build();
                final String response = client.post();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                            return;
                        }

                        final ValidateaddressBean responseBean;
                        responseBean = gson.fromJson(response, ValidateaddressBean.class);
                        // The User input is a neo address.
                        boolean isValid = responseBean.getResult().isIsvalid();

                        if (!isValid) {
                            if (doPay) {
                                attemptPayCode(true);
                            }
                            return;
                        }

                        // Show transfer window.
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
                            transfer(toAddressStr);
                        }
                    }
                });
            }
        }, "MainActivity::isValidAddress").start();
    }

    private void transfer(@NonNull String toAddressStr) {
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
        boolean isAmountEnough = false;

        BigDecimal amountBigDecimal = BigDecimal.valueOf(amountDouble);
        if (getString(R.string.tnc).equals(assetName)) {
            if (isCoinInteger || isCoinDigitsValid) {
                isCoinAmountOK = true;
            } else {
                inputAmount.setError("TNC balance is a decimal up to 8 digits.");
            }
            isAmountEnough = amountBigDecimal.compareTo(wApp.getChainTNC()) <= 0;
        } else if (getString(R.string.neo).equals(assetName)) {
            if (isCoinInteger) {
                isCoinAmountOK = true;
            } else {
                inputAmount.setError("NEO balance is a integer.");
            }
            isAmountEnough = amountBigDecimal.compareTo(wApp.getChainNEO()) <= 0;
        } else if (getString(R.string.gas).equals(assetName)) {
            if (isCoinInteger || isCoinDigitsValid) {
                isCoinAmountOK = true;
            } else {
                inputAmount.setError("GAS balance is a decimal up to 8 digits.");
            }
            isAmountEnough = amountBigDecimal.compareTo(wApp.getChainGAS()) <= 0;
        }

        if (!isCoinAmountOK) {
            inputAmount.requestFocus();
            return;
        }

        if (!isAmountEnough) {
            inputAmount.setError("Balance of current asset is not enough.");
            inputAmount.requestFocus();
            return;
        }

        ToastUtil.show(getBaseContext(), "Doing transfer.\nIt takes a very short time.");
        btnTransferTo.setClickable(false);
        postConstructTx(toAddressStr, amountTrim, assetName);
    }

    private void attemptPayCode(boolean doPay) {
        layAmount.setVisibility(View.GONE);
        labelAssetsTrans.setVisibility(View.GONE);
        inputAssetsTrans.setVisibility(View.GONE);

        if (!doPay) {
            return;
        }

        final boolean payCodeSuccess = false;
        // TODO Do something to know it is a payment code.

        if (!payCodeSuccess) {
            // All invalid.

            // Invalid text error.
            inputTransferTo.setError("Invalid input.");
            inputTransferTo.requestFocus();

        }
    }

    private void postConstructTx(@NonNull final String validToAddress, @NonNull final String validAmount, @NonNull final String assetName) {
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
                final String response = client.post();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                            btnTransferTo.setClickable(true);
                            return;
                        }

                        final ConstructTxBean bean = gson.fromJson(response, ConstructTxBean.class);
                        String txData = bean.getResult().getTxData();
                        String witness = bean.getResult().getWitness();
                        String txid = bean.getResult().getTxid();
                        if (txData == null || witness == null || txid == null) {
                            ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                            btnTransferTo.setClickable(true);
                            return;
                        }
                        postSendrawtransaction(txData, witness, txid);
                    }
                });
            }
        }, "MainActivity::postConstructTx").start();
    }

    private void postSendrawtransaction(@NonNull final String txData, @NonNull final String witness, @NonNull final String txid) {
        // Send post to transfer to address.
        final Wallet wallet = wApp.getWallet();
        if (wallet == null) {
            // Toast please login first.
            ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
            btnTransferTo.setClickable(true);
            return;
        }
        new Thread(new Runnable() {
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
                            ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                            btnTransferTo.setClickable(true);
                            return;
                        }

                        SendrawtransactionBean bean = gson.fromJson(response, SendrawtransactionBean.class);
                        if (!bean.isResult()) {
                            ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                            btnTransferTo.setClickable(true);
                            return;
                        }

                        ToastUtil.show(getBaseContext(), "Transfer succeed.\nBlock chain confirms in seconds.");
                        postGetBalance();
                        btnTransferTo.setClickable(true);
                    }
                });
            }
        }, "MainActivity::postSendrawtransaction").start();
    }

    private void attemptAddChannel() {
        inputTNAP.setError(null);
        inputDeposit.setError(null);
        inputAlias.setError(null);

        final Wallet wallet = wApp.getWallet();

        if (wallet == null) {
            ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
            return;
        }

        postGetBalance();

        final String publicKeyHex = HexUtil.byteArrayToHex(wallet.getPublicKey());

        if (wallet.getPublicKey() == null || "".equals(publicKeyHex)) {
            ToastUtil.show(getBaseContext(), getString(R.string.please_login) + '.');
            return;
        }

        final String sTNAPTrim = inputTNAP.getText().toString().toLowerCase().trim();

        if ("".equals(sTNAPTrim) || !sTNAPTrim.contains("@")) {
            inputTNAP.setError("Invalid input.");
            inputTNAP.requestFocus();
            return;
        }
        String[] sTNAPSplit = sTNAPTrim.split("@");
        if (sTNAPSplit.length != 2) {
            inputTNAP.setError("Invalid input.");
            inputTNAP.requestFocus();
            return;
        }

        final String sTNAP_PublicKey = sTNAPSplit[0];
        String sTNAP_IpPort = sTNAPSplit[1];
        String sTNAP_IpPort8766 = sTNAP_IpPort.split(":")[0] + ":8766";
        if (!sTNAP_PublicKey.matches("[0-9a-f]{66}")) {
            inputTNAP.setError("Invalid input.");
            inputTNAP.requestFocus();
            return;
        }
        if (!sTNAP_IpPort.matches(ConfigList.REGEX_IP_PORT)) {
            inputTNAP.setError("Invalid input.");
            inputTNAP.requestFocus();
            return;
        }

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

        boolean isAmountEnough = false;

        if (getString(R.string.tnc).equals(assetName)) {
            if (isCoinInteger || isCoinDigitsValid) {
                isCoinAmountOK = true;
            } else {
                inputDeposit.setError("TNC balance is a decimal up to 8 digits.");
            }
            isAmountEnough = depositBigDecimal.compareTo(wApp.getChainTNC()) <= 0;
        } else if (getString(R.string.neo).equals(assetName)) {
            if (isCoinInteger) {
                isCoinAmountOK = true;
            } else {
                inputDeposit.setError("NEO balance is a integer.");
            }
            isAmountEnough = depositBigDecimal.compareTo(wApp.getChainNEO()) <= 0;
        } else if (getString(R.string.gas).equals(assetName)) {
            if (isCoinInteger || isCoinDigitsValid) {
                isCoinAmountOK = true;
            } else {
                inputDeposit.setError("GAS balance is a decimal up to 8 digits.");
            }
            isAmountEnough = depositBigDecimal.compareTo(wApp.getChainGAS()) <= 0;
        }

        if (!isCoinAmountOK) {
            inputDeposit.requestFocus();
            return;
        }

        if (!isAmountEnough) {
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

        final String sTNAP8766 = (publicKeyHex + "@" + sTNAP_IpPort8766).toLowerCase();

        ToastUtil.show(getBaseContext(), "Doing add channel.\nIt takes a very short time.");

        final WebSocketClient webSocketClient = new WebSocketClient.Builder()
                .url("ws://" + sTNAP_IpPort8766)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                webSocketClient.connect(new WebSocketListener() {
                    private boolean iAmSender = true;

                    private ACRegisterChannelBean req_1;
                    private ACFounderBean resp_2;
                    private ACFounderSignBean req_3;
                    private JSONRpcClient client_4;
                    private FunderTransactionBean resp_5;
                    private ACFounderBean req_6;
                    private ACFounderSignBean resp_7;
                    private JSONRpcClient client_8;
                    private SendrawtransactionBean resp_9;

                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                        req_1 = new ACRegisterChannelBean();
                        req_1.setSender(sTNAP8766);
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

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        super.onMessage(webSocket, text);

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
                            req_3.setSender(sTNAP8766);
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

                            client_4 = new JSONRpcClient.Builder()
                                    .net(WalletApplication.getNetUrl())
                                    .method("FunderTransaction")
                                    .params(publicKeyHex,
                                            sTNAP_PublicKey,
                                            resp_2.getMessageBody().getFounder().getAddressFunding(),
                                            resp_2.getMessageBody().getFounder().getScriptFunding(),
                                            String.valueOf(resp_2.getMessageBody().getDeposit()),
                                            String.valueOf(resp_2.getMessageBody().getDeposit()),
                                            resp_2.getMessageBody().getFounder().getTxId(),
                                            ConfigList.ASSET_ID_MAP.get(resp_2.getMessageBody().getAssetType().trim().toUpperCase(Locale.getDefault())))
                                    .id(wallet.getAddress() + UUIDUtil.getRandomLowerNoLine())
                                    .build();

                            String json_5 = client_4.post();

                            if (json_5 == null) {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                            }
                                        }
                                );
                                webSocket.close(0, null);
                                return;
                            }

                            resp_5 = gson.fromJson(json_5, FunderTransactionBean.class);

                            req_6 = new ACFounderBean();
                            req_6.setChannelName(resp_2.getChannelName());
                            req_6.setSender(sTNAP8766);
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
                            client_8 = new JSONRpcClient.Builder()
                                    .net(WalletApplication.getNetUrlForNEO())
                                    .method("sendrawtransaction")
                                    .params(resp_7.getMessageBody().getFounder().getOriginalData().getTxData() + resp_7.getMessageBody().getFounder().getOriginalData().getWitness().replace("{signOther}", resp_7.getMessageBody().getFounder().getTxDataSign()).replace("{signSelf}", req_3.getMessageBody().getFounder().getTxDataSign()))
                                    .id(wallet.getAddress() + UUIDUtil.getRandomLowerNoLine())
                                    .build();

                            String json_9 = client_8.post();

                            if (json_9 == null) {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                                            }
                                        }
                                );
                                webSocket.close(0, null);
                                return;
                            }

                            resp_9 = gson.fromJson(json_9, SendrawtransactionBean.class);

                            boolean addChannelResult = resp_9.isResult();

                            if (addChannelResult) {
                                final ChannelBean channelBean = new ChannelBean(
                                        resp_7.getChannelName(),
                                        sTNAPTrim, resp_7.getTxNonce(),
                                        aliasTrim, resp_7.getMessageBody().getDeposit(),
                                        resp_7.getMessageBody().getAssetType(),
                                        ConfigList.CHANNEL_STATUS_CLEAR);

                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                addChannelView(wApp.getNet(), channelBean);
                                                ToastUtil.show(getBaseContext(), "Channel " + aliasTrim + " add success.");
                                            }
                                        }
                                );

                                webSocket.close(0, null);
                                return;
                            }

                            if (!addChannelResult) {
                                webSocket.cancel();
                            }
                        }
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                        super.onFailure(webSocket, t, response);
                        t.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnAddChannel.setClickable(true);
                                ToastUtil.show(getBaseContext(), "Internal server exception occurred.\nPlease try again later or contact the administrator.");
                            }
                        });
                    }
                });
            }
        }, "MainActivity::attemptAddChannel").start();
    }

    private void addChannelView(@NonNull String net, @Nullable ChannelBean channelBean) {
        List<Map<String, ChannelBean>> channelList = wApp.getChannelList();

        if (channelBean == null) {
            if (channelList == null) {
                channelListEmpty.setVisibility(View.VISIBLE);
                return;
            }
            int channelCurrentNetCount = 0;
            for (Map<String, ChannelBean> channelListWithType : channelList) {
                Iterator<String> typeIterator = channelListWithType.keySet().iterator();
                if (typeIterator.hasNext() && wApp.getNet().equals(typeIterator.next())) {
                    channelCurrentNetCount++;
                }
            }
            if (channelCurrentNetCount == 0) {
                channelListEmpty.setVisibility(View.VISIBLE);
                return;
            }
            return;
        }

        if (channelList == null) {
            channelList = new ArrayList<>();
        }

        HashMap<String, ChannelBean> channelBeanWithType = new HashMap<>();
        channelBeanWithType.put(net, channelBean);
        channelList.add(channelBeanWithType);
        wApp.setChannelList(channelList);

        if (!net.equals(wApp.getNet())) {
            return;
        }

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
