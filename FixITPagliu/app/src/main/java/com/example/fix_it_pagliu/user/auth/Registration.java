package com.example.fix_it_pagliu.user.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fix_it_pagliu.user.UserMenu;
import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.database.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    private static final String TAG = "[Registration] : ";
    private static final int PICK_IMAGE_REQUEST = 71;
    private EditText m_FullName, m_Surname, m_Birthday, m_CodiceFiscale, m_Username, m_Email, m_Password, m_PasswordConf;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageURI;
    private FirebaseAuth fAuth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(this);

        m_FullName = findViewById(R.id.nomeRegister);
        m_Surname = findViewById(R.id.cognomeRegister);
        m_Birthday = findViewById(R.id.Birthday);
        m_CodiceFiscale = findViewById(R.id.codiceFiscale);
        m_Email = findViewById(R.id.emailRegister);
        m_Username = findViewById(R.id.username);
        m_Password = findViewById(R.id.pswRegister);
        m_PasswordConf = findViewById(R.id.psWConfirm);
        Button m_RegisterBtn = findViewById(R.id.signupBtn);
        Button m_loadImageBtn = findViewById(R.id.loadImageButton);

        fAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), UserMenu.class));
            finish();
        }

        m_Birthday.setOnClickListener(view -> handleBirthdayDialog());
        mDateSetListener = (datePicker, year, month, day) -> m_Birthday.setText(day + "/" + month + "/" + year);
        m_RegisterBtn.setOnClickListener(v -> handleRegisterButton());
        m_loadImageBtn.setOnClickListener(v -> selectImage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            if (filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                storageReference = FirebaseStorage.getInstance().getReference("userImgs");

                String imageUUID = UUID.randomUUID().toString();
                storageReference = storageReference.child(imageUUID);

                storageReference.putFile(filePath)
                        .addOnSuccessListener(taskSnapshot -> {
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                imageURI = uri;
                                Log.d(TAG, imageURI.toString());
                            });
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, "Immagine caricata con successo", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, "Errore : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                        .addOnProgressListener(snapshot -> {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        });
            }
        }
    }

    public void registerUser(final String userName, final String fullname, final String surname, final String birthday, final String fiscalcode, final String email, String password) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final FirebaseUser userAuth = fAuth.getCurrentUser();
                assert userAuth != null;
                userAuth.sendEmailVerification().addOnSuccessListener(aVoid -> {
                    Toast.makeText(Registration.this, "L'e-Mail di verifica è stata spedita.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Utente registrato su Firebase Auth");
                }).addOnFailureListener(e -> Toast.makeText(Registration.this, "L'e-Mail di verifica non è stata spedita.", Toast.LENGTH_SHORT).show());

                final String uid = userAuth.getUid();
                User user = new User(
                        userName,
                        fullname,
                        surname,
                        uid,
                        fiscalcode,
                        birthday,
                        email,
                        imageURI.toString());

                databaseReference.child(uid).setValue(user).addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Utente creato sulla piattaforma RealTimeDatabase " + user.toString());
                    Toast.makeText(Registration.this, "Utente creato sulla piattaforma, è necessario attivare l'account.\n", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(Registration.this, "Si è stato un errore nella creazione dell'utente.\n", Toast.LENGTH_SHORT).show());

                startActivity(new Intent(getApplicationContext(), UserMenu.class));
            } else {
                Toast.makeText(Registration.this, "Errore\n\"" + Objects.requireNonNull(task.getException()).getMessage() + "\"", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean checkCampi(String userName, String fullname, String surname, String birthday, String fiscalCode, String email, String psw, String pswConf) {
        Pattern userNamePattern, fullnamePattern, emailPattern, fiscalCodePattern, passwordPattern, birthdayPattern;    //  TODO : birthday regex
        Matcher userNameMatcher, fullnameMatcher, emailMatcher, fiscalCodeMatcher, passwordMatcher, birthdayMatcher;

        if (userName.isEmpty() || (userName.length() < 8 || userName.length() > 20)) {
            m_Username.setError("Inserire un username valido");
            m_Username.requestFocus();
            return false;
        } else {
            userNamePattern = Pattern.compile(getString(R.string.usernameRegex));
            userNameMatcher = userNamePattern.matcher(userName);

            if (!userNameMatcher.find()) {
                m_Username.setError("Inserire un username valido");
                m_Username.requestFocus();
                return false;
            }
        }

        if (fullname.isEmpty() || (fullname.length() < 4 || fullname.length() >= 30)) {
            m_FullName.setError("Inserire un nome valido");
            m_FullName.requestFocus();
            return false;
        } else {
            fullnamePattern = Pattern.compile(getString(R.string.fullnameRegex));
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
            fullnamePattern = Pattern.compile(getString(R.string.surnameRegex));
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
            fiscalCodePattern = Pattern.compile(getString(R.string.fiscalcodeRegex));
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
            emailPattern = Pattern.compile(getString(R.string.emailRegex));
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

            passwordPattern = Pattern.compile(getString(R.string.passowrdRegex));
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
        return true;
    }

    public void handleRegisterButton() {
        final String userName = m_Username.getText().toString().trim(); //  TODO: checkckampi add
        final String fullName = m_FullName.getText().toString().trim();
        final String surName = m_Surname.getText().toString().trim();
        final String fiscalCode = m_CodiceFiscale.getText().toString().trim();
        final String email = m_Email.getText().toString().trim();
        final String birthday = m_Birthday.getText().toString().trim();
        final String password = m_Password.getText().toString().trim();
        final String passwordConf = m_PasswordConf.getText().toString().trim();

        if (!checkCampi(userName, fullName, surName, birthday, fiscalCode, email, password, passwordConf))
            return;

        registerUser(userName, fullName, surName, birthday, fiscalCode, email, password);
    }


    public void handleBirthdayDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(Registration.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener
                , year, month, day);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

}