package com.example.fix_it_pagliu.user.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PostAdapter extends FirebaseRecyclerAdapter<Post, PostAdapter.PastViewHolder> {
    public PostAdapter(@NonNull FirebaseRecyclerOptions<Post> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PastViewHolder holder, int i, @NonNull Post post) {
        holder.object.setText(post.getObject());
        holder.description.setText(post.getDescription());
        holder.date.setText(post.getDate());
    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post, parent, false);
        return new PastViewHolder(view);
    }

    static class PastViewHolder extends RecyclerView.ViewHolder {
        TextView object, description, date;
        TextView repObject, repID, repDate;

        public PastViewHolder(@NonNull View itemView) {
            super(itemView);
            object = itemView.findViewById(R.id.object);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);

            repObject = itemView.findViewById(R.id.repObject);
            //repDesc = itemView.findViewById(R.id.);
            repDate = itemView.findViewById(R.id.repDate);
        }
    }
}
