package com.mejorescolegios.planit.model;

public class User {

    private String fullName;
    private String email;
    private String uidUser;

    // Constructor sin argumentos
    public User() {
    }

    // Constructor con argumentos
    public User(String fullName, String email, String uidUser) {
        this.fullName = fullName;
        this.email = email;
        this.uidUser = uidUser;
    }

    // Getters y Setters


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }
}
