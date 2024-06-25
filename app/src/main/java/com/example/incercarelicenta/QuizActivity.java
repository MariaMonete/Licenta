package com.example.incercarelicenta;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.incercarelicenta.adapter.QuizOptionAdapter;
import com.example.incercarelicenta.clase.Parfum;
import com.example.incercarelicenta.clase.Question;
import com.example.incercarelicenta.clase.User;
import com.example.incercarelicenta.interfete.RecyclerViewInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity implements RecyclerViewInterface {

    private TextView questionTextView;
    private RecyclerView optionsRecyclerView;
    private Button nextButton, previousButton;

    private int currentQuestionIndex = 0;
    private List<Question> questions;
    private Map<Integer, String> answers;
    private QuizOptionAdapter adapter;
    private boolean isSubcategoryQuestionAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionTextView);
        optionsRecyclerView = findViewById(R.id.optionsRecyclerView);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);

        questions = getQuestions();
        answers = new HashMap<>();

        optionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        showQuestion(currentQuestionIndex);

        nextButton.setOnClickListener(v -> handleNextButtonClick());

        previousButton.setOnClickListener(v -> handlePreviousButtonClick());

        if (currentQuestionIndex == 0) {
            previousButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(int position) {
        // Additional logic if needed
    }

    private void handleNextButtonClick() {
        String selectedAnswer = adapter.getSelectedOption();
        if (selectedAnswer == null) {
            Toast.makeText(QuizActivity.this, "Selectează răspuns", Toast.LENGTH_SHORT).show();
            return;
        }

        answers.put(currentQuestionIndex, selectedAnswer);

        if (currentQuestionIndex == 2) {
            saveFavoriteNote();
            Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        } else {
            showSubcategoryQuestion(selectedAnswer);
        }
    }

    private void handlePreviousButtonClick() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion(currentQuestionIndex);
        }

        if (currentQuestionIndex == 1 && isSubcategoryQuestionAdded) {
            questions.remove(2);
            isSubcategoryQuestionAdded = false;
        }

        if (currentQuestionIndex == 0) {
            previousButton.setVisibility(View.INVISIBLE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }
    }

    private List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        List<String> answers1 = List.of("Ziua", "Seara", "Oricând");
        List<Integer> images1 = List.of(R.drawable.zi, R.drawable.noapte, R.drawable.oricand);
        Question q1 = new Question("Când folosești în mod normal parfum?", answers1, images1);
        q1.addSubcategory("Ziua", List.of("Fructat", "Floral", "Note verzi", "Mosc", "Citrat"));
        q1.addSubcategory("Seara", List.of("Condimentat", "Lemnos", "Gurmand"));
        q1.addSubcategory("Oricând", List.of("Floral", "Lemnos", "Mosc", "Gurmand"));

        List<String> answers2 = List.of("Pădure", "Pajiște cu iarbă", "Un loc curat", "Pajiște cu lămâi", "Bucătărie", "Pajiște cu flori");
        List<Integer> images2 = List.of(R.drawable.padure, R.drawable.pajiste, R.drawable.curat, R.drawable.copaclamaie, R.drawable.bucatarie, R.drawable.floripajiste);
        Question q2 = new Question("Alege locul care miroase cel mai bine pentru tine", answers2, images2);
        q2.addSubcategory("Pădure", List.of("Piper", "Scorțișoară", "Tămâie", "Lemn santal", "Oud"));
        q2.addSubcategory("Pajiște cu iarbă", List.of("Patchouli", "Mentă", "Iarbă", "Juniper"));
        q2.addSubcategory("Un loc curat", List.of("Ambră", "Mosc alb", "Piele", "Casmir"));
        q2.addSubcategory("Pajiște cu lămâi", List.of("Portocală", "Grapefruit", "Lămâie", "Lime"));
        q2.addSubcategory("Bucătărie", List.of("Vanilie", "Caramel", "Pralină", "Bergamotă", "Coacăze negre", "Piersică"));
        q2.addSubcategory("Pajiște cu flori", List.of("Trandafir", "Iasomie", "Lavandă", "Iris"));
        questions.add(q1);
        questions.add(q2);

        return questions;
    }

    private void showQuestion(int questionIndex) {
        if (questionIndex >= questions.size()) {
            Log.e("QuizActivity", "Question index out of bounds: " + questionIndex);
            return;
        }

        Question question = questions.get(questionIndex);
        questionTextView.setText(question.getQuestionText());

        List<String> answers = question.getAnswers();
        List<Integer> imageIds = question.getImageIds();

        adapter = new QuizOptionAdapter(answers, imageIds, this);
        optionsRecyclerView.setAdapter(adapter);

        if (currentQuestionIndex == 0) {
            previousButton.setVisibility(View.INVISIBLE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }
    }

    private void showSubcategoryQuestion(String mainCategory) {
        Question q2 = questions.get(1);
        List<String> subcategories = q2.getSubcategories().get(mainCategory);
        List<Integer> subcategoryImages = getSubcategoryImagesForMainCategory(mainCategory);

        if (subcategories == null || subcategoryImages == null) {
            Log.e("QuizActivity", "Subcategories or subcategoryImages are null for main category: " + mainCategory);
            //Toast.makeText(getApplicationContext(), "Subcategories or images are null", Toast.LENGTH_LONG).show();
            return;
        }

        questionTextView.setText("Alege un miros plăcut:");
        List<String> answers3 = subcategories;
        List<Integer> images3 = subcategoryImages;
        Question q3 = new Question("Alege un miros plăcut:", answers3, images3);
        if (isSubcategoryQuestionAdded) {
            questions.set(2, q3);
        } else {
            questions.add(q3);
            isSubcategoryQuestionAdded = true;
        }

        currentQuestionIndex++;
        showQuestion(currentQuestionIndex);
    }

    private void handleSubcategoryNextButtonClick() {
        String selectedAnswer = adapter.getSelectedOption();
        if (selectedAnswer == null) {
            Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        answers.put(currentQuestionIndex, selectedAnswer);

        showRecommendations();
    }

    private List<Integer> getSubcategoryImagesForMainCategory(String mainCategory) {
        switch (mainCategory) {
            case "Pădure":
                return List.of(R.drawable.piper, R.drawable.scortisoara, R.drawable.tamaie, R.drawable.lemn_santal, R.drawable.oud);
            case "Pajiște cu iarbă":
                return List.of(R.drawable.patchouli, R.drawable.menta, R.drawable.iarba, R.drawable.juniper);
            case "Un loc curat":
                return List.of(R.drawable.ambra, R.drawable.moscalb, R.drawable.piele, R.drawable.casmir);
            case "Pajiște cu lămâi":
                return List.of(R.drawable.portocala, R.drawable.grapefruit, R.drawable.lamaie, R.drawable.yuzu);
            case "Bucătărie":
                return List.of(R.drawable.vanilie, R.drawable.caramel, R.drawable.pralina, R.drawable.bergamota, R.drawable.coacaze, R.drawable.peach);
            case "Pajiște cu flori":
                return List.of(R.drawable.trandafir, R.drawable.iasomie, R.drawable.lavanda, R.drawable.iris);
            default:
                Log.e("QuizActivity", "Unknown main category: " + mainCategory);
                return new ArrayList<>();
        }
    }

    private void saveFavoriteNote() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String favoriteNote = answers.get(currentQuestionIndex);

        Map<String, String> noteTranslations = new HashMap<>();
        noteTranslations.put("Piper", "Black Pepper");
        noteTranslations.put("Scorțișoară", "Cinnamon");
        noteTranslations.put("Tămâie", "Incense");
        noteTranslations.put("Lemn santal", "Sandal Wood");
        noteTranslations.put("Oud", "Oud");
        noteTranslations.put("Patchouli", "Patchouli");
        noteTranslations.put("Mentă", "Mint");
        noteTranslations.put("Iarbă", "Grass");
        noteTranslations.put("Juniper", "Juniper");
        noteTranslations.put("Ambră", "Amber");
        noteTranslations.put("Mosc alb", "White Musk");
        noteTranslations.put("Piele", "Leather");
        noteTranslations.put("Casmir", "Cashmere");
        noteTranslations.put("Portocală", "Orange");
        noteTranslations.put("Grapefruit", "Grapefruit");
        noteTranslations.put("Lămâie", "Lemon");
        noteTranslations.put("Lime", "Lime");
        noteTranslations.put("Vanilie", "Vanilla");
        noteTranslations.put("Caramel", "Caramel");
        noteTranslations.put("Pralină", "Praline");
        noteTranslations.put("Bergamotă", "Bergamot");
        noteTranslations.put("Coacăze negre", "Black Currant");
        noteTranslations.put("Piersică", "Peach");
        noteTranslations.put("Trandafir", "Rose");
        noteTranslations.put("Iasomie", "Jasmine");
        noteTranslations.put("Lavandă", "Lavender");
        noteTranslations.put("Iris", "Iris");

        String favoriteNoteEnglish = noteTranslations.get(favoriteNote);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (user.getListaParfFav() == null) {
                        user.setNoteFav(new ArrayList<>());
                    }
                    List<String> noteFav=new ArrayList<>();
                    noteFav.add(favoriteNoteEnglish);
                    user.setNoteFav(noteFav);
                    userRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Toast.makeText(getApplicationContext(), "Quzi adaugat la user", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }});

//        if (favoriteNoteEnglish != null) {
//            Map<String, Object> userUpdates = new HashMap<>();
//            userUpdates.put("NoteFav", favoriteNoteEnglish);
//
//            db.collection("users").document(userId)
//                    .set(userUpdates, SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> Log.d("QuizActivity", "Favorite note successfully saved!"))
//                    .addOnFailureListener(e -> Log.w("QuizActivity", "Error saving favorite note", e));
//        } else {
//            Log.w("QuizActivity", "No translation found for the selected note: " + favoriteNote);
//        }
    }




    private void showRecommendations() {
        // Implement recommendation logic based on the answers
        // This part will depend on how you fetch and display the recommendations
    }
}



