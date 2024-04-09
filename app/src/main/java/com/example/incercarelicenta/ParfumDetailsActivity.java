package com.example.incercarelicenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.incercarelicenta.clase.Parfum;
import com.example.incercarelicenta.clase.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ParfumDetailsActivity extends AppCompatActivity {

    private ImageView imageViewParfum;
    private TextView textViewParfumName, textViewParfumBrand, textViewParfumDescription, textViewParfumNotes;
    private ImageButton btnFav;

    private String numeParfum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parfum_details);

        // Initialize views
        imageViewParfum = findViewById(R.id.imageViewParfum);
        textViewParfumName = findViewById(R.id.textViewParfumName);
        textViewParfumBrand = findViewById(R.id.textViewParfumBrand);
        textViewParfumDescription = findViewById(R.id.textViewParfumDescription);
        textViewParfumNotes = findViewById(R.id.textViewParfumNotes);
        btnFav=findViewById(R.id.favBtn);

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaSiSeteazaButonFavorit();
            }
        });

        // Get data from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Parfum parfum = bundle.getParcelable("parfum");
            if (parfum != null) {
                numeParfum = parfum.getName();
                //
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.getListaParfFav() != null) {
                            List<Parfum> listaFavorite = user.getListaParfFav();
                            for (Parfum parfum : listaFavorite) {
                                if (parfum.getName().equals(numeParfum)) {
                                    btnFav.setBackgroundResource(R.drawable.ic_favorite_pink_24);
                                    break;
                                }
                            }
                        }
                    }
                });
                Glide.with(this).load(parfum.getImgUrl()).into(imageViewParfum);
                textViewParfumName.setText(parfum.getName());
                textViewParfumBrand.setText(parfum.getBrand());
                textViewParfumDescription.setText(parfum.getDescription());

                // Concatenate notes into a single string
                StringBuilder notesStringBuilder = new StringBuilder();
                for (String note : parfum.getNotes()) {
                    notesStringBuilder.append("- ").append(note).append("\n");
                }
                textViewParfumNotes.setText(notesStringBuilder.toString());
            }
        }
    }

    private void verificaSiSeteazaButonFavorit() {
        existaParfum(new Callback() {
            @Override
            public void onSuccess(boolean existaParfum) {
                if (existaParfum) {
                    btnFav.setBackgroundResource(R.drawable.ic_favorite_border_24);
                    removeFromFavorites();
                } else {
                    btnFav.setBackgroundResource(R.drawable.ic_favorite_pink_24);
                    addToFavorites();
                }
            }
        });
    }

    private void addToFavorites() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (user.getListaParfFav() == null) {
                        user.setListaParfFav(new ArrayList<>());
                    }
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        Parfum parfum = bundle.getParcelable("parfum");
                        user.getListaParfFav().add(parfum);
                        userRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Parfum adaugat in lista de favorite", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void removeFromFavorites() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null && user.getListaParfFav() != null) {
                    List<Parfum> listaFavorite = user.getListaParfFav();
                    for (Parfum parfum : listaFavorite) {
                        if (parfum.getName().equals(numeParfum)) {
                            listaFavorite.remove(parfum);
                            break;
                        }
                    }
                    userRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Parfum eliminat din lista de favorite", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void existaParfum(final Callback callback) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<Parfum> listaFavorite = documentSnapshot.toObject(User.class).getListaParfFav();
                    if (listaFavorite != null) {
                        for (Parfum parfum : listaFavorite) {
                            if (parfum.getName().equals(numeParfum)) {
                                callback.onSuccess(true);
                                return;
                            }
                        }
                    }
                }
                callback.onSuccess(false);
            }
        });
    }

    interface Callback {
        void onSuccess(boolean existaParfum);
    }


}

