package com.asociadosmonterrubio.admin.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.asociadosmonterrubio.admin.R;

public class ActivitySplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startDelay();
    }

    private void startDelay(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(ActivitySplashScreen.this, ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

}
