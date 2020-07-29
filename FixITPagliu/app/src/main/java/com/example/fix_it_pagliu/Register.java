package com.example.fix_it_pagliu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fix_it_pagliu.database.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";

    EditText m_FullName, m_Surname, m_Birthday, m_CodiceFiscale, m_Username, m_Email, m_Password, m_PasswordConf;
    Button m_RegisterBtn;
    TextView m_LoginBtn;

    DatePickerDialog.OnDateSetListener mDateSetListener;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);

        m_FullName = findViewById(R.id.nomeRegister);
        m_Surname = findViewById(R.id.cognomeRegister);
        m_Birthday = findViewById(R.id.Birthday);
        m_CodiceFiscale = findViewById(R.id.codiceFiscale);
        m_Email = findViewById(R.id.emailRegister);
        m_Username = findViewById(R.id.username);
        m_Password = findViewById(R.id.pswRegister);
        m_PasswordConf = findViewById(R.id.psWConfirm);
        m_RegisterBtn = findViewById(R.id.signupBtn);
        m_LoginBtn = findViewById(R.id.signinActBtn);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        m_Birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBirthdayDialog();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, final int year, final int month, final int day) {
                m_Birthday.setText(day + "/" + month + "/" + year);
            }
        };
        m_RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterButton();
            }
        });
        m_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    public void handleRegisterButton() {
        final String fullName = m_FullName.getText().toString().trim();
        final String surName = m_Surname.getText().toString().trim();
        final String fiscalCode = m_CodiceFiscale.getText().toString().trim();
        final String email = m_Email.getText().toString().trim();
        String birthday = m_Birthday.getText().toString().trim();
        String password = m_Password.getText().toString().trim();
        String passwordConf = m_PasswordConf.getText().toString().trim();

        if (!checkCampi(fullName, surName, birthday, fiscalCode, email, password, passwordConf))
            return;

        registerUser(fullName, surName, birthday, fiscalCode, email, password);
    }

    public void registerUser(final String fullname, final String surname, final String birthday, final String fiscalcode, final String email, String password) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //  Link di verifica per la registrazione
                    FirebaseUser userAuth = fAuth.getCurrentUser();
                    userAuth.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
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

                    //  Creazione dell'utente sulla piattaforma
                    //String id = databaseReference.push().getKey();
                    User user = new User(fullname, surname, fiscalcode, birthday, email);
                    String uid = userAuth.getUid();
                    databaseReference.child(uid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Register.this, "Utente creato sulla piattaforma, è necessario attivare l'account.\n", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Si è stato un errore nella creazione dell'utente.\n", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Register.this, "Errore\n\"" + task.getException().getMessage() + "\"", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void handleBirthdayDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(Register.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener
                , year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public boolean checkCampi(String fullname, String surname, String birthday, String fiscalCode, String email, String psw, String pswConf) {

        Pattern fullnamePattern, emailPattern, fiscalCodePattern, passwordPattern, birthdayPattern;
        Matcher fullnameMatcher, emailMatcher, fiscalCodeMatcher, passwordMatcher, birthdayMatcher;


        if (fullname.isEmpty() || (fullname.length() < 4 || fullname.length() >= 30)) {
            m_FullName.setError("Inserire un nome valido");
            m_FullName.requestFocus();
            return false;
        } else {
            fullnamePattern = Pattern.compile("^[a-zA-Z]+(([\\'\\,\\.\\- ][a-zA-Z ])?[a-zA-Z]*)*$");
            fullnameMatcher = fullnamePattern.matcher(fullname);

            if (!fullnameMatcher.find()) {
                m_FullName.setError("Inserire un nome valido");
                m_FullName.requestFocus();
                return false;
            }
        }

        if (surname.isEmpty() || (surname.length() < 4 || surname.length() >= 30)) {
            m_Surname.setError("Inserire un cognome valido");
            m_Surname.requestFocus();
            return false;
        } else {
            fullnamePattern = Pattern.compile("^[a-zA-Z]+(([\\'\\,\\.\\- ][a-zA-Z ])?[a-zA-Z]*)*$");
            fullnameMatcher = fullnamePattern.matcher(fullname);

            if (!fullnameMatcher.find()) {
                m_FullName.setError("Inserire un nome valido");
                m_FullName.requestFocus();
                return false;
            }
        }


        if (fiscalCode.isEmpty() || fiscalCode.length() < 16) {
            m_CodiceFiscale.setError("Inserire un codice fiscale valido.");
            m_CodiceFiscale.requestFocus();
            return false;
        } else {
            fiscalCodePattern = Pattern.compile("^[A-Z]{6}[A-Z0-9]{2}[A-Z][A-Z0-9]{2}[A-Z][A-Z0-9]{3}[A-Z]$");
            fiscalCodeMatcher = fiscalCodePattern.matcher(fiscalCode);
            if (!fiscalCodeMatcher.find()) {
                m_CodiceFiscale.setError("Inserire un codice fiscale valido.");
                m_CodiceFiscale.requestFocus();
                return false;
            }
        }

        if (TextUtils.isEmpty(email)) {
            m_Email.setError("e-Mail richiesta.");
            m_Email.requestFocus();
            return false;
        } else {
            emailPattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
            emailMatcher = emailPattern.matcher(email);
            if (!emailMatcher.find()) {
                m_Email.setError("Inserire una e-Mail valida.");
                m_Email.requestFocus();
                return false;
            }
        }

        if (TextUtils.isEmpty(psw)) {
            m_Password.setError("Password richiesta.");
            m_Password.requestFocus();
            return false;
        } else {

            if (psw.length() <= 6 && psw.length() >= 15) {
                m_Password.setError("Password deve avere un minimo di 8 caratteri fino ad un massimo di 15.");
                m_Password.requestFocus();
                return false;
            } else {
                passwordPattern = Pattern.compile("^(?=.*\\d).{6,15}$");
                passwordMatcher = passwordPattern.matcher(psw);
                if (!passwordMatcher.find()) {
                    m_Password.setError("Inserire una password valida. Minimo 8 caratteri massimo 15. Ammessi caratteri alfanumerici e metacaratteri.");
                    m_Password.requestFocus();
                    return false;
                }

                if (!psw.equals(pswConf)) {
                    m_PasswordConf.setError("Questo campo deve coincidere con il campo password.");
                    m_PasswordConf.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

}