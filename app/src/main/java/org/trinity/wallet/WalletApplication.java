package org.trinity.wallet;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.securepreferences.SecurePreferences;

import org.trinity.wallet.entity.ChannelBean;
import org.trinity.wallet.entity.RecordBean;

import java.math.BigDecimal;
import java.util.List;

import neoutils.Neoutils;
import neoutils.Wallet;

public final class WalletApplication extends Application {
    /**
     * The net url of neo.
     */
    private static String net;
    private static String netUrl;
    private static String netUrlForNEO;
    private static String magic;
    private static WalletApplication instance;
    private static Gson gson = new Gson();
    private final String NOT_FIRST_TIME = "NOT_FIRST_TIME";
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
    private boolean isIdentity;
    private String passwordOnRAM;
    private SharedPreferences identityVerifyPrefs;

    public static Gson getGson() {
        return gson;
    }

    public static String getNetUrl() {
        return netUrl;
    }

    public static WalletApplication getInstance() {
        return instance;
    }

    public static String getNetUrlForNEO() {
        return netUrlForNEO;
    }

    public static String getMagic() {
        return magic;
    }

    public boolean isFirstTime() {
        SharedPreferences first_time_use = new SecurePreferences(this.getBaseContext(), NOT_FIRST_TIME, "first_time_use.xml");
        String firstTimeUseString = first_time_use.getString(NOT_FIRST_TIME, null);
        return firstTimeUseString == null || !NOT_FIRST_TIME.equals(firstTimeUseString);
    }

    public void iAmNotFirstTime(@Nullable String oldPassword, @NonNull String newPassword) {
        SharedPreferences first_time_use = new SecurePreferences(this.getBaseContext(), NOT_FIRST_TIME, "first_time_use.xml");
        SharedPreferences.Editor editorFirstTimeUse = first_time_use.edit();
        editorFirstTimeUse.clear();
        editorFirstTimeUse.putString(NOT_FIRST_TIME, NOT_FIRST_TIME);
        editorFirstTimeUse.apply();

        identityVerifyPrefs = new SecurePreferences(this.getBaseContext(), newPassword, "user_prefs.xml");

        if (oldPassword != null) {
            // TODO move things from old to new and then delete old.
        }

        SharedPreferences.Editor editorIdentityVerify = identityVerifyPrefs.edit();
        editorIdentityVerify.remove(ConfigList.USER_PASSWORD_KEY);
        editorIdentityVerify.putString(ConfigList.USER_PASSWORD_KEY, newPassword);
        editorIdentityVerify.apply();
    }

    public boolean isKeyFileOpen(String password) {
        identityVerifyPrefs = new SecurePreferences(this.getBaseContext(), password, "user_prefs.xml");
        String passwordInShare = identityVerifyPrefs.getString(ConfigList.USER_PASSWORD_KEY, null);
        if (passwordInShare != null && password.equals(passwordInShare)) {
            return true;
        } else {
            identityVerifyPrefs = null;
            return false;
        }
    }

    public void saveIdentity() {
        SharedPreferences.Editor editor = identityVerifyPrefs.edit();
        editor.remove(ConfigList.SAVE_KEY);
        boolean isValidWIF = wallet != null && wallet.getWIF() != null && !"".equals(wallet.getWIF()) && wallet.getAddress() != null && !"".equals(wallet.getAddress());
        if (isValidWIF) {
            editor.putString(ConfigList.SAVE_KEY, wallet.getWIF());
        }
        editor.apply();
    }

    public void loadIdentity() {
        String secureWIF = identityVerifyPrefs.getString(ConfigList.SAVE_KEY, null);
        if (secureWIF == null || "".equals(secureWIF)) {
            return;
        }
        Wallet wifGenWallet;
        try {
            wifGenWallet = Neoutils.generateFromWIF(secureWIF);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (wifGenWallet == null || wifGenWallet.getAddress() == null || "".equals(wifGenWallet.getAddress())) {
            return;
        }
        wallet = wifGenWallet;
    }

    public void saveData() {
        // TODO channel list record list
        if (ConfigList.NET_TYPE_MAIN.equals(net)) {
            return;
        }
        if (ConfigList.NET_TYPE_TEST.equals(net)) {
            return;
        }
    }

    public void loadData() {
        // TODO channel list record list
        if (ConfigList.NET_TYPE_MAIN.equals(net)) {
            return;
        }
        if (ConfigList.NET_TYPE_TEST.equals(net)) {
            return;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // TODO save net state
//        switchNet(net);
        switchNet(ConfigList.NET_TYPE_MAIN);
    }

    public void logOut() {
        clearBalance();
        channelList = null;
        recordList = null;
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
            net = netType;
            netUrl = ConfigList.MAIN_NET_URL;
            netUrlForNEO = ConfigList.MAIN_NET_URL_FOR_NEO;
            magic = ConfigList.MAIN_NET_MAGIC;
            ConfigList.ASSET_ID_MAP.put(ConfigList.ASSET_ID_MAP_KEY_TNC, ConfigList.ASSET_ID_TNC_MAIN);
            loadData();
            return;
        }
        if (ConfigList.NET_TYPE_TEST.equals(netType)) {
            net = netType;
            netUrl = ConfigList.TEST_NET_URL;
            netUrlForNEO = ConfigList.TEST_NET_URL_FOR_NEO;
            magic = ConfigList.TEST_NET_MAGIC;
            ConfigList.ASSET_ID_MAP.put(ConfigList.ASSET_ID_MAP_KEY_TNC, ConfigList.ASSET_ID_TNC_TEST);
            loadData();
            return;
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
        saveIdentity();
        this.channelList = channelList;
    }

    public List<RecordBean> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<RecordBean> recordList) {
        saveIdentity();
        this.recordList = recordList;
    }

    public void setIsIdentity(boolean isIdentity) {
        this.isIdentity = isIdentity;
    }

    public boolean isIdentity() {
        return isIdentity;
    }

    public void setIdentity(boolean isIdentity) {
        this.isIdentity = isIdentity;
    }

    public String getPasswordOnRAM() {
        return passwordOnRAM;
    }

    public void setPasswordOnRAM(String passwordOnRAM) {
        this.passwordOnRAM = passwordOnRAM;
    }
}
