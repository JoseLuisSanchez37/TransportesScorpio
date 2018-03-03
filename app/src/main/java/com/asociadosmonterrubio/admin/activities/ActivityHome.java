package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.BuildConfig;
import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.adapters.HomeAdapter;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.BlackListUser;
import com.asociadosmonterrubio.admin.models.ChekListCountryside;
import com.asociadosmonterrubio.admin.models.Employee;
import com.asociadosmonterrubio.admin.models.Usuario;
import com.asociadosmonterrubio.admin.utils.BlackListSingleton;
import com.asociadosmonterrubio.admin.utils.ChekListCountrysideSingleton;
import com.asociadosmonterrubio.admin.utils.SingletonEmployees;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.asociadosmonterrubio.admin.utils.UserPreferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityHome extends AppCompatActivity {

    private static final String APP_ID = "com.asociadosmonterrubio.admin"; //Production applicationId

    private boolean isPaseDeListaAvailable = true;
    ArrayList<String> campos;
    String sede;
    AlertDialog alertDialog1;
    String CampoSeleccionado;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.title_home);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new HomeAdapter(this));

        campos = SingletonUser.getInstance().getUsuario().getCampos();
        sede = SingletonUser.getInstance().getUsuario().getSede();
        if (campos.isEmpty()){
            isPaseDeListaAvailable = false;
            showSessionInformation();
            getBlackList();
        }else if (campos.size() == 1){
            CampoSeleccionado = campos.get(0);
            SingletonUser.getInstance().getUsuario().setCampo(CampoSeleccionado);
            getEmpleadosPaseLista(CampoSeleccionado,sede);
            showSessionInformation();
        }else {
            CreacionDeDialogo();
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
						if (SingletonUser.getInstance().getUsuario().getRol().equals(Usuario.ROL_SUPERVISOR))
							intent = new Intent(ActivityHome.this, ActivitySupervisorCheckList.class);
						else
							intent = new Intent(ActivityHome.this, ActivityDisplayEmployees.class);
                        startActivity(intent);
                        break;
                    case 1:
                        if (SingletonUser.getInstance().getUsuario().getRol().equals(Usuario.ROL_SUPERVISOR))
                            Toast.makeText(ActivityHome.this, "En desarrollo...", Toast.LENGTH_SHORT).show();
                        else {
                            if (isPaseDeListaAvailable) {
                                intent = new Intent(ActivityHome.this, ActivityCheckList.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ActivityHome.this, "No se puede pasar lista ya que no cuenta con ningun campo asignado", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
					case 2:
						intent = new Intent(ActivityHome.this, ActivityListEmployeesField.class);
						startActivity(intent);
						break;
					case 3:
                        seleccionaModoCredencial();
                        break;
                    case 4:
                        seleccionaTipoPrestamo();
                        break;
                    case 5:
                        if (CampoSeleccionado != null) {
                            intent = new Intent(ActivityHome.this, ActivityQuitEmployee.class);
                            startActivity(intent);
                        }
                        break;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                closeSession();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getEmpleadosPaseLista(String campo,String sede){
        progressDialog = new ProgressDialog(ActivityHome.this);
        progressDialog.setMessage("Descargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        FireBaseQuery.databaseReference.child(FireBaseQuery.PASE_DE_LISTA).child(sede).child(campo).addValueEventListener(new ValueEventListener() {
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
                getBlackList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled", databaseError.getMessage());
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
                showSessionInformation();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    public void seleccionaModoCredencial(){
        String [] items = new String[3];
        items[0] = "Por salidas";
        items[1] = "Individual";
        items[2] = "Por busqueda";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el modo de credencialización");
        builder.setCancelable(true);
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                alertDialog1.dismiss();
                Intent intent;
                switch (item){
                    case 0:
                        intent = new Intent(ActivityHome.this, ActivityGenerateCredentialsByDeparture.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(ActivityHome.this, ActivityGenerateCredentialsById.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(ActivityHome.this, ActivityGenerateCredentialsBySearch.class);
                        startActivity(intent);
                        break;
                }

            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    public void seleccionaTipoPrestamo(){
        String [] items = new String[2];
        items[0] = "Giros";
        items[1] = "Prestamos";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona:");
        builder.setCancelable(true);
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                alertDialog1.dismiss();
                Intent intent = new Intent(ActivityHome.this, ActivityGirosPrestamos.class);
                switch (item){
                    case 0:
                        intent.putExtra("tipo", "Giros");
                        startActivity(intent);
                        break;
                    case 1:
                        intent.putExtra("tipo", "Prestamos");
                        startActivity(intent);
                        break;
                }

            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }


    private void closeSession(){
        SingletonUser.getInstance().setUsuario(null);
        UserPreferences.clearUserSession();
        UserPreferences.savePreference(UserPreferences.LAST_SESSION_DATE, "");
        SingletonEmployees.getInstance().setEmployess(new ArrayList<Employee>());
        ChekListCountrysideSingleton.getInstance().clear();
        Intent intent = new Intent(ActivityHome.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }

    private void showSessionInformation(){
        String campo = CampoSeleccionado != null ? CampoSeleccionado : "";
        txv_info_user_field_selected.setText("Campo : ".concat(campo));
        txv_info_user_name.setText("Nombre: ".concat(SingletonUser.getInstance().getUsuario().getNombre()));
        txv_info_user_rol.setText("Rol: ".concat(SingletonUser.getInstance().getUsuario().getRol()));
        txv_info_user_sede.setText("Sede: ".concat(SingletonUser.getInstance().getUsuario().getSede()));
        txv_info_user_app_version.setText("Version: ".concat(BuildConfig.VERSION_NAME));

        if (BuildConfig.APPLICATION_ID.equals(APP_ID)){
            txv_info_user_environment.setText("Ambiente: PRODUCCION");
            txv_info_user_environment.setTextColor(getResources().getColor(R.color.green));
        }else {
            txv_info_user_environment.setText("Ambiente: PRUEBAS");
            txv_info_user_environment.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void getBlackList(){
        FireBaseQuery.databaseReference.child(FireBaseQuery.LISTA_NEGRA).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    BlackListUser blackListUser = iterator.next().getValue(BlackListUser.class);
                    BlackListSingleton.getInstance().addBlackListUser(blackListUser);
                }
                if (progressDialog != null) progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(ActivityHome.this, "Ocurrio un error al descargar el pase de lista", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @BindView(R.id.txv_info_user_name) TextView txv_info_user_name;
    @BindView(R.id.txv_info_user_rol) TextView txv_info_user_rol;
    @BindView(R.id.txv_info_user_field_selected) TextView txv_info_user_field_selected;
    @BindView(R.id.txv_info_user_sede) TextView txv_info_user_sede;
    @BindView(R.id.txv_info_user_app_version) TextView txv_info_user_app_version;
    @BindView(R.id.txv_info_user_environment) TextView txv_info_user_environment;

}
