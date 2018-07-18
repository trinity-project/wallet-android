package org.trinity.wallet.entity;

public class ChannelBean {
    private String name;
    private String TNAP;
    private String alias;
    private double deposit;
    private double balance;
    private String assetName;
    private String state;

    public ChannelBean(String name, String TNAP, String alias, double deposit, String assetName, String state) {
        this.name = name;
        this.TNAP = TNAP;
        this.alias = alias;
        this.deposit = deposit;
        this.balance = deposit;
        this.assetName = assetName;
        this.state = state;
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
}
