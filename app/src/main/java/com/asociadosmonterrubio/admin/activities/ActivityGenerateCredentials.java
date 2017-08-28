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
import java.util.Map;


public class ActivityGenerateCredentials extends AppCompatActivity implements AdapterView.OnItemClickListener{

	private PdfDocument document;

	/*//image
	static final int INIT_IMAGE_X = 15;
	static final int INIT_IMAGE_Y = 40;

	//campo
	static final int INIT_CAMPO_X = 150;
	static final int INIT_CAMPO_Y = 25;

	//nombre
	static final int INIT_NOMBRE_X = 150;
	static final int INIT_NOMBRE_Y = 70;

	//fecha_inicio
	static final int INIT_FECHA_INICIO_X = 150;
	static final int INIT_FECHA_INICIO_Y = 130;

	//fecha_fin
	static final int INIT_FECHA_FIN_X = 150;
	static final int INIT_FECHA_FIN_Y = 170;

	//borde
	static final int INIT_BORDE_X = 10;
	static final int INIT_BORDE_Y = 10;
	static final int FINAL_BORDE_X = 300;
	static final int FINAL_BORDE_Y = 200;

	//barcode
	static final int INIT_BARCODE_X = 11;
	static final int INIT_BARCODE_Y = 140;*/

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

	ValueEventListener loadUsuarios = new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			for (DataSnapshot children : dataSnapshot.getChildren()){

				//Gettin user information
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

				if (childrenData.containsKey("IDExterno")){
					employee.put("IDExterno", childrenData.get("IDExterno"));
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
				generarCredenciales();
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
		progressDialog.setMessage("Descargando fotografias...");
		progressDialog.show();

		//Obtenemos los empleados dados de alta en esa fecha.
		this.empleadosEncontrados = new ArrayList<>();
		for (Map<String, String> empleado : employees){
			if(empleado.get("Fecha_Salida").equals(this.fechaSeleccionada))
				empleadosEncontrados.add(empleado);
		}
		//Obtener la imagen de cada de ellos
		for (Map<String, String> empleadoEncontrado : empleadosEncontrados){
			downloadImageFromStorage(empleadoEncontrado.get("pushId"));
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
				if (imagenes.size() == empleadosEncontrados.size()){
					progressDialog.setMessage("Generando PDF...");
					PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentials.this, fechaSeleccionada);
					pdfGenerator.makeCredentials();
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
					PDFGenerator pdfGenerator = new PDFGenerator(document, ActivityGenerateCredentials.this, fechaSeleccionada);
					pdfGenerator.makeCredentials();
					progressDialog.dismiss();
				}
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		this.fechaSeleccionada = fechasSalidas.get(position);
		showConfirmation();
	}

	/*public void makeCredentials(){
		//Config PDF document
		PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(620, 836, 1).create();
		PdfDocument.Page page = document.startPage(pageInfo);

		//Initialization canvas
		Canvas canvas = page.getCanvas();

		//Initialization of coordinates
		//image
		int init_image_x = INIT_IMAGE_X;
		int init_image_y = INIT_IMAGE_Y;

		//campo
		int init_campo_x = INIT_CAMPO_X;
		int init_campo_y = INIT_CAMPO_Y;

		//nombre
		int init_nombre_x = INIT_NOMBRE_X;
		int init_nombre_y = INIT_NOMBRE_Y;

		//fecha_inicio
		int init_fecha_inicio_x = INIT_FECHA_INICIO_X;
		int init_fecha_inicio_y = INIT_FECHA_INICIO_Y;

		//fecha_fin
		int init_fecha_fin_x = INIT_FECHA_FIN_X;
		int init_fecha_fin_y = INIT_FECHA_FIN_Y;

		//borde
		int init_borde_x = INIT_BORDE_X;
		int init_borde_y = INIT_BORDE_Y;
		int final_borde_x = FINAL_BORDE_X;
		int final_borde_y = FINAL_BORDE_Y;

		//barcode
		int init_barcode_x = INIT_BARCODE_X;
		int init_barcode_y = INIT_BARCODE_Y;

		//number of jump lines
		int numberCredentials = 0;

		for (int i = 0; i < 100; i++){

			Boolean isSalto;
			if (numberCredentials > 1) {
				isSalto =((numberCredentials % 2) == 0);
				if (isSalto) {
					init_image_y += INIT_BORDE_Y + FINAL_BORDE_Y;
					init_campo_y += INIT_BORDE_Y + FINAL_BORDE_Y;
					init_nombre_y += INIT_BORDE_Y + FINAL_BORDE_Y;
					init_fecha_inicio_y += INIT_BORDE_Y + FINAL_BORDE_Y;
					init_fecha_fin_y += INIT_BORDE_Y + FINAL_BORDE_Y;
					init_barcode_y += INIT_BORDE_Y + FINAL_BORDE_Y;

					init_borde_y += INIT_BORDE_Y + FINAL_BORDE_Y;
					final_borde_y += INIT_BORDE_Y + FINAL_BORDE_Y;

					/*//********
					init_image_x = INIT_IMAGE_X;
					init_campo_x = INIT_CAMPO_X;
					init_nombre_x = INIT_NOMBRE_X;
					init_fecha_inicio_x = INIT_FECHA_INICIO_X;
					init_fecha_fin_x = INIT_FECHA_FIN_X;
					init_barcode_x = INIT_BARCODE_X;

					init_borde_x = INIT_BORDE_X;
					final_borde_x = FINAL_BORDE_X;

				} else {
					init_image_x += INIT_BORDE_X + FINAL_BORDE_X;
					init_campo_x += INIT_BORDE_X + FINAL_BORDE_X;
					init_nombre_x += INIT_BORDE_X + FINAL_BORDE_X;
					init_fecha_inicio_x += INIT_BORDE_X + FINAL_BORDE_X;
					init_fecha_fin_x += INIT_BORDE_X + FINAL_BORDE_X;
					init_barcode_x += INIT_BORDE_X + FINAL_BORDE_X;

					init_borde_x += INIT_BORDE_X + FINAL_BORDE_X;
					final_borde_x += INIT_BORDE_X + FINAL_BORDE_X;

				}
			}

			//Image
			Bitmap resized = Bitmap.createScaledBitmap(null, 100, 90, true);

			//Setting image
			canvas.drawBitmap(resized, init_image_x, init_image_y, null);

			//Setting field CAMPO
			Paint paintCampo = new Paint();
			paintCampo.setColor(Color.BLACK);
			paintCampo.setTextSize(15f);
			canvas.drawText("NAZARIO", init_campo_x, init_campo_y, paintCampo);

			//Setting field NOMBRE
			Paint paintNombre = new Paint();
			paintNombre.setColor(Color.BLACK);
			paintNombre.setTextSize(10f);
			canvas.drawText("JOSE LUIS SANCHEZ CRUZ", init_nombre_x, init_nombre_y, paintNombre);

			//Setting field FECHA_INICIO
			Paint paintFechaInicio = new Paint();
			paintFechaInicio.setColor(Color.BLACK);
			paintFechaInicio.setTextSize(10f);
			canvas.drawText("FECHA INICIO: 2017-8-25", init_fecha_inicio_x, init_fecha_inicio_y, paintFechaInicio);

			//Setting field FECHA_FIN
			Paint paintFechaFin = new Paint();
			paintFechaFin.setColor(Color.BLACK);
			paintFechaFin.setTextSize(10f);
			canvas.drawText("FECHA FIN: 2017-11-25", init_fecha_fin_x, init_fecha_fin_y, paintFechaFin);

			//Setting BORDER
			RectF rectF = new RectF(init_borde_x, init_borde_y, final_borde_x, final_borde_y);
			Paint paintBorder = new Paint();
			paintBorder.setStyle(Paint.Style.STROKE);
			paintBorder.setColor(Color.BLACK);
			canvas.drawRect(rectF, paintBorder);

			//Setting barcode
			AndroidBarcodeView barcodeView = new AndroidBarcodeView(ActivityGenerateCredentials.this, String.valueOf(i), init_barcode_x, init_barcode_y);
			barcodeView.draw(canvas);

			if (numberCredentials == 0){
				init_image_x += INIT_BORDE_X + FINAL_BORDE_X;
				init_campo_x += INIT_BORDE_X + FINAL_BORDE_X;
				init_nombre_x += INIT_BORDE_X + FINAL_BORDE_X;
				init_fecha_inicio_x += INIT_BORDE_X + FINAL_BORDE_X;
				init_fecha_fin_x += INIT_BORDE_X + FINAL_BORDE_X;
				init_barcode_x += INIT_BORDE_X + FINAL_BORDE_X;

				init_borde_x += INIT_BORDE_X + FINAL_BORDE_X;
				final_borde_x += INIT_BORDE_X + FINAL_BORDE_X;

			}

			if (numberCredentials == 7){
				numberCredentials = 0;
				//image
				init_image_x = INIT_IMAGE_X;
				init_image_y = INIT_IMAGE_Y;

				//campo
				init_campo_x = INIT_CAMPO_X;
				init_campo_y = INIT_CAMPO_Y;

				//nombre
				init_nombre_x = INIT_NOMBRE_X;
				init_nombre_y = INIT_NOMBRE_Y;

				//fecha_inicio
				init_fecha_inicio_x = INIT_FECHA_INICIO_X;
				init_fecha_inicio_y = INIT_FECHA_INICIO_Y;

				//fecha_fin
				init_fecha_fin_x = INIT_FECHA_FIN_X;
				init_fecha_fin_y = INIT_FECHA_FIN_Y;

				//borde
				init_borde_x = INIT_BORDE_X;
				init_borde_y = INIT_BORDE_Y;
				final_borde_x = FINAL_BORDE_X;
				final_borde_y = FINAL_BORDE_Y;

				//barcode
				init_barcode_x = INIT_BARCODE_X;
				init_barcode_y = INIT_BARCODE_Y;

				//Finishing document
				document.finishPage(page);
				page = document.startPage(pageInfo);
				canvas = page.getCanvas();

			}else {
				numberCredentials++;
			}
		}

		document.finishPage(page);

		//Saving PDF file in storage
		savePDFile();
	}

	private void savePDFile(){
		String targetPdf = Environment.getExternalStorageDirectory() + "/scorpioFiles";
		File filePath = new File(targetPdf);
		if (!filePath.exists())
			filePath.mkdir();
		File pdfFile = new File(filePath, "test.pdf");
		if (pdfFile.exists())
			pdfFile.delete();
		try{
			document.writeTo(new FileOutputStream(pdfFile));
			Toast.makeText(ActivityGenerateCredentials.this, "Done", Toast.LENGTH_LONG).show();
		}catch (IOException e){
			e.printStackTrace();
			Toast.makeText(ActivityGenerateCredentials.this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}*/

}
