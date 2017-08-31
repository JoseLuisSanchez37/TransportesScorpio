package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.adapters.ListEmployeeAdapter;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityListEmployeesField extends AppCompatActivity {

	private ProgressDialog progressDialog;
	private ArrayList<Map<String, String>> employees;
	private ListEmployeeAdapter listEmployeeAdapter;
	private ListView list_employees;

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
		FireBaseQuery.databaseReference.child(pathGetTemporadaActual).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String pathEmployees = FireBaseQuery.ASIGNACION_EMPLEADOS_CAMPO + "/" +
						SingletonUser.getInstance().getUsuario().getSede() + "/" +
 						dataSnapshot.getValue().toString() + "/" + //Esta es la temporada actual
						SingletonUser.getInstance().getUsuario().getCampo();

				FireBaseQuery.databaseReference.child(pathEmployees).addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for (DataSnapshot children : dataSnapshot.getChildren()){
							Map<String, String> childrenData = (Map<String, String>) children.getValue();
							Map<String, String> employee = new HashMap<>();
							employee.put("ID", children.getKey());
							employee.put("Nombre", childrenData.get("Nombre") != null ? childrenData.get("Nombre") : "");
							employee.put("Apellido_Paterno", childrenData.get("Apellido_Paterno") != null ? childrenData.get("Apellido_Paterno") : "");
							employee.put("Apellido_Materno", childrenData.get("Apellido_Materno") != null ? childrenData.get("Apellido_Materno") : "");
							employee.put("IDExterno",  childrenData.containsKey("IDExterno") ? childrenData.get("IDExterno") : "");
							employees.add(employee);
						}
						if (progressDialog != null)
							progressDialog.dismiss();

						if (employees.isEmpty()){
							Toast.makeText(ActivityListEmployeesField.this, "La lista de empleados esta vacia", Toast.LENGTH_SHORT).show();
						}else {
							listEmployeeAdapter = new ListEmployeeAdapter(ActivityListEmployeesField.this, employees);
							list_employees.setAdapter(listEmployeeAdapter);
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

}
