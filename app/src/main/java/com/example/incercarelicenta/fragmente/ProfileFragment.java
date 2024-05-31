package com.example.incercarelicenta.fragmente;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incercarelicenta.MainActivity;
import com.example.incercarelicenta.ParfumDetailsActivity;
import com.example.incercarelicenta.R;
import com.example.incercarelicenta.adapter.ParfumAdapter;
import com.example.incercarelicenta.clase.Parfum;
import com.example.incercarelicenta.interfete.RecyclerViewInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

// În ProfileFragment.java
public class ProfileFragment extends Fragment implements RecyclerViewInterface {

    private TextView tvNumeUtilizator;
    private FirebaseFirestore db;
    FirebaseAuth auth;
    public User user;
    TextView tvSchimbareParola,tvStergeCont;
    TextView tvDeconectare;
    AlertDialog.Builder stergeCont;
    private RecyclerView favoritePerfumesRecyclerView;
    private List<Parfum> favoritePerfumesList;
    private ParfumAdapter favoritePerfumeAdapter;
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

        favoritePerfumesRecyclerView = view.findViewById(R.id.favoritePerfumesRecyclerView);
        favoritePerfumesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        favoritePerfumesList = new ArrayList<>();

        favoritePerfumeAdapter = new ParfumAdapter(favoritePerfumesList, requireContext(), this);
        favoritePerfumesRecyclerView.setAdapter(favoritePerfumeAdapter);

        loadFavoritePerfumes();
        DocumentReference documentReference = db.collection("users").document(auth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    String username = documentSnapshot.getString("username");
                    if(username==null){
                    }
                    tvNumeUtilizator.setText(username);
                }
                else{
                    tvNumeUtilizator.setText("user");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Ștergere cont")
                        .setMessage("Doriți să ștergeți contul?")
                        .setPositiveButton("Șterge", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    // Stergere document din Firestore
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(user.getUid())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Stergere utilizator din Firebase Auth
                                                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "Utilizatorul a fost șters", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(getActivity(), MainActivity.class));
                                                            getActivity().finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), "Eroare la ștergerea utilizatorului: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Eroare la ștergerea documentului utilizatorului: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Niciun utilizator autentificat", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Anulează", null)
                        .show();
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

    private void loadFavoritePerfumes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                com.example.incercarelicenta.clase.User user = documentSnapshot.toObject(com.example.incercarelicenta.clase.User.class);
                if (user != null && user.getListaParfFav() != null) {
                    favoritePerfumesList.clear(); // Curățați lista curentă
                    favoritePerfumesList.addAll(user.getListaParfFav());
                    favoritePerfumeAdapter.notifyDataSetChanged();
                }
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("Username");
                    tvNumeUtilizator.setText(username);
                }
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Parfum clickedParfum=favoritePerfumesList.get(position);
        Intent intent = new Intent(requireContext(), ParfumDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("parfum", clickedParfum);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}