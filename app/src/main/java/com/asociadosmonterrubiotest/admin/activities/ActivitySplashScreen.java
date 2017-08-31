package com.asociadosmonterrubiotest.admin.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.asociadosmonterrubiotest.admin.R;
import com.asociadosmonterrubiotest.admin.firebase.FireBaseQuery;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseDatabase.getInstance().getReference(FireBaseQuery.ASISTENCIAS).keepSynced(true);
        FirebaseDatabase.getInstance().getReference(FireBaseQuery.EMPLEADOS).keepSynced(true);
        FirebaseDatabase.getInstance().getReference(FireBaseQuery.SALIDAS).keepSynced(true);
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
