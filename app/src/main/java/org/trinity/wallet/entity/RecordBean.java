package org.trinity.wallet.entity;

import android.support.annotation.NonNull;

import org.trinity.wallet.net.websocket.ACRsmcBean;
import org.trinity.wallet.net.websocket.ACRsmcSignBean;

public class RecordBean {
    private String channel_Name;
    private String channel_Alias;
    private String assetName;
    private double price;
    private double fee;
    private ACRsmcSignBean sign_C_R_HeSigned;
    private ACRsmcBean sign_BR_HeSigned;

    public RecordBean(@NonNull ChannelBean channelBean, double price, double fee, @NonNull ACRsmcSignBean sign_C_R_HeSigned, @NonNull ACRsmcBean sign_BR_HeSigned) {
        this.channel_Name = channelBean.getName();
        this.channel_Alias = channelBean.getAlias();
        this.assetName = channelBean.getAssetName();
        this.price = price;
        this.fee = fee;
        this.sign_C_R_HeSigned = sign_C_R_HeSigned;
        this.sign_BR_HeSigned = sign_BR_HeSigned;
    }

    public String getChannel_Name() {
        return channel_Name;
    }

    public void setChannel_Name(String channel_Name) {
        this.channel_Name = channel_Name;
    }

    public String getChannel_Alias() {
        return channel_Alias;
    }

    public void setChannel_Alias(String channel_Alias) {
        this.channel_Alias = channel_Alias;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
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

    public ACRsmcSignBean getSign_C_R_HeSigned() {
        return sign_C_R_HeSigned;
    }

    public void setSign_C_R_HeSigned(ACRsmcSignBean sign_C_R_HeSigned) {
        this.sign_C_R_HeSigned = sign_C_R_HeSigned;
    }

    public ACRsmcBean getSign_BR_HeSigned() {
        return sign_BR_HeSigned;
    }

    public void setSign_BR_HeSigned(ACRsmcBean sign_BR_HeSigned) {
        this.sign_BR_HeSigned = sign_BR_HeSigned;
    }
}
