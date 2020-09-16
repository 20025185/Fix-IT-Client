package com.application.fix_it_pagliuca.user.reports.recycle_view_menus.closed_reports_menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.fix_it_pagliuca.user.reports.recycle_view_menus.RecycleReportItem;
import com.example.fix_it_pagliuca.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ClosedReportAdapter extends FirebaseRecyclerAdapter<RecycleReportItem, ClosedReportAdapter.ReportViewHolder> {
    private Context oldInstance;

    public ClosedReportAdapter(@NonNull FirebaseRecyclerOptions<RecycleReportItem> options) {
        super(options);
    }

    public void setInstance(Context context) {
        oldInstance = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ClosedReportAdapter.ReportViewHolder holder, int i, @NonNull final RecycleReportItem report) {
        holder.repObj.setText(report.getObject());
        holder.repID.setText(report.getId());
        holder.repDate.setText(report.getDate());
        holder.repDesc.setText(report.getDescription());
        holder.repType.setText(report.getType());

        holder.repButton.setOnClickListener(view -> {
            Intent intent = new Intent(oldInstance, ReopenReport.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("REP_ID", report.getId());
            oldInstance.startActivities(new Intent[]{intent});
        });

        holder.repVote.setOnClickListener(view -> {
            Intent intent = new Intent(oldInstance, Vote.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("REP_ID", report.getId());
            oldInstance.startActivities(new Intent[]{intent});
        });
    }

    @NonNull
    @Override
    public ClosedReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_closed, parent, false);

        return new ClosedReportAdapter.ReportViewHolder(view);
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView repObj, repID, repDate, repDesc, repType;
        Button repButton, repVote;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            repObj = itemView.findViewById(R.id.repObject);
            repID = itemView.findViewById(R.id.repID);
            repDate = itemView.findViewById(R.id.repDate);
            repDesc = itemView.findViewById(R.id.repDesc);
            repButton = itemView.findViewById(R.id.repReopen);
            repVote = itemView.findViewById(R.id.repVote);
            repType = itemView.findViewById(R.id.repType);

        }
    }
}
