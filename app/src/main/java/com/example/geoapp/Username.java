package com.example.geoapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.geoapp.utils.utilsFirebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;
import com.example.geoapp.model.userModel;

public class Username extends AppCompatActivity {
    EditText username;
    Button finalizar;
    ProgressBar userbar;
    FirebaseFirestore db;
    String telefono;
    userModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_username);
        username = findViewById(R.id.user);
        finalizar = findViewById(R.id.finalizar);
        userbar = findViewById(R.id.progressBarUser);
        telefono = Objects.requireNonNull(getIntent().getExtras()).getString("telefono");
        getusuario();
        db = FirebaseFirestore.getInstance();
        finalizar.setOnClickListener((v -> setusername()));
    }
    void setusername() {
        String user = username.getText().toString();
        if (user.isEmpty() || user.length() < 3) {
            username.setError("debe de tener mas de 3 caracteres");
            return;
        }
        progreso(true);
        if (userModel != null) {
            userModel.setUsername(user);
        } else {
            userModel = new userModel(telefono, Timestamp.now(), user);
        }
        utilsFirebase.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(Username.this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
    void getusuario() {
        progreso(true);
        utilsFirebase.currentUserDetails().get().addOnCompleteListener(task -> {
            progreso(false);
            if (task.isSuccessful()) {
                task.getResult().toObject(userModel.class);
                if (userModel != null) {
                    username.setText(userModel.getUsername());
                }
            }
        });
    }
    void progreso(boolean enproceso) {
        if (enproceso) {
            userbar.setVisibility(View.VISIBLE);
            finalizar.setVisibility(View.GONE);
        } else {
            userbar.setVisibility(View.GONE);
            finalizar.setVisibility(View.VISIBLE);
        }
    }
}