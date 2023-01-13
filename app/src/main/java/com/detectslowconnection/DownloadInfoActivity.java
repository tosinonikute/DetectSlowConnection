package com.detectslowconnection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    private double  minimumSecs = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_info);


        downloadInfo();  // call downloadInfo to perform the download request
    }

    private void downloadInfo(){

        Request request = new Request.Builder()
                .url("IMAGE_URL_HERE")
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
                long fileLength;

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];

                    while (input.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                    byte[] docBuffer = bos.toByteArray();
                    fileLength = docBuffer.length;

                } finally {
                    input.close();
                }

                endTime = System.currentTimeMillis();

                // calculate how long it took by subtracting endtime from starttime

                double timeTakenInSecs = Math.floor(endTime - startTime) / 1000;  // divide by 1000 to get speed in seconds
                double kilobytePerSec = Math.round(1024 / timeTakenInSecs);

                if(timeTakenInSecs > minimumSecs){
                    // slow connection
                }

                Log.d(TAG, "Time taken in secs " + timeTakenInSecs);
                Log.d(TAG, "kilobyte per sec " + kilobytePerSec);


            }
        });
    }


}
