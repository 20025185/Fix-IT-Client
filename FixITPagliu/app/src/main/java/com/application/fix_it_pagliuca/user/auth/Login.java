package com.application.fix_it_pagliuca.user.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.fix_it_pagliuca.user.dashboard.Dashboard;
import com.example.fix_it_pagliuca.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Objects;

public class Login extends AppCompatActivity {
    //final private static String TAG = "[Login]";  //  DEBUG

    private EditText m_Email, m_Password;
    private ProgressBar m_progressBar;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        m_Email = findViewById(R.id.emailLogin);
        m_Password = findViewById(R.id.passwordLogin);
        Button m_LoginBtn = findViewById(R.id.signinBtn);
        Button m_SignupBtn = findViewById(R.id.signupActBtn);
        TextView m_forgotPsw = findViewById(R.id.forgotPsw);
        m_progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }

        m_LoginBtn.setOnClickListener(view -> {
            String email = m_Email.getText().toString().trim();
            String password = m_Password.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                m_Email.setError("e-Mail richiesta.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                m_Password.setError("Password richiesta.");
                return;
            }

            if (password.length() <= 6) {
                m_Password.setError("Password deve avere almeno 6 caratteri.");
                return;
            }

            m_progressBar.setVisibility(View.VISIBLE);

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    m_progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                } else {
                    Toast.makeText(Login.this, "Error\n\"" + Objects.requireNonNull(task.getException()).getMessage() + "\"", Toast.LENGTH_SHORT).show();
                    m_progressBar.setVisibility(View.GONE);
                }
            });
        });

        m_SignupBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Registration.class)));

        m_forgotPsw.setOnClickListener(view -> {
            final EditText resetMail = new EditText(view.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());

            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Inserisci la tua e-Mail per ricevere il link di recupero password.");
            passwordResetDialog.setView(resetMail);
            passwordResetDialog.setPositiveButton("Si", (dialogInterface, i) -> {
                String email = resetMail.getText().toString();
                fAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(aVoid -> Toast.makeText(Login.this, "Il link di recupero password è stato inviato alla tua e-Mail.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(Login.this, "Il link di recupero password non è stato inviato.", Toast.LENGTH_SHORT).show());
            });

            passwordResetDialog.setNegativeButton("No", (dialogInterface, i) -> {
            });

            passwordResetDialog.create().show();
        });
    }
}