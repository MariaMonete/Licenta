package com.example.incercarelicenta.fragmente;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incercarelicenta.HomeActivity;
import com.example.incercarelicenta.QuizActivity;
import com.example.incercarelicenta.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginTabFragment extends Fragment {

    private FirebaseAuth auth;
    private EditText edtLoginEmail, edtLoginPassword;
    private Button loginButton;
    AlertDialog.Builder resert_alert;


    TextView txtResetPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login_tab, container, false);

        auth=FirebaseAuth.getInstance();

        edtLoginEmail=view.findViewById(R.id.login_email);
        edtLoginPassword=view.findViewById(R.id.login_password);
        loginButton=view.findViewById(R.id.login_button);
        txtResetPass=view.findViewById(R.id.login_reset_pass);

        resert_alert = new AlertDialog.Builder(view.getContext());
        txtResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPassword(view,inflater);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButonPress(view);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().getFragmentManager().popBackStack();
        }
    }
    public void loginButonPress(View view) {
        String email = edtLoginEmail.getText().toString().trim();
        String pass = edtLoginPassword.getText().toString().trim();

        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(view.getContext(),"Conectare reușită",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), QuizActivity.class));
                        getActivity().getFragmentManager().popBackStack();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "Conectare nereușită", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                edtLoginPassword.setError("Introduceți parola");
            }
        }else if(email.isEmpty()) {
            edtLoginEmail.setError("Introduceți email-ul");
        }else{
            edtLoginEmail.setError("Email invalid");
        }
    }

    public void ResetPassword(View view,LayoutInflater inflater) {
        View view_inflater=inflater.inflate(R.layout.reset_passwird_pop,null);
        resert_alert.setTitle("Resetare parola").setMessage("Introduceți email-ul pentru a reseta parola")
                .setPositiveButton("Resetează", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText email = view_inflater.findViewById(R.id.edt_email_reset_password_pop);
                        if(email.getText().toString().isEmpty()){
                            email.setError("Introduceți email-ul");
                            return ;
                        }
                        auth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(view.getContext(),"Un email a fost trimis",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).setNegativeButton("Anulează",null)
                .setView(view_inflater).create().show();
    }
}