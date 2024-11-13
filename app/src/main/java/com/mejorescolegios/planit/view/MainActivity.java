package com.mejorescolegios.planit.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mejorescolegios.planit.R;
import com.mejorescolegios.planit.model.User;
import com.mejorescolegios.planit.viewmodel.UserViewModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ImageButton ibMail, ibSignInWithGoogle;
    private TextView tvMail, tvSignInGoogle, tvSignUp;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instancia de Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Quitar la action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Iniciar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configuración de Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso);



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

        // Hacer login con Google al pulsar botón o texto de Google
        ibSignInWithGoogle = findViewById(R.id.ibSignInWithGoogle);
        tvSignInGoogle = findViewById(R.id.tvSignInGoogle);
        ibSignInWithGoogle.setOnClickListener(v -> signInWithGoogle());
        tvSignInGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserViewModel userViewModel = new UserViewModel();
                            userViewModel.checkUserExists(user.getUid(), exists -> {
                                if (!exists) {
                                    // Si el usuario no existe, lo creamos en la base de datos
                                    User newUser = new User(user.getDisplayName(), user.getEmail(), user.getUid());
                                    userViewModel.saveUserToDatabase(newUser.getFullName(), newUser.getEmail(), user);
                                }
                                // mensaje de login exitoso
                                Toast.makeText(MainActivity.this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                                // Abrir TaskListActivity
                                startActivity(new Intent(MainActivity.this, TaskListActivity.class));
                                finish();
                            });
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}