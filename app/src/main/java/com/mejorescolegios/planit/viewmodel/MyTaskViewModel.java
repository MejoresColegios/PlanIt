package com.mejorescolegios.planit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mejorescolegios.planit.model.MyTask;

public class MyTaskViewModel extends ViewModel {

    private final DatabaseReference databaseReference;
    private final MutableLiveData<Boolean> taskSaveResult = new MutableLiveData<>();

    // Constructor
    public MyTaskViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks");
    }

    // Método para obtener el resultado de guardar la tarea
    public LiveData<Boolean> getTaskSaveResult() {
        return taskSaveResult;
    }

    // Método para registrar una tarea
    public void registerTask(String title, long dueDate, int progress, boolean prioritary, String description, FirebaseUser user) {
        if (user != null) {
            String uidUser = user.getUid();
            MyTask newTask = new MyTask(title, dueDate, progress, prioritary, description, uidUser);

            databaseReference.push().setValue(newTask).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    taskSaveResult.setValue(true);
                } else {
                    taskSaveResult.setValue(false);
                }
            });
        }
    }
}