package com.asociadosmonterrubio.admin.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySplashScreen extends AppCompatActivity {

    private static final int REQUEST_STORAGE = 1;
    private static final int REQUEST_CAMERA = 2;

    private boolean isStorageEnabled = false;
    private boolean isCameraEnabled = false;

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
                checkPermissions();
            }
        }, 2000);
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            else
                isStorageEnabled = true;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            else
                isCameraEnabled = true;
        }else {
            initLogin();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                else
                    isStorageEnabled = true;

                break;

            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                else
                    isCameraEnabled = true;
                break;

        }

        if (isCameraEnabled && isStorageEnabled){
            initLogin();
        }

    }

    private void initLogin(){
        Intent intent = new Intent(ActivitySplashScreen.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }

}
