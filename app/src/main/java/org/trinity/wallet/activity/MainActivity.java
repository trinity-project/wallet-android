package org.trinity.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.trinity.util.HexUtil;
import org.trinity.util.net.json.JSONRpcClient;
import org.trinity.util.net.json.bean.GetBalanceBean;
import org.trinity.wallet.R;
import org.trinity.wallet.WalletApplication;
import org.trinity.wallet.logic.DevLogic;
import org.trinity.wallet.logic.IDevCallback;

import java.io.IOException;
import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import neoutils.Wallet;

public class MainActivity extends BaseActivity {
    /**
     * The activity object.
     */
    @SuppressLint("StaticFieldLeak")
    public static MainActivity currentActivity;
    /**
     * The menu button.
     */
    @BindView(R.id.btnMainMenu)
    public Button mainMenu;
    /**
     * The frame of a single card view.
     */
    @BindView(R.id.cardsShell)
    public CoordinatorLayout cardsShell;
    /**
     * The total number of cards.
     */
    public int cardAccount = 3;
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
     * The application object.
     */
    public WalletApplication application;
    /**
     * The body of dev tab.
     */
    @BindView(R.id.tabDev)
    public ConstraintLayout tabDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        application = getWalletApplication();
        currentActivity = MainActivity.this;

        // Init events of toolbar's menu button click action.
        initToolbarMenu();
        // Init events of cards' click action.
        initCards();
        // Init events of tabs' click action.
        initTabs();

        // #Dev init.
        devInit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            postGetBalance();
            refreshCardUI();
        }
    }

    private void postGetBalance() {
        // TODO
        Wallet wallet = application.getWallet();
        JSONRpcClient client = new JSONRpcClient.Builder().method("getBalance").params(wallet.getAddress()).build();
        try {
            String response = client.post();
            GetBalanceBean responseBean = JSON.parseObject(response, GetBalanceBean.class);
            application.setChainTNC(BigDecimal.valueOf(responseBean.getResult().getTncBalance()));
            application.setChainNEO(BigDecimal.valueOf(responseBean.getResult().getNeoBalance()));
            application.setChainGAS(BigDecimal.valueOf(responseBean.getResult().getGasBalance()));
        } catch (IOException e) {
            e.printStackTrace();
            //TODO Time out ect.
        }
    }

    private void initToolbarMenu() {
        mainMenu.setOnClickListener(new View.OnClickListener() {
            private PopupMenu popupMenu;

            @Override
            public void onClick(View view) {
                popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        CharSequence title = item.getTitle();
                        int itemId = item.getItemId();
                        switch (itemId) {
                            case R.id.menuMe:
                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivityForResult(intent, 0);
                                break;
                            case R.id.menuScan:
                                break;
                            case R.id.menuSwitchNet:
                                break;
                            case R.id.menuMainNet:
                                // TODO Main Net
                                Toast.makeText(getBaseContext(), title, Toast.LENGTH_LONG).show();
                                break;
                            case R.id.menuTestNet:
                                // TODO Test Net
                                Toast.makeText(getBaseContext(), title, Toast.LENGTH_LONG).show();
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
    }

    private void initTabs() {
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
                    case R.id.navigationDev:
                        tabDev.setVisibility(View.VISIBLE);
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
        tabDev.setVisibility(View.GONE);
    }

    @Deprecated
    private void devInit() {
        findViewById(R.id.testNeoUtil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DevLogic().testNeoutil(new IDevCallback() {
                    @Override
                    public void invoke(final byte[] privateKey, final byte[] publicKey, final byte[] hashedSignature, final String wif, final String address, final String sign, final String sign16, final byte[] signed) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = findViewById(R.id.devInfo);
                                String message = "" +
                                        "Private Key:\n" + HexUtil.byteArrayToHex(privateKey) + "\n\n" +
                                        "Public Key:\n" + HexUtil.byteArrayToHex(publicKey) + "\n\n" +
                                        "Hashed Signature:\n" + HexUtil.byteArrayToHex(hashedSignature) + "\n\n" +
                                        "WIF:\n" + wif + "\n\n" +
                                        "Address:\n" + address + "\n\n" +
                                        "Sign:\n" + sign + "\n\n" +
                                        "Sign16:\n" + sign16 + "\n\n" +
                                        "Signed:\n" + HexUtil.byteArrayToHex(signed);
                                textView.setText(message);
                            }
                        });
                    }
                });
            }
        });
    }

    private void refreshCardUI() {
        View card;
        for (int cardIndex = 0; cardIndex < cardContainer.getChildCount(); cardIndex++) {
            card = cardContainer.getChildAt(cardIndex);
            refreshCardUI(card);
        }
    }

    private void refreshCardUI(View view) {
        Wallet wallet = application.getWallet();
        TextView cardHeader = view.findViewById(R.id.cardHeader);
        TextView chainBalance = view.findViewById(R.id.cardChainBalance);
        TextView channelBalance = view.findViewById(R.id.cardChannelBalance);
        TextView cardAddress = view.findViewById(R.id.cardAddress);

        if (wallet == null) {
            cardAddress.setText(getString(R.string.please_login));
        } else if (wallet.getPrivateKey() != null && wallet.getAddress() != null) {
            cardAddress.setText(wallet.getAddress());
        }

        String[] split = cardHeader.getText().toString().split("WALLET INFO\n");
        if (split.length > 0) {
            switch (split[split.length - 1]) {
                case "TNC":
                    chainBalance.setText(application.getChainTNC().toPlainString());
                    channelBalance.setText(application.getChannelTNC().toPlainString());
                    break;
                case "NEO":
                    chainBalance.setText(application.getChainNEO().toPlainString());
                    channelBalance.setText(application.getChannelNEO().toPlainString());
                    break;
                case "GAS":
                    chainBalance.setText(application.getChainGAS().toPlainString());
                    channelBalance.setText(application.getChannelGAS().toPlainString());
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
            currentActivity.refreshCardUI(rootView);
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
