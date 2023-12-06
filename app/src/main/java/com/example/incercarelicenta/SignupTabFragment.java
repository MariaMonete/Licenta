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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignupTabFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    EditText signUpUsername, signUpEmail,signUpPassword,signUpConfirmPass;
    Button signUpButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_signup_tab, container, false);
        signUpUsername=view.findViewById(R.id.signup_username);
        signUpEmail=view.findViewById(R.id.signup_email);
        signUpPassword=view.findViewById(R.id.signup_password);
        signUpConfirmPass=view.findViewById(R.id.signup_confirm);
        signUpButton=view.findViewById(R.id.signup_button);

        auth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignup(view);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    public void btnSignup(View view) {
        String username=signUpUsername.getText().toString().trim();
        String email=signUpEmail.getText().toString().trim();
        String password=signUpPassword.getText().toString().trim();
        String confirmPassword=signUpConfirmPass.getText().toString().trim();

        if(username.isEmpty()){
            signUpUsername.setError("Introduceți numele de utilizator!");
            return;
        }
        if(email.isEmpty()|| !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpEmail.setError("Introduceți email-ul!");
            return;
        }
        if(password.isEmpty()){
            signUpPassword.setError("Introduceți parola!");
            return;
        }
        if(confirmPassword.isEmpty()){
            signUpConfirmPass.setError("Confirmați parola!");
            return;
        }
        if(!password.equals(confirmPassword)){
            signUpConfirmPass.setError("Parola nu se potrivește!");
            return ;
        }

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   Toast.makeText(view.getContext(),"Inregistrare reusita",Toast.LENGTH_SHORT).show();
                   DocumentReference documentReference=fStore.collection("users").document(auth.getCurrentUser().getUid());
                   Map<String,Object> user=new HashMap<>();
                   user.put("Username",username);
                   user.put("Email",email);
                   documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                       }
                   });
                   startActivity(new Intent(getActivity(), QuizActivity.class));
                   getActivity().getFragmentManager().popBackStack();
               }
               else{
                   Toast.makeText(view.getContext(),"Inregistrare nereusita "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
               }
            }
        });
    }
}