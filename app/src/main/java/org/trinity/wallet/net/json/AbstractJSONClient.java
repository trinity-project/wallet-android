package org.trinity.wallet.net.json;

import org.trinity.wallet.ConfigList;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

abstract class AbstractJSONClient {
    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .readTimeout(ConfigList.READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(ConfigList.WRITE_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(ConfigList.CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    private final MediaType MEDIA_TYPE = MediaType.parse(ConfigList.CLIENT_MEDIA_TYPE);

    void post(String url, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
