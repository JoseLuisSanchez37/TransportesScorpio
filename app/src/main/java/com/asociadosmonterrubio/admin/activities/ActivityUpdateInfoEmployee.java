package com.asociadosmonterrubio.admin.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ActivityUpdateInfoEmployee extends AppCompatActivity implements View.OnClickListener{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public Bitmap picture_taken = null;
    private ProgressDialog progressDialog;
    private Map<String, String> employeeSelected;
    private String currentFieldPath;
    private Calendar calendar = Calendar.getInstance();
    private boolean isRenovacion = false;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_info_employee);
        ButterKnife.bind(this);
        btn_take_picture.setOnClickListener(this);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("employeeData"))
            employeeSelected = (Map<String, String>) getIntent().getExtras().getSerializable("employeeData");
        currentFieldPath = getIntent().getExtras().getString("path");

        if (getSupportActionBar() != null) {
            String ID = !employeeSelected.get("IDExterno").isEmpty() ? employeeSelected.get("IDExterno") : employeeSelected.get("ID");
            String subtitle = getString(R.string.subtitle_update_worker) + " " + ID;
            getSupportActionBar().setTitle(subtitle);
        }

        checkIfUserHasDepartureDate();
        fillEmployeeData();
    }

    public void showTimePickerDialog(View v) {
        new DatePickerDialog(this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void fillEmployeeData(){
        edt_employee_activity.setText(employeeSelected.get("Actividad"));
        edt_employee_activity.setEnabled(false);
        edt_employee_curp.setText(employeeSelected.get("CURP"));
        edt_employee_full_name.setText(employeeSelected.get("Nombre"));
        edt_employee_first_name.setText(employeeSelected.get("Apellido_Paterno"));
        edt_employee_last_name.setText(employeeSelected.get("Apellido_Materno"));
        edt_employee_origin.setText(employeeSelected.get("Lugar_Nacimiento"));
        String[] fechaNacimiento = employeeSelected.get("Fecha_Nacimiento").split("/");
        edt_employee_date_birth_day.setText(fechaNacimiento[0]);
        edt_employee_date_birth_month.setText(fechaNacimiento[1]);
        edt_employee_date_birth_year.setText(fechaNacimiento[2]);
    }

    private void checkIfUserHasDepartureDate(){
        if (employeeSelected != null && employeeSelected.containsKey("Modalidad") && employeeSelected.get("Modalidad").equalsIgnoreCase("Renovacion")) {
            layout_departure_date.setVisibility(View.VISIBLE);
            String[] departure_date = employeeSelected.get("Fecha_Salida").split("-");
            int year = Integer.parseInt(departure_date[0]);
            int month = Integer.parseInt(departure_date[1]);
            int day = Integer.parseInt(departure_date[2]);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            edt_employee_departure_date.setText(getCurrentFormattedDate());
            isRenovacion = true;
        }else
            layout_departure_date.setVisibility(View.GONE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_update_info_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_worker:
                if (areFieldsCompleted()) {
                    progressDialog = ProgressDialog.show(this, "Guardando información", "Espere porfavor...", true, false);
                    FireBaseQuery.updateInfoEmployee(currentFieldPath, getInfoEmployeeToUpdate());
                    if (picture_taken != null)
                        upLoadImage(employeeSelected.get("pushId"));
                    else
                        onBack();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    this.picture_taken = imageBitmap;
                    img_took.setImageBitmap(imageBitmap);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_picture:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                }else {
                    dispatchTakePictureIntent();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    dispatchTakePictureIntent();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
                        Toast.makeText(this, "Es necesario activar la camara para poder tomar fotos", Toast.LENGTH_LONG).show();
                }
            break;


        }
    }

    private boolean areFieldsCompleted(){
        ArrayList<EditText> fields = new ArrayList<>();
        fields.add(edt_employee_first_name);
        fields.add(edt_employee_last_name);
        fields.add(edt_employee_full_name);
        fields.add(edt_employee_origin);
        fields.add(edt_employee_date_birth_day);
        fields.add(edt_employee_date_birth_month);
        fields.add(edt_employee_date_birth_year);
        fields.add(edt_employee_activity);
        fields.add(edt_employee_curp);

        for (int i = 0; i < fields.size(); i++){
            EditText field = fields.get(i);
            if (field.getText().toString().isEmpty()) {
                field.requestFocus();
                field.setError("El campo no puede ir vacio");
                return false;
            }
        }

        int day = Integer.parseInt(edt_employee_date_birth_day.getText().toString());
        if (day > 31 || day < 1){
            Toast.makeText(this, "El dia ingresado no es valido", Toast.LENGTH_SHORT).show();
            return false;
        }

        int month = Integer.parseInt(edt_employee_date_birth_month.getText().toString());
        if (month > 12 || month < 1){
            Toast.makeText(this, "El mes ingresado no es valido", Toast.LENGTH_SHORT).show();
            return false;
        }

        int year = Integer.parseInt(edt_employee_date_birth_year.getText().toString());
        if (year < 1930 || year > 2010 ){
            Toast.makeText(this, "El año ingresado no es valido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edt_employee_curp.getText().toString().length() < 18){
            Toast.makeText(this, "La CLABE O CURP deben ser de 18 digitos", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private Map<String, String> getInfoEmployeeToUpdate(){
        String birthDay = edt_employee_date_birth_day.getText().toString() +"/"+
                edt_employee_date_birth_month.getText().toString()+"/"+
                edt_employee_date_birth_year.getText().toString();

        Map<String, String> employee = new HashMap<>();
        employee.put("Nombre", edt_employee_full_name.getText().toString());
        employee.put("Apellido_Paterno", edt_employee_first_name.getText().toString());
        employee.put("Apellido_Materno", edt_employee_last_name.getText().toString());
        employee.put("Fecha_Nacimiento", birthDay);
        employee.put("Lugar_Nacimiento", edt_employee_origin.getText().toString());
        employee.put("CURP", edt_employee_curp.getText().toString());
        if (isRenovacion)
            employee.put("Fecha_Salida", getCurrentFormattedDate());
        return employee;
    }

    private void upLoadImage(String pushId){
        if (picture_taken != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            picture_taken.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            // Create file metadata including the content type
            StorageReference storageReference = FireBaseQuery.getReferenceForSaveUserImage(pushId);
            UploadTask uploadTask = storageReference.putBytes(baos.toByteArray());
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception exception) {
                    // Handle unsuccessful uploads
                    //Save in database and Try to send the images in background when internet connection will be available :)
                    onBack();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //TaskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    onBack();
                }
            });
        }
    }

    private String getCurrentFormattedDate(){
        return calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edt_employee_departure_date.setText(getCurrentFormattedDate());
        }
    };

    private void onBack(){
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        Util.hideKeyboard(this);
        onBackPressed();
    }

    @BindView(R.id.layout_departure_date) LinearLayout layout_departure_date;
    @BindView(R.id.btn_take_picture) Button btn_take_picture;
    @BindView(R.id.edt_employee_departure_date) EditText edt_employee_departure_date;
    @BindView(R.id.img_took) ImageView img_took;
    @BindView(R.id.edt_employee_key) EditText edt_employee_id;
    @BindView(R.id.edt_employee_first_name) EditText edt_employee_first_name;
    @BindView(R.id.edt_employee_last_name) EditText edt_employee_last_name;
    @BindView(R.id.edt_employee_full_name) EditText edt_employee_full_name;
    @BindView(R.id.edt_employee_origin) EditText edt_employee_origin;
    @BindView(R.id.edt_employee_date_birth_day) EditText edt_employee_date_birth_day;
    @BindView(R.id.edt_employee_date_birth_month) EditText edt_employee_date_birth_month;
    @BindView(R.id.edt_employee_date_birth_year) EditText edt_employee_date_birth_year;
    @BindView(R.id.edt_employee_activity) EditText edt_employee_activity;
    @BindView(R.id.edt_employee_curp) EditText edt_employee_curp;

}
