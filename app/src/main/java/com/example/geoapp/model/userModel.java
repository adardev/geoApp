package com.example.geoapp.model;
import com.google.firebase.Timestamp;
public class userModel {
    private  String telefono;
    private  String username;
    private Timestamp timestamp;
    public userModel() {}
    public userModel(String telefono, Timestamp timestamp, String username) {
        this.telefono = telefono;
        this.timestamp = timestamp;
        this.username = username;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}