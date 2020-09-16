package com.application.fix_it_pagliuca.user.reports.employee_chat.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliuca.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Messages> messages;

    public MessageAdapter(Context applicationContext, ArrayList<Messages> messages) {
        this.messages = messages;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
