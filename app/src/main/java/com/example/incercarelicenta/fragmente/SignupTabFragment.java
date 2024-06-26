package com.example.incercarelicenta.fragmente;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.incercarelicenta.QuizActivity;
import com.example.incercarelicenta.R;
import com.example.incercarelicenta.clase.Parfum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignupTabFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    EditText signUpUsername, signUpEmail,signUpPassword,signUpConfirmPass;
    Button signUpButton;
    ToggleButton toggle;
    EditText passwordEditText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_signup_tab, container, false);
        signUpUsername=view.findViewById(R.id.signup_username);
        signUpEmail=view.findViewById(R.id.signup_email);
        signUpPassword=view.findViewById(R.id.signup_password);
        signUpConfirmPass=view.findViewById(R.id.signup_confirm);
        signUpButton=view.findViewById(R.id.signup_button);
        toggle = view.findViewById(R.id.toggle_password_visibility1);
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                signUpPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                signUpConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                signUpPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                signUpConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            signUpPassword.setSelection(signUpPassword.getText().length());
            signUpConfirmPass.setSelection(signUpConfirmPass.getText().length());
        });


        auth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignup(view);
            }
        });

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
                   Toast.makeText(view.getContext(),"Înregistrare reușita",Toast.LENGTH_SHORT).show();
                   DocumentReference documentReference=fStore.collection("users").document(auth.getCurrentUser().getUid());
                   Map<String,Object> user=new HashMap<>();
                   user.put("Username",username);
                   user.put("Email",email);
                   Parfum parfumTest=new Parfum("Odin","00 Auriel Eau de Parfum");
                   List<Parfum> lista = new ArrayList<>();
                   //lista.add(parfumTest);
                   user.put("ListaParfFav", lista);
                   user.put("isModerator",false);
                   documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                       }
                   });
                   startActivity(new Intent(getActivity(), QuizActivity.class));
                   getActivity().getFragmentManager().popBackStack();
               }
               else{
                   Toast.makeText(view.getContext(),"Înregistrare nereușită "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
               }
            }
        });
    }
}