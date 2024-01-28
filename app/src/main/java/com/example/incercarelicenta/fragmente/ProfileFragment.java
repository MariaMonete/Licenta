package com.example.incercarelicenta.fragmente;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incercarelicenta.MainActivity;
import com.example.incercarelicenta.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

// În ProfileFragment.java
public class ProfileFragment extends Fragment {

    private TextView tvNumeUtilizator;
    private FirebaseFirestore db;
    FirebaseAuth auth;
    public User user;
    TextView tvSchimbareParola,tvStergeCont;
    TextView tvDeconectare;
    AlertDialog.Builder stergeCont;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        stergeCont = new AlertDialog.Builder(view.getContext());
        tvNumeUtilizator=view.findViewById(R.id.userName);
        tvSchimbareParola=view.findViewById(R.id.changePasswordButton);
        tvStergeCont=view.findViewById(R.id.deleteAccountButton);
        tvDeconectare=view.findViewById(R.id.logoutButton);

        DocumentReference documentReference = db.collection("users").document(auth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("Username");
                    tvNumeUtilizator.setText(username);
                }
            }
        });

        tvSchimbareParola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(view.getContext(), ResetParola.class));
            }
        });

        tvStergeCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stergeCont.setTitle("Stergere cont").setMessage("Doriti sa stergeti contul?")
                        .setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String idUtilizator = auth.getCurrentUser().getUid();
                                DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("users").document(idUtilizator);
                                documentReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                });
                                FirebaseUser userAcum = auth.getCurrentUser();
                                auth.signOut();
                                userAcum.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "Utilizatorul a fost sters", Toast.LENGTH_SHORT).show();
                                        auth.signOut();
                                        startActivity(new Intent(ProfileFragment.this.getActivity(), MainActivity.class));
                                        getActivity().finish();
                                    }
                                });

                            }
                        }).setNegativeButton("Anuleaza",null)
                        .create().show();
            }
        });
        tvDeconectare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(view.getContext(), MainActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }

}
