package com.example.fix_it_pagliu.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.employee.EmployeeMenu;
import com.example.fix_it_pagliu.user.auth.Login;
import com.example.fix_it_pagliu.user.map_and_stats.MapZone;
import com.example.fix_it_pagliu.user.map_and_stats.StatsReports;
import com.example.fix_it_pagliu.user.reports.ClosedReports;
import com.example.fix_it_pagliu.user.reports.OpenReports;
import com.example.fix_it_pagliu.user.news.PostListActivity;
import com.example.fix_it_pagliu.user.reports.SendReport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = "[UserMenu] : ";
    //  Firebase
    private FirebaseDatabase rootNode;
    private DatabaseReference databaseReference;
    private FirebaseAuth fAuth;
    private FirebaseUser currentUser;
    private static String currentUid;
    private static String currentMail;

    //  XML
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Menu menu;
    private TextView verifyMsg;
    private Button resendCode;
    private TextView nomeUser, emailUser,
            roleUser, fiscalUser, birthdayUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        //  XML
        nomeUser = findViewById(R.id.nomeUser);
        emailUser = findViewById(R.id.emailUser);
        roleUser = findViewById(R.id.roleUser);
        fiscalUser = findViewById(R.id.fiscalUser);
        birthdayUser = findViewById(R.id.birthdayUser);
        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.emailNotVerified);

        //  Navigation hooks (XML)
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        //  Firebase
        fAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        databaseReference = rootNode.getReference("users");

        //  Toolbar
        setSupportActionBar(toolbar);

        //  Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);


        //  Email verification
        if (!fAuth.getCurrentUser().isEmailVerified() && !roleUser.getText().equals("Impiegato")) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);
            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Task<Void> voidTask = fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UserMenu.this, "È stata inviata una e-Mail di verifica.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserMenu.this, "Errore nell'invio della e-Mail.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
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
        currentUid = currentUser.getUid();

        if (currentUser == null)
            logout();



        currentMail = currentUser.getEmail();
        emailUser.setText(currentMail);

        menu = navigationView.getMenu();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("role").getValue(String.class).equals("user")) {
                    roleUser.setText("Utente standard");
                } else {
                    roleUser.setText("Impiegato");
                    startActivity(new Intent(getApplicationContext(), EmployeeMenu.class));
                }

                nomeUser.setText(dataSnapshot.child("fullname").getValue(String.class) + " " + dataSnapshot.child("surname").getValue(String.class));
                fiscalUser.setText(dataSnapshot.child("fiscalCode").getValue(String.class));
                birthdayUser.setText(dataSnapshot.child("birthday").getValue(String.class));
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
                    startActivity(new Intent(UserMenu.this, SendReport.class));
                } else {
                    Toast.makeText(UserMenu.this, "Devi attivare l'account tramite e-mail per accedere a questo servizio.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_news:
                startActivity(new Intent(UserMenu.this, PostListActivity.class));
                break;

            case R.id.nav_mapReports:
                startActivity(new Intent(UserMenu.this, MapZone.class));
                break;

            case R.id.nav_segnAperte:
                if (currentUser.isEmailVerified()) {
                    intent = new Intent(UserMenu.this, OpenReports.class);
                    intent.putExtra("UID", currentUser.getUid());
                    startActivity(intent);
                } else {
                    Toast.makeText(UserMenu.this, "Devi attivare l'account tramite e-mail per accedere a questo servizio.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_segnChiuse:
                if (currentUser.isEmailVerified()) {
                    intent = new Intent(UserMenu.this, ClosedReports.class);
                    intent.putExtra("UID", currentUser.getUid());
                    startActivity(intent);
                } else {
                    Toast.makeText(UserMenu.this, "Devi attivare l'account tramite e-mail per accedere a questo servizio.", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.nav_statsreport:
                if (currentUser.isEmailVerified()) {
                    intent = new Intent(UserMenu.this, StatsReports.class);
                    intent.putExtra("UID", currentUser.getUid());
                    startActivity(intent);
                } else {
                    Toast.makeText(UserMenu.this, "Devi attivare l'account tramite e-mail per accedere a questo servizio.", Toast.LENGTH_SHORT).show();
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