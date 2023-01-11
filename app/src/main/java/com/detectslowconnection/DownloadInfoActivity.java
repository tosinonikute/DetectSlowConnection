package com.detectslowconnection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DownloadInfoActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private String msg = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_info);


    }

    private void downloadInfo(){

        Request request = new Request.Builder()
                .url("IMAGE_URL_HERE")
                .build();

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
                    Log.d(msg, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                InputStream in = response.body().byteStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

//                String result, line = reader.readLine();
//                result = line;
//                while((line = reader.readLine()) != null) {
//                    result += line;
//                }
//                System.out.println(result);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while (reader.read() != -1) {
                    bos.write(buffer);
                }
                byte[] docbuffer = bos.toByteArray();
                int fileLength = docbuffer.length;


            }
        });
    }


}
