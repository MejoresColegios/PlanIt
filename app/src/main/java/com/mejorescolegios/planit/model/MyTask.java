package com.mejorescolegios.planit.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTask implements Parcelable {

    // Atributos
    private String Title;
    private Long creationDate;
    private Long dueDate;
    private Integer progress;
    private Boolean priority;
    private String description;
    private String uidUser;
    private String id;

    // Constructor vacío
    public MyTask() {
    }

    // Constructor
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

    public void setCreationDate(Long creationDate) {
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

    // Método para calcular los días desde la fecha actual hasta la due date y devuelve el resultado
    public int calculateDays() {
        long currentTime = System.currentTimeMillis();
        long diff = this.dueDate - currentTime;
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    // Método que convierte dueDate a un String con formato dd/MM/yyyy
    public String getDueDateString() {
        Date date = new Date(this.dueDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }


    // Métodos de la interfaz Parcelable
    protected MyTask(Parcel in) {
        Title = in.readString();
        creationDate = in.readLong();
        dueDate = in.readLong();
        progress = in.readInt();
        byte tmpPriority = in.readByte();
        priority = tmpPriority == 0 ? null : tmpPriority == 1;
        description = in.readString();
        uidUser = in.readString();
        id = in.readString();
    }

    public static final Creator<MyTask> CREATOR = new Creator<MyTask>() {
        @Override
        public MyTask createFromParcel(Parcel in) {
            return new MyTask(in);
        }

        @Override
        public MyTask[] newArray(int size) {
            return new MyTask[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
        dest.writeLong((Long) creationDate);
        dest.writeLong(dueDate);
        dest.writeInt(progress);
        dest.writeByte((byte) (priority == null ? 0 : priority ? 1 : 2));
        dest.writeString(description);
        dest.writeString(uidUser);
        dest.writeString(id);
    }
}
