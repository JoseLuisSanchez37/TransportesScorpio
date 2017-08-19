package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.adapters.HomeAdapter;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.ChekListCountryside;
import com.asociadosmonterrubio.admin.utils.ChekListCountrysideSingleton;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ActivityHome extends AppCompatActivity {

    private boolean isPaseDeListaAvailable = true;
    private boolean doubleBackToExitPressedOnce = false;
    ArrayList<String> campos;
    String sede;
    AlertDialog alertDialog1;
    String CampoSeleccionado;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_home);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new HomeAdapter(this));

        campos = SingletonUser.getInstance().getUsuario().getCampos();
        sede = SingletonUser.getInstance().getUsuario().getSede();
        if (campos.isEmpty()){
            isPaseDeListaAvailable = false;
        }else if (campos.size() == 1){
            CampoSeleccionado = campos.get(0);
            SingletonUser.getInstance().getUsuario().setCampo(CampoSeleccionado);
            getEmpleadosPaseLista(CampoSeleccionado,sede);
        }else {
            CreacionDeDialogo();
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(ActivityHome.this, ActivityDisplayEmployees.class);
                        startActivity(intent);
                        break;
                    case 1:
                        if (isPaseDeListaAvailable) {
                            intent = new Intent(ActivityHome.this, ActivityCheckList.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(ActivityHome.this, "No se puede pasar lista ya que no cuenta con ningun campo asignado", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });

    }


    public void getEmpleadosPaseLista(String campo,String sede){
        progressDialog = new ProgressDialog(ActivityHome.this);
        progressDialog.setMessage("Descargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        FireBaseQuery.databaseReference.child(FireBaseQuery.PASE_DE_LISTA).child(sede).child(campo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ChekListCountryside listado = new ChekListCountryside();
                    listado.setIdEmpleado(Integer.parseInt(ds.getKey()));
                    listado.setPerfil(ds.getValue(String.class));
                    ChekListCountrysideSingleton.getInstance().add(listado);
                    Log.d("gettingCheckList", "ID: "+listado.getIdEmpleado() +"  -  "+ "PERFIL: " + listado.getPerfil());
                }
                if (progressDialog != null)
                    progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.getCode();
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
    }


    public void CreacionDeDialogo(){
        String [] itemscampos = new String[campos.size()];
        itemscampos = campos.toArray(itemscampos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona Un Campo");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(itemscampos, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                CampoSeleccionado = campos.get(item);
                alertDialog1.dismiss();

                SingletonUser.getInstance().getUsuario().setCampo(CampoSeleccionado);
                getEmpleadosPaseLista(CampoSeleccionado, sede);

            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(ActivityHome.this, ActivityLogin.class);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
    }
}
