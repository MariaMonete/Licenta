package com.example.incercarelicenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.incercarelicenta.adapter.CommentsAdapter;
import com.example.incercarelicenta.clase.Comment;
import com.example.incercarelicenta.clase.Parfum;
import com.example.incercarelicenta.clase.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    private List<Comment> commentList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private RecyclerView recyclerViewComments;
    private EditText editTextNewComment;
    private Button buttonAddComment;
    private String currentUserId;
    private int parfumId;
    private boolean isModerator = false;
    private String numeParfum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parfum_details);

        imageViewParfum = findViewById(R.id.imageViewParfum);
        textViewParfumName = findViewById(R.id.textViewParfumName);
        textViewParfumBrand = findViewById(R.id.textViewParfumBrand);
        textViewParfumDescription = findViewById(R.id.textViewParfumDescription);
        textViewParfumNotes = findViewById(R.id.textViewParfumNotes);
        btnFav = findViewById(R.id.favBtn);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        editTextNewComment = findViewById(R.id.editTextNewComment);
        buttonAddComment = findViewById(R.id.buttonAddComment);
        Button backButton = findViewById(R.id.backButton);

        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        commentsAdapter = new CommentsAdapter(commentList, currentUserId, isModerator, new CommentsAdapter.OnCommentClickListener() {
            @Override
            public void onEditClick(Comment comment) {
                showEditCommentDialog(comment);
            }

            @Override
            public void onDeleteClick(Comment comment) {
                deleteComment(comment);
            }
        });
        recyclerViewComments.setAdapter(commentsAdapter);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaSiSeteazaButonFavorit();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Parfum parfum = bundle.getParcelable("parfum");
            if (parfum != null) {
                numeParfum = parfum.getName();
                parfumId = parfum.getIndex();
                Log.d("ParfumDetailsActivity", "Parfum ID: " + parfumId);
                checkIfUserIsModerator();
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

                StringBuilder notesStringBuilder = new StringBuilder();
                for (String note : parfum.getNotes()) {
                    notesStringBuilder.append("- ").append(note).append("\n");
                }
                textViewParfumNotes.setText(notesStringBuilder.toString());
            }
        }
    }

    private void checkIfUserIsModerator() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUserId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    isModerator = user.isModerator();
                }
            }
        });
    }

    private void loadComments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Toast.makeText(getApplicationContext(),parfumId,Toast.LENGTH_SHORT).show();
        db.collection("comments").whereEqualTo("parfumId", parfumId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        commentList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Comment comment = documentSnapshot.toObject(Comment.class);
                            commentList.add(comment);
                        }
                        commentsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void showEditCommentDialog(Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editează comentariul");

        final EditText input = new EditText(this);
        input.setText(comment.getText());
        builder.setView(input);

        builder.setPositiveButton("Salvează", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newText = input.getText().toString().trim();
                if (!newText.isEmpty()) {
                    editComment(comment, newText);
                }
            }
        });
        builder.setNegativeButton("Anulează", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void editComment(Comment comment, String newText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comments").document(comment.getId())
                .update("text", newText)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int index = commentList.indexOf(comment);
                        if (index != -1) {
                            comment.setText(newText);
                            commentsAdapter.notifyItemChanged(index);
                        }
                    }
                });
    }

    private void addComment() {
        String text = editTextNewComment.getText().toString().trim();
        if (!text.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUserId);

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        String username = user.getUsername();
                        String commentId = db.collection("comments").document().getId();
                        Comment comment = new Comment(commentId, currentUserId, username, text, parfumId);

                        db.collection("comments").document(commentId)
                                .set(comment)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        commentList.add(comment);
                                        commentsAdapter.notifyItemInserted(commentList.size() - 1);
                                        editTextNewComment.setText("");
                                    }
                                });
                    }
                }
            });
        }
    }

    private void deleteComment(Comment comment) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comments").document(comment.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int index = commentList.indexOf(comment);
                        if (index != -1) {
                            commentList.remove(index);
                            commentsAdapter.notifyItemRemoved(index);
                        }
                    }
                });
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
