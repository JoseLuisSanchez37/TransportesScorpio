package com.asociadosmonterrubio.admin.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.Bind;

public class ActivityLogin extends AppCompatActivity {

    @Bind(R.id.input_email) EditText edt_email;
    @Bind(R.id.input_password) EditText edt_password;
    @Bind(R.id.btn_login) Button btn_login;

    private FirebaseAuth auth;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        btn_login.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Iniciando sesion...");
        progressDialog.show();

        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();

        signIn(email, password);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Autenticación fallida. Intentelo nuevamente", Toast.LENGTH_LONG).show();
        btn_login.setEnabled(true);
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
                onLoginFailed();
            }
        });

        /*task.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String email = auth.getCurrentUser().getEmail();
                    if (!TextUtils.isEmpty(email)) {
                        String emailWithoutDomain = email.substring(0, email.indexOf("@"));
                        getUserInformation(emailWithoutDomain);
                    }else
                        Toast.makeText(ActivityLogin.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityLogin.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    public void getUserInformation(String userName){
        FireBaseQuery.databaseReference.child(FireBaseQuery.USUARIOS).child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SingletonUser.getInstance().setUsuario(dataSnapshot);
                Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.getCode();
            }
        });
    }

}
