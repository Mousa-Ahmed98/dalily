package com.example.dalily;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileCreation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int LOCATION_REQUEST_CODE = 100;
    private SearchableSpinner services_spinner;
    private String service = "";
    private ArrayAdapter<CharSequence> adapter;
    private static final int SELECT_PHOTO = 1;
    private ImageView ProfileImageView;
    private Button LocationSignupBtn;
    private FusedLocationProviderClient client;
    private boolean locate = true;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    //views
    private TextInputEditText title_et;
    private EditText description_et;
    private TextInputEditText phone1_et, phone2_et;
    private TextView spinner_required_tv;
    private RelativeLayout progress_bar;

    //Service object properties
    private String title;
    private Uri photo_uri;
    private String service_kind = "";
    private String description;
    private String phone1, phone2;
    double latitude = 0.0, longitude = 0.0;
    public static String city;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference documentReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        instantiatingViewsAndObjects();

        setUpSpinner();
        ProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryForPickingUpImage();


            }
        });


        LocationSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locateOrSignup();
            }
        });



    }

    private void openGalleryForPickingUpImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    private void instantiatingViewsAndObjects() {
        title_et = findViewById(R.id.titleEditText);
        description_et = findViewById(R.id.services_description_edittext);
        phone1_et = findViewById(R.id.phone1EditText);
        phone2_et = findViewById(R.id.phone2EditText);
        services_spinner = findViewById(R.id.services_spinner);
        ProfileImageView = findViewById(R.id.profile_image);
        spinner_required_tv = findViewById(R.id.spinner_required_tv);
        LocationSignupBtn = findViewById(R.id.location_signup_btn);
        progress_bar = findViewById(R.id.progress_bar_layout);
        client = LocationServices.getFusedLocationProviderClient(ProfileCreation.this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_images");
    }


    private void locateOrSignup() {
        if (locate) {
            locateFun();
            Toast.makeText(this, "Locating...", Toast.LENGTH_SHORT).show();
        }
        else {
            signupFun();
          //  Toast.makeText(ProfileCreation.this, "sign up", Toast.LENGTH_LONG).show();
        }

    }

    private void signupFun() {
        extractServiceInformation();
        if(!isServiceInformationOk())
            return;
        signupNow();
        

    }

    private void signupNow() {
        uploadProfileImage();
    }

    private Service createServiceObject() {

        return new Service(photo_uri.toString(), title, service_kind, description, phone1, phone2, latitude, longitude,
                documentReference.getId());
    }


    private void createServiceAccountOnFirebase(Service service) {

        double result = ReferencePoint.calcDistFromRefPoint(new LatLng(longitude, latitude));
        Toast.makeText(ProfileCreation.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
                documentReference.set(service).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileCreation.this, "Created Successfully", Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.GONE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileCreation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.GONE);
            }
        });


    }



    private void uploadProfileImage(){
       // db.collection(city).document(auth.getCurrentUser().getUid()).set("");
        documentReference = db.collection(city).document(auth.getCurrentUser().getUid());

        if (photo_uri != null) {
            progress_bar.setVisibility(View.VISIBLE);
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(photo_uri));
            mUploadTask = fileReference.putFile(photo_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileCreation.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    photo_uri = uri;
                                    Service service = createServiceObject();
                                    service.setPhoto_uri(photo_uri.toString());
                                    createServiceAccountOnFirebase(service);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileCreation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }




    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    private void extractServiceInformation() {
        title = title_et.getText().toString().trim();
        description = description_et.getText().toString().trim();
        phone1 = phone1_et.getText().toString().trim();
        phone2 = phone2_et.getText().toString().trim();
    }

    private boolean isServiceInformationOk() {

        if (photo_uri == null)
        {
            ProfileImageView.requestFocus();
            Toast.makeText(ProfileCreation.this, "You have to upload an image at first.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(title.isEmpty())
        {
            title_et.setError("title is required");
            title_et.requestFocus();
            return false;
        }

        if(service_kind.isEmpty())
        {
            Toast.makeText(ProfileCreation.this, "You have to choose a service kind.", Toast.LENGTH_LONG).show();
            return false;
        }
       
        if(description.isEmpty())
        {
            description_et.setError("description is required");
            description_et.requestFocus();
            return false;
        }

        if(phone1.isEmpty() || phone1.length()<11)
        {
            phone1_et.setError("length must be more than 10 numbers");
            phone1_et.requestFocus();
            return false;
        }

        if(!phone2.isEmpty() &&  phone2.length()<11)
        {
            phone2_et.setError("length must be more than 10 numbers");
            return false;
        }
        
        if (latitude == 0.0 || longitude == 0.0)
        {
            locateFun();
            Toast.makeText(ProfileCreation.this, "Locate your position at first", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private void locateFun() {
        if (checkLocationPermission())
            getServiceLocation();
        else
            requestPermissionForGettingLocation();
        //locate = false;
    }

    private void setUpSpinner() {
        services_spinner.setFocusable(true);
        services_spinner.setFocusableInTouchMode(true);
        adapter = ArrayAdapter.createFromResource(this, R.array.services_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        services_spinner.setAdapter(adapter);
        services_spinner.setOnItemSelectedListener(this);
    }

    private void pickUpImage(Intent data) {
        photo_uri = data.getData();
        Glide.with(ProfileCreation.this).load(photo_uri).centerCrop().into(ProfileImageView);

    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(ProfileCreation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                (ContextCompat.checkSelfPermission(ProfileCreation.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            return true;
        else
            return false;
    }

    @SuppressLint("MissingPermission")
    private void getServiceLocation() {

        final LocationManager locationManager = (LocationManager) ProfileCreation.this.getSystemService(Context.LOCATION_SERVICE);
        if (isGpsOrNetworkEnabled(locationManager)) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    getCurrentLocation();
                }
            });
        }
        else
        {
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
        final LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(1000)
                .setNumUpdates(1);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                Geocoder geocoder = new Geocoder(ProfileCreation.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                   // Toast.makeText(ProfileCreation.this, "Longitude = " + addresses.get(0).getLongitude() + "Latitude = " + addresses.get(0).getLatitude()
                   //          + "governorate is " + addresses.get(0).getAdminArea(), Toast.LENGTH_LONG).show();
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();
                    city = addresses.get(0).getAdminArea();
                    LocationSignupBtn.setText(getString(R.string.Signup));
                } catch (IOException e) {
                    e.printStackTrace();
                    LocationSignupBtn.setText(getString(R.string.get_location));
                }
            }
        };
        locate = false;

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
        service_kind = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null && data.getData() != null)
        {
            pickUpImage(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED))
        {
            getServiceLocation();
        }
        else
        {
            Toast.makeText(ProfileCreation.this, getString(R.string.you_have_to_grantee_permission), Toast.LENGTH_LONG).show();
        }
    }



    

}