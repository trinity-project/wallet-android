package org.trinity.wallet;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import org.trinity.wallet.entity.ChannelBean;
import org.trinity.wallet.entity.RecordBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import neoutils.Neoutils;
import neoutils.Wallet;

public final class WalletApplication extends Application {

    /**
     * The net url of neo.
     */
    private transient volatile static String net;
    private transient volatile static String netUrl;
    private transient volatile static String netUrlForNEO;
    private transient volatile static String magic;
    private static WalletApplication instance;
    private static Gson gson = new Gson();
    private final String NOT_FIRST_TIME = "NOT_FIRST_TIME";
    /**
     * This is the NEO wallet model.
     */
    private transient volatile boolean isIdentity;
    private transient String passwordOnRAM;
    private transient Wallet wallet;
    private volatile BigDecimal chainTNC;
    private volatile BigDecimal channelTNC;
    private volatile BigDecimal chainNEO;
    private volatile BigDecimal channelNEO;
    private volatile BigDecimal chainGAS;
    private volatile BigDecimal channelGAS;
    /**
     * Key: Net type. Value: Bean.
     */
    private transient volatile List<Map<String, ChannelBean>> channelList;
    /**
     * Key: Net type. Value: Bean.
     */
    private transient volatile List<Map<String, RecordBean>> recordList;

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

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public synchronized boolean isFirstTime() {
        SharedPreferences first_time_use = new SecurePreferences(this.getBaseContext(), NOT_FIRST_TIME, "not_first_time.xml");
        String firstTimeUseString = first_time_use.getString(NOT_FIRST_TIME, null);
        return firstTimeUseString == null || !NOT_FIRST_TIME.equals(firstTimeUseString);
    }

    public synchronized void iAmNotFirstTime(@NonNull String newPassword) {
        SharedPreferences first_time_use = new SecurePreferences(this.getBaseContext(), NOT_FIRST_TIME, "not_first_time.xml");
        SharedPreferences.Editor editorFirstTimeUse = first_time_use.edit();
        editorFirstTimeUse.clear();
        editorFirstTimeUse.putString(NOT_FIRST_TIME, NOT_FIRST_TIME);
        editorFirstTimeUse.apply();

        String oldPassword = passwordOnRAM;
        boolean tranOldPref = oldPassword != null;

        Map<String, String> old_All_Map = null;
        if (tranOldPref) {
            SharedPreferences old_IDPrefs = identityVerifyPrefs;
            SharedPreferences.Editor old_Editor = old_IDPrefs.edit();

            old_Editor.remove(ConfigList.SAVE_USER_PASSWORD);
            old_Editor.apply();

            old_All_Map = new LinkedHashMap<>();
            String _SAVE_VALUE;
            for (String _SAVE_KEY : ConfigList.SAVE_LIST) {
                _SAVE_VALUE = old_IDPrefs.getString(_SAVE_KEY, null);
                if (_SAVE_VALUE == null) {
                    continue;
                }
                old_All_Map.put(_SAVE_KEY, _SAVE_VALUE);
            }

            old_Editor.clear();
            old_Editor.apply();
        }

        SharedPreferences new_IDPref = new SecurePreferences(this.getBaseContext(), newPassword, "user_prefs.xml");
        SharedPreferences.Editor new_Editor = new_IDPref.edit();

        if (tranOldPref) {
            for (String key : old_All_Map.keySet()) {
                String value = old_All_Map.get(key);
                new_Editor.putString(key, value);
            }
        }

        new_Editor.remove(ConfigList.SAVE_USER_PASSWORD);
        new_Editor.putString(ConfigList.SAVE_USER_PASSWORD, newPassword);
        new_Editor.apply();
        identityVerifyPrefs = new_IDPref;

        passwordOnRAM = newPassword;
    }

    public synchronized boolean isKeyFileOpen(String password) {
        identityVerifyPrefs = new SecurePreferences(this.getBaseContext(), password, "user_prefs.xml");
        String passwordInShare = identityVerifyPrefs.getString(ConfigList.SAVE_USER_PASSWORD, null);
        if (passwordInShare != null && password.equals(passwordInShare)) {
            passwordOnRAM = password;
            return true;
        } else {
            identityVerifyPrefs = null;
            return false;
        }
    }

