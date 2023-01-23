package com.detectslowconnection;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownloadInfoActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private String TAG = this.getClass().getSimpleName();
    private long startTime;
    private long endTime;
    private long fileSize;
    private View mRunningBar;
    private TextView timeTaken, kbPerSec, downloadSpeed, fSize, bandwidthType;

    // Bandwidth range in kbps copied from FBConnect Class
    private int POOR_BANDWIDTH = 150;
    private int AVERAGE_BANDWIDTH = 550;
    private int GOOD_BANDWIDTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.test_download_btn).setOnClickListener(testButtonClicked);
        mRunningBar = findViewById(R.id.download_running_bar);
        timeTaken = (TextView) findViewById(R.id.time_taken);
        kbPerSec = (TextView) findViewById(R.id.kilobyte_per_sec);
        downloadSpeed = (TextView) findViewById(R.id.download_speed);
        fSize = (TextView) findViewById(R.id.file_size);
        bandwidthType = (TextView) findViewById(R.id.bandwidth_type);

    }

    private void downloadInfo(){

        Request request = new Request.Builder()
                .url("IMAGE_URL_HERE") // replace image url
                .build();

        mRunningBar.setVisibility(View.VISIBLE);
        bandwidthType.setText("");
        startTime = System.currentTimeMillis();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                InputStream input = response.body().byteStream();

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];

                    while (input.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                    byte[] docBuffer = bos.toByteArray();
                    fileSize = bos.size();

                } finally {
                    input.close();
                }

                endTime = System.currentTimeMillis();

                // calculate how long it took by subtracting endtime from starttime

                final double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                final double timeTakenInSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                final int kilobytePerSec = (int) Math.round(1024 / timeTakenInSecs);
                final double speed = Math.round(fileSize / timeTakenMills);

                Log.d(TAG, "Time taken in secs: " + timeTakenInSecs);
                Log.d(TAG, "Kb per sec: " + kilobytePerSec);
                Log.d(TAG, "Download Speed: " + speed);
                Log.d(TAG, "File size in kb: " + fileSize);


                // update the UI with the speed test results
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRunningBar.setVisibility(View.GONE);
                        timeTaken.setText(getResources().getString(R.string.time_taken) + " " + String.valueOf(timeTakenInSecs));
                        kbPerSec.setText(getResources().getString(R.string.kilobyte_per_sec) + " " + String.valueOf(kilobytePerSec));
                        downloadSpeed.setText(getResources().getString(R.string.download_speed) + " " + String.valueOf(speed));
                        fSize.setText(getResources().getString(R.string.file_size) + " " + String.valueOf(fileSize));

                        if(kilobytePerSec <= POOR_BANDWIDTH){
                            // slow connection
                            bandwidthType.setText(getResources().getString(R.string.poor_bandwidth));

                        } else if (kilobytePerSec > POOR_BANDWIDTH && kilobytePerSec <= AVERAGE_BANDWIDTH){
                            // Average connection
                            bandwidthType.setText(getResources().getString(R.string.average_bandwidth));

                        } else if (kilobytePerSec > AVERAGE_BANDWIDTH && kilobytePerSec <= GOOD_BANDWIDTH){
                            // Fast connection
                            bandwidthType.setText(getResources().getString(R.string.good_bandwidth));

                        }

                    }
                });



            }
        });



    }

    private final View.OnClickListener testButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(cm.getActiveNetworkInfo().isConnected()) {

                downloadInfo();  // call downloadInfo to perform the download request

            } else {

                // display snack bar message
                String msg = getResources().getString(R.string.connection_error);
                Snackbar snack = Snackbar.make(v, msg, Snackbar.LENGTH_LONG).setAction("Action", null);
                ViewGroup group = (ViewGroup) snack.getView();
                group.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.error));
                snack.show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
