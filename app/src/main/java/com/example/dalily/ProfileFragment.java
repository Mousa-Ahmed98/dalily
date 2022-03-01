package com.example.dalily;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import static com.example.dalily.Home.city;

public class ProfileFragment extends Fragment {

    private ViewSwitcher viewSwitcher;
    private ConstraintLayout secondview;
    private TextView No_Account_Textview;
    private ImageView No_Account_Imageview;
    private ProgressBar progressBar;
    private RelativeLayout progressBar1;
    private static int Request_Code = 3;
    private FusedLocationProviderClient client;

    private ImageView profile_image;
    private TextView profile_name, profile_description,
    profile_address, profile_phone1, profile_phone2;
    private Button profile_map_button;


    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference ref;
    private Context context;
    private Service service;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = instantiatingViewAndVariables(inflater, container);
        progressBar1.setVisibility(View.GONE);
        setProfileViewsToGone();
        doSnapShotListener();

        profile_map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("lat", service.getLat());
                intent.putExtra("lang", service.getLang());
                startActivity(intent);
            }
        });

        return rootview;
    }

    private void doSnapShotListener() {
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && value.exists()) {
                    setViewsToGone();
                    if(viewSwitcher.getCurrentView() == secondview)
                    {
                        viewSwitcher.showNext();
                    }
                    fillTheProfile();
                } else {
                    setViewsToVisible();
                    viewSwitcher.showNext();
                    spanningTextView();
                }
            }
        });
    }

    private void fillTheProfile() {
        progressBar1.setVisibility(View.VISIBLE);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                service = documentSnapshot.toObject(Service.class);
                Glide.with(context).load(service.getPhoto_uri()).centerCrop().into(profile_image);
                profile_name.setText(service.getTitle());
                profile_description.setText(service.getDescription());
                profile_phone1.setText(service.getPhone1());
                profile_phone2.setText(service.getPhone2());
                progressBar1.setVisibility(View.GONE);
                setProfileViewsToVisible();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }



    @NotNull
    private View instantiatingViewAndVariables(LayoutInflater inflater, ViewGroup container) {
        context = getContext();
        View rootview = inflater.inflate(R.layout.fragment_profile, container, false);
        client = LocationServices.getFusedLocationProviderClient(context);
        viewSwitcher = rootview.findViewById(R.id.view_switcher);
        secondview = rootview.findViewById(R.id.empty_profile_layout);
        No_Account_Textview = rootview.findViewById(R.id.no_data_tv);
        No_Account_Imageview = rootview.findViewById(R.id.no_account_imageview);
        progressBar = rootview.findViewById(R.id.progress_bar);
        progressBar1 = rootview.findViewById(R.id.progress_bar_layout);
        No_Account_Textview.setVisibility(View.GONE);
        No_Account_Imageview.setVisibility(View.GONE);

        profile_image = rootview.findViewById(R.id.profile_image);
        profile_name = rootview.findViewById(R.id.profile_name);
        profile_description = rootview.findViewById(R.id.profile_description);
        profile_address = rootview.findViewById(R.id.profile_address);
        profile_phone1 = rootview.findViewById(R.id.profile_phone1);
        profile_phone2 = rootview.findViewById(R.id.profile_phone2);
        profile_map_button = rootview.findViewById(R.id.profile_map_button);

        ref = db.collection(city).document(auth.getCurrentUser().getUid());
        Toast.makeText(context, city, Toast.LENGTH_SHORT).show();
        return rootview;
    }

    private void spanningTextView() {
        SpannableString spannableString = new SpannableString(No_Account_Textview.getText().toString());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(context, ProfileCreation.class));
            }
        };
        spannableString.setSpan(clickableSpan, 36, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        No_Account_Textview.setText(spannableString);
        No_Account_Textview.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setViewsToVisible() {
        progressBar.setVisibility(View.GONE);
        No_Account_Imageview.setVisibility(View.VISIBLE);
        No_Account_Textview.setVisibility(View.VISIBLE);
    }

    private void setViewsToGone() {
        progressBar.setVisibility(View.GONE);
        No_Account_Imageview.setVisibility(View.GONE);
        No_Account_Textview.setVisibility(View.GONE);
       // Toast.makeText(context, "Exists", Toast.LENGTH_SHORT).show();
    }

    private void setProfileViewsToGone() {
        profile_image.setVisibility(View.GONE);
        profile_name.setVisibility(View.GONE);
        profile_description.setVisibility(View.GONE);
        profile_address.setVisibility(View.GONE);
        profile_phone1.setVisibility(View.GONE);
        profile_phone2.setVisibility(View.GONE);
        profile_map_button.setVisibility(View.GONE);
    }

    private void setProfileViewsToVisible() {
        profile_image.setVisibility(View.VISIBLE);
        profile_name.setVisibility(View.VISIBLE);
        profile_description.setVisibility(View.VISIBLE);
        profile_address.setVisibility(View.VISIBLE);
        profile_phone1.setVisibility(View.VISIBLE);
        profile_phone2.setVisibility(View.VISIBLE);
        profile_map_button.setVisibility(View.VISIBLE);
    }

}