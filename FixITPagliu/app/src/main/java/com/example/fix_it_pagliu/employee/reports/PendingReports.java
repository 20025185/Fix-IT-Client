package com.example.fix_it_pagliu.employee.reports;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.user.reports.OReport;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PendingReports extends AppCompatActivity {
    //  XML
    private RecyclerView recyclerView;
    private PendingReportAdapter pendingReportAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pending_list_employee);
        recyclerView = findViewById(R.id.recyclerPendingReports);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<OReport> options =
                new FirebaseRecyclerOptions.Builder<OReport>()
                        .setQuery((Query) FirebaseDatabase.getInstance().getReference().child("reports").orderByChild("status").startAt("Pending_").endAt("b\uf8ff"), OReport.class).build();

        pendingReportAdapter = new PendingReportAdapter(options);
        pendingReportAdapter.setInstance(getBaseContext());
        recyclerView.setAdapter(pendingReportAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pendingReportAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pendingReportAdapter.stopListening();
    }
}