    public synchronized void saveGlobal() {
        SharedPreferences.Editor editor = identityVerifyPrefs.edit();
        editor.remove(ConfigList.SAVE_WALLET_KEY);
        if (wallet != null) {
            editor.putString(ConfigList.SAVE_WALLET_KEY, wallet.getWIF());
        }
        editor.putString(ConfigList.SAVE_NET, net);
        editor.apply();
    }

    public synchronized void loadGlobal() {
        String savedWIF = identityVerifyPrefs.getString(ConfigList.SAVE_WALLET_KEY, null);
        if (savedWIF == null || "".equals(savedWIF)) {
            wallet = null;
        } else {
            Wallet walletFromWIF = null;
            try {
                walletFromWIF = Neoutils.generateFromWIF(savedWIF);
            } catch (Exception ignored) {
                this.wallet = null;
            }
            this.wallet = walletFromWIF;
        }
        net = identityVerifyPrefs.getString(ConfigList.SAVE_NET, ConfigList.NET_TYPE_TEST);
    }

    public synchronized void saveData() {
        // Use WIF not public key because channel alias info is private.
        SharedPreferences pref = new SecurePreferences(this.getBaseContext(), wallet.getWIF(), wallet.getAddress() + ".xml");
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(ConfigList.SAVE_CHANNEL_LIST);
        editor.remove(ConfigList.SAVE_RECORD_LIST);
        editor.putString(ConfigList.SAVE_CHANNEL_LIST, gson.toJson(channelList));
        editor.putString(ConfigList.SAVE_RECORD_LIST, gson.toJson(recordList));
        editor.apply();
    }

    public synchronized void loadData() {
        // Use WIF not public key because channel alias info is private.
        SharedPreferences pref = new SecurePreferences(this.getBaseContext(), wallet.getWIF(), wallet.getAddress() + ".xml");
        String channelListJson = pref.getString(ConfigList.SAVE_CHANNEL_LIST, null);
        String recordListJson = pref.getString(ConfigList.SAVE_RECORD_LIST, null);
        if (channelListJson == null) {
            channelList = null;
        } else {
            class ChannelListTypeToken extends TypeToken<ArrayList<HashMap<String, ChannelBean>>> {
            }
            channelList = gson.fromJson(channelListJson, new ChannelListTypeToken().getType());
        }
        if (recordListJson == null) {
            recordList = null;
        } else {
            class RecordListTypeToken extends TypeToken<ArrayList<HashMap<String, RecordBean>>> {
            }
            recordList = gson.fromJson(recordListJson, new RecordListTypeToken().getType());
        }
    }

    public synchronized void switchNet(String netType) {
        switch (netType) {
            case ConfigList.NET_TYPE_MAIN:
                net = netType;
                netUrl = ConfigList.MAIN_NET_URL;
                netUrlForNEO = ConfigList.MAIN_NET_URL_FOR_NEO;
                magic = ConfigList.MAIN_NET_MAGIC;
                ConfigList.ASSET_ID_MAP.put(ConfigList.ASSET_ID_MAP_KEY_TNC, ConfigList.ASSET_ID_TNC_MAIN);
                saveGlobal();
                break;
            case ConfigList.NET_TYPE_TEST:
                net = netType;
                netUrl = ConfigList.TEST_NET_URL;
                netUrlForNEO = ConfigList.TEST_NET_URL_FOR_NEO;
                magic = ConfigList.TEST_NET_MAGIC;
                ConfigList.ASSET_ID_MAP.put(ConfigList.ASSET_ID_MAP_KEY_TNC, ConfigList.ASSET_ID_TNC_TEST);
                saveGlobal();
                break;
        }
        if (wallet == null) {
            return;
        }

        loadData();
    }

    public synchronized void logOut() {
        wallet = null;
        clearBalance();
        channelList = null;
        recordList = null;
        saveGlobal();
    }

    public void clearBalance() {
        chainTNC = null;
        channelTNC = null;
        chainNEO = null;
        channelNEO = null;
        chainGAS = null;
        channelGAS = null;
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

    public List<Map<String, ChannelBean>> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Map<String, ChannelBean>> channelList) {
        this.channelList = channelList;
        saveData();
    }

    public List<Map<String, RecordBean>> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Map<String, RecordBean>> recordList) {
        this.recordList = recordList;
        saveData();
    }

    public boolean isIdentity() {
        return isIdentity;
    }

    public void setIdentity(boolean identity) {
        isIdentity = identity;
    }

    public String getNet() {
        return net;
    }
}
