package com.example.incercarelicenta.fragmente;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.incercarelicenta.ParfumDetailsActivity;
import com.example.incercarelicenta.R;
import com.example.incercarelicenta.adapter.ParfumAdapter;
import com.example.incercarelicenta.clase.Parfum;
import com.example.incercarelicenta.interfete.RecyclerViewInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SugestiiFragment extends Fragment implements RecyclerViewInterface {

    private RecyclerView recommendedPerfumesRecyclerView;
    private RecyclerView recommendedPerfumesRecyclerView1;
    private List<Parfum> recommendedPerfumesList;
    private List<Parfum> recommendedPerfumesList1;
    private ParfumAdapter recommendedPerfumeAdapter;
    private ParfumAdapter recommendedPerfumeAdapter1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<Parfum> favPerfumes = new ArrayList<>();
    private TextView parfRec, parfQuiz;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getActivity()));
        }
        copyAssetsToPythonDir();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sugestii, container, false);

        parfRec = view.findViewById(R.id.parfRec);
        parfQuiz = view.findViewById(R.id.parfQuiz);
        recommendedPerfumesRecyclerView = view.findViewById(R.id.recommendedPerfumesRecyclerView);
        recommendedPerfumesRecyclerView1 = view.findViewById(R.id.recommendedPerfumesRecyclerView1);

        recommendedPerfumesList = new ArrayList<>();
        recommendedPerfumesList1 = new ArrayList<>();
        recommendedPerfumeAdapter = new ParfumAdapter(recommendedPerfumesList, requireContext(), this);
        recommendedPerfumeAdapter1 = new ParfumAdapter(recommendedPerfumesList1, requireContext(), this);

        recommendedPerfumesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recommendedPerfumesRecyclerView.setAdapter(recommendedPerfumeAdapter);
        recommendedPerfumesRecyclerView1.setLayoutManager(new LinearLayoutManager(requireContext()));
        recommendedPerfumesRecyclerView1.setAdapter(recommendedPerfumeAdapter1);

        loadFavoritePerfumesAndRecommendPerfumes();
        return view;
    }

    private void copyAssetsToPythonDir() {
        AssetManager assetManager = getContext().getAssets();
        String[] files = {"knn_model.pkl", "note_index.pkl"};
        File pythonDir = new File(getContext().getFilesDir(), "chaquopy/AssetFinder/app");

        if (!pythonDir.exists()) {
            pythonDir.mkdirs();
        }

        for (String filename : files) {
            try (InputStream in = assetManager.open(filename);
                 OutputStream out = new FileOutputStream(new File(pythonDir, filename))) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                Log.d("SugestiiFragment", "Copied asset file: " + filename);
            } catch (Exception e) {
                Log.e("SugestiiFragment", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void loadFavoriteNotesAndRecommendPerfumes() {
        DocumentReference userRef = db.collection("users").document(auth.getCurrentUser().getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            com.example.incercarelicenta.clase.User user = documentSnapshot.toObject(com.example.incercarelicenta.clase.User.class);
            if (user != null && user.getNoteFav() != null) {
                List<String> favoriteNotes = user.getNoteFav();
                Log.d("SugestiiFragment", "Favorite Notes: " + favoriteNotes);
                fetchRecommendedPerfumes(favoriteNotes);
            } else {
                Log.d("SugestiiFragment", "No favorite notes found.");
            }
        });
    }

    private void loadFavoritePerfumesAndRecommendPerfumes() {
        Toast.makeText(getContext(), "Încărcare parfumuri recomandate...", Toast.LENGTH_SHORT).show();
        DocumentReference userRef = db.collection("users").document(auth.getCurrentUser().getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            com.example.incercarelicenta.clase.User user = documentSnapshot.toObject(com.example.incercarelicenta.clase.User.class);
            if (user != null && user.getListaParfFav() != null && !user.getListaParfFav().isEmpty()) {
                Set<String> favoriteNames = extractNamesFromPerfumes(user.getListaParfFav());
                List<Parfum> favoritePerfumes = user.getListaParfFav();
                favPerfumes = favoritePerfumes;
                List<String> favoriteNotes = extractNotesFromPerfumes(favoritePerfumes);
                Log.d("SugestiiFragment", "Favorite Notes from Perfumes: " + favoriteNotes);
                fetchRecommendedPerfumesFromPython(favoriteNotes, favoriteNames);
                showFavoritePerfumes();
            } else {
                Log.d("SugestiiFragment", "No favorite perfumes found. Loading quiz recommendations.");
                loadFavoriteNotesAndRecommendPerfumes();
                showQuizRecommendations();
            }
        }).addOnFailureListener(e -> {
            Log.e("SugestiiFragment", "Failed to load user's favorite perfumes", e);
            loadFavoriteNotesAndRecommendPerfumes();
            showQuizRecommendations();
        });
    }

    private Set<String> extractNamesFromPerfumes(List<Parfum> favoritePerfumes) {
        Set<String> favoriteNames = new HashSet<>();
        for (Parfum parfum : favoritePerfumes) {
            favoriteNames.add(parfum.getName());
        }
        return favoriteNames;
    }

    private List<String> extractNotesFromPerfumes(List<Parfum> favoritePerfumes) {
        Set<String> favoriteNotes = new HashSet<>();
        for (Parfum parfum : favoritePerfumes) {
            favoriteNotes.addAll(parfum.getNotes());
        }
        return new ArrayList<>(favoriteNotes);
    }

    private void fetchRecommendedPerfumes(List<String> favoriteNotes) {
        recommendedPerfumesList.clear();
        Set<String> processedPerfumeIds = new HashSet<>();

        for (String note : favoriteNotes) {
            final String normalizedNote = normalizeText(note);
            db.collection("perfumes")
                    .whereArrayContains("notes", normalizedNote)
                    .limit(10)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Parfum parfum = document.toObject(Parfum.class);
                                if (!processedPerfumeIds.contains(parfum.getName())) {
                                    processedPerfumeIds.add(parfum.getName());
                                    recommendedPerfumesList.add(parfum);
                                }
                            }
                            recommendedPerfumeAdapter.notifyDataSetChanged();
                            Log.d("SugestiiFragment", "Recommended Perfumes: " + recommendedPerfumesList.size());
                        } else {
                            Log.d("SugestiiFragment", "Failed to fetch recommendations.");
                        }
                    });
        }
    }

    private void fetchRecommendedPerfumesFromPython(List<String> favoriteNotes, Set<String> favoriteNames) {
        try {
            Python py = Python.getInstance();
            PyObject pyobj = py.getModule("recommendations");

            PyObject pyFavoriteNotes = PyObject.fromJava(favoriteNotes.toArray(new String[0]));

            PyObject recommendations = pyobj.callAttr("get_recommendations", pyFavoriteNotes);

            if (recommendations == null) {
                Log.e("SugestiiFragment", "Python recommendations returned null");
                return;
            }

            List<Integer> recommendedIndices = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                recommendedIndices = recommendations.asList().stream()
                        .map(PyObject::toInt)
                        .collect(Collectors.toList());
            }

            Log.d("SugestiiFragment", "Python recommended indices: " + recommendedIndices);

            recommendedPerfumesList1.clear();
            Set<String> processedPerfumeIds = new HashSet<>();

            for (int index : recommendedIndices) {
                db.collection("perfumes").whereEqualTo("index", index)
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Parfum parfum = document.toObject(Parfum.class);
                                    if (parfum != null && !processedPerfumeIds.contains(parfum.getName()) && !favoriteNames.contains(parfum.getName())) {
                                        recommendedPerfumesList1.add(parfum);
                                        processedPerfumeIds.add(parfum.getName());
                                    }
                                }

                                recommendedPerfumeAdapter1.notifyDataSetChanged();
                                Log.d("SugestiiFragment", "Python Recommended Perfumes: " + recommendedPerfumesList1.size());
                            } else {
                                Log.d("SugestiiFragment", "Failed to fetch Python recommendations.");
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("SugestiiFragment", "Failed to fetch Python perfume with index: " + index, e);
                        });
            }
        } catch (Exception e) {
            Log.e("SugestiiFragment", "Error fetching Python recommendations", e);
        }
    }

    private void showFavoritePerfumes() {
        recommendedPerfumesRecyclerView1.setVisibility(View.VISIBLE);
        recommendedPerfumesRecyclerView.setVisibility(View.GONE);
        parfQuiz.setVisibility(View.GONE);
        parfRec.setVisibility(View.VISIBLE);
    }

    private void showQuizRecommendations() {
        recommendedPerfumesRecyclerView1.setVisibility(View.GONE);
        recommendedPerfumesRecyclerView.setVisibility(View.VISIBLE);
        parfRec.setVisibility(View.GONE);
        parfQuiz.setVisibility(View.VISIBLE);
    }

    private String normalizeText(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        text = text.replaceAll("\\s+", "");
        return text.toLowerCase();
    }

    @Override
    public void onResume() {
        loadFavoritePerfumesAndRecommendPerfumes();
        loadFavoriteNotesAndRecommendPerfumes();
        super.onResume();
    }

    @Override
    public void onItemClick(int position) {
        Parfum clickedParfum;
        if (!favPerfumes.isEmpty()) {
            clickedParfum = recommendedPerfumesList1.get(position);
        } else {
            clickedParfum = recommendedPerfumesList.get(position);
        }

        if (clickedParfum != null) {
            Intent intent = new Intent(requireContext(), ParfumDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("parfum", clickedParfum);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Parfumul selectat nu este disponibil.", Toast.LENGTH_SHORT).show();
        }
    }
}




