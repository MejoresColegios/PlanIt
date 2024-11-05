package com.mejorescolegios.planit.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTask {

    // Atributos
    private String Title;
    private Object creationDate;
    private Long dueDate;
    private Integer progress;
    private Boolean priority;
    private String description;
    private String uidUser;
    private String id;

    // Constructor vacío
    public MyTask() {
    }

    // Constructor con argumentos
    public MyTask(String title, Long dueDate, Integer progress, Boolean priority, String description, String uidUser) {
        Title = title;
        this.creationDate = System.currentTimeMillis();
        this.dueDate = dueDate;
        this.progress = progress;
        this.priority = priority;
        this.description = description;
        this.uidUser = uidUser;
    }

    // Getters y Setters

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Object getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Object creationDate) {
        this.creationDate = creationDate;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Método para calcular los días desde la fecha actual hasta la due date y devuelve un string con el resultado
    public String calculateDays() {
        Long currentTime = System.currentTimeMillis();
        Long days = (this.dueDate - currentTime) / (1000 * 60 * 60 * 24);
        return days.toString();
    }

    // Método que convierte dueDate a un String con formato dd/MM/yyyy
    public String getDueDateString() {
        Date date = new Date(this.dueDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }


}
