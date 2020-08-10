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

public class ClosedReportAdapter extends FirebaseRecyclerAdapter<OReport, ClosedReportAdapter.ReportViewHolder> {

    public ClosedReportAdapter(@NonNull FirebaseRecyclerOptions<OReport> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ClosedReportAdapter.ReportViewHolder holder, int i, @NonNull OReport report) {
        holder.repObj.setText(report.getObject());
        holder.repID.setText(report.getId());
        holder.repDate.setText(report.getDate());
        holder.repDesc.setText(report.getDescription());
    }

    @NonNull
    @Override
    public ClosedReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_closed, parent, false);

        return new ClosedReportAdapter.ReportViewHolder(view);
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView repObj, repID, repDate, repDesc;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            repObj = itemView.findViewById(R.id.repObject);
            repID = itemView.findViewById(R.id.repID);
            repDate = itemView.findViewById(R.id.repDate);
            repDesc = itemView.findViewById(R.id.repDesc);

        }
    }
}
