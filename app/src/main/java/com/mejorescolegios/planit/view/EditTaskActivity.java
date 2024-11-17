package com.mejorescolegios.planit.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mejorescolegios.planit.R;
import com.mejorescolegios.planit.model.MyTask;
import com.mejorescolegios.planit.viewmodel.MyTaskViewModel;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    private TextView tv_newTask, tvNewProgress;
    private EditText etNewTitle, etNewDueDate, etNewDescription;
    private SeekBar sbNewProgress;
    private Switch switchNewPrioritary;
    private Button btnNewSave, btnNewCancel;
    private MyTask myTask;
    private Long dueDate;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private MyTaskViewModel myTaskViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Reutilizo NewTaskActivity
        setContentView(R.layout.activity_new_task);
        // Inicializo los elementos
        tv_newTask = findViewById(R.id.tv_newTask);
        etNewTitle = findViewById(R.id.etNewTitle);
        etNewDueDate = findViewById(R.id.etNewDueDate);
        etNewDescription = findViewById(R.id.etNewDescription);
        switchNewPrioritary = findViewById(R.id.switchNewPrioritary);
        tvNewProgress = findViewById(R.id.tvNewProgress);
        sbNewProgress = findViewById(R.id.sbNewProgress);
        btnNewSave = findViewById(R.id.btnNewSave);
        btnNewCancel = findViewById(R.id.btnNewCancel);

        // Inicializar FirebaseAuth y FirebaseDatabase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://planit-4944e-default-rtdb.europe-west1.firebasedatabase.app");
        myTaskViewModel = new MyTaskViewModel();

        // Cambio el título
        tv_newTask.setText(getString(R.string.edit_task));

        // Obtengo la tarea desde el Intent
        Intent intent = getIntent();
        if (intent != null) {
            myTask = intent.getParcelableExtra("myTask");
            if (myTask != null) {
                // Asigno dueDate de la tarea
                dueDate = myTask.getDueDate();
            }
        }

        // Configuración del botón de fecha
        etNewDueDate.setFocusable(false);
        etNewDueDate.setOnClickListener(v -> showDatePickerDialog());

        // Observador para el resultado de guardado de la tarea
        myTaskViewModel.getTaskSaveResult().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, R.string.task_saved_successfully, Toast.LENGTH_SHORT).show();
                finish();  // Volver a la actividad anterior
            } else {
                Toast.makeText(this, R.string.failed_to_add_task_to_database, Toast.LENGTH_SHORT).show();
            }
        });

        // Configuración de SeekBar y TextView
        sbNewProgress.setMax(getResources().getInteger(R.integer.max_progress));
        sbNewProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvNewProgress.setText(getResources().getString(R.string.progress) + " " + progress + " %");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // relleno los campos con la información de la tarea
        etNewTitle.setText(myTask.getTitle());
        etNewDueDate.setText(myTask.getDueDateString());
        etNewDescription.setText(myTask.getDescription());
        switchNewPrioritary.setChecked(myTask.getPriority());
        tvNewProgress.setText(getResources().getString(R.string.progress) + " " + myTask.getProgress() + " %");
        sbNewProgress.setProgress(myTask.getProgress());

        // Botón cancelar
        btnNewCancel.setOnClickListener(v -> finish());

        // Botón guardar
        btnNewSave.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = etNewTitle.getText().toString().trim();
        String description = etNewDescription.getText().toString().trim();
        int progress = sbNewProgress.getProgress();
        boolean prioritary = switchNewPrioritary.isChecked();

        // Validar los campos de entrada
        if (!validateInputs(title, description)) {
            return;
        }

        // Actualizar los datos de la tarea
        myTask.setTitle(title);
        myTask.setDueDate(dueDate);
        myTask.setDescription(description);
        myTask.setProgress(progress);
        myTask.setPriority(prioritary);

        // Guardar la tarea en la base de datos
        DatabaseReference reference = database.getReference("Tasks").child(myTask.getId());
        reference.setValue(myTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, R.string.task_saved_successfully, Toast.LENGTH_SHORT).show();
                finish();  // Volver a la actividad anterior
            } else {
                Toast.makeText(this, R.string.failed_to_add_task_to_database, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Mostrar el diálogo de selección de fecha
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            etNewDueDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);
            dueDate = selectedDate.getTimeInMillis();
        }, year, month, day);
        datePickerDialog.show();
    }

    // Método para validar los campos de entrada
    private boolean validateInputs(String title, String description) {
        boolean isValid = true;
        if (title.isEmpty()) {
            etNewTitle.setError(getString(R.string.title_cannot_be_empty));
            isValid = false;
        }
        if (dueDate == null) {
            etNewDueDate.setError(getString(R.string.due_date_cannot_be_empty));
            isValid = false;
        }
        return isValid;
    }
}