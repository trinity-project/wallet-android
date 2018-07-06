package org.trinity.wallet.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.trinity.util.HexUtil;
import org.trinity.wallet.R;
import org.trinity.wallet.activity.card.SectionsPagerAdapter;
import org.trinity.wallet.logic.IDevCallback;
import org.trinity.wallet.logic.DevLogic;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

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
     * The body of dev tab.
     */
    @BindView(R.id.tabDev)
    public ConstraintLayout tabDev;

    /**
     * The framework of card views.
     */
    @BindView(R.id.cardsShell)
    public CoordinatorLayout cardsShell;
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
    private SectionsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Init events of tabs' click action.
        initTabs();

        // Init events of cards' click action.
        initCards();

        // #Dev init.
        devInit();
    }

    private void initTabs() {
        tab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                letAllTabsGone();
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

    private void letAllTabsGone() {
        tabTransfer.setVisibility(View.GONE);
        tabAddChannel.setVisibility(View.GONE);
        tabChannelList.setVisibility(View.GONE);
        tabRecord.setVisibility(View.GONE);
        tabDev.setVisibility(View.GONE);
    }

    private void initCards() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        cardContainer.setAdapter(pagerAdapter);
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

}
