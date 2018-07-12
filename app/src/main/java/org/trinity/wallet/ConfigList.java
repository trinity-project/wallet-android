package org.trinity.wallet;

import java.util.HashMap;
import java.util.Map;

public final class ConfigList {
    public static final String MAIN_NET_URL = "http://47.96.175.193:21332";
    public static final String MAIN_NET_URL_FOR_NEO = "http://47.96.175.193:10332";
    public static final String TEST_NET_URL = "http://47.254.64.251:21332";
    public static final String TEST_NET_URL_FOR_NEO = "http://47.254.64.251:20332";
    public static final int COIN_TYPE_ACCOUNT = 3;
    public static final int SIGN_IN_RESULT = 0;
    public static final int SIGN_OUT_RESULT = 1;
    public static final int READ_TIME_OUT = 20;
    public static final int WRITE_TIME_OUT = 20;
    public static final int CONNECT_TIME_OUT = 20;
    public static final String CLIENT_MEDIA_TYPE = "application/json; charset=utf-8";
    public static final int RETRY_TIMES = 3;
    public static final int NEO_ADDRESS_LENGTH = 34;
    public static final String NEO_ADDRESS_FIRST = "A";
    public static final String NET_TYPE_MAIN = "MAIN";
    public static final String NET_TYPE_TEST = "TEST";
    public static final Map<String, String> ASSET_ID_MAP = new HashMap<>();
    public static final String ASSET_ID_MAP_KEY_TNC = "TNC";
    public static final String ASSET_ID_MAP_KEY_NEO = "NEO";
    public static final String ASSET_ID_MAP_KEY_GAS = "GAS";
    public static final String ASSET_ID_TNC_MAIN = "0x08e8c4400f1af2c20c28e0018f29535eb85d15b6";
    public static final String ASSET_ID_TNC_TEST = "0x849d095d07950b9e56d0c895ec48ec5100cfdff1";
    public static final String ASSET_ID_NEO = "0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";
    public static final String ASSET_ID_GAS = "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7";

    static {
        ASSET_ID_MAP.put(ASSET_ID_MAP_KEY_TNC, ASSET_ID_TNC_MAIN);
        ASSET_ID_MAP.put(ASSET_ID_MAP_KEY_NEO, ASSET_ID_NEO);
        ASSET_ID_MAP.put(ASSET_ID_MAP_KEY_GAS, ASSET_ID_GAS);
    }
}
