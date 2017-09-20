package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.adapters.ListViajesAdapter;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;

import com.asociadosmonterrubio.admin.models.Usuario;
import com.asociadosmonterrubio.admin.utils.PDFGenerator;
import com.asociadosmonterrubio.admin.utils.SingletonUser;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityGenerateCredentials extends AppCompatActivity implements AdapterView.OnItemClickListener{

	private static final int MAX_REQUEST_IN_QUEUE = 100;

	private PdfDocument document;

	public Map<String,Bitmap> imagenes;
	private ArrayList<Map<String, String>> employees;
	private Usuario usuario;
	private ProgressDialog progressDialog;
	private DatabaseReference databaseReferenceEmpleados;

	private ArrayList<String> fechasSalidas;

	private ListView list_viajes;
	private ListViajesAdapter viajesAdapter;
	private String fechaSeleccionada;
	public ArrayList<Map<String, String>> empleadosEncontrados;

	public ArrayList<List<Map<String, String>>> chunkEmpleadosEncontrados;
    private int currentIndexChunk = 0;
    private int totalIndexChunkSize = -1;
    private List<Map<String, String>> currentChunkList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_credentials);
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle(R.string.title_generar_credenciales);

		list_viajes = (ListView) findViewById(R.id.list_viajes);

		usuario = SingletonUser.getInstance().getUsuario();
		document = new PdfDocument();
		imagenes = new HashMap<>();
		employees = new ArrayList<>();
		fechasSalidas = new ArrayList<>();
		chunkEmpleadosEncontrados = new ArrayList<>();

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Descargando...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);

        loadEmployeesFromField();
	}

	private void loadEmployeesFromField(){
		progressDialog.show();
		String pathGetTemporadaActual = FireBaseQuery.TEMPORADAS_SEDES + "/" + usuario.getSede() + "/Temporada_Actual";
		FireBaseQuery.databaseReference.child(pathGetTemporadaActual).addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String pathEmployees = FireBaseQuery.ASIGNACION_EMPLEADOS_CAMPO + "/" +
						usuario.getSede() + "/" +
						dataSnapshot.getValue().toString() + "/" + //Esta es la temporada actual
						usuario.getCampo();

				databaseReferenceEmpleados = FireBaseQuery.databaseReference.child(pathEmployees);
				databaseReferenceEmpleados.addValueEventListener(loadUsuarios);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				if (progressDialog != null)
					progressDialog.dismiss();
				Toast.makeText(ActivityGenerateCredentials.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@SuppressWarnings("unchecked")
	ValueEventListener loadUsuarios = new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			for (DataSnapshot children : dataSnapshot.getChildren()){

				//Getting user information
				Map<String, String> childrenData = (Map<String, String>) children.getValue();
				Map<String, String> employee = new HashMap<>();
				employee.put("ID", children.getKey());

				String nombre = childrenData.get("Nombre") != null ? childrenData.get("Nombre") : "";
				String apellidoP = childrenData.get("Apellido_Paterno") != null ? childrenData.get("Apellido_Paterno") : "";
				String apellidoM = childrenData.get("Apellido_Materno") != null ? childrenData.get("Apellido_Materno") : "";

				String nombreCompleto = nombre + " " +apellidoP + " " +apellidoM;
				employee.put("NombreCompleto", nombreCompleto);
				employee.put("pushId", childrenData.get("pushId"));
				employee.put("Fecha_Salida", childrenData.get("Fecha_Salida"));
				employee.put("Lugar_Nacimiento", childrenData.get("Lugar_Nacimiento"));

				//Cuando un empleado proviene de un campo especial, este numero es indispensable
				if (childrenData.containsKey("IDExterno")){
					employee.put("IDExterno", childrenData.get("IDExterno"));
				}

				//Este atributo nos indica si un empleado es solo, esta nos servira como referencia para determinar la fecha de inicio y fin
				if (childrenData.containsKey("Modalidad")){
					employee.put("Modalidad", childrenData.get("Modalidad"));
				}

				//Getting fecha de salida
				boolean isFechaAdded = false;
				for (String fechaSalida : fechasSalidas){
					if (employee.get("Fecha_Salida").equals(fechaSalida)){
						isFechaAdded = true;
						break;
					}
				}
				if (!isFechaAdded){
					fechasSalidas.add(employee.get("Fecha_Salida"));
				}

				employees.add(employee);
			}
			if (progressDialog != null)
				progressDialog.dismiss();

			if (employees.isEmpty()){
				Toast.makeText(ActivityGenerateCredentials.this, "La lista de empleados esta vacia", Toast.LENGTH_SHORT).show();
			}else {
				viajesAdapter = new ListViajesAdapter(ActivityGenerateCredentials.this, fechasSalidas);
				list_viajes.setAdapter(viajesAdapter);
				list_viajes.setOnItemClickListener(ActivityGenerateCredentials.this);
			}
		}

		@Override
		public void onCancelled(DatabaseError databaseError) {
			if (progressDialog != null)
				progressDialog.dismiss();
			Toast.makeText(ActivityGenerateCredentials.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (databaseReferenceEmpleados != null){
			if (loadUsuarios != null){
				databaseReferenceEmpleados.removeEventListener(loadUsuarios);
			}
		}
	}

	private void showConfirmation(){
		AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
		dialogo1.setTitle("Crear credenciales con fecha "+this.fechaSeleccionada);
		dialogo1.setMessage("Le recomendamos que tenga una buena conexión a internet.");
		dialogo1.setCancelable(false);
		dialogo1.setPositiveButton("Generar credenciales", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogo1, int id) {
				dialogo1.dismiss();

                progressDialog = new ProgressDialog(ActivityGenerateCredentials.this);
				progressDialog.setMessage("Descargando fotografias...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setProgress(0);

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						generarCredenciales();
					}
				});
				thread.start();
			}
		});
		dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogo1, int id) {
				dialogo1.dismiss();
			}
		});
		dialogo1.show();
	}

	public void generarCredenciales() {

		//Obtenemos los empleados dados de alta en esa fecha.
		this.empleadosEncontrados = new ArrayList<>();
		for (Map<String, String> empleado : employees){
			if(empleado.get("Fecha_Salida").equals(this.fechaSeleccionada))
				empleadosEncontrados.add(empleado);
		}

        progressDialog.setMax(empleadosEncontrados.size());

		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });

		if (empleadosEncontrados.size() > MAX_REQUEST_IN_QUEUE){
			float chunks = (float)empleadosEncontrados.size()/MAX_REQUEST_IN_QUEUE;
			float residuo = chunks % 1;
            int finalChunks = (int) chunks;
			if (residuo > 0)
                finalChunks += 1;

            totalIndexChunkSize = finalChunks;

            int fromIndex;
            int toIndex = 0;
            for (int i = 1; i <= finalChunks; i++){
                fromIndex = toIndex;
                toIndex += MAX_REQUEST_IN_QUEUE;
                if (i == finalChunks)
                    chunkEmpleadosEncontrados.add(empleadosEncontrados.subList(fromIndex, empleadosEncontrados.size()));
                else
                    chunkEmpleadosEncontrados.add(empleadosEncontrados.subList(fromIndex, toIndex));
            }

            //Obtener la imagen de cada de ellos
            currentChunkList = chunkEmpleadosEncontrados.get(currentIndexChunk);
            for (Map<String, String> empleadoEncontrado : currentChunkList) {
                downloadImageFromStorage(empleadoEncontrado.get("pushId"));
            }

		}else {

            //Obtener la imagen de cada de ellos
            for (Map<String, String> empleadoEncontrado : empleadosEncontrados) {
                downloadImageFromStorage(empleadoEncontrado.get("pushId"));
            }
        }
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
                if (totalIndexChunkSize == -1) {
                    if (imagenes.size() == empleadosEncontrados.size()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage("Generando PDF...");
                                PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentials.this, fechaSeleccionada, empleadosEncontrados, imagenes);
                                pdfGenerator.makeCredentials();
                                progressDialog.dismiss();
                                onBackPressed();
                            }
                        });
                    }
                }else {
                    if ((currentIndexChunk + 1) < totalIndexChunkSize){
                        int currentSize = 0;
                        for (int i = 0; i < totalIndexChunkSize; i++){
                            if (i <= currentIndexChunk)
                                currentSize += chunkEmpleadosEncontrados.get(i).size();
                        }

                        if (imagenes.size() == currentSize){
                            currentIndexChunk++;
                            //Obtener la imagen de cada de ellos
                            currentChunkList = chunkEmpleadosEncontrados.get(currentIndexChunk);
                            for (Map<String, String> empleadoEncontrado : currentChunkList) {
                                downloadImageFromStorage(empleadoEncontrado.get("pushId"));
                            }
                        }
                    }else {
                        if (imagenes.size() == empleadosEncontrados.size()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setMessage("Generando PDF...");
                                    PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentials.this, fechaSeleccionada, empleadosEncontrados, imagenes);
                                    pdfGenerator.makeCredentials();
                                    progressDialog.dismiss();
                                    onBackPressed();
                                }
                            });
                        }
                    }
                }
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle any errors
				imagenes.put(pushId, null);
                progressDialog.setProgress(imagenes.size());
                if (totalIndexChunkSize == -1) {
                    if (imagenes.size() == empleadosEncontrados.size()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage("Generando PDF...");
                                PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentials.this, fechaSeleccionada, empleadosEncontrados, imagenes);
                                pdfGenerator.makeCredentials();
                                progressDialog.dismiss();
                                onBackPressed();
                            }
                        });
                    }
                }else {
                    if ((currentIndexChunk + 1) < totalIndexChunkSize){
                        int currentSize = 0;
                        for (int i = 0; i < totalIndexChunkSize; i++){
                            if (i <= currentIndexChunk)
                                currentSize += chunkEmpleadosEncontrados.get(i).size();
                        }

                        if (imagenes.size() == currentSize){
                            currentIndexChunk++;
                            //Obtener la imagen de cada de ellos
                            currentChunkList = chunkEmpleadosEncontrados.get(currentIndexChunk);
                            for (Map<String, String> empleadoEncontrado : currentChunkList) {
                                downloadImageFromStorage(empleadoEncontrado.get("pushId"));
                            }
                        }
                    }else {
                        if (imagenes.size() == empleadosEncontrados.size()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setMessage("Generando PDF...");
                                    PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentials.this, fechaSeleccionada, empleadosEncontrados, imagenes);
                                    pdfGenerator.makeCredentials();
                                    progressDialog.dismiss();
                                    onBackPressed();
                                }
                            });
                        }
                    }
                }
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		this.fechaSeleccionada = fechasSalidas.get(position);
		showConfirmation();
	}

}
