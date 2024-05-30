package com.example.incercarelicenta.fragmente;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
// Cod existent...

// Cod existent...

public class SugestiiFragment extends Fragment implements RecyclerViewInterface {

    private RecyclerView recommendedPerfumesRecyclerView;
    private RecyclerView recommendedPerfumesRecyclerView1;
    private List<Parfum> recommendedPerfumesList;
    private List<Parfum> recommendedPerfumesList1;
    private ParfumAdapter recommendedPerfumeAdapter;
    private ParfumAdapter recommendedPerfumeAdapter1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initializează Python
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getActivity()));
            Toast.makeText(getContext(), "Python initialized", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Python already initialized", Toast.LENGTH_SHORT).show();
        }

        copyAssetsToPythonDir();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sugestii, container, false);

        recommendedPerfumesRecyclerView = view.findViewById(R.id.recommendedPerfumesRecyclerView);
        recommendedPerfumesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recommendedPerfumesList = new ArrayList<>();
        recommendedPerfumeAdapter = new ParfumAdapter(recommendedPerfumesList, requireContext(), this);
        recommendedPerfumesRecyclerView.setAdapter(recommendedPerfumeAdapter);

        recommendedPerfumesRecyclerView1 = view.findViewById(R.id.recommendedPerfumesRecyclerView1);
        recommendedPerfumesRecyclerView1.setLayoutManager(new LinearLayoutManager(requireContext()));
        recommendedPerfumesList1 = new ArrayList<>();
        recommendedPerfumeAdapter1 = new ParfumAdapter(recommendedPerfumesList1, requireContext(), this);
        recommendedPerfumesRecyclerView1.setAdapter(recommendedPerfumeAdapter1);

        loadFavoriteNotesAndRecommendPerfumes();
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
                //Toast.makeText(getContext(), "Failed to copy asset file: " + filename, Toast.LENGTH_SHORT).show();
                Log.e("SugestiiFragment", "Failed to copy asset file: " + filename, e);
            }
        }
    }


    private void loadFavoriteNotesAndRecommendPerfumes() {
        //Toast.makeText(getContext(), "Fetching favorite notes...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                com.example.incercarelicenta.clase.User user = documentSnapshot.toObject(com.example.incercarelicenta.clase.User.class);
                if (user != null && user.getNoteFav() != null) {
                    List<String> favoriteNotes = user.getNoteFav();
                    Log.d("SugestiiFragment", "Favorite Notes: " + favoriteNotes.toString());
                    fetchRecommendedPerfumes(favoriteNotes);
                } else {
                    Log.d("SugestiiFragment", "No favorite notes found.");
                    Toast.makeText(getContext(), "No favorite notes found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadFavoritePerfumesAndRecommendPerfumes() {
        Toast.makeText(getContext(), "Fetching favorite perfumes...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                com.example.incercarelicenta.clase.User user = documentSnapshot.toObject(com.example.incercarelicenta.clase.User.class);
                if (user != null && user.getListaParfFav() != null) {
                    List<Parfum> favoritePerfumes = user.getListaParfFav();
                    List<String> favoriteNotes = extractNotesFromPerfumes(favoritePerfumes);
                    Log.d("SugestiiFragment", "Favorite Notes from Perfumes: " + favoriteNotes.toString());
                    fetchRecommendedPerfumesFromPython(favoriteNotes); // Directly use favoriteNotes
                } else {
                    Log.d("SugestiiFragment", "No favorite perfumes found.");
                    Toast.makeText(getContext(), "No favorite perfumes found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    .limit(10)  // Limităm la 10 rezultate per notă pentru a evita prea multe rezultate
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Parfum parfum = document.toObject(Parfum.class);
                                if (!processedPerfumeIds.contains(parfum.getName())) {
                                    recommendedPerfumesList.add(parfum);
                                    processedPerfumeIds.add(parfum.getName());
                                }
                            }
                            recommendedPerfumeAdapter.notifyDataSetChanged();
                            Log.d("SugestiiFragment", "Recommended Perfumes: " + recommendedPerfumesList.size());
                            Toast.makeText(getContext(), "Found " + recommendedPerfumesList.size() + " perfumes", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("SugestiiFragment", "Failed to fetch recommendations.");
                            Toast.makeText(getContext(), "Failed to fetch recommendations", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void fetchRecommendedPerfumesFromPython(List<String> favoriteNotes) {
        Toast.makeText(getContext(), "Fetching recommended perfumes from Python...", Toast.LENGTH_SHORT).show();
        try {
            Python py = Python.getInstance();
            PyObject pyobj = py.getModule("recommendations");

            // Convertim List<String> în PyObject
            PyObject pyFavoriteNotes = PyObject.fromJava(favoriteNotes.toArray(new String[0]));

            PyObject recommendations = pyobj.callAttr("get_recommendations", pyFavoriteNotes);

            if (recommendations == null) {
                Toast.makeText(getContext(), "Python recommendations returned null", Toast.LENGTH_SHORT).show();
                Log.e("SugestiiFragment", "Python recommendations returned null");
                return;
            }

            List<Integer> recommendedIndices = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                recommendedIndices = recommendations.asList().stream()
                        .map(pyObject -> pyObject.toInt())
                        .collect(Collectors.toList());
                Toast.makeText(getContext(), "Python recommended indices: " + recommendedIndices.toString(), Toast.LENGTH_SHORT).show();
                Log.d("SugestiiFragment", "Python recommended indices: " + recommendedIndices.toString());
            } else {
                Toast.makeText(getContext(), "Android version not supported for streams", Toast.LENGTH_SHORT).show();
                Log.e("SugestiiFragment", "Android version not supported for streams");
            }

            recommendedPerfumesList1.clear();
            Set<String> processedPerfumeIds = new HashSet<>();

            for (int index : recommendedIndices) {
                db.collection("perfumes").whereEqualTo("index", index) // Assuming you have an "index" field in your Firestore documents
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Parfum parfum = document.toObject(Parfum.class);
                                    if (parfum != null && !processedPerfumeIds.contains(parfum.getName())) {
                                        recommendedPerfumesList1.add(parfum);
                                        processedPerfumeIds.add(parfum.getName());
                                        Toast.makeText(getContext(), "Added perfume from Python: " + parfum.getName(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Python perfume already processed or null", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                recommendedPerfumeAdapter1.notifyDataSetChanged();
                                Log.d("SugestiiFragment", "Python Recommended Perfumes: " + recommendedPerfumesList1.size());
                                Toast.makeText(getContext(), "Found " + recommendedPerfumesList1.size() + " perfumes from Python", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("SugestiiFragment", "Failed to fetch Python recommendations.");
                                Toast.makeText(getContext(), "Failed to fetch Python recommendations", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to fetch Python perfume with index: " + index + " Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("SugestiiFragment", "Failed to fetch Python perfume with index: " + index, e);
                        });
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error fetching Python recommendations: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("SugestiiFragment", "Error fetching Python recommendations", e);
        }
    }


    private String normalizeText(String text) {
        // Normalizează textul, elimină diacriticele și spațiile suplimentare
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        text = text.replaceAll("\\s+", ""); // Elimină spațiile suplimentare
        return text.toLowerCase(); // Transformă în litere mici
    }

    @Override
    public void onItemClick(int position) {
        Parfum clickedParfum = recommendedPerfumesList.get(position);
        Intent intent = new Intent(requireContext(), ParfumDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("parfum", clickedParfum);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}



