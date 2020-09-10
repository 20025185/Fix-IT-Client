package com.example.fix_it_pagliu.user.reports;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.database.Report;
import com.example.fix_it_pagliu.kafka.api.KafkaAPI;
import com.example.fix_it_pagliu.kafka.api.KafkaRecords;
import com.example.fix_it_pagliu.kafka.api.Records;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Vote extends AppCompatActivity {
    //  Importa vars
    private String TAG = "[Vote] : ";
    private String repID;
    private String uidKey;
    private boolean enabledRating = false;

    //  XML
    private RatingBar ratingBar;
    private Button button;
    private TextView textView;

    //  Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbr;
    //  Kafka REST Proxy
    private KafkaAPI kafkaAPI;
    private Report reportToSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.3:8082")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        kafkaAPI = retrofit.create(KafkaAPI.class);

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

                uidKey = Objects.requireNonNull(snapshot.child("uid").getValue()).toString();
                String date = Objects.requireNonNull(snapshot.child("date").getValue()).toString();
                String description = Objects.requireNonNull(snapshot.child("description").getValue()).toString();
                String position = Objects.requireNonNull(snapshot.child("position").getValue()).toString();
                String object = Objects.requireNonNull(snapshot.child("object").getValue()).toString();
                String priority = Objects.requireNonNull(snapshot.child("priority").getValue()).toString();
                String social = Objects.requireNonNull(snapshot.child("social").getValue()).toString();
                String status = Objects.requireNonNull(snapshot.child("status").getValue()).toString().split("_")[0];
                String time = Objects.requireNonNull(snapshot.child("time").getValue()).toString();
                String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();

                reportToSend = new Report(uidKey, repID, object, date, time, position, Boolean.getBoolean(social), description, type, "null", status, priority);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (enabledRating) {
                dbr.child("rating").setValue(ratingBar.getRating());
                reportToSend.setRating(String.valueOf(ratingBar.getRating()));

                KafkaRecords kafkaRecords = new KafkaRecords();
                ArrayList<Records> recordsArrayList = new ArrayList<>();

                Records records = new Records();

                records.setKey(uidKey);
                records.setValue(reportToSend);
                recordsArrayList.add(records);

                kafkaRecords.setRecordsList(recordsArrayList);

                Call<Void> call = kafkaAPI.createPost(kafkaRecords);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d(TAG, response.toString());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(TAG, t.toString());
                    }
                });

                /*} else {
                    Toast.makeText(Vote.this, "È già presente un tuo voto per questa segnalazione all'interno della nostra piattaforma.", Toast.LENGTH_SHORT).show();
                }*/
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
