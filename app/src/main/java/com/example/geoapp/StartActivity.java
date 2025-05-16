package com.example.geoapp;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    com.example.geoapp.chat chat;
    com.example.geoapp.map map;
    String nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        chat = new chat();
        map = new map();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            global.getInstance().setUid(uid);
            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("username");
                            global.getInstance().setNombre(nombre);
                        } else {
                            Toast.makeText(StartActivity.this, "Usuario no encontrado en Firestore", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(StartActivity.this, "Error al obtener datos", Toast.LENGTH_LONG).show();
                    });
        }
        bottomNavigationView = findViewById(R.id.nav_menu);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrame, chat)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.chat);
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.map) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", nombre);
                    map.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFrame, map)
                            .commit();
                }
                if (item.getItemId() == R.id.chat) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFrame, chat)
                            .commit();
                }
                return true;
            }
        });
    }
}