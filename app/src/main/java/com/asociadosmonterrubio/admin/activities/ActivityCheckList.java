package com.asociadosmonterrubio.admin.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.ChekListCountryside;
import com.asociadosmonterrubio.admin.utils.ChekListCountrysideSingleton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


public class ActivityCheckList extends AppCompatActivity implements EditText.OnEditorActionListener, View.OnClickListener{

    private EditText EditTextCodigoEmpleado, edt_check_list_single_date;
    private ImageButton btn_send;
    private ArrayList<ChekListCountryside> listado;
    private DatabaseReference refAsistenciasDeHoy;
    private TextView tv_total_asistencias;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_check_list_to_one);

        btn_send = (ImageButton) findViewById(R.id.btn_send);
        listado = ChekListCountrysideSingleton.getInstance().getChekListCountryside();
        EditTextCodigoEmpleado = (EditText) findViewById(R.id.edt_id_empleado);
        edt_check_list_single_date = (EditText) findViewById(R.id.edt_check_list_single_date);
        TextView tv_total_pase_de_lista = (TextView) findViewById(R.id.tv_total_check_list);
        tv_total_asistencias = (TextView) findViewById(R.id.tv_total_assistence);
        tv_total_pase_de_lista.setText(String.valueOf(listado.size()));

        edt_check_list_single_date.setText(getCurrentFormattedDate());
        edt_check_list_single_date.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        EditTextCodigoEmpleado.setOnEditorActionListener(this);
    }

    private ValueEventListener listenerAsistenciasDeHoy = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<DataSnapshot> lista_asistencias = new ArrayList<>();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                lista_asistencias.add(postSnapshot);
            }
            String total_asistencias = String.valueOf(lista_asistencias.size());
            tv_total_asistencias.setText(total_asistencias);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) { }
    };

    @Override
    protected void onResume() {
        super.onResume();
        refAsistenciasDeHoy = FireBaseQuery.obtenerAsistencias(getCurrentFormattedDate());
        refAsistenciasDeHoy.addValueEventListener(listenerAsistenciasDeHoy);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (refAsistenciasDeHoy != null){
            if (listenerAsistenciasDeHoy != null)
            refAsistenciasDeHoy.removeEventListener(listenerAsistenciasDeHoy);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == 0) {
            processCheckList();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edt_check_list_single_date:
                new DatePickerDialog(ActivityCheckList.this,
                        date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_check_list_single_date.getWindowToken(), 0);

                break;
            case R.id.btn_send:
                processCheckList();
                break;
        }

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            edt_check_list_single_date.setText(getCurrentFormattedDate());

            if (refAsistenciasDeHoy != null){
                if (listenerAsistenciasDeHoy != null)
                    refAsistenciasDeHoy.removeEventListener(listenerAsistenciasDeHoy);
            }

            refAsistenciasDeHoy = FireBaseQuery.obtenerAsistencias(getCurrentFormattedDate());
            refAsistenciasDeHoy.addValueEventListener(listenerAsistenciasDeHoy);
        }

    };

    private void processCheckList(){
        String input = EditTextCodigoEmpleado.getText().toString();
        if (!input.isEmpty()) {
            ChekListCountryside employee = isValidID(input);
            if (employee != null) {
                FireBaseQuery.PushCheckList(input, employee.getPerfil(), calendar);
                EditTextCodigoEmpleado.setOnEditorActionListener(null);
                EditTextCodigoEmpleado.setText("");
                EditTextCodigoEmpleado.setOnEditorActionListener(this);
            }else {
                EditTextCodigoEmpleado.setOnEditorActionListener(null);
                EditTextCodigoEmpleado.setText("");
                EditTextCodigoEmpleado.setOnEditorActionListener(this);
                Toast.makeText(this, "Numero  de empleado no existe", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ChekListCountryside isValidID(String ID){
        ChekListCountryside employee = null;
        Integer empleadoID = Integer.parseInt(ID);
        ArrayList<ChekListCountryside> paseDeLista = ChekListCountrysideSingleton.getInstance().getChekListCountryside();
        for (ChekListCountryside item : paseDeLista){
            if (empleadoID == item.getIdEmpleado())
                employee = item;
        }
        return employee;
    }

    private String getCurrentFormattedDate(){
        return calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }
}
