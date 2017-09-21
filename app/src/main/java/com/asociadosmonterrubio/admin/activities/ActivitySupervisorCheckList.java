package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.adapters.CheckListSupervisorAdapter;
import com.asociadosmonterrubio.admin.adapters.ListViajesAdapter;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivitySupervisorCheckList extends AppCompatActivity implements AdapterView.OnItemClickListener{

	private String fechaSeleccionada, camionSeleccionado;
	private int backPressedCounter = 0;
	private ListView lv_list;
	private ListViajesAdapter adapter;
	private Map<String, DataSnapshot> mapSalidas;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_supervisor_check_list);
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle(R.string.title_supervisor_salidas);
		lv_list = (ListView) findViewById(R.id.lv_list);
		mapSalidas = new HashMap<>();
		getSalidas();
	}

	private void getSalidas(){
		FireBaseQuery.databaseReference.child(FireBaseQuery.SALIDAS).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				ArrayList<String> fechas = new ArrayList<>();
				for (DataSnapshot snapshot : dataSnapshot.getChildren()){
					fechas.add(snapshot.getKey());
					mapSalidas.put(snapshot.getKey(), snapshot);
				}

				if (getSupportActionBar() != null)
					getSupportActionBar().setTitle("Seleccione la fecha");

				if (fechas.isEmpty()){
					Toast.makeText(ActivitySupervisorCheckList.this, "No hay salidas disponibles", Toast.LENGTH_SHORT).show();
				}else {
					adapter = new ListViajesAdapter(ActivitySupervisorCheckList.this, fechas);
					lv_list.setAdapter(adapter);
					lv_list.setOnItemClickListener(ActivitySupervisorCheckList.this);

				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Toast.makeText(ActivitySupervisorCheckList.this, "Ocurrio un error al obtener al obtener las salidas. "
								.concat(databaseError.getDetails() != null ? databaseError.getDetails() : "")
						, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (backPressedCounter == 0) {
			fechaSeleccionada = (String) parent.getItemAtPosition(position);
			backPressedCounter++;
			getCamiones();
		}else if (backPressedCounter == 1){
			camionSeleccionado = (String) parent.getItemAtPosition(position);
			backPressedCounter++;
			getEmpleados();
		}
	}

	private void getCamiones(){
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle("Seleccione el camion");
		ArrayList<String> camiones = new ArrayList<>();
		for (DataSnapshot dataSnapshot : mapSalidas.get(fechaSeleccionada).getChildren()){
			camiones.add(dataSnapshot.getKey());
		}
		adapter = new ListViajesAdapter(ActivitySupervisorCheckList.this, camiones);
		lv_list.setAdapter(adapter);
		lv_list.setOnItemClickListener(ActivitySupervisorCheckList.this);
	}

	private void getEmpleados(){
		for (DataSnapshot dataSnapshotCamion : mapSalidas.get(fechaSeleccionada).getChildren()){
			if (camionSeleccionado.equals(dataSnapshotCamion.getKey())){
				final long lengthChildren = dataSnapshotCamion.getChildrenCount();

				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("Descargando...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(false);
				progressDialog.show();

				final ArrayList<Employee> employees = new ArrayList<>();

				for (DataSnapshot pushId : dataSnapshotCamion.getChildren()){
					FireBaseQuery.databaseReference.child(FireBaseQuery.EMPLEADOS).child(pushId.getKey()).addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							employees.add(dataSnapshot.getValue(Employee.class));
							if (employees.size() >= lengthChildren){
								if (progressDialog != null)
									progressDialog.dismiss();
								CheckListSupervisorAdapter checkListSupervisorAdapter = new CheckListSupervisorAdapter(ActivitySupervisorCheckList.this, employees);
								lv_list.setAdapter(checkListSupervisorAdapter);
								if (getSupportActionBar() != null)
									getSupportActionBar().setTitle("Fecha: ".concat(fechaSeleccionada).concat("  Camion: ".concat(camionSeleccionado)));
							}
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {
							Toast.makeText(ActivitySupervisorCheckList.this, "Ocurrio un error al obtener al obtener los empleados. "
											.concat(databaseError.getDetails() != null ? databaseError.getDetails() : "")
									, Toast.LENGTH_SHORT).show();
						}
					});
				}
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		switch (backPressedCounter){
			case 0:
				super.onBackPressed();
				break;
			case 1:
				getSalidas();
				backPressedCounter--;
				break;
			case 2:
				getCamiones();
				backPressedCounter--;
				break;

		}


	}
}
