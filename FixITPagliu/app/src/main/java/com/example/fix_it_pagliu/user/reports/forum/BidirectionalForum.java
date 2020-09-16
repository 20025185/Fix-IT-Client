package com.example.fix_it_pagliu.user.reports.forum;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fix_it_pagliu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BidirectionalForum extends AppCompatActivity {
    private EditText textToSend;
    private RecyclerView recyclerView;

    private String fullName = null;
    private String repID;
    private int msgIndex = 0;
    private ArrayList<Messages> messageList;

    private MessageAdapter messageAdapter;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidir);

        //  XML
        textToSend = findViewById(R.id.textToSend);
        Button sendMsg = findViewById(R.id.sendButton);
        messageList = new ArrayList<>();

        //  Retrieving ID & CurrentUser
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        retrieveReportID();

        //  Setting up RecycleView
        recyclerView = findViewById(R.id.messagesRecycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //  Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("reports")
                .child(repID)
                .child("discussion");

        Runnable r = this::getDataFromFirebase;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(r, 0, 100, TimeUnit.MILLISECONDS);

        //  Showing Messages
        getDataFromFirebase();

        //  Retrieving the correct
        retrieveMsgIndex();

        //  Sending message
        sendMsg.setOnClickListener(view -> {
            msgIndex++;
            sendMessage(msgIndex);
        });
    }

    private void getDataFromFirebase() {
        Query query = databaseReference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClearAll();
                for (int i = 1; i <= snapshot.getChildrenCount(); i++) {
                    Messages message = new Messages();
                    message.setMsg(Objects.requireNonNull(snapshot.child("msg_" + i).getValue()).toString());
                    messageList.add(message);
                }
                messageAdapter = new MessageAdapter(getApplicationContext(), messageList);
                recyclerView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void retrieveMsgIndex() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msgIndex = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ClearAll() {
        if (messageList != null) {
            messageList.clear();
            if (messageAdapter != null) {
                messageAdapter.notifyDataSetChanged();
            }
        }
        messageList = new ArrayList<>();
    }

    public void sendMessage(final int msgIndex) {
        final String msg = textToSend.getText().toString();
        textToSend.setText("");

        if (!msg.isEmpty()) {
            DatabaseReference dbr_user = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(firebaseUser.getUid());

            dbr_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    fullName = Objects.requireNonNull(snapshot.child("fullname").getValue()).toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                final String finalMsg = fullName + " : " + msg;
                databaseReference.child("msg_" + msgIndex).setValue(finalMsg);
                getDataFromFirebase();
            }, 600);
        } else {
            Toast.makeText(BidirectionalForum.this,
                    "Non è stato inviato nulla", Toast.LENGTH_SHORT).show();
        }
    }

    public void retrieveReportID() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            repID = extras.getString("REP_ID");
        } else {
            repID = null;
        }
    }

}
