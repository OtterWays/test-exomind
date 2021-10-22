package com.otterways.kata;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class ProgressActivity extends AppCompatActivity {

    private final int MAX = 60;
    private final String[] CITY = {"Rennes", "Paris", "Nantes", "Bordeaux", "Lyon"};

    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Weather[] weathers = new Weather[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        doStartProgressBar();
    }

    private void doStartProgressBar() {
        progressBar.setMax(MAX);
        Thread thread = new Thread(() -> {
            for (int i = 0; i < MAX; i++) {

                if (i % 10 == 0) {
                    displayWeather(i / 10);
                }

                final int progress = i + 1;

                SystemClock.sleep(1000); // Sleep 1000 milliseconds = 1s.
                // Update interface.
                handler.post(() -> {
                    progressBar.setProgress(progress);
                    if (progress == MAX) { //progress finish

                    }
                });
            }
        });
        thread.start();
    }

    private void displayWeather(Integer i) {
        if (i >= CITY.length) {
            return;
        }

        WeatherTask weatherTask = new WeatherTask(this);
        weatherTask.execute(CITY[i], weather -> {
            if (weather != null) {
                weathers[i] = weather;
            }
        });
    }

    private interface WeatherListener {
        void onWeatherReceived(Weather weather);
    }

    //manage weather report
    private static class WeatherTask extends AsyncTask<String, Void, Weather> {
        private WeakReference<Context> weakContext;
        private String city;
        private WeatherListener listener;

        public WeatherTask(Context context) {
            weakContext = new WeakReference<>(context);
        }

        protected void execute(String city, WeatherListener listener) {
            this.listener = listener;
            super.execute(city);
        }

        @Override
        protected Weather doInBackground(String... strings) {
            if (strings != null && strings.length > 0 && weakContext.get() != null) {
                city = strings[0];
                JSONObject jsonObject = WebService.getWeather(weakContext.get(), city);
                if (jsonObject != null && jsonObject.length() > 0) {
                    Weather weather = new Weather();
                    try {
                        weather.setCity(Utils.getStringValueFromJSON(jsonObject, "name"));
                        JSONObject main = Utils.getJSONObjectFromJSON(jsonObject, "main");
                        weather.setTemp(Utils.getDoubleValueFromJSON(main, "temp"));
                        JSONArray jsonWeather = Utils.getJSONArrayFromJSON(jsonObject, "weather");
                        if (jsonWeather != null && jsonWeather.length() > 0) {
                            weather.setWeather(Utils.getStringValueFromJSON((JSONObject) jsonWeather.get(0), "main"));
                            weather.setIconWeather(Utils.getStringValueFromJSON((JSONObject) jsonWeather.get(0), "icon"));
                        }
                        return weather;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Weather result) {
            if (listener != null)
                listener.onWeatherReceived(result);
        }
    }
}


