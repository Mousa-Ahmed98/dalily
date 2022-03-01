package com.example.dalily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.dalily.ProfileCreation.LOCATION_REQUEST_CODE;

public class TurnOnGpsActivity extends AppCompatActivity {
    private FusedLocationProviderClient client;
    private Button gps_btn;
    private LocationManager locationManager;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isGpsOrNetworkEnabled(locationManager)){
            startActivity(new Intent(TurnOnGpsActivity.this, Home.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_on_gps);
        client = LocationServices.getFusedLocationProviderClient(TurnOnGpsActivity.this);
        gps_btn = findViewById(R.id.gps_btn);
        locationManager = (LocationManager) TurnOnGpsActivity.this.getSystemService(Context.LOCATION_SERVICE);

        gps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableGPS();
            }
        });

    }
    private void enableGPS() {
        if (checkLocationPermission())
            if (isGpsOrNetworkEnabled(locationManager)){
                startActivity(new Intent(TurnOnGpsActivity.this, Home.class));
                finish();
            }
        else{
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        else
            requestPermissionForGettingLocation();
        //locate = false;
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(TurnOnGpsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                (ContextCompat.checkSelfPermission(TurnOnGpsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            return true;
        else
            return false;
    }




    private void requestPermissionForGettingLocation() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    private boolean isGpsOrNetworkEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}