package com.asociadosmonterrubio.admin.activities;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.Employee;
import com.asociadosmonterrubio.admin.models.Usuario;
import com.asociadosmonterrubio.admin.utils.SingletonEmployees;
import com.asociadosmonterrubio.admin.adapters.EmployeeAdapter;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.forusers.heinsinputdialogs.HeinsInputDialog;
import br.com.forusers.heinsinputdialogs.interfaces.OnInputLongListener;
import butterknife.Bind;
import butterknife.ButterKnife;


public class ActivityDisplayEmployees extends AppCompatActivity {

    @Bind(R.id.checkbox_select_all) CheckBox checkbox_select_all;
    @Bind(R.id.empty) TextView empty;
    @Bind(R.id.recycler_employees) RecyclerView recycler_employees;

    private boolean goBackDiscardChanges = false;
    private EmployeeAdapter employeeAdapter;
    private String userRol = SingletonUser.getInstance().getUsuario().getRol();

    private ProgressDialog progressDialog;

    //Estos dos valores nos ayudan a realizar la consulta en Firebase
    //La temporada nos indica el nombre de la temporada actual.
    private String temporadaActual = "";
    private boolean isCampoEspecial = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_display_employees);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null){
            if (userRol.equals(Usuario.ROL_ENCARGADO_CAMPO))
                getSupportActionBar().setTitle(getString(R.string.title_new_worker_solo));
            else
                getSupportActionBar().setTitle(getString(R.string.title_new_worker));
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        init();

        obtenerTemporada();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (userRol.equals(Usuario.ROL_ENCARGADO_CAMPO))
            inflater.inflate(R.menu.menu_asignar_solo_a_campo, menu);
        else
            inflater.inflate(R.menu.menu_display_employees, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_employee:
                Intent intent = new Intent(this, ActivityNewEmployee.class);
                startActivity(intent);
                return true;

            case R.id.send_trip:
                if (employeeAdapter.getEmployeesSelected().isEmpty())
                    Toast.makeText(this, getString(R.string.empty_workers_selected), Toast.LENGTH_SHORT).show();
                else
                   setNumberTrip();
                return true;

            case R.id.asignar_solos:
                if (employeeAdapter.getEmployeesSelected().isEmpty())
                    Toast.makeText(this, getString(R.string.empty_workers_selected), Toast.LENGTH_SHORT).show();
                else {
                    if (!TextUtils.isEmpty(temporadaActual))
                        asignarEmpleadoSoloACampo();
                    else
                        Toast.makeText(this, "TEMPORADA ACTUAL NO ENCONTRADA", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onResume() {
        super.onResume();
        employeeAdapter.updateEmployees(SingletonEmployees.getInstance().getEmployees());
        refreshList();
    }

    @Override
    public void onBackPressed() {
        if (employeeAdapter.isEmpty() || goBackDiscardChanges)
            super.onBackPressed();
        else
            manageOnBackPressed();
    }

    private void init(){
        employeeAdapter = new EmployeeAdapter(SingletonEmployees.getInstance().getEmployees(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_employees.setLayoutManager(layoutManager);
        recycler_employees.setAdapter(employeeAdapter);
        checkbox_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                employeeAdapter.selectAllEmployees(isChecked);
            }
        });
        refreshList();
    }

    private void setNumberTrip(){
        HeinsInputDialog dialog = new HeinsInputDialog(this);
        dialog.setPositiveButton(new OnInputLongListener() {
            @Override
            public boolean onInputLong(AlertDialog dialog, Long busNumber) {
                setTrip(String.valueOf(busNumber));
                return false;
            }
        });
        dialog.setTitle(getString(R.string.input_bus_number));
        dialog.show();
    }

    public void refreshList(){
        if (employeeAdapter.isEmpty()) {
            recycler_employees.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            recycler_employees.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    private void asignarEmpleadoSoloACampo(){
        final String pathEmployees = FireBaseQuery.ASIGNACION_EMPLEADOS_CAMPO + "/" +
                SingletonUser.getInstance().getUsuario().getSede() + "/" +
                temporadaActual + "/" + //Esta es la temporada actual
                SingletonUser.getInstance().getUsuario().getCampo();

        FireBaseQuery.databaseReference.child("index").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long index = dataSnapshot.getValue(Long.class);
                setEmpleadoSoloACampo(pathEmployees, index);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityDisplayEmployees.this, "Ocurrio un error al obtener el index", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void obtenerTemporada(){
        String pathGetTemporadaActual = FireBaseQuery.TEMPORADAS_SEDES + "/" + SingletonUser.getInstance().getUsuario().getSede() + "/Temporada_Actual";
        FireBaseQuery.databaseReference.child(pathGetTemporadaActual).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                temporadaActual = dataSnapshot.getValue().toString();
                if (progressDialog != null)
                    progressDialog.dismiss();

                //Si es encargado de campo, entonces consultar si el tipo de asignación
                if (userRol.equals(Usuario.ROL_ENCARGADO_CAMPO)){
                    progressDialog.show();
                    FireBaseQuery.databaseReference
                            .child(FireBaseQuery.TEMPORADA_CAMPO)
                            .child(temporadaActual)
                            .child(SingletonUser.getInstance().getUsuario().getCampo())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            isCampoEspecial = dataSnapshot.getValue(String.class).equals("true");
                            if (progressDialog != null)
                                progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (progressDialog != null)
                                progressDialog.dismiss();
                            Toast.makeText(ActivityDisplayEmployees.this, "OCURRIO UN ERROR AL OBTENER EL TIPO DE CAMPO",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(ActivityDisplayEmployees.this, "OCURRIO UN ERROR AL OBTENER LA TEMPORADA ACTUAL",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEmpleadoSoloACampo(String pathRoot, long currentIndex){
        ArrayList<String> employeesSelected = employeeAdapter.getEmployeesSelected();
        ArrayList<Employee> employees = SingletonEmployees.getInstance().getEmployees();
        for (String key : employeesSelected){
            for (Employee employee : employees) {
                if (employee.getKey().equals(key)) {
                    currentIndex = currentIndex+1;
                    FireBaseQuery.pushEmployeeSoloToField(pathRoot, employee, currentIndex, isCampoEspecial);
                    employees.remove(employee);
                    break;
                }
            }
        }
        FireBaseQuery.databaseReference.child("index").setValue(currentIndex);
        employeeAdapter.updateEmployees(SingletonEmployees.getInstance().getEmployees());
        employeesSelected.clear();
        refreshList();
        Toast.makeText(this, "LOS EMPLEADOS SOLOS SE HAN ASIGNADO CORRECTAMENTE",Toast.LENGTH_SHORT).show();
    }

    private void setTrip(String busNumber){
        ArrayList<String> employeesSelected = employeeAdapter.getEmployeesSelected();
        ArrayList<Employee> employees = SingletonEmployees.getInstance().getEmployees();
        for (String key : employeesSelected){
            for (Employee employee : employees) {
                if (employee.getKey().equals(key)) {
                    FireBaseQuery.pushEmployeeToTrip(busNumber, key, employee.getNombre());
                    employees.remove(employee);
                    break;
                }
            }
        }
        employeeAdapter.updateEmployees(SingletonEmployees.getInstance().getEmployees());
        employeesSelected.clear();
        refreshList();
    }

    private void manageOnBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_exit_dialog));
        builder.setMessage(getString(R.string.message_exit_dialog));
        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goBackDiscardChanges = true;
                onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.continue_workers_register, null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}