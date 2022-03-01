package com.example.dalily;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.dalily.Home.city;
import static com.example.dalily.Home.service_latlng;

public class ServicesActivity extends AppCompatActivity {
    TextView title;
    ConstraintLayout parent_view;
    String name;
    private StorageReference firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private List<Service> serviceList;
    private RecyclerView recyclerView;
    private ServicesAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        
        instantiateObjects();

        name = getIntent().getStringExtra("Name");
        title.setText(name);

        settingUpRecyclerView();
        gettingServicesOnLine();

        serviceAdapter.setOnItemClickListener(new ServicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
              //  Toast.makeText(ServicesActivity.this, serviceList.get(position).getId(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ServicesActivity.this, ViewSingleService.class);
                intent.putExtra("id", serviceList.get(position).getId());
                startActivity(intent);
            }
        });




    }

    private void settingUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        serviceAdapter = new ServicesAdapter(ServicesActivity.this, serviceList);
        recyclerView.setAdapter(serviceAdapter);
    }

    private void gettingServicesOnLine() {
            firebaseFirestore.collection(city)
                    .whereEqualTo("service_type", name).addSnapshotListener(ServicesActivity.this,
                    new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                    serviceList.clear();
                    for (QueryDocumentSnapshot snapshots1 : snapshots){
                        Service service = snapshots1.toObject(Service.class);
                        if (serviceRange(service) <= 1000){
                            service.setDistance_from_center(serviceRange(service));
                            serviceList.add(service);
                        }
                      //  snapshots1.getId();
                        serviceAdapter.notifyDataSetChanged();
                    }

                    serviceAdapter.swapDataSet(serviceList);
                }
            });

    }

    private double serviceRange(Service service) {
        Location a = new Location("point a");
        a.setLatitude(service.getLat());
        a.setLongitude(service.getLang());

        Location b = new Location("point b");
        b.setLatitude(service_latlng.latitude);
        b.setLongitude(service_latlng.longitude);
        double result = a.distanceTo(b);
        return result;
    }

    private void instantiateObjects(){
        title = findViewById(R.id.name_view);
        recyclerView = findViewById(R.id.recycler_view);
        parent_view = findViewById(R.id.parent_view);
        firebaseStorage = FirebaseStorage.getInstance().getReference("profile_images");
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        serviceList = new ArrayList<Service>();
    }
}