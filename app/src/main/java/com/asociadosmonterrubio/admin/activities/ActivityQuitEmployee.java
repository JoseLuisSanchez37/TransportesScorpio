package com.asociadosmonterrubio.admin.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.asociadosmonterrubio.admin.R;

public class ActivityQuitEmployee extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit_employee);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_quit_employee);
    }
}
