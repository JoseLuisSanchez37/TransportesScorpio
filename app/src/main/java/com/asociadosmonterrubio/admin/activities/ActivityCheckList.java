package com.asociadosmonterrubio.admin.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.ChekListCountryside;
import com.asociadosmonterrubio.admin.utils.ChekListCountrysideSingleton;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static com.asociadosmonterrubio.admin.firebase.FireBaseQuery.ASISTENCIAS;


public class ActivityCheckList extends AppCompatActivity implements EditText.OnEditorActionListener{

    private EditText EditTextCodigoEmpleado;
    private ArrayList<ChekListCountryside> listado;
    private DatabaseReference refAsistenciasDeHoy;
    private TextView tv_total_asistencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_check_list_to_one);

        listado = ChekListCountrysideSingleton.getInstance().getChekListCountryside();
        EditTextCodigoEmpleado = (EditText) findViewById(R.id.edt_id_empleado);
        TextView tv_total_pase_de_lista = (TextView) findViewById(R.id.tv_total_check_list);
        tv_total_asistencias = (TextView) findViewById(R.id.tv_total_assistence);
        tv_total_pase_de_lista.setText(String.valueOf(listado.size()));

        EditTextCodigoEmpleado.setOnEditorActionListener(this);
    }

    private void obtenerAsistenciaDeHoy(){
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        String campo = SingletonUser.getInstance().getUsuario().getCampo();
        String sede = SingletonUser.getInstance().getUsuario().getSede();
        String path = ASISTENCIAS + "/" +  sede + "/" + campo + "/" + date ;
        refAsistenciasDeHoy = FireBaseQuery.databaseReference.child(path);
        refAsistenciasDeHoy.addValueEventListener(listenerAsistenciasDeHoy);
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
        obtenerAsistenciaDeHoy();
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
            if (!EditTextCodigoEmpleado.getText().toString().isEmpty()) {
                FireBaseQuery.PushCheckList(EditTextCodigoEmpleado.getText().toString(), "Jornalero");
                EditTextCodigoEmpleado.setOnEditorActionListener(null);
                EditTextCodigoEmpleado.setText("");
                EditTextCodigoEmpleado.setOnEditorActionListener(this);
            }
        }
        return true;
    }
}
