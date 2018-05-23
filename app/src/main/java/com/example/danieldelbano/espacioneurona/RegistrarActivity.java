package com.example.danieldelbano.espacioneurona;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.danieldelbano.espacioneurona.Config.FirebaseReferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarActivity extends AppCompatActivity {

    FirebaseDatabase database= FirebaseDatabase.getInstance();
    final DatabaseReference bbddUsuarios=database.getReference(FirebaseReferences.USUARIOS_REFERENCE);
    Button btnAceptarRegistro;
    EditText edNombre,edApellidos,edEmail,edPass1,edPass2;
    private FirebaseAuth mAuth;
// ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        btnAceptarRegistro=(Button)findViewById(R.id.btnAceptarRegistro);
        edNombre=(EditText)findViewById(R.id.edNombreReg);
        //edApellidos=(EditText)findViewById(R.id.edApellidosReg);
        edEmail=(EditText)findViewById(R.id.edEmailReg);
        edPass1=(EditText)findViewById(R.id.edPassReg);
        edPass2=(EditText)findViewById(R.id.edPassReg2);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
       /* mAuth=FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });*/

        btnAceptarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (edNombre.getText().toString().isEmpty() | edApellidos.getText().toString().isEmpty() | edEmail.getText().toString().isEmpty() | edPass1.getText().toString().isEmpty() | edPass2.getText().toString().isEmpty()){
                    Toast.makeText(RegistrarActivity.this, "Faltan campos por completar", Toast.LENGTH_SHORT).show();
                }else{
                    if (edPass1.getText().toString().equalsIgnoreCase(edPass2.getText().toString())){
                        Registrar(edEmail.getText().toString(),edPass1.getText().toString());
                        Usuario usuario=new Usuario(edNombre.getText().toString(),edApellidos.getText().toString(),edEmail.getText().toString());
                        bbddUsuarios.push().setValue(usuario);
                    }else{
                        Toast.makeText(RegistrarActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }

                }*/
                if (edPass1.getText().toString().equalsIgnoreCase(edPass2.getText().toString())){
                    Registrar(edEmail.getText().toString(),edPass1.getText().toString());
                    //Usuario usuario=new Usuario(edNombre.getText().toString(),edEmail.getText().toString());
                    //bbddUsuarios.push().setValue(usuario);
                }else{
                    Toast.makeText(RegistrarActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void Registrar(String email, String pass){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegistrarActivity.this, "Usuario creado correctamente, acepte su email de verificación para continuar", Toast.LENGTH_SHORT).show();
                    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification();
                    InsertarUserOnBBDD();
                }else{
                    Toast.makeText(RegistrarActivity.this, task.getException().getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createAccount(String email, String password) {
        //Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

       // showProgressDialog();

        // [START create_user_with_email]
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            //Toast.makeText(RegistrarActivity.this, "Usuario creado correctamente, acepte su email de verificación para continuar", Toast.LENGTH_SHORT).show();
                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification();
                            ConstraintLayout constraintLayout=(ConstraintLayout)findViewById(R.id.layoutRegistrar);
                            Snackbar snackbar=Snackbar.make(constraintLayout,"Usuario creado correctamente, acepte su email de verificación",Snackbar.LENGTH_LONG);
                            snackbar.setAction("Ir a Login", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                            snackbar.show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrarActivity.this, "Autenticación fallida.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }


    private boolean validateForm() {
        boolean valid = true;

        String nombreyapellidos = edNombre.getText().toString();
        if (TextUtils.isEmpty(nombreyapellidos)) {
            edNombre.setError("Requerido.");
            valid = false;
        } else {
            edNombre.setError(null);
        }

        String email = edEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edEmail.setError("Requerido.");
            valid = false;
        } else {
            edEmail.setError(null);
        }

        String password = edPass1.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edPass1.setError("Requerido.");
            valid = false;
        } else {
            edPass1.setError(null);
        }

        String password2 = edPass2.getText().toString();
        if (TextUtils.isEmpty(password2)) {
            edPass2.setError("Requerido.");
            valid = false;
        } else {
            edPass2.setError(null);
        }

        return valid;
    }

    private void InsertarUserOnBBDD(){

    }
}
