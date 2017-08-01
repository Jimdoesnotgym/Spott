package com.example.chiangj.spott.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.example.chiangj.spott.R;

public class SplashScreenActivity extends Activity {

    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashTransitionIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(splashTransitionIntent);
            }
        }, 3000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
