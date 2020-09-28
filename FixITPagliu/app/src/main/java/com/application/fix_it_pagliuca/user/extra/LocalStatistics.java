package com.application.fix_it_pagliuca.user.extra;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fix_it_pagliuca.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@SuppressWarnings("ConstantConditions")
public class LocalStatistics extends AppCompatActivity {
    private String TAG = "[Local Statistics] : ";
    private SearchView searchView;
    private LineChart lineChart;
    private DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("reports");
    private TreeMap<String, Integer> data;    //  Le TreeMap sono sempre ordinate in base al valore delle chiavi, in questo caso la data del Report.
    private List<Address> addresses = null;
    private ArrayList<String> dates = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_statistics);

        lineChart = findViewById(R.id.lineChart);
        searchView = findViewById(R.id.svLocation);

        Toast.makeText(this, "Cercare la località desiderata.", Toast.LENGTH_SHORT).show();
        
        data = new TreeMap<>();
        searchListeners();
    }

    private void searchListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z])*$")) {
                    final Geocoder geocoder = new Geocoder(getApplicationContext());

                    dbr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String[] longLat;

                            for (DataSnapshot s : snapshot.getChildren()) {
                                final String pos = Objects.requireNonNull(s.child("position").getValue()).toString();
                                longLat = pos.split(" ");

                                try {
                                    addresses = geocoder.getFromLocation(Double.parseDouble(longLat[0]), Double.parseDouble(longLat[1]), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (addresses.size() != 0) {
                                    if (addresses.toString().toLowerCase().contains(query.toLowerCase())) {
                                        //  Controllo nella TreeMap se sia presente la chiave del report che contiene la query
                                        if (data.containsKey(Objects.requireNonNull(s.child("date").getValue()).toString())) {
                                            //  In tal caso incremento il contatore di val e aggiorno la TreeMap
                                            int val = data.get(s.child("date").getValue().toString());
                                            ++val;
                                            data.put(Objects.requireNonNull(s.child("date").getValue()).toString(), val);
                                        } else {
                                            //  Altrimenti significa che è la prima volta che trovo il record quindi inizializzo nella TreeMap con 1.
                                            data.put(Objects.requireNonNull(s.child("date").getValue()).toString(), 1);
                                        }
                                    }
                                }

                                updateGraph(query);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(LocalStatistics.this, "Inserisci uno nome di località che sia valido", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void updateGraph(String query) {
        String place = Character.toUpperCase(query.charAt(0)) + query.substring(1);

        LineDataSet lineDataSet = new LineDataSet(loadDataOnList(), "Segnalazione per " + place);
        lineDataSet.setColor(Color.RED);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(dataSets);
        lineChart.setScaleMinima(1.5f, 1.5f);
        lineChart.getAxisLeft().setAxisMinimum(0.0f);
        lineChart.getAxisLeft().setGranularity(1.0f);
        lineChart.getAxisLeft().setAxisMaximum(10.0f);

        lineChart.getAxisRight().setAxisMinimum(0.0f);
        lineChart.getAxisRight().setGranularity(1.0f);
        lineChart.getAxisRight().setAxisMaximum(10.0f);

        lineChart.getXAxis().setGranularity(1.0f);
        lineChart.getXAxis().setAxisMaximum(10.0f);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));

        lineChart.setData(data);
        lineChart.invalidate();
    }


    private ArrayList<Entry> loadDataOnList() {
        ArrayList<Entry> datVals = new ArrayList<>();
        int index = 0;

        for (final TreeMap.Entry<String, Integer> entry : data.entrySet()) {
            datVals.add(new Entry(index++, entry.getValue()));
            dates.add(entry.getKey());
        }

        return datVals;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}
