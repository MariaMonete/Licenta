package com.example.incercarelicenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.incercarelicenta.databinding.ActivityHomeBinding;
import com.example.incercarelicenta.fragmente.HomeFragment;
import com.example.incercarelicenta.fragmente.ProfileFragment;
import com.example.incercarelicenta.fragmente.SearchFragment;


public class HomeActivity extends AppCompatActivity  {

//    private RecyclerView recyclerView;
//    private ParfumAdapter adapter;
//    private List<Parfum> parfumList;
//    private FirebaseFirestore db;
//    //search
//    private List<Parfum> filteredList;
//    private SearchView searchView;

    ActivityHomeBinding binding;

    /**
     * Objiect utilizat pentru adaugarea de parfumuri in Firebase
     * dintr-un fisier csv
     */
    AddPerfumes addPerfumes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*db= FirebaseFirestore.getInstance();

        parfumList = new ArrayList<>();
        filteredList = new ArrayList<>();
//        parfumList = generateParfumData();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParfumAdapter(parfumList, this,this);
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        EventChangeListener();*/

        /**
         * Codul prin care sunt adaugate in baza de date parfumurile.
         * Acesta este apelat o singura data.
         * In rest va ramane comentat.
         */
//        addPerfumes = new AddPerfumes();
//        try {
//            addPerfumes.addInFirebase(getAssets().open("final_perfume_data.csv"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item->{
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.searchBy:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return  true;
                }

        );
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();
    }

    /*private void filterList(String query) {
        filteredList.clear();

        for (Parfum parfum : parfumList) {
            if (parfum.getName().toLowerCase().contains(query.toLowerCase()) ||
                    parfum.getBrand().toLowerCase().contains(query.toLowerCase()) ||
                    containsNote(parfum.getNotes(), query.toLowerCase())) {
                filteredList.add(parfum);
            }
        }

        adapter.setData(filteredList);
        adapter.notifyDataSetChanged();
    }

    private boolean containsNote(List<String> notes, String query) {
        for (String note : notes) {
            if (note.toLowerCase().contains(query)) {
                return true;
            }
        }
        return false;
    }

    // Metodă de generare a unui set de date de parfumuri pentru testare
    private void EventChangeListener() {
//        List<Parfum> generatedList = new ArrayList<>();
        db.collection("perfumes").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error: ", error.getMessage());
                    return;
                }
                parfumList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Parfum parfum = doc.toObject(Parfum.class);
                    parfumList.add(parfum);
                }
//                adapterCampanie.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Parfum clickedParfum;
        if(filteredList.isEmpty()){
            clickedParfum = parfumList.get(position);
        }
        else{
            clickedParfum = filteredList.get(position);
        }
        Intent intent = new Intent(HomeActivity.this, ParfumDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("parfum", clickedParfum);
        intent.putExtras(bundle);

        startActivity(intent);

    }*/
}


/*package com.example.incercarelicenta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.incercarelicenta.interfete.RecyclerViewInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Fragment implements RecyclerViewInterface {

    private RecyclerView recyclerView;
    private ParfumAdapter adapter;
    private List<Parfum> parfumList;
    private FirebaseFirestore db;

    //search
    private List<Parfum> filteredList;
    private SearchView searchView;

    *//**
     * Objiect utilizat pentru adaugarea de parfumuri in Firebase
     * dintr-un fisier csv
     *//*
    AddPerfumes addPerfumes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        db = FirebaseFirestore.getInstance();

        parfumList = new ArrayList<>();
        filteredList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView); // Folosim referința la view pentru a găsi RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // requireContext() în loc de this
        adapter = new ParfumAdapter(parfumList, requireContext(), this);
        recyclerView.setAdapter(adapter);

        searchView = view.findViewById(R.id.searchView); // Folosim referința la view pentru a găsi SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        EventChangeListener();


        return view;
    }*/


   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db= FirebaseFirestore.getInstance();

        parfumList = new ArrayList<>();
        filteredList = new ArrayList<>();
//        parfumList = generateParfumData();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParfumAdapter(parfumList, this,this);
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        EventChangeListener();

        *//**
         * Codul prin care sunt adaugate in baza de date parfumurile.
         * Acesta este apelat o singura data.
         * In rest va ramane comentat.
         *//*
//        addPerfumes = new AddPerfumes();
//        try {
//            addPerfumes.addInFirebase(getAssets().open("final_perfume_data.csv"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
*/
/*    private void filterList(String query) {
        filteredList.clear();

        for (Parfum parfum : parfumList) {
            if (parfum.getName().toLowerCase().contains(query.toLowerCase()) ||
                    parfum.getBrand().toLowerCase().contains(query.toLowerCase()) ||
                    containsNote(parfum.getNotes(), query.toLowerCase())) {
                filteredList.add(parfum);
            }
        }

        adapter.setData(filteredList);
        adapter.notifyDataSetChanged();
    }

    private boolean containsNote(List<String> notes, String query) {
        for (String note : notes) {
            if (note.toLowerCase().contains(query)) {
                return true;
            }
        }
        return false;
    }

    // Metodă de generare a unui set de date de parfumuri pentru testare
    private void EventChangeListener() {
//        List<Parfum> generatedList = new ArrayList<>();
        db.collection("perfumes").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error: ", error.getMessage());
                    return;
                }
                parfumList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Parfum parfum = doc.toObject(Parfum.class);
                    parfumList.add(parfum);
                }
//                adapterCampanie.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Parfum clickedParfum;
        if(filteredList.isEmpty()){
            clickedParfum = parfumList.get(position);
        }
        else{
            clickedParfum = filteredList.get(position);
        }
        Intent intent = new Intent(requireContext(), ParfumDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("parfum", clickedParfum);
        intent.putExtras(bundle);

        startActivity(intent);

    }

}*/


