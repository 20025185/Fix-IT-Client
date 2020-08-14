package com.example.fix_it_pagliu.employee;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fix_it_pagliu.MainActivity;
import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.user.auth.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginEmployee extends AppCompatActivity {
    private final static String TAG = "[Login Impiegato] : ";


    private FirebaseAuth fAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference dbReference;

    //  XML
    private EditText m_Email, m_Password;
    private TextView m_forgotPsw, m_userLogin;
    private Button m_LoginBtn;
    private ProgressBar m_progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_employee);
        FirebaseApp.initializeApp(this);
        m_Email = findViewById(R.id.emailLogin);
        m_Password = findViewById(R.id.passwordLogin);
        m_LoginBtn = findViewById(R.id.signinBtn);
        m_forgotPsw = findViewById(R.id.forgotPsw);
        m_userLogin = findViewById(R.id.usersLogin);
        m_progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        dbReference = rootNode.getReference("users");

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        m_userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        m_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
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

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = fAuth.getCurrentUser().getUid();
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("role").getValue(String.class).equals("employee")) {
                                        Toast.makeText(LoginEmployee.this, "Impiegato loggato con successo.", Toast.LENGTH_SHORT).show();
                                        m_progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    } else {
                                        Toast.makeText(LoginEmployee.this, "Stai tentando di accedere attraverso la pagina non corretta per la tua tipologia di utente.\n", Toast.LENGTH_SHORT).show();
                                        m_progressBar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d(TAG, databaseError.getMessage());
                                }
                            };
                            dbReference.child(uid).addListenerForSingleValueEvent(valueEventListener);
                        } else {
                            Toast.makeText(LoginEmployee.this, "Error\n\"" + task.getException().getMessage() + "\"", Toast.LENGTH_SHORT).show();
                            m_progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });

    }
}
