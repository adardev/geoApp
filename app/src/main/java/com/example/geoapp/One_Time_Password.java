package com.example.geoapp;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class One_Time_Password extends AppCompatActivity {
    Long duracion=60L;
    EditText OTP;
    Button SiguienteOTP;
    ProgressBar OTPbar;
    FirebaseAuth auth = FirebaseAuth.getInstance ();
    String TELEFONO;
    Context context;
    String codigoVerificacion;
    PhoneAuthProvider.ForceResendingToken ResendingToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        EdgeToEdge.enable (this);
        setContentView (R.layout.activity_one_time_password);
        OTP=findViewById (R.id.OTP);
        SiguienteOTP=findViewById (R.id.siguienteOTP);
        OTPbar=findViewById (R.id.progressBarOTP);
        TELEFONO=getIntent ().getExtras ().getString ("telefono");
        enviarOTP (TELEFONO,false);
        SiguienteOTP.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String codigoOTP=OTP.getText ().toString ();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential (codigoVerificacion,codigoOTP);
                singIn (credential);
                progreso (true);
            }
        });
    }
    void enviarOTP(String telefono, boolean enviado){
        startResendTimer();
        progreso (true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions
                        .newBuilder (auth)
                        .setPhoneNumber (TELEFONO).setTimeout (duracion, TimeUnit.SECONDS)
                        .setActivity (this)
                        .setCallbacks (new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                singIn(phoneAuthCredential);
                                progreso (false);
                            }
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(One_Time_Password.this, "error en la verificacion", Toast.LENGTH_SHORT).show();
                                progreso (false);
                            }
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent (s, forceResendingToken);
                                codigoVerificacion=s;
                                ResendingToken=forceResendingToken;
                                Toast.makeText(One_Time_Password.this, "codigo enviado correctamente", Toast.LENGTH_SHORT).show();
                                progreso (false);
                            }
                        });
        if(enviado){
            PhoneAuthProvider.verifyPhoneNumber (builder.setForceResendingToken (ResendingToken).build ());
        }
        else {
            PhoneAuthProvider.verifyPhoneNumber (builder.build ());
        }
    }
    void progreso(boolean enproceso){
        if(enproceso){
            OTPbar.setVisibility (View.VISIBLE);
            SiguienteOTP.setVisibility (View.GONE);
        }else{
            OTPbar.setVisibility (View.GONE);
            SiguienteOTP.setVisibility (View.VISIBLE);
        }
    }
    void singIn(PhoneAuthCredential phoneAuthCredential){
        progreso (true);
        auth.signInWithCredential (phoneAuthCredential).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progreso (false);
                if(task.isSuccessful ()){
                    Intent intent = new Intent (One_Time_Password.this,Username.class);
                    intent.putExtra ("telefono",TELEFONO);
                    startActivity (intent);
                }else{
                    Toast.makeText(One_Time_Password.this, "error en la verificacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void startResendTimer(){}
}