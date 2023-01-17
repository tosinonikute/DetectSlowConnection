package com.detectslowconnection;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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
    private double  minimumSecs = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.test_download_btn).setOnClickListener(testButtonClicked);


    }

    private void downloadInfo(){

        Request request = new Request.Builder()
                .url("URL_HERE")
                .build();

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

                double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                double timeTakenInSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                double kilobytePerSec = Math.round(1024 / timeTakenInSecs);

                if(timeTakenInSecs > minimumSecs){
                    // slow connection
                }

                // get the download speed by dividing the file size by time taken to download
                double speed = fileSize / timeTakenMills;

                Log.d(TAG, "Time taken in secs: " + timeTakenInSecs);
                Log.d(TAG, "kilobyte per sec: " + kilobytePerSec);
                Log.d(TAG, "Download Speed: " + speed);
                Log.d(TAG, "File size: " + fileSize);


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
