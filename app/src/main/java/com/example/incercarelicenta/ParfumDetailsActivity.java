package com.example.incercarelicenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.incercarelicenta.clase.Parfum;

public class ParfumDetailsActivity extends AppCompatActivity {

    private ImageView imageViewParfum;
    private TextView textViewParfumName, textViewParfumBrand, textViewParfumDescription, textViewParfumNotes;

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

        // Get data from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Parfum parfum = bundle.getParcelable("parfum");

            if (parfum != null) {
                // Populate views with parfum data
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
}
