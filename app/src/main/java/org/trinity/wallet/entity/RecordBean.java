package org.trinity.wallet.entity;

import android.support.annotation.NonNull;

public class RecordBean {
    private ChannelBean channelBean;
    private String channel_alias;
    private double price;
    private double fee;

    public RecordBean(@NonNull ChannelBean channelBean, double price, double fee) {
        this.channelBean = channelBean;
        this.channel_alias = channelBean.getAlias();
        this.price = price;
        this.fee = fee;
    }

    public ChannelBean getChannelBean() {
        return channelBean;
    }

    public void setChannelBean(ChannelBean channelBean) {
        this.channelBean = channelBean;
    }

    public String getChannel_alias() {
        return channel_alias;
    }

    public void setChannel_alias(String channel_alias) {
        this.channel_alias = channel_alias;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
