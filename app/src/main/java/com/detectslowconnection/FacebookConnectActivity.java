package com.detectslowconnection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class FacebookConnectActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;
    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private ConnectionChangedListener mListener;
    private int mTries = 0;
    private String msg = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_connect);

        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();

        checkNetworkQuality();

    }

    public void checkNetworkQuality(){

        Request request = new Request.Builder()
                .url("IMAGE_URL_HERE")
                .build();

        mDeviceBandwidthSampler.startSampling();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                mDeviceBandwidthSampler.stopSampling();
                // Retry for up to 10 times until we find a ConnectionClass.
                if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
                    mTries++;
                    checkNetworkQuality();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.d(msg, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                Log.d(msg, response.body().string());
                Log.d(msg, mConnectionClassManager.getCurrentBandwidthQuality().toString());

                mDeviceBandwidthSampler.stopSampling();
            }
        });


        mListener = new ConnectionChangedListener();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mConnectionClassManager.remove(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectionClassManager.register(mListener);
    }



    // Listener to update the UI upon connectionclass change.
    private class ConnectionChangedListener
            implements ConnectionClassManager.ConnectionClassStateChangeListener {

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mConnectionClass = bandwidthState;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // do something
                }
            });
        }
    }



}
