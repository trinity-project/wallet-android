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

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        switchNetUrl(ConfigList.MAIN_NET_URL);
    }

    public void switchNetUrl(String netUrl) {
        WalletApplication.netUrl = netUrl;
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

