package com.asociadosmonterrubio.admin.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityQuitEmployee extends AppCompatActivity {

    private Spinner spinner_motivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit_employee);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_quit_employee);
        spinner_motivos = (Spinner) findViewById(R.id.spinner_motivos);
        getReasons();
    }

    private void getReasons(){
		FireBaseQuery.databaseReference.child(FireBaseQuery.MOTIVOS_BAJA).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				displayData(getData(dataSnapshot));
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
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

            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
                String reason = "The reason is " + parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), reason, Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> arg0) { }
        });
	}

}
