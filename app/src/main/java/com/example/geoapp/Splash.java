package com.example.geoapp;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.geoapp.utils.utilsFirebase;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (utilsFirebase.islogged ()) {
                    Intent intent = new Intent(Splash.this, StartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Splash.this, LoginNumber.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 1500);
    }
}