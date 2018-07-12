package org.trinity.wallet;

import android.app.Application;

import org.trinity.wallet.entity.ChannelBean;
import org.trinity.wallet.entity.RecordBean;

import java.math.BigDecimal;
import java.util.List;

import neoutils.Wallet;

public final class WalletApplication extends Application {
    /**
     * The net url of neo.
     */
    private static String netUrl;
    private static String netUrlForNEO;
    private static WalletApplication instance;
    /**
     * This is the NEO wallet model.
     */
    private Wallet wallet;
    private BigDecimal chainTNC;
    private BigDecimal channelTNC;
    private BigDecimal chainNEO;
    private BigDecimal channelNEO;
    private BigDecimal chainGAS;
    private BigDecimal channelGAS;
    private List<ChannelBean> channelList;
    private List<RecordBean> recordList;

    public static String getNetUrl() {
        return netUrl;
    }

    public static WalletApplication getInstance() {
        return instance;
    }

    public static void setInstance(WalletApplication instance) {
        WalletApplication.instance = instance;
    }

    public static String getNetUrlForNEO() {
        return netUrlForNEO;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        switchNet(ConfigList.NET_TYPE_MAIN);
    }

    public void clearBalance() {
        chainTNC = null;
        channelTNC = null;
        chainNEO = null;
        channelNEO = null;
        chainGAS = null;
        channelGAS = null;
    }

    public void switchNet(String netType) {
        if (ConfigList.NET_TYPE_MAIN.equals(netType)) {
            netUrl = ConfigList.MAIN_NET_URL;
            netUrlForNEO = ConfigList.MAIN_NET_URL_FOR_NEO;
            ConfigList.ASSET_ID_MAP.put(ConfigList.ASSET_ID_MAP_KEY_TNC, ConfigList.ASSET_ID_TNC_MAIN);
        }
        if (ConfigList.NET_TYPE_TEST.equals(netType)) {
            netUrl = ConfigList.TEST_NET_URL;
            netUrlForNEO = ConfigList.TEST_NET_URL_FOR_NEO;
            ConfigList.ASSET_ID_MAP.put(ConfigList.ASSET_ID_MAP_KEY_TNC, ConfigList.ASSET_ID_TNC_TEST);
        }
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public BigDecimal getChainTNC() {
        return chainTNC;
    }

    public void setChainTNC(BigDecimal chainTNC) {
        this.chainTNC = chainTNC;
    }

    public BigDecimal getChannelTNC() {
        return channelTNC;
    }

    public void setChannelTNC(BigDecimal channelTNC) {
        this.channelTNC = channelTNC;
    }

    public BigDecimal getChainNEO() {
        return chainNEO;
    }

    public void setChainNEO(BigDecimal chainNEO) {
        this.chainNEO = chainNEO;
    }

    public BigDecimal getChannelNEO() {
        return channelNEO;
    }

    public void setChannelNEO(BigDecimal channelNEO) {
        this.channelNEO = channelNEO;
    }

    public BigDecimal getChainGAS() {
        return chainGAS;
    }

    public void setChainGAS(BigDecimal chainGAS) {
        this.chainGAS = chainGAS;
    }

    public BigDecimal getChannelGAS() {
        return channelGAS;
    }

    public void setChannelGAS(BigDecimal channelGAS) {
        this.channelGAS = channelGAS;
    }

    public List<ChannelBean> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<ChannelBean> channelList) {
        this.channelList = channelList;
    }

    public List<RecordBean> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<RecordBean> recordList) {
        this.recordList = recordList;
    }
}

