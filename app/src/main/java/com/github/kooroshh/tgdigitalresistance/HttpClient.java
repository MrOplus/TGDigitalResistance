package com.github.kooroshh.tgdigitalresistance;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Oplus on 2018/05/03.
 */

public class HttpClient {
    public static final String API_URL = "https://gist.githubusercontent.com/kooroshh/eb1861819caad12d67b0c9acfb12e0e4/raw/16cb94c63f2dfc5988e390b3149e1aa178ac08de/servers.json";
    private final OkHttpClient client = new OkHttpClient();
    public void Get(String url, final IResistanceServerResult result){

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                   result.onResistanceServerNotAvailable();
                   e.printStackTrace();
                }

                @Override public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        ResponseBody responseBody = response.body();
                        if (!response.isSuccessful())
                            result.onResistanceServerNotAvailable();

                        String responseText = responseBody.string();
                        Gson gson = new Gson();
                        ResistanceServer[] servers =  gson.fromJson(responseText,ResistanceServer[].class);
                        List<ResistanceServer> output = new ArrayList<>();
                        Collections.addAll(output, servers);
                        result.onResistanceServerAvailable(output);
                    }catch (Exception e){
                        e.printStackTrace();
                        result.onResistanceServerNotAvailable();
                    }
                }
            });

    }
}
