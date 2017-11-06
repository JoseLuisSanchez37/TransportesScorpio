package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.adapters.ListEmployeeAdapter;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ActivityListEmployeesField extends AppCompatActivity implements AdapterView.OnItemClickListener{

	private ProgressDialog progressDialog;
	private ArrayList<Map<String, String>> employees;
	private ListEmployeeAdapter listEmployeeAdapter;
	private ListView list_employees;
    private boolean isSpecialField = false;
    private String currentFieldPath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_employees_field);
		employees = new ArrayList<>();
		list_employees = (ListView) findViewById(R.id.list_employees);
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle(getString(R.string.subtitle_listado_empleados_campo)  + " " + SingletonUser.getInstance().getUsuario().getCampo());

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Descargando...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		loadEmployeesFromField();
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
                currentFieldPath = pathEmployees;

				FireBaseQuery.databaseReference.child(pathEmployees).addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
                        employees.clear();
						for (DataSnapshot children : dataSnapshot.getChildren()){
							Map<String, String> childrenData = (Map<String, String>) children.getValue();
							Map<String, String> employee = new HashMap<>();
							employee.put("ID", children.getKey());
							employee.put("Nombre", childrenData.get("Nombre") != null ? childrenData.get("Nombre") : "");
							employee.put("Apellido_Paterno", childrenData.get("Apellido_Paterno") != null ? childrenData.get("Apellido_Paterno") : "");
							employee.put("Apellido_Materno", childrenData.get("Apellido_Materno") != null ? childrenData.get("Apellido_Materno") : "");
                            if (childrenData.containsKey("IDExterno"))
                                isSpecialField = true;
							employee.put("IDExterno",  childrenData.containsKey("IDExterno") ? childrenData.get("IDExterno") : "");
                            employee.put("pushId", childrenData.get("pushId"));
                            employee.put("Fecha_Nacimiento", childrenData.get("Fecha_Nacimiento"));
                            employee.put("Lugar_Nacimiento", childrenData.get("Lugar_Nacimiento"));
                            employee.put("CURP", childrenData.get("CURP"));
                            employee.put("Actividad", childrenData.get("Actividad"));
							employees.add(employee);
						}
						if (progressDialog != null)
							progressDialog.dismiss();

						if (employees.isEmpty()){
							Toast.makeText(ActivityListEmployeesField.this, "La lista de empleados esta vacia", Toast.LENGTH_SHORT).show();
						}else {
                            Collections.sort(employees, new Comparator<Map<String, String>>() {
                                @Override
                                public int compare(Map<String, String> map1, Map<String, String> map2) {
                                    return isSpecialField ? map1.get("IDExterno").compareToIgnoreCase(map2.get("IDExterno")) : map1.get("ID").compareToIgnoreCase(map2.get("ID"));
                                }
                            });
                            Collections.reverse(employees);
							listEmployeeAdapter = new ListEmployeeAdapter(ActivityListEmployeesField.this, employees);
							list_employees.setAdapter(listEmployeeAdapter);
                            list_employees.setOnItemClickListener(ActivityListEmployeesField.this);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						if (progressDialog != null)
							progressDialog.dismiss();
						Toast.makeText(ActivityListEmployeesField.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				if (progressDialog != null)
					progressDialog.dismiss();
				Toast.makeText(ActivityListEmployeesField.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ActivityUpdateInfoEmployee.class);
        intent.putExtra("employeeData", (Serializable) employees.get(position));
        intent.putExtra("path", currentFieldPath +"/"+ employees.get(position).get("ID"));
        startActivity(intent);
	}
}
