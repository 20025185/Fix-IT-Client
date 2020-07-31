package com.example.fix_it_pagliu.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.fix_it_pagliu.R;
import com.example.fix_it_pagliu.database.Report;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.*;

public class MapZone extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "[MapZone] : ";
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;


    //  XML
    private SearchView searchView;

    //  Firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private Report[] reports;
    private int numberOfReports = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        retrieveData();

        searchView = findViewById(R.id.svLocation);
        //supportMapFragment = findViewById(R.id.mapFrag);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
    }

    private void retrieveData() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("reports");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numberOfReports = (int) snapshot.getChildrenCount();
                reports = new Report[numberOfReports + 1];
                int i = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String position = ds.child("position").getValue().toString();
                    String priority = ds.child("priority").getValue().toString();
                    String object = ds.child("object").getValue().toString();
                    String description = ds.child("description").getValue().toString();
                    reports[i] = new Report(position, priority, description, object);
                    ++i;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + " " +
                            currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
                    supportMapFragment.getMapAsync(MapZone.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Sono qui");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        for (int i = 0; i < numberOfReports; ++i) {
            String[] coords = reports[i].getPosition().split(" ");
            LatLng latLng_t = new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));

            float markerColor = 0.0f;

            if (reports[i].getPriority().equals("0")) {
                markerColor = 120.0f;
            } else if (reports[i].getPriority().equals("1")) {
                markerColor = 60.0f;
            } else if (reports[i].getPriority().equals("2")) {
                markerColor = 0.0f;
            }

            Log.d(TAG, "priority " + reports[i].getPriority() + "colore" + markerColor);
            googleMap.addMarker(new MarkerOptions().position(latLng_t)
                    .title(reports[i].getObject())
                    .snippet(reports[i].getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }
}
