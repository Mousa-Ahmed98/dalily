package com.example.dalily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.maps.model.LatLng;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.dalily.ProfileCreation.LOCATION_REQUEST_CODE;

public class Home extends AppCompatActivity{


    private ChipNavigationBar navigationBar;
    public static LatLng service_latlng = null;
    protected static String city = null;
    private FusedLocationProviderClient client;
    private LoadingDialog loadingDialog;


    @Override
    public void onBackPressed() {
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        client = LocationServices.getFusedLocationProviderClient(Home.this);
        navigationBar = findViewById(R.id.bottom_navigation_bar);
        navigationBar.setItemSelected(R.id.bottom_nav_home,true);
        loadingDialog = new LoadingDialog(Home.this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        bottomMenu();
        locateFun();



    }


    private void bottomMenu()
    {
        navigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i){
                    case R.id.bottom_nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.bottom_nav_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.bottom_nav_settings:
                        fragment = new SettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }


    private void locateFun() {
        if (checkLocationPermission())
            getServiceLocation();
        else
            requestPermissionForGettingLocation();
        //locate = false;
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            return true;
        else
            return false;
    }

    @SuppressLint("MissingPermission")
    private void getServiceLocation() {
        final LocationManager locationManager = (LocationManager) Home.this.getSystemService(Context.LOCATION_SERVICE);
        if (isGpsOrNetworkEnabled(locationManager)) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    getCurrentLocation();
                }
            });
        }
        else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }



    private void requestPermissionForGettingLocation() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    private boolean isGpsOrNetworkEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        loadingDialog.startLoadingDialog();
        final LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(1000)
                .setNumUpdates(1);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                Geocoder geocoder = new Geocoder(Home.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    service_latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    city = addresses.get(0).getAdminArea().trim();
                    if (city.equals("أسيوط"))
                        city = "Assiut Governorate";
                    loadingDialog.dismissDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                    loadingDialog.dismissDialog();
                }
            }
        };

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getServiceLocation();
        }
        else {
            Toast.makeText(Home.this, getString(R.string.you_have_to_grantee_permission), Toast.LENGTH_LONG).show();
            finish();
        }
    }


}