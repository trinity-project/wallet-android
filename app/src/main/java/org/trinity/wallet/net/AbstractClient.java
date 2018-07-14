package org.trinity.wallet.net;

import org.trinity.wallet.ConfigList;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

abstract class AbstractClient {
    protected static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .readTimeout(ConfigList.READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(ConfigList.WRITE_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(ConfigList.CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }
}
