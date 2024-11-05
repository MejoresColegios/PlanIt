package com.mejorescolegios.planit.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mejorescolegios.planit.R;
import com.mejorescolegios.planit.viewmodel.MyTaskViewModel;

import java.util.Calendar;

public class NewTaskActivity extends AppCompatActivity {

    private EditText etNewTitle, etNewDueDate, etNewDescription;
    private SeekBar sbNewProgress;
    private TextView tvNewProgress;
    private Button btnSave, btnCancel;
    private Switch switchNewPrioritary;
    private Long dueDate;

    private FirebaseAuth auth;
    private MyTaskViewModel myTaskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // Initialize Firebase y ViewModel
        auth = FirebaseAuth.getInstance();
        myTaskViewModel = new MyTaskViewModel();

        // Inicializar los componentes
        etNewTitle = findViewById(R.id.etNewTitle);
        etNewDueDate = findViewById(R.id.etNewDueDate);
        etNewDescription = findViewById(R.id.etNewDescription);
        sbNewProgress = findViewById(R.id.sbNewProgress);
        tvNewProgress = findViewById(R.id.tvNewProgress);
        switchNewPrioritary = findViewById(R.id.switchNewPrioritary);

        // Observador para el resultado de guardado de la tarea
        myTaskViewModel.getTaskSaveResult().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, R.string.task_saved_successfully, Toast.LENGTH_SHORT).show();
                finish();  // Volver a la actividad anterior
            } else {
                Toast.makeText(this, R.string.failed_to_add_task_to_database, Toast.LENGTH_SHORT).show();
            }
        });

        // Configuración inicial de SeekBar y TextView
        int defaultProgress = getResources().getInteger(R.integer.default_progress);
        sbNewProgress.setProgress(defaultProgress);
        tvNewProgress.setText(getResources().getString(R.string.progress) + " " + defaultProgress + " %");

        // Configuración del botón de fecha
        etNewDueDate.setFocusable(false);
        etNewDueDate.setOnClickListener(v -> showDatePickerDialog());

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

        // Configuración de los botones
        btnCancel = findViewById(R.id.btnNewCancel);
        btnCancel.setOnClickListener(v -> finish());

        btnSave = findViewById(R.id.btnNewSave);
        btnSave.setOnClickListener(v -> saveTask());

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
        // Guardar la tarea
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            myTaskViewModel.registerTask(title, dueDate, progress, prioritary, description, user);
        }
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
        if (description.isEmpty()) {
            etNewDescription.setError(getString(R.string.description_cannot_be_empty));
            isValid = false;
        }
        if (dueDate == null) {
            etNewDueDate.setError(getString(R.string.due_date_cannot_be_empty));
            isValid = false;
        }
        return isValid;
    }
}