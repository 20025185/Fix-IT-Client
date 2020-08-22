package com.example.fix_it_pagliu.user.reports;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.user.reports.forum.BidirectionalForum;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class OpenReportAdapter extends FirebaseRecyclerAdapter<OReport, OpenReportAdapter.ReportViewHolder> {
    Context oldInstance;

    public OpenReportAdapter(@NonNull FirebaseRecyclerOptions<OReport> options) {
        super(options);
    }

    public void setInstance(Context instance) {
        oldInstance = instance;
    }

    @Override
    protected void onBindViewHolder(@NonNull final OpenReportAdapter.ReportViewHolder holder, int i, @NonNull final OReport report) {
        holder.repObj.setText(report.getObject());

        final SpannableString id = new SpannableString(report.getId());
        id.setSpan(new UnderlineSpan(), 0, id.length(), 0);
        holder.repID.setText(id);
        holder.repDate.setText(report.getDate());
        holder.repDesc.setText(report.getDescription());

        holder.repID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(oldInstance, BidirectionalForum.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("REP_ID", report.getId());
                oldInstance.startActivities(new Intent[]{intent});
            }
        });
    }

    @NonNull
    @Override
    public OpenReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_open, parent, false);

        return new OpenReportAdapter.ReportViewHolder(view);
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
