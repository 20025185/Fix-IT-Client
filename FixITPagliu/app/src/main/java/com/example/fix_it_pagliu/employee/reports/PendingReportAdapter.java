package com.example.fix_it_pagliu.employee.reports;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.user.reports.OReport;
import com.example.fix_it_pagliu.user.reports.OpenReportAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PendingReportAdapter extends FirebaseRecyclerAdapter<OReport, PendingReportAdapter.ReportViewHolder> {
    Context oldInstance;

    public PendingReportAdapter(@NonNull FirebaseRecyclerOptions<OReport> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReportViewHolder holder, int i, @NonNull final OReport report) {
        holder.repObj.setText(report.getObject());
        holder.repID.setText(report.getId());
        holder.repDate.setText(report.getDate());
        holder.repDesc.setText(report.getDescription());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reports").child(report.getId())
                        .child("status");
                databaseReference.setValue("Aperta_" + report.getId());
            }
        });
    }

    public void setInstance(Context context) {
        oldInstance = context;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_pending, parent, false);

        return new PendingReportAdapter.ReportViewHolder(view);
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView repObj, repID, repDate, repDesc;
        Button editButton, openButton;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            repObj = itemView.findViewById(R.id.repObject);
            repID = itemView.findViewById(R.id.repID);
            repDate = itemView.findViewById(R.id.repDate);
            repDesc = itemView.findViewById(R.id.repDesc);
            editButton = itemView.findViewById(R.id.repEdit);
            openButton = itemView.findViewById(R.id.repOpen);
        }
    }
}
