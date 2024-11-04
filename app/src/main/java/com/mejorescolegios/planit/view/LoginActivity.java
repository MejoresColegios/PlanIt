package com.mejorescolegios.planit.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mejorescolegios.planit.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Button loginEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // ocultar la action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginEmailButton = findViewById(R.id.loginButton);

        // Login con email y contraseña
        loginEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) { auth.signInWithEmailAndPassword(email, pass) .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, TaskListActivity.class ));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                    } else { loginPassword.setError(getString(R.string.password_cannot_be_empty));
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError(getString(R.string.email_cannot_be_empty));
                } else { loginEmail.setError(getString(R.string.please_enter_a_valid_email));
                }
            }
        });

    }
}