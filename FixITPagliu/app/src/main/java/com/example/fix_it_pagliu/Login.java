package com.example.fix_it_pagliu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.example.fix_it_pagliu.user.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    final private static String TAG = "[Login]";

    private EditText m_Email, m_Password;
    private TextView m_forgotPsw, m_employeeLogin;
    private Button m_LoginBtn, m_SignupBtn;
    private ProgressBar m_progressBar;

    private FirebaseAuth fAuth;
    private DatabaseReference dbReference;
    private FirebaseDatabase rootNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        m_Email = findViewById(R.id.emailLogin);
        m_Password = findViewById(R.id.passwordLogin);
        m_LoginBtn = findViewById(R.id.signinBtn);
        m_SignupBtn = findViewById(R.id.signupActBtn);
        m_forgotPsw = findViewById(R.id.forgotPsw);
        m_progressBar = findViewById(R.id.progressBar);
        m_employeeLogin = findViewById(R.id.employLogin);

        fAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        dbReference = rootNode.getReference("users");

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        m_employeeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginEmployee.class));
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
                                    if (dataSnapshot.child("role").getValue(String.class).equals("user")) {
                                        Toast.makeText(Login.this, "User loggato con successo.", Toast.LENGTH_SHORT).show();
                                        m_progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    } else {
                                        Toast.makeText(Login.this, "Login non corretto per la tipologia di account utilizzato.\n", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Login.this, "Error\n\"" + task.getException().getMessage() + "\"", Toast.LENGTH_SHORT).show();
                            m_progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        m_SignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        m_forgotPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Inserisci la tua e-Mail per ricevere il link di recupero password.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Il link di recupero password è stato inviato alla tua e-Mail.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Il link di recupero password non è stato inviato.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //  Chiude il dialog
                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }
}