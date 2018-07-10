package org.trinity.wallet;

import android.app.Application;

import java.math.BigDecimal;

import neoutils.Wallet;

public class WalletApplication extends Application {
    /**
     * This is the NEO wallet model.
     */
    private Wallet wallet;

    private BigDecimal chainTNC = BigDecimal.ZERO;
    private BigDecimal channelTNC = BigDecimal.ZERO;
    private BigDecimal chainNEO = BigDecimal.ZERO;
    private BigDecimal channelNEO = BigDecimal.ZERO;
    private BigDecimal chainGAS = BigDecimal.ZERO;
    private BigDecimal channelGAS = BigDecimal.ZERO;

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
}
