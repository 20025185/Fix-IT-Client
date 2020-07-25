package com.example.fix_it_pagliu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    EditText m_FullName, m_Email, m_Password;
    Button m_RegisterBtn;
    TextView m_LoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        m_FullName = findViewById(R.id.nomeRegister);
        m_Email = findViewById(R.id.emailRegister);
        m_Password = findViewById(R.id.pswRegister);
        m_RegisterBtn = findViewById(R.id.signupBtn);
        m_LoginBtn = findViewById(R.id.signinActBtn);
        fAuth = FirebaseAuth.getInstance();

        //  Controllo se l'utente è già loggato
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        m_RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String email = m_Email.getText().toString().trim();
                String password = m_Password.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    m_Email.setError("e-Mail richiesta.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    m_Password.setError("Password richiesta.");
                    return;
                }

                if(password.length() <= 6 ){
                    m_Password.setError("Password deve avere almeno 6 caratteri.");
                    return;
                }

                //  progressbar si può mettere qua.

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        //  Link di verifica per la registrazione

                        FirebaseUser user = fAuth.getCurrentUser();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Register.this, "L'e-Mail di verifica è stata spedita.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "L'e-Mail di verifica non è stata spedita.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(Register.this, "Utente creato.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else {
                        Toast.makeText(Register.this, "Errore\n\"" + task.getException().getMessage() + "\"", Toast.LENGTH_SHORT).show();
                    }
                    }
                });

            }
        });

        m_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}