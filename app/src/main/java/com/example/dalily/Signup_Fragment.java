package com.example.dalily;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup_Fragment extends Fragment {
    private EditText emailEditText, passwordEditText,userNameEditText;
    private Button signup;
    private RelativeLayout progressBar;
    private View rootview;
    private String email, password, username;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        context = getContext();


        // Inflate the layout for this fragment
        rootview = (View)inflater.inflate(R.layout.fragment_signup_, container, false);
        emailEditText = rootview.findViewById(R.id.email);
        passwordEditText = rootview.findViewById(R.id.password);
        userNameEditText = rootview.findViewById(R.id.username);
        signup = rootview.findViewById(R.id.signup);
        progressBar = rootview.findViewById(R.id.progress_bar_layout);


        //signup process
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        return rootview;
    }

    private void signup() {

         email = emailEditText.getText().toString().trim();
         password = passwordEditText.getText().toString().trim();
         username = userNameEditText.getText().toString().trim();

        //validating the inputs
        if(email.isEmpty())
        {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty())
        {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            passwordEditText.setError("Password's length must be more than six");
            passwordEditText.requestFocus();
            return;
        }
        if(username.isEmpty())
        {
            userNameEditText.setError("Password is required");
            userNameEditText.requestFocus();
            return;
        }
        if(username.length() < 2)
        {
            userNameEditText.setError("Password's length must be more than six");
            userNameEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    String userId  = mAuth.getCurrentUser().getUid();
                    recordUSerInFireStore(userId);
                    startActivity(new Intent(getContext(), Home.class));
                    getActivity().finish();
                    Toast.makeText(context,"User registered successfully",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(context,"User already signed in",Toast.LENGTH_SHORT).show();
                    }
                    else
                    Toast.makeText(context,"User not signed",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });



    }

    private void recordUSerInFireStore(String userId)
    {
        // instantiating
        firestore = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("email", email);
        user.put("username", username);
        DocumentReference document = firestore.collection("users").document(userId);
        document.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context,"Recorded Successfully",Toast.LENGTH_SHORT).show();

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Recording failed",Toast.LENGTH_SHORT).show();
            }
        });

    }
}