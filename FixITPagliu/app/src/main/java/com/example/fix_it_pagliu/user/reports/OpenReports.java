package com.example.fix_it_pagliu.user.reports;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OpenReports extends AppCompatActivity {
    OpenReportAdapter openReportAdapter;
    RecyclerView recyclerView;
    String openable = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_reports);

        recyclerView = findViewById(R.id.recyclerOpenReports);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle extras = getIntent().getExtras();
        String userID;

        if (extras != null) {
            userID = extras.getString("UID");
        } else {
            userID = null;
        }

        FirebaseRecyclerOptions<OReport> options =
                new FirebaseRecyclerOptions.Builder<OReport>()
                        .setQuery((Query) FirebaseDatabase.getInstance().getReference().child("reports")
                                .orderByChild("status").equalTo("Aperta_" + userID), OReport.class)
                        .build();

        openReportAdapter = new OpenReportAdapter(options);
        openReportAdapter.setInstance(getBaseContext());

        recyclerView.setAdapter(openReportAdapter);

        if (openable != null)
            Toast.makeText(this, openable, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        openReportAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        openReportAdapter.stopListening();
    }


}
