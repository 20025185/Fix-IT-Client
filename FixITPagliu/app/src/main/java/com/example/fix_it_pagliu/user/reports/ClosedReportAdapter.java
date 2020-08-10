package com.example.fix_it_pagliu.user.reports;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.user.reports.forum.BidirectionalForum;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ClosedReportAdapter extends FirebaseRecyclerAdapter<OReport, ClosedReportAdapter.ReportViewHolder> {
    private Context oldInstance;

    public ClosedReportAdapter(@NonNull FirebaseRecyclerOptions<OReport> options) {
        super(options);
    }

    public void setInstance(Context context) {
        oldInstance = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ClosedReportAdapter.ReportViewHolder holder, int i, @NonNull final OReport report) {
        holder.repObj.setText(report.getObject());
        holder.repID.setText(report.getId());
        holder.repDate.setText(report.getDate());
        holder.repDesc.setText(report.getDescription());


        holder.repButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(oldInstance, ReopenReport.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("REP_ID", report.getId());
                oldInstance.startActivities(new Intent[]{intent});
            }
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
        TextView repObj, repID, repDate, repDesc;
        Button repButton;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            repObj = itemView.findViewById(R.id.repObject);
            repID = itemView.findViewById(R.id.repID);
            repDate = itemView.findViewById(R.id.repDate);
            repDesc = itemView.findViewById(R.id.repDesc);
            repButton = itemView.findViewById(R.id.repReopen);

        }
    }
}
