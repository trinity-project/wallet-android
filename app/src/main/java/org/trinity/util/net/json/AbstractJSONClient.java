package org.trinity.util.net.json;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

abstract class AbstractJSONClient {
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .connectTimeout(2, TimeUnit.SECONDS)
            .build();
    private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    public String post(String url, String json) throws IOException {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        ResponseBody responseBody = response.body();
        if (responseBody != null) return responseBody.string();
        else return null;
    }
}
