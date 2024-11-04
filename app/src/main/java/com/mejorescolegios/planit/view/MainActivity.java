package com.mejorescolegios.planit.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mejorescolegios.planit.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ImageButton ibMail, ibSignInWithGoogle;
    private TextView tvMail, tvSignInGoogle, tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instancia de Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Quitar la action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Abrir la actividad de login al pulsar botón o texto de email
        ibMail = findViewById(R.id.ibMail);
        tvMail = findViewById(R.id.tvMail);
        ibMail.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        tvMail.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Abrir la actividad de registro al pulsar botón o texto de registro
        tvSignUp = findViewById(R.id.tvSignUpMain);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

    }
}