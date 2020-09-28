package com.application.fix_it_pagliuca.user.reports.send_report_menu;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.application.fix_it_pagliuca.mapped_objects.Report;
import com.application.fix_it_pagliuca.user.dashboard.Dashboard;
import com.example.fix_it_pagliuca.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SendReport extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, NavigationView.OnNavigationItemSelectedListener, TimePickerDialog.OnTimeSetListener {
    private Uri fileURI;
    private static String attachmentUUID = "";
    private String tipoSegnalazione = "undefined";
    private boolean socialDiffusion = false;

    private EditText objectEdit, dateEdit, timeEdit, placeEdit, descriptionEdit;
    private CheckBox socialCheck;
    private Button typeButton;

    private FusedLocationProviderClient client;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private FirebaseDatabase rootNode;
    private DatabaseReference dbReference;
    private StorageReference storageReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendreport);

        objectEdit = findViewById(R.id.oggettoEdit);
        dateEdit = findViewById(R.id.dataEdit);
        timeEdit = findViewById(R.id.timeEdit);
        placeEdit = findViewById(R.id.placeEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        socialCheck = findViewById(R.id.socialCheck);
        typeButton = findViewById(R.id.typeButton);

        Button coordButton = findViewById(R.id.getCoordsBtn);
        Button sendReportButton = findViewById(R.id.inviaSegnBtn);

        rootNode = FirebaseDatabase.getInstance();
        dbReference = rootNode.getReference("reports");

        //  Location per GPS
        getSystemService(Context.LOCATION_SERVICE);
        client = LocationServices.getFusedLocationProviderClient(this);

        //  Controllo social
        socialCheck.setOnClickListener(view -> socialDiffusion = socialCheck.isChecked());

        //  Data
        dateEdit.setOnClickListener(view -> showDateDialog());
        mDateSetListener = (datePicker, year, month, day) -> dateEdit.setText(year + "/" + month + "/" + day);

        //  Orario
        timeEdit.setOnClickListener(view -> showTimeDialog(timeEdit));
        mTimeSetListener = (timePicker, hour, minute) -> timeEdit.setText(hour + ":" + minute);

        //  Conoscere le coordinate correnti
        coordButton.setOnClickListener(view -> {
            requestPermission();
            client = LocationServices.getFusedLocationProviderClient(SendReport.this);

            if (ActivityCompat.checkSelfPermission(SendReport.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            client.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    placeEdit.setText(location.getLatitude() + " " + location.getLongitude());
                }
            });

        });
        sendReportButton.setOnClickListener(view -> sendReport());
    }

    //  Invio segnalazione
    public void sendReport() {
        if (checkCampi()) {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            String repId = rootNode.getReference("reports").push().getKey();
            Report pendingReport = new Report(
                    uid,
                    repId,
                    objectEdit.getText().toString(),
                    dateEdit.getText().toString(),
                    timeEdit.getText().toString(),
                    placeEdit.getText().toString(),
                    socialDiffusion,
                    descriptionEdit.getText().toString(),
                    tipoSegnalazione,
                    fileURI.toString());
            assert repId != null;

            System.out.println(pendingReport.toString());

            dbReference
                    .child(repId)
                    .setValue(pendingReport)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(
                                SendReport.this,
                                "Segnalazione inviata con successo sulla piattaforma.\nUn nostro operatore prenderà in impegno la sua segnalazione.",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SendReport.this, Dashboard.class));
                    }).addOnFailureListener(e -> Toast.makeText(
                    SendReport.this,
                    "Si è verificato un problema nell'invio della segnalazione sulla piattaforma\n",
                    Toast.LENGTH_SHORT).show());
        }
    }

    public boolean checkCampi() {
        if (!objectEdit.getText().toString().isEmpty() && objectEdit.getText().toString().length() >= 5 && objectEdit.getText().toString().length() <= 25) {
            if (!dateEdit.getText().toString().isEmpty()) {
                if (!timeEdit.getText().toString().isEmpty()) {
                    if (checkCoordinate(placeEdit.getText().toString())) {
                        if (descriptionEdit.getText().toString().length() >= 15) {
                            if (!tipoSegnalazione.equals("undefined")) {
                                return true;
                            } else {
                                typeButton.setError("Selezionare una tipologia valida");
                                return false;
                            }
                        } else {
                            descriptionEdit.setError("Vanno inserita una descrizione di almeno 15 caratteri.");
                            return false;
                        }
                    } else {
                        placeEdit.setError("Inserire delle coordinate valide");
                        return false;
                    }
                } else {
                    timeEdit.setError("Inserire un orario valido");
                    return false;
                }
            } else {
                dateEdit.setError("Inserire una data valida");
                return false;

            }
        } else {
            objectEdit.setError("Inserire un oggetto valido di minimo 5 caratteri e massimo 25");
            return false;
        }
    }

    private boolean checkCoordinate(String coordPos) {
        final String[] ll = coordPos.split(" ");
        final double latitude = Double.parseDouble(ll[0]);
        final double longitude = Double.parseDouble(ll[1]);

        if (longitude >= 90.0 || longitude <= 0.0 || latitude >= 90.0 || latitude <= 0.0) {
            return false;
        } else
            return true;

    }

    //  Permesso coordinate attuali
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    //  Tipi di segnalazione (Menu)
    public void showTipo(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.tipology_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                tipoSegnalazione = "Problematica Stradale";
                break;
            case R.id.item2:
                tipoSegnalazione = "Problematica di origine naturale";
                break;
            case R.id.item3:
                tipoSegnalazione = "Attività sospette";
                break;
            case R.id.item4:
                tipoSegnalazione = "Altro";
                break;
            default:
                break;
        }
        typeButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_green));
        return false;
    }

    //  Menu Orario
    public void showTimeDialog(final EditText timeEdit) {
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                R.style.themeOnverlay_timePicker,
                mTimeSetListener,
                hour,
                minute,
                false);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hourOfDay, minute1) -> {
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute1);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            timeEdit.setText(simpleDateFormat.format(cal));
        };

        new TimePickerDialog(SendReport.this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
    }

    //  Menu Data
    public void showDateDialog() {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(SendReport.this,
                android.R.style.Theme_NoTitleBar_Fullscreen,
                mDateSetListener
                , year, month, day);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    public void loadAttachment(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleziona l'allegato"), 8777);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8777 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            if (filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                storageReference = FirebaseStorage.getInstance().getReference("attachments");

                attachmentUUID = UUID.randomUUID().toString();
                storageReference = storageReference.child(attachmentUUID);

                storageReference.putFile(filePath)
                        .addOnSuccessListener(taskSnapshot -> {
                            storageReference
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> fileURI = uri);
                            progressDialog.dismiss();
                            Toast.makeText(SendReport.this, "Allegato caricato con successo", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(SendReport.this, "Errore : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                        .addOnProgressListener(snapshot -> {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        });
            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
