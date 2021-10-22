package com.otterways.kata;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class ProgressActivity extends AppCompatActivity {

    private final int MAX = 60;
    private final String[] CITY = {"Rennes", "Paris", "Nantes", "Bordeaux", "Lyon"};
    private final String[] MESSAGE = {
            "Nous téléchargeons les données…",
            "C’est presque fini…",
            "Plus que quelques secondes avant d’avoir le résultat…"
    };

    private ProgressBar progressBar;
    private TextView textViewMessage;
    private TextView textViewBanquier;
    private AppCompatButton btnRestart;
    private TableLayout tableLayout;
    private Handler handler;
    private Weather[] weathers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        progressBar = findViewById(R.id.progressBar);
        textViewMessage = findViewById(R.id.message);
        textViewBanquier = findViewById(R.id.textViewBanquier);
        btnRestart = findViewById(R.id.btn_restart);
        tableLayout = findViewById(R.id.tableLayout);
        init();
    }

    private void init() {
        weathers = new Weather[5];
        handler = new Handler();
        progressBar.setProgress(0);
        textViewBanquier.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        textViewMessage.setVisibility(View.VISIBLE);
        tableLayout.setVisibility(View.GONE);
        btnRestart.setVisibility(View.GONE);
        doStartProgressBar();
    }

    private void doStartProgressBar() {
        progressBar.setMax(MAX);
        Thread thread = new Thread(() -> {
            for (int i = 0; i < MAX; i++) {

                if (i % 10 == 0) {
                    downloadWeather(i / 10);
                }
                if (i % 6 == 0) { //toutes les 6s
                    textViewMessage.setText(MESSAGE[i % 18 / 6]); //on ramène i à 0;6 ou 12 et on divise par 6 pour avoir 0;1 ou 2
                }

                final int progress = i + 1;

                SystemClock.sleep(1000); // Sleep 1000 milliseconds = 1s.
                // Update interface.
                handler.post(() -> {
                    progressBar.setProgress(progress);
                    if (progress == MAX) { //progress finish
                        progressBar.setVisibility(View.GONE);
                        textViewBanquier.setVisibility(View.GONE);
                        textViewMessage.setVisibility(View.GONE);
                        btnRestart.setVisibility(View.VISIBLE);
                        displayWeather();
                    }
                });
            }
        });
        thread.start();
    }

    //display weather report when downloading is finished
    private void displayWeather() {
        for (int i = 0; i < weathers.length; i++) {
            Weather weather = weathers[i];
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView tvCity = new TextView(this);
            tvCity.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            tvCity.setText(weather.getCity());
            tvCity.setBackground(AppCompatResources.getDrawable(this, R.drawable.border));

            TextView tvTemp = new TextView(this);
            tvTemp.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            String temp = weather.getTemp() + "°F";
            tvTemp.setText(temp);
            tvTemp.setBackground(AppCompatResources.getDrawable(this, R.drawable.border));
            tvTemp.setPadding(0,10,0,10);

            ImageView ivIcon = new ImageView(this);
            ivIcon.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            String urlIcon = "https://openweathermap.org/img/w/" + weather.getIconWeather() + ".png";
            Picasso.get().load(urlIcon).into(ivIcon);
            ivIcon.setBackground(AppCompatResources.getDrawable(this, R.drawable.border));

            tr.addView(tvCity);
            tr.addView(tvTemp);
            tr.addView(ivIcon);
            tr.setBackground(AppCompatResources.getDrawable(this, R.drawable.border));

            tableLayout.addView(tr);
        }
        tableLayout.setVisibility(View.VISIBLE);
    }

    //download the weather report for each city
    private void downloadWeather(Integer i) {
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

    public void clickRestart(View view) {
        init();
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


