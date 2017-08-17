package com.asociadosmonterrubio.admin.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.asociadosmonterrubio.admin.R;

import java.util.Calendar;


public class ActivityCheckList extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private DatePickerDialog datePickerDialog;
    private EditText edt_date;
    private Button btn_select_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_check_list_to_one);

        edt_date = (EditText) findViewById(R.id.edt_check_list_single_date);
        btn_select_date = (Button) findViewById(R.id.btn_select_date) ;

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(
                        ActivityCheckList.this,
                        ActivityCheckList.this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
