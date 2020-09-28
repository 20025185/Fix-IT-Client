package com.application.fix_it_pagliuca.user.reports.employee_chat.components;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliuca.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Messages> messages;
    private String fullnameUser;

    public MessageAdapter(Context applicationContext, ArrayList<Messages> messages, String userFullname) {
        this.messages = messages;
        fullnameUser = userFullname;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView msgObj;
        ConstraintLayout msgLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msgObj = itemView.findViewById(R.id.msgText);
            msgLayout = itemView.findViewById(R.id.msgBg);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.msgObj.setText(messages.get(position).getMsg());

        if (parseMsg(holder.msgObj.getText().toString())) {
            holder.msgLayout.setBackgroundResource(R.drawable.bg_message_from_user);

            ((RecyclerView.LayoutParams) holder.msgLayout.getLayoutParams()).setMarginStart(420);


        } else {
            holder.msgLayout.setBackgroundResource(R.drawable.bg_message_from_employee);
        }
    }

    private boolean parseMsg(String msg) {
        final int idx = msg.indexOf(":");
        String substr = msg.substring(0, idx - 1);
        substr.replace(" ", "");

        return substr.equals(fullnameUser);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
