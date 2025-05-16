package com.example.geoapp;

public class global {
    private static global instance;
    private String uid;
    private String nombre;
    private global() {}
    public static global getInstance() {
        if (instance == null) {
            instance = new global();
        }
        return instance;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
