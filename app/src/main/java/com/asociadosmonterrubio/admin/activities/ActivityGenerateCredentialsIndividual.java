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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
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
import java.util.Map;


public class ActivityGenerateCredentialsIndividual extends AppCompatActivity{

	private PdfDocument document;

	public Map<String,Bitmap> imagenes;
	private ArrayList<Map<String, String>> employees;
	private Usuario usuario;
	private ProgressDialog progressDialog;
	private DatabaseReference databaseReferenceEmpleados;

	public ArrayList<Map<String, String>> empleadosEncontrados;
    private String temporadaActual;

	private Button btn_buscar;
	private EditText edt_id_trabajador;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_credentials_individual);
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle(R.string.title_generar_credenciales);

		btn_buscar = (Button) findViewById(R.id.btn_buscar);
		edt_id_trabajador = (EditText) findViewById(R.id.edt_id_empleado);

		btn_buscar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if (temporadaActual != null) {
                    if (!edt_id_trabajador.getText().toString().isEmpty()) {
                        FireBaseQuery.databaseReference.child(FireBaseQuery.TEMPORADA_CAMPO).child(temporadaActual).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot campos : dataSnapshot.getChildren()) {
                                    if (campos.getKey().equals(SingletonUser.getInstance().getUsuario().getCampo())) {
                                        String isEspecial = (String) campos.getValue();
                                        searchID(isEspecial);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
								Toast.makeText(ActivityGenerateCredentialsIndividual.this, "Ocurrio un error al consultar la informacion", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(ActivityGenerateCredentialsIndividual.this, "Ocurrio un error al consultar la información", Toast.LENGTH_SHORT).show();
                    }
                }
			}
		});

		usuario = SingletonUser.getInstance().getUsuario();
		document = new PdfDocument();
		imagenes = new HashMap<>();
		employees = new ArrayList<>();
        empleadosEncontrados = new ArrayList<>();

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
                temporadaActual = dataSnapshot.getValue().toString();
				String pathEmployees = FireBaseQuery.ASIGNACION_EMPLEADOS_CAMPO + "/" +
						usuario.getSede() + "/" +
						dataSnapshot.getValue().toString() + "/" + //Esta es la temporada actual
						usuario.getCampo();

				databaseReferenceEmpleados = FireBaseQuery.databaseReference.child(pathEmployees);
				databaseReferenceEmpleados.addValueEventListener(loadUsers);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				if (progressDialog != null)
					progressDialog.dismiss();
				Toast.makeText(ActivityGenerateCredentialsIndividual.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@SuppressWarnings("unchecked")
	ValueEventListener loadUsers = new ValueEventListener() {
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
				employee.put("Contrato", childrenData.get("Contrato"));

				//Cuando un empleado proviene de un campo especial, este numero es indispensable
				if (childrenData.containsKey("IDExterno")){
					employee.put("IDExterno", childrenData.get("IDExterno"));
				}

				//Este atributo nos indica si un empleado es solo, esta nos servira como referencia para determinar la fecha de inicio y fin
				if (childrenData.containsKey("Modalidad"))
					employee.put("Modalidad", childrenData.get("Modalidad"));

				employees.add(employee);
			}
			if (progressDialog != null)
				progressDialog.dismiss();

			if (employees.isEmpty())
				Toast.makeText(ActivityGenerateCredentialsIndividual.this, "La lista de empleados esta vacia", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onCancelled(DatabaseError databaseError) {
			if (progressDialog != null)
				progressDialog.dismiss();
			Toast.makeText(ActivityGenerateCredentialsIndividual.this, "Ocurrio un error al descargar la lista de empleados", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (databaseReferenceEmpleados != null){
			if (loadUsers != null)
				databaseReferenceEmpleados.removeEventListener(loadUsers);
		}
	}

	private void showConfirmation(){
		AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
		dialogo1.setTitle("Crear credencial");
		dialogo1.setMessage("Le recomendamos que tenga una buena conexión a internet.");
		dialogo1.setCancelable(false);
		dialogo1.setPositiveButton("Generar credencial", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogo1, int id) {
				dialogo1.dismiss();
				generarCredencial();
			}
		});
		dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogo1, int id) {
				dialogo1.dismiss();
			}
		});
		dialogo1.show();
	}

	public void generarCredencial() {
		progressDialog.setMessage("Descargando fotografia...");
		progressDialog.show();

		//Obtener la imagen de cada de ellos
		for (Map<String, String> empleadoEncontrado : empleadosEncontrados)
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
				if (imagenes.size() == empleadosEncontrados.size()){
					progressDialog.setMessage("Generando PDF...");
                    Map<String, String> emp = empleadosEncontrados.get(0);
                    String ID = emp.containsKey("IDExterno") ? emp.get("IDExterno") : emp.get("ID");
					PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentialsIndividual.this, ID, empleadosEncontrados, imagenes);
					pdfGenerator.makeCredentials();
                    empleadosEncontrados.clear();
                    imagenes.clear();
					progressDialog.dismiss();
				}
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle any errors
				imagenes.put(pushId, null);
				if (imagenes.size() == empleadosEncontrados.size()){
					progressDialog.setMessage("Generando PDF...");
                    Map<String, String> emp = empleadosEncontrados.get(0);
                    String ID = emp.containsKey("IDExterno") ? emp.get("IDExterno") : emp.get("ID");
					PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentialsIndividual.this, ID, empleadosEncontrados, imagenes);
					pdfGenerator.makeCredentials();
                    empleadosEncontrados.clear();
                    imagenes.clear();
					progressDialog.dismiss();
				}
			}
		});

	}

	private void searchID(String isCampoActualEspecial){
        String number = edt_id_trabajador.getText().toString();
        if (isCampoActualEspecial.equals("true")){
			boolean isFound = false;
            for (Map<String, String> empleado : employees){
                if (empleado.get("IDExterno").equals(number)){
                    empleadosEncontrados.add(empleado);
                    showConfirmation();
                    isFound = true;
                    break;
                }
            }

            if (!isFound)
                Toast.makeText(this, "ID NO ENCONTRADO", Toast.LENGTH_SHORT).show();
        }else {
            boolean isFound = false;
            for (Map<String, String> empleado : employees){
                if (empleado.get("ID").equals(number)){
                    empleadosEncontrados.add(empleado);
                    showConfirmation();
                    isFound = true;
                    break;
                }
            }

            if (!isFound)
                Toast.makeText(this, "ID NO ENCONTRADO", Toast.LENGTH_SHORT).show();
        }
    }

}
