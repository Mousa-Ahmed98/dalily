package com.example.dalily;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Fragment extends Fragment {
    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailEditTextLT, passwordEditTextLT;
    private TextView forget_password;
    private Button login;
    private RelativeLayout progressBar;
    private float v = 0;
    private FirebaseAuth mAuth;
    private Context currentContext;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        currentContext = getContext();

        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_login_, container, false);
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_login_, container, false);
        emailEditText = root.findViewById(R.id.email);
        passwordEditText = root.findViewById(R.id.password);
        emailEditTextLT = root.findViewById(R.id.text_input_layout_email);
        passwordEditTextLT = root.findViewById(R.id.text_input_layout_password);
        forget_password = root.findViewById(R.id.forget_password);
        login = root.findViewById(R.id.login_button);
        progressBar = root.findViewById(R.id.progress_bar_layout);

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword(forget_password);
            }
        });

        //Animating...
        animating();

        //login process
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(getActivity(), TurnOnGpsActivity.class));
            getActivity().finish();
        }
    }

    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

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
        if(!isGpsOrNetworkEnabled()){
            Toast.makeText(getContext(), getString(R.string.you_have_to_enable_gps), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        login.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(currentContext, Home.class));
                    getActivity().finish();
                }
                else
                {
                    Toast.makeText(currentContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });


    }


    private boolean isGpsOrNetworkEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSOrNetworkEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isGPSOrNetworkEnabled;
    }

    private void animating() {
        //Horizontal translation
       // emailEditText.setTranslationX(800);
       // passwordEditText.setTranslationX(800);
        emailEditTextLT.setTranslationX(800);
        passwordEditTextLT.setTranslationX(800);
        forget_password.setTranslationX(800);
        login.setTranslationX(800);

        //opacity
       // emailEditText.setAlpha(v);
       // passwordEditText.setAlpha(v);
        emailEditTextLT.setAlpha(v);
        passwordEditTextLT.setAlpha(v);
        forget_password.setAlpha(v);
        login.setAlpha(v);

        //emailEditText.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
      //  passwordEditText.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        emailEditTextLT.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        passwordEditTextLT.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forget_password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
    }

    public void forgetPassword(View view) {
        final EditText resetMail = new EditText(currentContext);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(currentContext);
        passwordResetDialog.setTitle("Reset Password ?");
        passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link");
        passwordResetDialog.setView(resetMail);
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = resetMail.getText().toString();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(currentContext, "Reset Link Sent To Your Email, Go And Check It",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(currentContext, "Error, Reset Email Is Not Sent" + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        passwordResetDialog.show();
    }
}