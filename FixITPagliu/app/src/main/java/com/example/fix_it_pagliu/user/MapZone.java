package com.example.fix_it_pagliu.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.List;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.*;

public class MapZone extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "[MapZone] : ";
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private GoogleMap map;
    private SupportMapFragment supportMapFragment;

    private static LatLng favoritePlace;
    private static boolean firstSession = true;
    //  XML
    private SearchView searchView;

    //  Firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private static Report[] reports;
    private int numberOfReports = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        retrieveData();


        searchView = findViewById(R.id.svLocation);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (!location.isEmpty()) {
                    Geocoder geocoder = new Geocoder(MapZone.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        Toast.makeText(MapZone.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        favoritePlace = latLng;
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    } else {
                        Toast.makeText(MapZone.this, "Cercare una località che sia valida o non ambigua.", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //supportMapFragment.getMapAsync(this);

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
        map = googleMap;

        if (firstSession) {
            loadPriorityMarkers(googleMap);

            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Sono qui")
                    .icon(BitmapDescriptorFactory.defaultMarker(HUE_MAGENTA)));

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

        /*
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(favoritePlace));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(favoritePlace, 15));
        */
    }

    private void loadPriorityMarkers(GoogleMap googleMap) {
        for (int i = 0; i < numberOfReports; ++i) {
            String[] coords = reports[i].getPosition().split(" ");
            LatLng latLng_t = new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));

            float markerColor = HUE_RED;

            if (reports[i].getPriority().equals("0")) {
                markerColor = HUE_GREEN;
            } else if (reports[i].getPriority().equals("1")) {
                markerColor = HUE_YELLOW;
            } else if (reports[i].getPriority().equals("2")) {
                markerColor = HUE_RED;
            }

            Log.d(TAG, latLng_t.toString());

            googleMap.addMarker(new MarkerOptions().position(latLng_t)
                    .title(reports[i].getObject())
                    .snippet(reports[i].getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
        }
        firstSession = false;
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
