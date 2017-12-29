package com.asociadosmonterrubio.admin.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivityGirosPrestamos extends AppCompatActivity implements View.OnClickListener{


    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giros_prestamos);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            String kinfOf = getIntent().getStringExtra("tipo");
            getSupportActionBar().setTitle(kinfOf);
        }

        edt_loan_date.setText(getCurrentFormattedDate());
        edt_loan_date.setOnClickListener(this);
        btn_insert_loan.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_loan_date:
                new DatePickerDialog(this,
                        date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(edt_loan_date.getWindowToken(), 0);

                break;
            case R.id.btn_insert_loan:
                if (isValidFields()){
                    FireBaseQuery.pushGirosPrestamos(edt_loan_id.getText().toString(),
                            getCurrentFormattedDate(),
                            edt_loan_import.getText().toString(),
                            getIntent().getStringExtra("tipo"));
                    Toast.makeText(this, "El registro se ha guardado correctamente", Toast.LENGTH_SHORT).show();
                    edt_loan_import.getText().clear();
                    edt_loan_id.getText().clear();
                }
                break;
        }
    }

    private boolean isValidFields(){
        if (edt_loan_id.getText().toString().isEmpty()){
            Toast.makeText(this, "El ID del trabajador esta vacio", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edt_loan_import.getText().toString().isEmpty()){
            Toast.makeText(this, "La cantidad esta vacia", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edt_loan_import.getText().toString().isEmpty()){
            Toast.makeText(this, "La cantidad esta vacia", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(edt_loan_import.getText().toString()) <= 0 ){
            Toast.makeText(this, "El importe no es valido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            edt_loan_date.setText(getCurrentFormattedDate());
        }

    };

    private String getCurrentFormattedDate(){
        return calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Bind(R.id.edt_loan_date) EditText edt_loan_date;
    @Bind(R.id.edt_loan_id) EditText edt_loan_id;
    @Bind(R.id.edt_loan_import) EditText edt_loan_import;
    @Bind(R.id.btn_insert_loan) Button btn_insert_loan;
}
