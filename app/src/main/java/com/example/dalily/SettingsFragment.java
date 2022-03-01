package com.example.dalily;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class SettingsFragment extends Fragment {

    private Button logout_btn;
    private View view;
    private TextView username, username1, email, email1;
    private RelativeLayout progress_bar_layout;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_settings, container, false);
        instantiateViews();
        wantToShowViews(false);
        FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setText(documentSnapshot.getString("username"));
                email.setText(documentSnapshot.getString("email"));
                progress_bar_layout.setVisibility(View.GONE);
                wantToShowViews(true);
            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });
        return view;
    }

    private void instantiateViews() {
        logout_btn = view.findViewById(R.id.logout_btn);
        username = view.findViewById(R.id.username);
        username1 = view.findViewById(R.id.username1);
        progress_bar_layout = view.findViewById(R.id.progress_bar_layout);
        email = view.findViewById(R.id.email);
        email1 = view.findViewById(R.id.email1);
    }

    private void wantToShowViews(boolean is){
        if (is){
            logout_btn.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            username1.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            email1.setVisibility(View.VISIBLE);
        }
        else {
            logout_btn.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            username1.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            email1.setVisibility(View.GONE);
        }
    }
}