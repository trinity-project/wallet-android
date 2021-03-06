package org.trinity.wallet;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import org.trinity.util.android.QRCodeUtil;
import org.trinity.util.thread.ExecutorUtil;
import org.trinity.wallet.entity.BillBean;
import org.trinity.wallet.entity.ChannelBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import neoutils.Neoutils;
import neoutils.Wallet;

@SuppressLint("ApplySharedPref")
public final class WalletApplication extends Application {
    /**
     * The instance of app.
     */
    private static WalletApplication instance;
    private static Gson gson = new Gson();
    /**
     * The net url of neo.
     */
    private transient volatile static String net;
    private transient volatile static String netUrl;
    private transient volatile static String netUrlForNEO;
    private transient volatile static String magic;
    /**
     * The thread pool.
     */
    private static ExecutorService ioExecutor;

    static {
        ioExecutor = ExecutorUtil.getHighIOModeDynamicThreadPool();
    }

    /**
     * The identity verify.
     */
    private final String NOT_FIRST_TIME = "NOT_FIRST_TIME";
    private transient volatile boolean isIdentity;
    private transient String passwordOnRAM;
    /**
     * The wallet things.
     */
    private transient Wallet wallet;
    private Bitmap addressQR;
    private volatile Double chainTNC;
    private volatile Double channelTNC;
    private volatile Double chainNEO;
    private volatile Double channelNEO;
    private volatile Double chainGAS;
    private volatile Double channelGAS;
    /**
     * Key: Net type. Value: Bean.
     */
    private transient volatile List<Map<String, ChannelBean>> channelList;
    /**
     * Key: Net type. Value: Bean.
     */
    private transient volatile List<Map<String, BillBean>> billList;

    private SharedPreferences identityVerifyPrefs;

    // ====== STATIC GETTER ====== //

    public static WalletApplication getInstance() {
        return instance;
    }

    public static Gson getGson() {
        return gson;
    }

    public static ExecutorService getIoExecutor() {
        return ioExecutor;
    }

    public static String getNetUrl() {
        return netUrl;
    }

    public static String getNetUrlForNEO() {
        return netUrlForNEO;
    }

    public static String getMagic() {
        return magic;
    }

    // ====== LIFE CYCLE AND METHODS ====== //

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

    public synchronized void iAmFirstTime(@NonNull String newPassword) {
        SharedPreferences first_time_use = new SecurePreferences(this.getBaseContext(), NOT_FIRST_TIME, "not_first_time.xml");
        SharedPreferences.Editor editorFirstTimeUse = first_time_use.edit();
        editorFirstTimeUse.clear();
        editorFirstTimeUse.putString(NOT_FIRST_TIME, NOT_FIRST_TIME);
        editorFirstTimeUse.commit();

        String oldPassword = passwordOnRAM;
        boolean tranOldPref = oldPassword != null;

        Map<String, String> old_All_Map = null;
        if (tranOldPref) {
            SharedPreferences old_IDPrefs = identityVerifyPrefs;
            SharedPreferences.Editor old_Editor = old_IDPrefs.edit();

            old_Editor.remove(ConfigList.SAVE_USER_PASSWORD);
            old_Editor.commit();

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
            old_Editor.commit();
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
        new_Editor.commit();
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
            loadData();
        }
        editor.putString(ConfigList.SAVE_NET, net);
        editor.commit();
    }

    public synchronized void loadGlobal() {
        String savedWIF = identityVerifyPrefs.getString(ConfigList.SAVE_WALLET_KEY, null);
        if (savedWIF == null || "".equals(savedWIF)) {
            wallet = null;
        } else {
            Wallet walletFromWIF;
            try {
                walletFromWIF = Neoutils.generateFromWIF(savedWIF);
                this.wallet = walletFromWIF;
                addressQR = QRCodeUtil.encodeAsBitmap(walletFromWIF.getAddress(), ConfigList.QR_CODE_WIDTH, ConfigList.QR_CODE_HEIGHT);
                loadData();
            } catch (Exception ignored) {
                this.wallet = null;
            }
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
        editor.putString(ConfigList.SAVE_RECORD_LIST, gson.toJson(billList));
        editor.commit();
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
            billList = null;
        } else {
            class RecordListTypeToken extends TypeToken<ArrayList<HashMap<String, BillBean>>> {
            }
            billList = gson.fromJson(recordListJson, new RecordListTypeToken().getType());
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

    public synchronized void signOut() {
        wallet = null;
        addressQR = null;
        clearBalance();
        channelList = null;
        billList = null;
        saveGlobal();
    }

    public synchronized void signIn(Wallet wallet) {
        // Wallet object persistence.
        this.wallet = wallet;
        // Create the QR code.
        addressQR = QRCodeUtil.encodeAsBitmap(wallet.getAddress(), ConfigList.QR_CODE_WIDTH, ConfigList.QR_CODE_HEIGHT);
        // Save the wallet via user password.
        saveGlobal();
    }

    // ====== NORMAL GETTER SETTER ====== //

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

    public Bitmap getAddressQR() {
        return addressQR;
    }

    public void setAddressQR(Bitmap addressQR) {
        this.addressQR = addressQR;
    }

    public Double getChainTNC() {
        return chainTNC;
    }

    public void setChainTNC(Double chainTNC) {
        this.chainTNC = chainTNC;
    }

    public Double getChannelTNC() {
        return channelTNC;
    }

    public void setChannelTNC(Double channelTNC) {
        this.channelTNC = channelTNC;
    }

    public Double getChainNEO() {
        return chainNEO;
    }

    public void setChainNEO(Double chainNEO) {
        this.chainNEO = chainNEO;
    }

    public Double getChannelNEO() {
        return channelNEO;
    }

    public void setChannelNEO(Double channelNEO) {
        this.channelNEO = channelNEO;
    }

    public Double getChainGAS() {
        return chainGAS;
    }

    public void setChainGAS(Double chainGAS) {
        this.chainGAS = chainGAS;
    }

    public Double getChannelGAS() {
        return channelGAS;
    }

    public void setChannelGAS(Double channelGAS) {
        this.channelGAS = channelGAS;
    }

    public List<Map<String, ChannelBean>> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Map<String, ChannelBean>> channelList) {
        this.channelList = channelList;
        saveData();
    }

    public List<Map<String, BillBean>> getBillList() {
        return billList;
    }

    public void setBillList(List<Map<String, BillBean>> billList) {
        this.billList = billList;
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
