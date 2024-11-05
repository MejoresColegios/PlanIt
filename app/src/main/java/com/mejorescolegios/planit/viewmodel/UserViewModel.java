package com.mejorescolegios.planit.viewmodel;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mejorescolegios.planit.model.User;

public class UserViewModel extends ViewModel {

    private final DatabaseReference databaseReference;
    private final FirebaseAuth auth;
    private final MutableLiveData<Boolean> registrationStatus = new MutableLiveData<>();

    // Constructor
    public UserViewModel() {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    // Método para obtener el estado de registro
    public LiveData<Boolean> getRegistrationStatus() {
        return registrationStatus;
    }

    // Método para autenticar y registrar al usuario en Realtime Database
    public void registerUser(final String fullName, final String email, final String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    // Actualizar el perfil del usuario con el nombre
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(profileUpdateTask -> {
                        if (profileUpdateTask.isSuccessful()) {
                            saveUserToDatabase(fullName, email, user);
                        } else {
                            registrationStatus.setValue(false);
                        }
                    });
                }
            } else {
                registrationStatus.setValue(false);
            }
        });
    }

    // Método para guardar al usuario en Realtime Database
    private void saveUserToDatabase(String fullName, String email, FirebaseUser user) {
        String uid = user.getUid();
        User newUser = new User(fullName, email, uid);

        // Utilizar push() para generar un ID aleatorio
        databaseReference.push().setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("UserViewModel", "User added successfully to database with random ID");
                registrationStatus.setValue(true);
            } else {
                Log.e("UserViewModel", "Failed to add user to database", task.getException());
                registrationStatus.setValue(false);
            }
        });
    }
}


