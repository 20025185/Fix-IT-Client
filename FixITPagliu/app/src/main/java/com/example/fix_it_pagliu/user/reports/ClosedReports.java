package com.example.fix_it_pagliu.user.reports;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ClosedReports extends AppCompatActivity {
    ClosedReportAdapter closedReportAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_reports);

        recyclerView = findViewById(R.id.recyclerClosedReports);

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
                        .setQuery((Query) FirebaseDatabase.getInstance().getReference().child("reports").orderByChild("status").equalTo("Chiusa_" + userID), OReport.class)
                        .build();

        closedReportAdapter = new ClosedReportAdapter(options);
        closedReportAdapter.setInstance(getBaseContext());

        recyclerView.setAdapter(closedReportAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        closedReportAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closedReportAdapter.stopListening();
    }
}
