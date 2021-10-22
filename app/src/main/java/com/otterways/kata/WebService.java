package com.otterways.kata;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebService {

    private final static String baseURL = "https://api.openweathermap.org/data/2.5/weather?";
    private final static String TAG = "WS";

    public static JSONObject getWeather(Context context, String city) {
        JSONObject jsonObject = null;

        try {
            String urlString = baseURL;
            urlString += "q=";
            urlString += city;
            urlString += "&appid=";
            urlString += context.getResources().getString(R.string.api_key);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urlString)
                    .build();

            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            if (responseData != null && responseData.length() > 0) {
                jsonObject = new JSONObject(responseData);
            }

        } catch (Exception e) {
            Log.v(TAG, "Error in getArticle() : " + e.getMessage());
        }

        return jsonObject;
    }
}
