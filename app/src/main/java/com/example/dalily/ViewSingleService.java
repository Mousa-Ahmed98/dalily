package com.example.dalily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import static com.example.dalily.Home.city;
import static com.example.dalily.Home.service_latlng;

public class ViewSingleService extends AppCompatActivity {

    private ImageView profile_image;
    private TextView name, description, address, phone1, phone2;
    private Button map_btn;
    private FirebaseFirestore db;
    private DocumentReference ref;
    private Service service;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_service);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        instantiateViewsAndObjects();
        getTheDocument();
        //Toast.makeText(this, "1"+id+"1", Toast.LENGTH_LONG).show();
        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSingleService.this, MapActivity.class);
                intent.putExtra("lat", service.getLat());
                intent.putExtra("lang", service.getLang());
                startActivity(intent);

            }
        });

    }

    private void getTheDocument() {
        db.collection(city).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                service = documentSnapshot.toObject(Service.class);
                Glide.with(ViewSingleService.this).load(service.getPhoto_uri()).centerCrop().into(profile_image);
                name.setText(service.getTitle());
                description.setText(service.getDescription());
                phone1.setText(service.getPhone1());
                phone2.setText(service.getPhone2());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void instantiateViewsAndObjects() {
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        description = findViewById(R.id.description);
        address = findViewById(R.id.address);
        phone1 = findViewById(R.id.phone1);
        phone2 = findViewById(R.id.phone2);
        map_btn = findViewById(R.id.map_button);
        db = FirebaseFirestore.getInstance();
    }
}