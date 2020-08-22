package com.example.fix_it_pagliu.user.reports;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;

import com.example.fix_it_pagliu.user.UserMenu;
import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.database.Report;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SendReport extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "[SendReport] : ";
    private String tipoSegnalazione = "undefined";
    private boolean socialDiffusion = false;


    private EditText objectEdit, dateEdit, timeEdit, placeEdit, descriptionEdit;
    private CheckBox socialCheck;
    private Button typeButton, coordButton, sendReportButton;

    private FusedLocationProviderClient client;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private FirebaseDatabase rootNode;
    private DatabaseReference dbReference;

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
        coordButton = findViewById(R.id.getCoordsBtn);
        sendReportButton = findViewById(R.id.inviaSegnBtn);

        //  Firebase
        rootNode = FirebaseDatabase.getInstance();
        dbReference = rootNode.getReference("reports");

        //  Location per GPS
        getSystemService(Context.LOCATION_SERVICE);
        client = LocationServices.getFusedLocationProviderClient(this);

        //  Controllo social
        socialCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (socialCheck.isChecked()) {
                    socialDiffusion = true;
                } else {
                    socialDiffusion = false;
                }
            }
        });

        //  Data
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(dateEdit);
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, final int year, final int month, final int day) {
                dateEdit.setText(year + "/" + month + "/" + day);
            }
        };

        //  Orario
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(timeEdit);
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                timeEdit.setText(hour + ":" + minute);
            }
        };

        //  Coordinate correnti
        coordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();

                client = LocationServices.getFusedLocationProviderClient(SendReport.this);
                if (ActivityCompat.checkSelfPermission(SendReport.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            placeEdit.setText(location.getLatitude() + " " + location.getLongitude());
                        }
                    }
                });

            }
        });

        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReport();
            }
        });

    }

    //  Invio segnalazione
    public void sendReport() {
        if (checkCampi()) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String repId = rootNode.getReference("reports").push().getKey();
            Report pendingReport = new Report(uid, repId, objectEdit.getText().toString(), dateEdit.getText().toString(),
                    timeEdit.getText().toString(), placeEdit.getText().toString(),
                    socialDiffusion, descriptionEdit.getText().toString(), tipoSegnalazione);
            Log.d(TAG, pendingReport.getStatus());
            dbReference.child(repId).setValue(pendingReport).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SendReport.this, "Segnalazione inviata con successo sulla piattaforma.\nUn nostro operatore prenderà in impegno la sua segnalazione.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SendReport.this, UserMenu.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SendReport.this, "Si è verificato un problema nell'invio della segnalazione sulla piattaforma\n", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean checkCampi() {
        if (!objectEdit.getText().toString().isEmpty() && objectEdit.getText().toString().length() >= 5 && objectEdit.getText().toString().length() <= 25) {
            if (!dateEdit.getText().toString().isEmpty()) {
                if (!timeEdit.getText().toString().isEmpty()) {
                    if (!placeEdit.getText().toString().isEmpty() && placeEdit.getText().toString().length() >= 5) {
                        if (descriptionEdit.getText().toString().length() >= 15) {
                            if (tipoSegnalazione != "undefined") {
                                return true;
                            } else {
                                typeButton.setError("Selezionare una tipologia valida");
                            }
                        } else {
                            descriptionEdit.setError("Vanno inserita una descrizione di almeno 15 caratteri.");
                        }
                    } else {
                        placeEdit.setError("Almeno 5 caratteri per descrivere la posizione.");
                    }
                } else {
                    timeEdit.setError("Inserire un orario valido");
                }
            } else {
                dateEdit.setError("Inserire una data valida");
            }
        } else {
            objectEdit.setError("Inserire un oggetto valido di minimo 5 caratteri e massimo 25");
        }
        return false;
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
                typeButton.setBackgroundColor(0xFF85de9d);
                tipoSegnalazione = "Problematica Stradale";
                break;
            case R.id.item2:
                typeButton.setBackgroundColor(0xFF85de9d);
                tipoSegnalazione = "Problematica di origine naturale";
                break;
            case R.id.item3:
                typeButton.setBackgroundColor(0xFF85de9d);
                tipoSegnalazione = "Attività sospette";
                break;
            case R.id.item4:
                typeButton.setBackgroundColor(0xFF85de9d);
                tipoSegnalazione = "Altro";
                break;
            default:
                break;
        }
        return false;
    }

    //  Menu Orario
    public void showTimeDialog(final EditText timeEdit) {
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(SendReport.this, mTimeSetListener, hour, minute, false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                timeEdit.setText(simpleDateFormat.format(cal));
            }
        };

        new TimePickerDialog(SendReport.this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
    }

    //  Menu Data
    public void showDateDialog(final EditText dateEdit) {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(SendReport.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener
                , year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
