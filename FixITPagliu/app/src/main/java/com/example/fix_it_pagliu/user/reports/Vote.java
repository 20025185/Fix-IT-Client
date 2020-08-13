package com.example.fix_it_pagliu.user.reports;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
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

public class Vote extends AppCompatActivity {
    private String TAG = "[Vote] : ";
    private String repID;
    private boolean enabledRating;

    private RatingBar ratingBar;
    private Button button;
    private TextView textView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        retrieveReportID();

        ratingBar = findViewById(R.id.ratingBar);
        button = findViewById(R.id.sendVode);
        textView = findViewById(R.id.grazieVoto);
        textView.setVisibility(View.INVISIBLE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbr = firebaseDatabase.getReference("reports").child(repID);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("rating").getValue() == null) {
                    enabledRating = true;
                    textView.setVisibility(View.INVISIBLE);
                } else {
                    enabledRating = false;
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enabledRating) {
                    Log.d(TAG, "" + ratingBar.getRating());
                    dbr.child("rating").setValue(ratingBar.getRating());
                } else {
                    Toast.makeText(Vote.this, "È già presente un tuo voto per questa segnalazione all'interno della nostra piattaforma.", Toast.LENGTH_SHORT).show();
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
