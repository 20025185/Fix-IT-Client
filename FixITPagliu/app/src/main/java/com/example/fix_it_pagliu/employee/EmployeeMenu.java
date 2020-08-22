package com.example.fix_it_pagliu.employee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.employee.reports.PendingReports;
import com.example.fix_it_pagliu.user.UserMenu;
import com.example.fix_it_pagliu.user.auth.Login;
import com.example.fix_it_pagliu.user.map_and_stats.MapZone;
import com.example.fix_it_pagliu.user.map_and_stats.StatsReports;
import com.example.fix_it_pagliu.user.news.PostListActivity;
import com.example.fix_it_pagliu.user.reports.ClosedReports;
import com.example.fix_it_pagliu.user.reports.OpenReports;
import com.example.fix_it_pagliu.user.reports.SendReport;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = "[EmployeeMenu] : ";

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
    private TextView nomeEmployee, emailEmployee,
            roleEmployee, fiscalEmployee, birthdayEmployee;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_menu);

        //  XML
        nomeEmployee = findViewById(R.id.nome);
        emailEmployee = findViewById(R.id.email);
        roleEmployee = findViewById(R.id.role);
        fiscalEmployee = findViewById(R.id.fiscal);
        birthdayEmployee = findViewById(R.id.birthday);
        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.emailNotVerified);

        //  Navigation hooks (XML)
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbarEmployee);

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
        emailEmployee.setText(currentMail);

        menu = navigationView.getMenu();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("role").getValue(String.class).equals("user")) {
                    roleEmployee.setText("Utente standard");
                } else {
                    roleEmployee.setText("Impiegato");
                }

                nomeEmployee.setText(dataSnapshot.child("fullname").getValue(String.class) + " " + dataSnapshot.child("surname").getValue(String.class));
                fiscalEmployee.setText(dataSnapshot.child("fiscalCode").getValue(String.class));
                birthdayEmployee.setText(dataSnapshot.child("birthday").getValue(String.class));

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
            case R.id.nav_imp_pending_list:
                Log.d(TAG, "asda");
                intent = new Intent(getApplicationContext(), PendingReports.class);
                startActivity(intent);
                break;
            case R.id.nav_imp_open_report:
                break;
            case R.id.nav_imp_closed_report:
                break;
            case R.id.nav_imp_load_report:
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
