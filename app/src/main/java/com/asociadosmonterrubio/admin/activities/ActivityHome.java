package com.asociadosmonterrubio.admin.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.adapters.HomeAdapter;

public class ActivityHome extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_home);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new HomeAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(ActivityHome.this, ActivityDisplayEmployees.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(ActivityHome.this, ActivityCheckList.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                handler.removeCallbacks(null);
            }
        }, 2000);
    }
}
