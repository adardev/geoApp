package com.example.geoapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

public class LoginNumber extends AppCompatActivity {
    EditText Number;
    Button SiguienteNumber;
    ProgressBar Numberbar;
    CountryCodePicker Countrycode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        EdgeToEdge.enable (this);
        setContentView (R.layout.activity_login_number);
        Number=findViewById (R.id.telefono);
        SiguienteNumber=findViewById (R.id.siguienteTelefono);
        Numberbar=findViewById (R.id.progressBarNumero);
        Countrycode=findViewById (R.id.coutrycode);
        Countrycode.registerCarrierNumberEditText (Number);
        Numberbar.setVisibility (View.GONE);
        SiguienteNumber.setOnClickListener ((v) -> {
            if(!Countrycode.isValidFullNumber ()){
                Number.setError ("telefono invalido");
                return;
            }
            Intent intent = new Intent (LoginNumber.this,One_Time_Password.class);
            intent.putExtra ("telefono",Countrycode.getFullNumberWithPlus ());
            startActivity (intent);
        });
    }
}