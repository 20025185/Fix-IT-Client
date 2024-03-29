package com.application.fix_it_pagliuca.user.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.application.fix_it_pagliuca.user.extra.LocalStatistics;
import com.application.fix_it_pagliuca.user.extra.Map;
import com.application.fix_it_pagliuca.user.reports.recycle_view_menus.open_reports_menu.OpenReports;
import com.example.fix_it_pagliuca.R;
import com.application.fix_it_pagliuca.user.auth.Login;
import com.application.fix_it_pagliuca.user.reports.recycle_view_menus.closed_reports_menu.ClosedReports;
import com.application.fix_it_pagliuca.user.news.PostListActivity;
import com.application.fix_it_pagliuca.user.reports.send_report_menu.SendReport;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static java.util.Objects.requireNonNull;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = "[UserMenu] : ";

    private DrawerLayout drawerLayout;
    private ImageView profileImg;
    private TextView nomeUser, emailUser,
            roleUser, fiscalUser, birthdayUser;

    private DatabaseReference databaseReference;
    private FirebaseAuth fAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        nomeUser = findViewById(R.id.nomeUser);
        emailUser = findViewById(R.id.emailUser);
        roleUser = findViewById(R.id.roleUser);
        fiscalUser = findViewById(R.id.fiscalUser);
        birthdayUser = findViewById(R.id.birthdayUser);
        profileImg = findViewById(R.id.profileImg);
        Button resendCode = findViewById(R.id.resendCode);
        TextView verifyMsg = findViewById(R.id.emailNotVerified);
        drawerLayout = findViewById(R.id.drawer_layout);

        fAuth = FirebaseAuth.getInstance();
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        databaseReference = rootNode.getReference("users");

        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);

        if (!requireNonNull(fAuth.getCurrentUser()).isEmailVerified()
                && !roleUser.getText().equals("Impiegato")) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);
            resendCode.setOnClickListener(view -> fAuth.getCurrentUser()
                    .sendEmailVerification()
                    .addOnSuccessListener(aVoid -> Toast.makeText(
                            Dashboard.this,
                            "È stata inviata una e-Mail di verifica.",
                            Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(
                            Dashboard.this,
                            "Errore nell'invio della e-Mail.",
                            Toast.LENGTH_SHORT).show()));
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        String currentUid = currentUser.getUid();

        if (currentUser == null)
            logout();

        String currentMail = currentUser.getEmail();
        emailUser.setText(currentMail);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fullname").getValue() == null)
                    logout();

                roleUser.setText("Utente standard");
                nomeUser.setText(dataSnapshot.child("fullname").getValue(String.class) + " " + dataSnapshot.child("surname").getValue(String.class));
                fiscalUser.setText(dataSnapshot.child("fiscalCode").getValue(String.class));
                birthdayUser.setText(dataSnapshot.child("birthday").getValue(String.class));

                if (dataSnapshot.child("imageURL").getValue() != null) {
                    Picasso.get().load(requireNonNull(dataSnapshot.child("imageURL").getValue()).toString()).into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        databaseReference.child(currentUid).addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                break;

            case R.id.nav_sendReport:
                if (currentUser.isEmailVerified()) {
                    startActivity(new Intent(Dashboard.this, SendReport.class));
                } else {
                    Toast.makeText(Dashboard.this, "Devi attivare l'account tramite e-mail per accedere a questo servizio.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_news:
                startActivity(new Intent(Dashboard.this, PostListActivity.class));
                break;

            case R.id.nav_mapReports:
                startActivity(new Intent(Dashboard.this, Map.class));
                break;

            case R.id.nav_segnAperte:
                if (currentUser.isEmailVerified()) {
                    intent = new Intent(Dashboard.this, OpenReports.class);
                    intent.putExtra("UID", currentUser.getUid());
                    startActivity(intent);
                } else {
                    Toast.makeText(Dashboard.this, "Devi attivare l'account tramite e-mail per accedere a questo servizio.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_segnChiuse:
                if (currentUser.isEmailVerified()) {
                    intent = new Intent(Dashboard.this, ClosedReports.class);
                    intent.putExtra("UID", currentUser.getUid());
                    startActivity(intent);
                } else {
                    Toast.makeText(Dashboard.this, "Devi attivare l'account tramite e-mail per accedere a questo servizio.", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.nav_statsreport:
                if (currentUser.isEmailVerified()) {
                    intent = new Intent(Dashboard.this, LocalStatistics.class);
                    intent.putExtra("UID", currentUser.getUid());
                    startActivity(intent);
                } else {
                    Toast.makeText(Dashboard.this, "Devi attivare l'account tramite e-mail per accedere a questo servizio.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_logout:
                logout();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

}