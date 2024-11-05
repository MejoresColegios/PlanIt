package com.mejorescolegios.planit.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mejorescolegios.planit.R;
import com.mejorescolegios.planit.viewmodel.UserViewModel;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText signupName, signupEmail, signupPassword;
    private Button signupButton;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Quitar la action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Inicializa las vistas
        signupName = findViewById(R.id.signup_fullName);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);

        // Inicializa el ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observa el estado de registro para manejar el resultado
        userViewModel.getRegistrationStatus().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(SignUpActivity.this, getString(R.string.signup_successful), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            } else {
                Toast.makeText(SignUpActivity.this, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(view -> registerUser());
    }

    // Método para registrar al usuario
    private void registerUser() {
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        if (validateInputs(name, email, password)) {
            userViewModel.registerUser(name, email, password);
        }
    }

    // Método para validar los campos de entrada
    private boolean validateInputs(String name, String email, String password) {
        boolean isValid = true;
        if (name.isEmpty()) {
            signupName.setError(getString(R.string.name_cannot_be_empty));
            isValid = false;
        }
        if (email.isEmpty()) {
            signupEmail.setError(getString(R.string.email_cannot_be_empty));
            isValid = false;
        }
        if (password.isEmpty()) {
            signupPassword.setError(getString(R.string.password_cannot_be_empty));
            isValid = false;
        }
        return isValid;
    }
}
