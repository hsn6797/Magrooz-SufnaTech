package com.sufnatech.magrooz.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.sufnatech.magrooz.R;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SplashTimeOut = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        //TODO: Splash Screen Here:
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent HomeIntent = new Intent(SplashScreenActivity.this,LoginActivity.class);
                startActivity(HomeIntent);
                finish();
            }
        },SplashTimeOut);
    }

}