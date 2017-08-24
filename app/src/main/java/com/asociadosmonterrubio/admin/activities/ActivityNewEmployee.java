package com.asociadosmonterrubio.admin.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.utils.SingletonEmployees;
import com.asociadosmonterrubio.admin.utils.Util;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.Employee;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ActivityNewEmployee extends AppCompatActivity implements View.OnClickListener{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public Bitmap picture_taken = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_get_info_employee);
        ButterKnife.bind(this);
        btn_take_picture.setOnClickListener(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.subtitle_new_worker));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_employee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                if (areFieldsCompleted()) {
                    progressDialog = ProgressDialog.show(this, "Guardando información", "Espere porfavor...", true, false);
                    Employee employee = getEmployee();
                    employee = FireBaseQuery.pushNewEmployee(employee);
                    employee.setImage(picture_taken);
                    SingletonEmployees.getInstance().add(employee);
                    upLoadImage(employee.getKey());
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
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                this.picture_taken = imageBitmap;
                img_took.setImageBitmap(imageBitmap);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "Es necesario activar la camara para poder tomar fotos", Toast.LENGTH_LONG).show();
                    }
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
        fields.add(edt_employee_date_birth);
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
        if (this.picture_taken == null) {
            Toast.makeText(this, "Es necesario tomar la fotografia", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Employee getEmployee(){
        Employee employee = new Employee();
        employee.setNombre(edt_employee_full_name.getText().toString());
        employee.setApellido_Paterno(edt_employee_first_name.getText().toString());
        employee.setApellido_Materno(edt_employee_last_name.getText().toString());
        employee.setLugar_Nacimiento(edt_employee_origin.getText().toString());
        employee.setFecha_Nacimiento(edt_employee_date_birth.getText().toString());
        employee.setActividad(Employee.JORNALERO);
        employee.setCURP(edt_employee_curp.getText().toString());
        employee.setEnganche("1000");
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        employee.setFechaSalida(date);
        return employee;
    }

    private void upLoadImage(String pushId){
        if (picture_taken != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            picture_taken.compress(Bitmap.CompressFormat.JPEG, 100, baos);

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
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    onBack();
                }
            });
        }
    }

    private void onBack(){
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        Util.hideKeyboard(this);
        onBackPressed();
    }


    @Bind(R.id.btn_take_picture) Button btn_take_picture;
    @Bind(R.id.img_took) ImageView img_took;
    @Bind(R.id.edt_employee_key) EditText edt_employee_id;
    @Bind(R.id.edt_employee_first_name) EditText edt_employee_first_name;
    @Bind(R.id.edt_employee_last_name) EditText edt_employee_last_name;
    @Bind(R.id.edt_employee_full_name) EditText edt_employee_full_name;
    @Bind(R.id.edt_employee_origin) EditText edt_employee_origin;
    @Bind(R.id.edt_employee_date_birth) EditText edt_employee_date_birth;
    @Bind(R.id.edt_employee_activity) EditText edt_employee_activity;
    @Bind(R.id.edt_employee_curp) EditText edt_employee_curp;

}
