package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.Usuario;
import com.asociadosmonterrubio.admin.utils.Session;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.asociadosmonterrubio.admin.utils.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.BindView;

public class ActivityLogin extends AppCompatActivity{

    @BindView(R.id.input_email) EditText edt_email;
    @BindView(R.id.input_password) EditText edt_password;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.checkbox_save_login) CheckBox checkbox_save_login;

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        TextView version = (TextView) findViewById(R.id.versionName);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version.setText("Version ".concat(versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        checkbox_save_login.setChecked(UserPreferences.getPreferenceb(UserPreferences.LOGIN_SAVE_CREDEN));
        checkbox_save_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserPreferences.savePreference(UserPreferences.LOGIN_SAVE_CREDEN, isChecked);
            }
        });

        if (UserPreferences.getPreferenceb(UserPreferences.LOGIN_SAVE_CREDEN)){
            edt_email.setText(UserPreferences.getPreference(UserPreferences.LOGIN_EMAIL));
            edt_password.setText(UserPreferences.getPreference(UserPreferences.LOGIN_PASS));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Session.isSessionValid()){
            SingletonUser.getInstance().setSavedUsuario(UserPreferences.getUserSession());
            Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
            startActivity(intent);
            finish();
        }
    }

    public void login() {
        if (!validate()) {
            onLoginFailed("Algunos campos estan vacios");
            return;
        }

        btn_login.setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando Session...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();

        signIn(email, password);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        btn_login.setEnabled(true);
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edt_email.setError("Ingresa un correo electrónico valido");
            valid = false;
        } else {
            edt_email.setError(null);
        }

        if (password.isEmpty() || password.length() < 5) {
            edt_password.setError("La contraseña debe tener un tamaño minimo de 5 caracteres");
            valid = false;
        } else {
            edt_password.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password){
        Task<AuthResult> task = auth.signInWithEmailAndPassword(email, password);
        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String email = authResult.getUser().getEmail();
                String emailWithoutDomain = email.substring(0, email.indexOf("@"));
                getUserInformation(emailWithoutDomain);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseException firebaseException = ((FirebaseException)e);
                if (firebaseException instanceof FirebaseAuthException) {
                    String errorCode = ((FirebaseAuthException)e).getErrorCode();
                    if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                        onLoginFailed("El correo ingresado no es válido. Intentalo nuevamente.");
                    } else if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                        onLoginFailed("Tu contraseña es incorrecta. Intentalo nuevamente.");
                    } else {
                        onLoginFailed("Error desconocido");
                    }
                }else  if (firebaseException instanceof FirebaseNetworkException){
                    onLoginFailed("Error de conexión. Comprueba tu conexión a internet");
                } else {
                    onLoginFailed("Error desconocido");
                }
            }
        });
    }

    public void getUserInformation(final String userName){
        FireBaseQuery.databaseReference.child(FireBaseQuery.USUARIOS).child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                SingletonUser.getInstance().setUsuario(dataSnapshot);
                Usuario usuario = SingletonUser.getInstance().getUsuario();
                UserPreferences.saveUserSession(usuario);
                saveCredentials();
                Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.getCode();
                Toast.makeText(ActivityLogin.this, "Ocurrio un error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
    }

    public void saveCredentials(){
        if(UserPreferences.getPreferenceb(UserPreferences.LOGIN_SAVE_CREDEN)){
            String email = edt_email.getText().toString();
            String password = edt_password.getText().toString();
            UserPreferences.savePreference(UserPreferences.LOGIN_EMAIL, email);
            UserPreferences.savePreference(UserPreferences.LOGIN_PASS, password);
        }else {
            UserPreferences.savePreference(UserPreferences.LOGIN_EMAIL, "");
            UserPreferences.savePreference(UserPreferences.LOGIN_PASS, "");
        }
    }

}
