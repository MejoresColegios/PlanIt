package com.mejorescolegios.planit.viewmodel;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mejorescolegios.planit.R;
import com.mejorescolegios.planit.model.MyTask;

import java.util.List;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.MyTaskViewHolder> {
    private Context context;
    private List<MyTask> myTasks;
    private MyTask selectedMyTask;

    private boolean boolPioritary;
    public void setBoolPrioritary(boolean boolPioritary) { this.boolPioritary = boolPioritary; }

    public MyTaskAdapter(Context context, List<MyTask> myTasks) {
        this.context = context;
        this.myTasks = myTasks;
    }

    @NonNull
    @Override
    public MyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false);
        return new MyTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTaskViewHolder holder, int position) {
        MyTask myTask = myTasks.get(position);
        // Si myTask.getPriority() es true, cambiar la estrella a amarilla
        if (myTask.getPriority()) {
            holder.ivStar.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.ivStar.setImageResource(android.R.drawable.btn_star_big_off);
        }
        holder.tvTaskTitle.setText(myTask.getTitle());
        holder.pbTask.setProgress(myTask.getProgress());
        holder.tvDate.setText(myTask.getDueDateString());
        int daysLeft = myTask.calculateDays();
        holder.tvDays.setText(String.valueOf(daysLeft)); // Convertimos el int a String
        // Si el número de días es menor que 0, cambiar el color a rojo
        if (daysLeft < 0) {
            holder.tvDays.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.tvDays.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() { return myTasks.size(); }

    public MyTask getSelectedMyTask() { return selectedMyTask; }

    // Clase interna para el ViewHolder
    public class MyTaskViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStar;
        TextView tvTaskTitle, tvDate, tvDays;
        ProgressBar pbTask;

        // Constructor
        public MyTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStar = itemView.findViewById(R.id.ivStar);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDays = itemView.findViewById(R.id.tvDays);
            pbTask = itemView.findViewById(R.id.pbTask);

            // Muestro el menú contextual
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        MenuInflater inflater = new MenuInflater(context);
                        inflater.inflate(R.menu.context_menu, menu);
                        selectedMyTask = myTasks.get(getAdapterPosition());
                    }
                }
            });
        }
    }
}
