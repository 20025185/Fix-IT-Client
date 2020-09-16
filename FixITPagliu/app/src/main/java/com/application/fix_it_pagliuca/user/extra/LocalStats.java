package com.application.fix_it_pagliuca.user.extra;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fix_it_pagliuca.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class LocalStats extends AppCompatActivity {
    private String TAG = "[StatsReports] : ";

    private GraphView graphView;
    private LineGraphSeries<DataPoint> lineGraphSeries;
    private EditText editText;
    private Button button;

    private DatabaseReference dbr;

    private TreeMap<String, Integer> data;
    private String[] keySet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_report);

        button = findViewById(R.id.searchLoc);
        editText = findViewById(R.id.scriviLoc);
        graphView = findViewById(R.id.graphView);
        lineGraphSeries = new LineGraphSeries<>();
        graphView.addSeries(lineGraphSeries);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbr = database.getReference("reports");

        setListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeGraph();

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return "null";
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    private void setListeners() {
        button.setOnClickListener(view -> {
            if (!(editText.getText().toString().isEmpty())) {
                final Geocoder geocoder = new Geocoder(getApplicationContext());

                dbr.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String searchedLoc = editText.getText().toString();
                        String[] ll;
                        data = new TreeMap<>();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            String pos = Objects.requireNonNull(s.child("position").getValue()).toString();
                            ll = pos.split(" ");
                            List<Address> addresses = null;

                            try {
                                addresses = geocoder.getFromLocation(Double.parseDouble(ll[0]), Double.parseDouble(ll[1]), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            assert addresses != null;

                            if (addresses.toString().contains(searchedLoc)) {
                                if (data.containsKey(Objects.requireNonNull(s.child("date").getValue()).toString())) {
                                    @SuppressWarnings("ConstantConditions") int val = data.get(s.child("date").getValue().toString());
                                    ++val;
                                    data.put(Objects.requireNonNull(s.child("date").getValue()).toString(), val);
                                } else {
                                    data.put(Objects.requireNonNull(s.child("date").getValue()).toString(), 1);
                                }
                            }

                        }
                        Log.d(TAG, data.toString());
                        updateGraph();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(LocalStats.this, "Inserire la località da analizzare", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void initializeGraph() {
        graphView.getViewport().setMinY(0.0);
        graphView.getViewport().setMaxY(15.0);
        graphView.getViewport().setMaxX(4.0);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setTextSize(29.f);
        graphView.getGridLabelRenderer().setNumVerticalLabels(16);
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Numero Segnalazioni");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Data della segnalazione");
    }

    private void updateGraph() {
        DataPoint[] dp = new DataPoint[data.size()];
        int index = 0;

        for (final TreeMap.Entry<String, Integer> entry : data.entrySet()) {
            dp[index] = new DataPoint(index, entry.getValue());
            index++;
        }

        keySet = data.keySet().toString().split(" ");
        final int indexKey = keySet.length;

        for (int i = 0; i < keySet.length; i++) {
            keySet[i] = keySet[i].replace("[", "");
            keySet[i] = keySet[i].replace("]", "");
            keySet[i] = keySet[i].replace(",", "");
            Log.d(TAG, keySet[i]);
        }

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            int i = 0;

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX && i < indexKey) {
                    return keySet[i++];
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        lineGraphSeries.resetData(dp);
    }
}
