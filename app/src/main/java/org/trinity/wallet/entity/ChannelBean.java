package org.trinity.wallet.entity;

import android.support.annotation.NonNull;

import org.trinity.wallet.net.websocket.ACFounderSignBean;

public class ChannelBean {
    private String name;
    private String TNAP;
    private int txNonce;
    private String alias;
    private double deposit;
    private double balance;
    private String assetName;
    private String state;
    private ACFounderSignBean founderSign_HeSigned;

    public ChannelBean(@NonNull String name,
                       @NonNull String TNAP,
                       int txNonce,
                       @NonNull String alias,
                       double deposit,
                       double balance,
                       @NonNull String assetName,
                       @NonNull String state,
                       @NonNull ACFounderSignBean founderSign_HeSigned) {
        this.name = name;
        this.TNAP = TNAP;
        this.txNonce = txNonce;
        this.alias = alias;
        this.deposit = deposit;
        this.balance = balance;
        this.assetName = assetName;
        this.state = state;
        this.founderSign_HeSigned = founderSign_HeSigned;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTNAP() {
        return TNAP;
    }

    public void setTNAP(String TNAP) {
        this.TNAP = TNAP;
    }

    public int getTxNonce() {
        return txNonce;
    }

    public void setTxNonce(int txNonce) {
        this.txNonce = txNonce;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ACFounderSignBean getFounderSign_HeSigned() {
        return founderSign_HeSigned;
    }

    public void setFounderSign_HeSigned(ACFounderSignBean founderSign_HeSigned) {
        this.founderSign_HeSigned = founderSign_HeSigned;
    }
}
