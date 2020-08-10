package com.example.fix_it_pagliu.user.reports.forum;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = "[Message Adapter] : ";
    private Context context;
    private ArrayList<Messages> messages;

    public MessageAdapter(Context context, ArrayList<Messages> messages) {
        this.context = context;
        this.messages = messages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView msgObj;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msgObj = itemView.findViewById(R.id.msgText);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.message, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.msgObj.setText(messages.get(position).getMsg());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
