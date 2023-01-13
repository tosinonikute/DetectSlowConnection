package com.detectslowconnection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements android.view.View.OnClickListener{

    private Button networkTestButton;
    private Button downloadTestButton;
    private Button facebookTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        networkTestButton = (Button) findViewById(R.id.network_test_buttom);
        downloadTestButton =  (Button) findViewById(R.id.download_test_button);
        facebookTestButton =  (Button) findViewById(R.id.facebook_test_button);

        networkTestButton.setOnClickListener(this);
        downloadTestButton.setOnClickListener(this);
        facebookTestButton.setOnClickListener(this);

    }

    public void onClick(View v){

        Context context = v.getContext();

        if(v.getId() == R.id.network_test_buttom){
            Intent intent = new Intent(this, NetworkInfoActivity.class);
            startActivityForResult(intent, 500);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        if(v.getId() == R.id.download_test_button  ){
            Intent intent = new Intent(this, DownloadInfoActivity.class);
            startActivityForResult(intent, 500);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        if(v.getId() == R.id.facebook_test_button){
            Intent intent = new Intent(this, FacebookConnectActivity.class);
            startActivityForResult(intent, 500);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
