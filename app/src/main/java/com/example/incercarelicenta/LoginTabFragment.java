package com.example.incercarelicenta;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginTabFragment extends Fragment {

    private FirebaseAuth auth;
    private EditText edtLoginEmail, edtLoginPassword;
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login_tab, container, false);

        auth=FirebaseAuth.getInstance();

        edtLoginEmail=view.findViewById(R.id.login_email);
        edtLoginPassword=view.findViewById(R.id.login_password);
        loginButton=view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButonPress(view);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    public void loginButonPress(View view) {
        String email = edtLoginEmail.getText().toString().trim();
        String pass = edtLoginPassword.getText().toString().trim();

        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(view.getContext(),"Conectare reusita",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), QuizActivity.class));//TODO de schimbat quiz1!!!
                        getActivity().getFragmentManager().popBackStack();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "Conectare nereusita", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                edtLoginPassword.setError("Introduceti parola");
            }
        }else if(email.isEmpty()) {
            edtLoginEmail.setError("Introduceti email-ul");
        }else{
            edtLoginEmail.setError("Email invalid");
        }
    }

}