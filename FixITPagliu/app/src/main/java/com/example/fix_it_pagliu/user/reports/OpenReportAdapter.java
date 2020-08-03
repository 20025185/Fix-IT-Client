package com.example.fix_it_pagliu.user.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class OpenReportAdapter extends FirebaseRecyclerAdapter<OReport, OpenReportAdapter.ReportViewHolder> {

    public OpenReportAdapter(@NonNull FirebaseRecyclerOptions<OReport> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OpenReportAdapter.ReportViewHolder holder, int i, @NonNull OReport report) {
        holder.repObj.setText(report.getObject());
        holder.repID.setText(report.getId());
        holder.repDate.setText(report.getDate());
    }

    @NonNull
    @Override
    public OpenReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report, parent, false);

        return new OpenReportAdapter.ReportViewHolder(view);
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView repObj, repID, repDate;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            repObj = itemView.findViewById(R.id.repObject);
            repID = itemView.findViewById(R.id.repID);
            repDate = itemView.findViewById(R.id.repDate);
        }
    }
}
