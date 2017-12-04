package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.asociadosmonterrubio.admin.R;

import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.asociadosmonterrubio.admin.utils.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivityQuitEmployee extends AppCompatActivity{

    private ProgressDialog progressDialog;
    private Map<String, String> employeeFound;
    private ArrayList<Map<String, String>> employees;
    private String reason_selected;
    private boolean isSpecialField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit_employee);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_quit_employee);
        spinner_motivos = (Spinner) findViewById(R.id.spinner_motivos);
        employees = new ArrayList<>();
        isSpecialField = false;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        loadEmployeesFromField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fire_employee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fire_employee:
                if (areAllDataCompleted()){
                    Map<String, String> data = new HashMap<>();
                    data.put("IDExterno", employeeFound.get("IDExterno"));
                    data.put("motivo", reason_selected);
                    data.put("observaciones", edt_notes.getText().toString());
                    FireBaseQuery.sendEmployeePendingToBeFired(employeeFound.get("ID"), data);
                    Toast.makeText(this, "Se agrego el trabajador a la lista de bajas pendientes", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getReasons(){
		FireBaseQuery.databaseReference.child(FireBaseQuery.MOTIVOS_BAJA).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
                if (progressDialog != null) progressDialog.dismiss();
				displayData(getData(dataSnapshot));
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
                if (progressDialog != null) progressDialog.dismiss();
				Toast.makeText(ActivityQuitEmployee.this, "Ocurrio un error al obtener los motivos de baja", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private ArrayList<String> getData(DataSnapshot dataSnapshot){
		ArrayList<String> reasons = new ArrayList<>();
		for (DataSnapshot snapshot : dataSnapshot.getChildren())
			reasons.add(String.valueOf(snapshot.getValue()));
		return reasons;
	}

	private void displayData(ArrayList<String> data){
        ArrayAdapter<String> adp = new ArrayAdapter<> (this, R.layout.spinner_item, data);
        spinner_motivos.setAdapter(adp);
        spinner_motivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reason_selected = (String) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
	}

	private boolean areAllDataCompleted(){
        employeeFound = Util.isValidID(edt_id_empleado.getText().toString(), employees);
        if (employeeFound == null) {
            Toast.makeText(this, "El ID ingresado no es valido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (reason_selected == null) {
            Toast.makeText(this, "El motivo seleccionado no es válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void loadEmployeesFromField(){
        String pathGetTemporadaActual = FireBaseQuery.TEMPORADAS_SEDES + "/" + SingletonUser.getInstance().getUsuario().getSede() + "/Temporada_Actual";
        FireBaseQuery.databaseReference.child(pathGetTemporadaActual).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pathEmployees = FireBaseQuery.ASIGNACION_EMPLEADOS_CAMPO + "/" +
                        SingletonUser.getInstance().getUsuario().getSede() + "/" +
                        dataSnapshot.getValue().toString() + "/" + //Esta es la temporada actual
                        SingletonUser.getInstance().getUsuario().getCampo();

                FireBaseQuery.databaseReference.child(pathEmployees).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        employees.clear();
                        for (DataSnapshot children : dataSnapshot.getChildren()){
                            Map<String, String> childrenData = (Map<String, String>) children.getValue();
                            Map<String, String> employee = new HashMap<>();
                            employee.put("ID", children.getKey());
                            employee.put("CURP", childrenData.get("CURP"));
                            employee.put("Nombre", childrenData.get("Nombre"));
                            employee.put("pushId", childrenData.get("pushId"));
                            employee.put("Actividad", childrenData.get("Actividad"));
                            employee.put("Apellido_Paterno", childrenData.get("Apellido_Paterno"));
                            employee.put("Apellido_Materno", childrenData.get("Apellido_Materno"));
                            employee.put("Fecha_Nacimiento", childrenData.get("Fecha_Nacimiento"));
                            employee.put("Lugar_Nacimiento", childrenData.get("Lugar_Nacimiento"));
                            employee.put("IDExterno",  childrenData.containsKey("IDExterno") ? childrenData.get("IDExterno") : "");
                            employees.add(employee);
                            Log.d("employee", employee.toString());
                        }

                        if (employees.isEmpty()) {
                            if (progressDialog != null) progressDialog.dismiss();
                            Toast.makeText(ActivityQuitEmployee.this, "La lista de empleados esta vacia", Toast.LENGTH_SHORT).show();
                        }else
                            getReasons();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (progressDialog != null) progressDialog.dismiss();
                        Toast.makeText(ActivityQuitEmployee.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(ActivityQuitEmployee.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Bind(R.id.spinner_motivos) Spinner spinner_motivos;
    @Bind(R.id.edt_id_employee) TextInputEditText edt_id_empleado;
    @Bind(R.id.edt_notes) TextInputEditText edt_notes;

}
