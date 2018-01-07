package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.adapters.ListEmployeeAdapter;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.utils.PDFGenerator;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivityGenerateCredentialsBySearch extends AppCompatActivity implements AdapterView.OnItemClickListener, TextWatcher{

    private PdfDocument document;
    public Map<String,Bitmap> imagenes;
	private ProgressDialog progressDialog;
	private ArrayList<Map<String, String>> employees, employeesSelected;
	private ListEmployeeAdapter listEmployeeAdapter, listEmployeesSelectedAdapter;
    private boolean isSpecialField = false;
    private String currentFieldPath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_layout_for_printing);
		ButterKnife.bind(this);
		employees = new ArrayList<>();
        imagenes = new HashMap<>();
        document = new PdfDocument();
		employeesSelected = new ArrayList<>();
		initLoadingAnActionBar();
		loadEmployeesFromField();
	}

	private void initLoadingAnActionBar(){
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.subtitle_listado_empleados_impresion));

		listEmployeesSelectedAdapter = new ListEmployeeAdapter(this, employeesSelected, ListEmployeeAdapter.PRINTING_TOUCH_TO_REMOVE);
		list_employees_selected.setAdapter(listEmployeesSelectedAdapter);
		list_employees_selected.setEmptyView(empty_list_selected);
		list_employees_selected.setOnItemClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
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
                            employee.put("Fecha_Salida", childrenData.containsKey("Fecha_Salida") ? childrenData.get("Fecha_Salida") : "");
                            employee.put("Modalidad", childrenData.containsKey("Modalidad") ? childrenData.get("Modalidad") : "");

                            String nombreCompleto = employee.get("Nombre") + " " + employee.get("Apellido_Paterno") + " " + employee.get("Apellido_Materno");
                            employee.put("NombreCompleto", nombreCompleto);

							employees.add(employee);
						}

						if (progressDialog != null)
							progressDialog.dismiss();

						if (employees.isEmpty())
							Toast.makeText(ActivityGenerateCredentialsBySearch.this, "La lista de empleados esta vacia", Toast.LENGTH_SHORT).show();
						else {
                            Collections.sort(employees, new Comparator<Map<String, String>>() {
                                @Override
                                public int compare(Map<String, String> map1, Map<String, String> map2) {
                                    return isSpecialField ? map1.get("IDExterno").compareToIgnoreCase(map2.get("IDExterno")) : map1.get("ID").compareToIgnoreCase(map2.get("ID"));
                                }
                            });
                            Collections.reverse(employees);
							listEmployeeAdapter = new ListEmployeeAdapter(ActivityGenerateCredentialsBySearch.this, employees, ListEmployeeAdapter.PRINTING_TOUCH_TO_ADD);
							list_employees.setAdapter(listEmployeeAdapter);
                            list_employees.setOnItemClickListener(ActivityGenerateCredentialsBySearch.this);

                            auto_complete_finder.setAdapter(listEmployeeAdapter);
                            auto_complete_finder.addTextChangedListener(ActivityGenerateCredentialsBySearch.this);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						if (progressDialog != null)
							progressDialog.dismiss();
						onBackPressed();
						Toast.makeText(ActivityGenerateCredentialsBySearch.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				if (progressDialog != null)
					progressDialog.dismiss();
				onBackPressed();
				Toast.makeText(ActivityGenerateCredentialsBySearch.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
			}
		});

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.print:
                if (employeesSelected.size() > 1) {
                    showgGenerateCredentialsConfirmation();
                }else
                    Toast.makeText(this, "Debes seleccionar al menos dos elementos para poder generar el PDF", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void downloadInformation(){
	    //Display progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando fotografias...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(employeesSelected.size());
        progressDialog.setProgress(0);
        progressDialog.show();

        //Obtener la imagen de cada de ellos
        for (Map<String, String> empleadoEncontrado : employeesSelected)
            downloadImageFromStorage(empleadoEncontrado.get("pushId"));
    }

    private void downloadImageFromStorage(final String pushId){
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imagenes/".concat(pushId));
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagenes.put(pushId, bitmap);
                progressDialog.setProgress(imagenes.size());

                if (imagenes.size() == employeesSelected.size()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Generando PDF...");
                            PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentialsBySearch.this, getCurrentFormattedDate(), employeesSelected, imagenes);
                            pdfGenerator.makeCredentials();
                            progressDialog.dismiss();
                            employeesSelected.clear();
                            onBackPressed();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imagenes.put(pushId, null);
                progressDialog.setProgress(imagenes.size());

                if (imagenes.size() == employeesSelected.size()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Generando PDF...");
                            PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentialsBySearch.this, getCurrentFormattedDate(), employeesSelected, imagenes);
                            pdfGenerator.makeCredentials();
                            progressDialog.dismiss();
                            employeesSelected.clear();
                            onBackPressed();
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        auto_complete_finder.getText().clear();
        refreshSearchList(null);
    }

    @SuppressWarnings("unchecked")
    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    if (parent.getAdapter() == listEmployeesSelectedAdapter){
            employeesSelected.remove(position);
            listEmployeesSelectedAdapter.updateEmployeesList(employeesSelected);

        } else if (parent.getAdapter() == listEmployeeAdapter) {
	        Map<String, String> employeeSelected = (Map<String, String>) listEmployeeAdapter.getItem(position);
	        boolean isFound = false;
            for (Map<String, String> employee : employeesSelected)
                if (employeeSelected.equals(employee)) {
                    isFound = true;
                    break;
                }
            if (!isFound) {
                employeesSelected.add(employeeSelected);
                listEmployeesSelectedAdapter.updateEmployeesList(employeesSelected);
            }
        }
	}

    private String getCurrentFormattedDate(){
        Calendar c = Calendar.getInstance();
        return (c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)+"__"+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+"::"+c.get(Calendar.SECOND);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        refreshSearchList(s);
    }

    private void refreshSearchList(CharSequence s){
        if (listEmployeeAdapter != null) {
            if (!TextUtils.isEmpty(s)) {
                auto_complete_finder.dismissDropDown();
                listEmployeeAdapter.getFilter().filter(s);
			}else
                listEmployeeAdapter.updateEmployeesList(employees);
        }
    }

    @Override
    public void onBackPressed() {
        if (employeesSelected.size() > 2){
            manageOnBackPressed();
        }else
            super.onBackPressed();
    }

    private void manageOnBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_exit_dialog));
        builder.setMessage(getString(R.string.message_exit_dialog_credentials));
        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                employeesSelected.clear();
                onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.continue_adding_workers, null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showgGenerateCredentialsConfirmation(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Generar credenciales");
        dialogo.setMessage("Le recomendamos que tenga una buena conexión a internet.");
        dialogo.setCancelable(false);
        dialogo.setPositiveButton("Generar credenciales", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                dialogo.dismiss();
                downloadInformation();
            }
        });
        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                dialogo.dismiss();
            }
        });
        dialogo.show();
    }

    @Bind(R.id.autocomplete_finder) AutoCompleteTextView auto_complete_finder;
    @Bind(R.id.list_employees) ListView list_employees;
    @Bind(R.id.list_employees_selected) ListView list_employees_selected;
    @Bind(R.id.empty_list_employees_selected) TextView empty_list_selected;
}
