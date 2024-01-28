package com.example.incercarelicenta.fragmente;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.incercarelicenta.AddPerfumes;
import com.example.incercarelicenta.clase.Parfum;
import com.example.incercarelicenta.ParfumAdapter;
import com.example.incercarelicenta.ParfumDetailsActivity;
import com.example.incercarelicenta.R;
import com.example.incercarelicenta.interfete.RecyclerViewInterface;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecyclerViewInterface {
    private RecyclerView recyclerView;
    private ParfumAdapter adapter;
    private List<Parfum> parfumList;
    private FirebaseFirestore db;

    //search
    private List<Parfum> filteredList;
    private SearchView searchView;
    AddPerfumes addPerfumes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
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


        //Toast.makeText(view.getContext(),"Suntem in home fragment",Toast.LENGTH_LONG).show();
        return view;

    }
    private void filterList(String query) {
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

}