package com.otterways.kata;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private final int MAX = 60;

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


}
