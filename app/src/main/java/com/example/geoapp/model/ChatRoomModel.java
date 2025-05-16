package com.example.geoapp.model;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.List;
public class ChatRoomModel {
    String chatroomidd;
    private String ultimoMensaje;
    private Timestamp timestamp;
    private List<String> usuarios;
    public ChatRoomModel(String chatroomidd, Timestamp timestamp, String ultimoMensaje, List<String> usuarios) {
        this.chatroomidd = chatroomidd;
        this.timestamp = timestamp;
        this.ultimoMensaje = ultimoMensaje;
        this.usuarios = usuarios;
    }
    public String getChatroomidd() {
        return chatroomidd;
    }
    public void setChatroomidd(String chatroomidd) {
        this.chatroomidd = chatroomidd;
    }
    public ChatRoomModel() {
    }
    public ChatRoomModel(List<String> usuarios) {
        this.usuarios = usuarios;
    }
    public String getUltimoMensaje() {
        return ultimoMensaje;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    @PropertyName("users")
    public List<String> getUsuarios() {
        return usuarios;
    }
    @PropertyName("users")
    public void setUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }
}