package com.example.fix_it_pagliu.user.reports;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fix_it_pagliu.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReopenReport extends AppCompatActivity {
    private final String TAG = "[Reopen Report] : ";
    private String repID;

    //  XML
    private EditText textRequest;
    private Button buttonSend;

    //  Utils
    private boolean unRequested = true;

    //  Firebase
    private FirebaseDatabase database;
    private DatabaseReference dbr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reopen_report);

        //  Retrieve ReportID
        retrieveReportID();

        //  XML
        textRequest = findViewById(R.id.textRequest);
        buttonSend = findViewById(R.id.sendRequest);

        //  Firebase
        database = FirebaseDatabase.getInstance();
        dbr = database.getReference("reports").child(repID);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("request").getValue() != null) {
                    unRequested = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textRequest.getText().toString().isEmpty() && unRequested) {
                    dbr.child("request").setValue(textRequest.getText().toString());
                } else {
                    Toast.makeText(ReopenReport.this, "Impossibile, richiesta già effettuata.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void retrieveReportID() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            repID = extras.getString("REP_ID");
        } else {
            repID = null;
        }
    }

}
