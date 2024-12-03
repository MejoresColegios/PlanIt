package com.mejorescolegios.planit.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mejorescolegios.planit.R;
import com.mejorescolegios.planit.model.MyTask;
import com.mejorescolegios.planit.viewmodel.MyTaskAdapter;

import java.util.ArrayList;

public class TaskListActivity extends AppCompatActivity {

    private ArrayList<MyTask> myTasks = new ArrayList<>();
    private RecyclerView rvTasks;
    private MyTaskAdapter adapter;
    private String uidUser;
    private MenuItem menuItemPrioritary;
    private MenuItem menuItemCompleted;
    private boolean boolprioritary = false;
    private boolean boolcompleted = true;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference myTasksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        adapter = new MyTaskAdapter(this, myTasks);
        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setAdapter(adapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Inicializo Firebase Database y Auth
        database = FirebaseDatabase.getInstance("https://planit-4944e-default-rtdb.europe-west1.firebasedatabase.app");
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            uidUser = user.getUid();
            myTasksRef = database.getReference("Tasks");

            // Leo base de datos filtrando por uidUser y meto las tareas en el ArrayList
            myTasksRef.orderByChild("uidUser").equalTo(uidUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myTasks.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MyTask myTask = dataSnapshot.getValue(MyTask.class);
                        if (myTask != null) {
                            myTask.setId(dataSnapshot.getKey());
                            myTasks.add(myTask);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //Error en log
                    Log.e("ERROR", error.getMessage());
                }
            });
        } else {
            // Muestra un mensaje si el usuario no está autenticado
            Toast.makeText(TaskListActivity.this, getResources().getString(R.string.user_error), Toast.LENGTH_SHORT).show();
            // Opcional: redirigir a la pantalla de login
            Intent intent = new Intent(TaskListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    // Incluyo el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menuItemPrioritary = menu.findItem(R.id.it_prioritary);
        menuItemCompleted = menu.findItem(R.id.it_completed);
        // Coloco el icono adecuado
        iconPrioritary();
        return super.onCreateOptionsMenu(menu);
    }

    // Le doy funcionalidad al menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (R.id.it_new == item.getItemId()) {
            Intent intent = new Intent(TaskListActivity.this, NewTaskActivity.class);
            startActivity(intent);
        } else if (R.id.it_completed == item.getItemId()) {
            // Al pulsar el botón de menú "Completadas", se muestran solo las tareas no completadas
            //Conmutamos el valor booleando
            boolcompleted = !boolcompleted;
            //Colocamos el icono adecuado
            iconCompleted();
            adapter.setBoolCompleted(boolcompleted);
            // si es true se muestran todas las tareas
            if (boolcompleted) {
                myTasksRef.orderByChild("uidUser").equalTo(uidUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myTasks.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MyTask myTask = dataSnapshot.getValue(MyTask.class);
                            if (myTask != null) {
                                myTask.setId(dataSnapshot.getKey());
                                myTasks.add(myTask);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error en log
                        Log.e("ERROR", error.getMessage());
                    }
                });
            } else {
                // si es false, solo se muestran las tareas no completadas
                myTasksRef.orderByChild("uidUser").equalTo(uidUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myTasks.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MyTask myTask = dataSnapshot.getValue(MyTask.class);
                            if (myTask != null && myTask.getProgress() < 100) {
                                myTask.setId(dataSnapshot.getKey());
                                myTasks.add(myTask);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error en log
                        Log.e("ERROR", error.getMessage());
                    }
                });
            }
        } else if (R.id.it_prioritary == item.getItemId()) {
            // Al pulsar el botón de menú "Prioritarias", se muestran solo las tareas prioritarias
            //Conmutamos el valor booleando
            boolprioritary = !boolprioritary;
            //Colocamos el icono adecuado
            iconPrioritary();
            adapter.setBoolPrioritary(boolprioritary);
            // si es true, solo se muestran las tareas prioritarias
            if (boolprioritary) {
                myTasksRef.orderByChild("uidUser").equalTo(uidUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myTasks.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MyTask myTask = dataSnapshot.getValue(MyTask.class);
                            if (myTask != null && myTask.getPriority()) {
                                myTask.setId(dataSnapshot.getKey());
                                myTasks.add(myTask);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error en log
                        Log.e("ERROR", error.getMessage());
                    }
                });
            } else {
                // si es false, se muestran todas las tareas
                myTasksRef.orderByChild("uidUser").equalTo(uidUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myTasks.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MyTask myTask = dataSnapshot.getValue(MyTask.class);
                            if (myTask != null) {
                                myTask.setId(dataSnapshot.getKey());
                                myTasks.add(myTask);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ERROR", error.getMessage());
                    }
                });
            }

            adapter.notifyDataSetChanged();

        } else if (R.id.it_about == item.getItemId()) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.logo);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            // Creo un FrameLayout como contenedor del ImageView
            FrameLayout frameLayout = new FrameLayout(this);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(250, 250);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);

            frameLayout.addView(imageView);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.about));
            alertDialogBuilder.setView(frameLayout); // Usar el FrameLayout como vista principal
            alertDialogBuilder.setMessage("IES El Majuelo\nJosé María Sánchez Infante\n2024.");

            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss(); // Cierra el cuadro de diálogo al presionar "Aceptar"
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (R.id.it_logout == item.getItemId()) {
            // Crear un AlertDialog para confirmar si se desea cerrar sesión
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.confirm_logout))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Si el usuario selecciona "Sí", cerrar sesión
                            auth.signOut();
                            Toast.makeText(TaskListActivity.this, getResources().getString(R.string.text_logout), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TaskListActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Convierte a MainActivity en la primera actividad de la pila
                            startActivity(intent);
                            finish(); // Cierra la actividad actual para evitar regresar a ella con el botón de retroceso
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss(); // Si el usuario selecciona "No", cerrar el diálogo
                        }
                    })
                    .show();
        } else if (R.id.it_exit == item.getItemId()) {
            // Crear un AlertDialog para confirmar si desea salir de la aplicación
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.confirm_exit))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Si el usuario selecciona "Sí", salir de la aplicación
                            finishAffinity(); // Cierra todas las actividades y termina la aplicación
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss(); // Si el usuario selecciona "No", cerrar el diálogo
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    // Funcionalidad al menú contextual
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.cmEdit) {
            // Editar el contacto seleccionado en EditContactActivity
            Intent intent = new Intent(TaskListActivity.this, EditTaskActivity.class);
            intent.putExtra("myTask", adapter.getSelectedMyTask());
            startActivity(intent);

        } else if (itemId == R.id.cmDelete) {
            // Obtener la tarea seleccionada
            MyTask myTask = adapter.getSelectedMyTask();
            if (myTask != null && myTask.getId() != null) {
                // Crear y mostrar un AlertDialog para confirmar la eliminación
                new AlertDialog.Builder(TaskListActivity.this)
                        .setTitle(R.string.confirm_delete_title)
                        .setMessage(R.string.confirm_delete_message)
                        .setPositiveButton(R.string.delete, (dialog, which) -> {
                            // Eliminar la tarea si el usuario confirma
                            myTasksRef.child(myTask.getId()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(TaskListActivity.this, R.string.deleted_task, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TaskListActivity.this, R.string.error_deleting_task, Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            // Cerrar el diálogo si el usuario cancela
                            dialog.dismiss();
                        })
                        .show();
            } else {
                Toast.makeText(TaskListActivity.this, R.string.invalid_task, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    //Método para cambiar el icono de acción para mostrar todas las tareas o solo prioritarias
    private void iconPrioritary(){
        if(boolprioritary)
            //Ponemos en la barra de herramientas el icono PRIORITARIAS
            menuItemPrioritary.setIcon(android.R.drawable.btn_star_big_on);
        else
            //Ponemos en la barra de herramientas el icono NO PRIORITARIAS
            menuItemPrioritary.setIcon(android.R.drawable.btn_star_big_off);
    }

    //Método para cambiar el icono de acción para mostrar todas las tareas o solo completadas
    private void iconCompleted(){
        if(boolcompleted)
            menuItemCompleted.setIcon(android.R.drawable.checkbox_on_background);
        else
            menuItemCompleted.setIcon(android.R.drawable.checkbox_off_background);
    }
}