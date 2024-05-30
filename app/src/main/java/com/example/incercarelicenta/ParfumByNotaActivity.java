package com.example.incercarelicenta;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.incercarelicenta.adapter.ParfumAdapter;
import com.example.incercarelicenta.clase.Parfum;
import com.example.incercarelicenta.interfete.RecyclerViewInterface;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParfumByNotaActivity extends AppCompatActivity implements RecyclerViewInterface {

    private RecyclerView recyclerView;
    private ParfumAdapter adapter;
    private List<Parfum> parfumList;

    private List<Parfum> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parfum_by_nota);

        String notaIntent = getIntent().getStringExtra("nota");

        parfumList = new ArrayList<>();
        filteredList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewParfumByNota); // Folosim referința la view pentru a găsi RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // requireContext() în loc de this
        adapter = new ParfumAdapter(parfumList, this, this);
        recyclerView.setAdapter(adapter);


        FirebaseFirestore.getInstance().collection("perfumes").addSnapshotListener(new EventListener<QuerySnapshot>() {

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
                filterList(notaIntent);
            }
        });




    }

    private void filterList(String query) {
        filteredList.clear();

        for (Parfum parfum : parfumList) {
            if (containsNote(parfum.getNotes(), query.toLowerCase())) {
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

    @Override
    public void onItemClick(int position) {
        Parfum clickedParfum;

        clickedParfum = filteredList.get(position);

        Intent intent = new Intent(this, ParfumDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("parfum", clickedParfum);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}