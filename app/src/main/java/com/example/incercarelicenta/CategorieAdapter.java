package com.example.incercarelicenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.incercarelicenta.clase.CategorieNote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategorieAdapter extends RecyclerView.Adapter<CategorieAdapter.CategorieViewHolder> {

    private final List<CategorieNote> categoryList;
    private final Map<CategorieNote, List<String>> allCategories;
    private final Map<CategorieNote, Integer> categoryImageMap;

    public CategorieAdapter(Map<CategorieNote, List<String>> allCategories, Map<CategorieNote, Integer> categoryImageMap) {
        this.allCategories = allCategories;
        this.categoryList = new ArrayList<>(allCategories.keySet());
        this.categoryImageMap = categoryImageMap;
    }

    @NonNull
    @Override
    public CategorieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categorie, parent, false);
        return new CategorieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategorieViewHolder holder, int position) {
        CategorieNote category = categoryList.get(position);
        holder.bind(category, allCategories.get(category));
        if (categoryImageMap.containsKey(category)) {
            int imageResource = categoryImageMap.get(category);
            holder.categoryImageView.setImageResource(imageResource);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategorieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryImageView;
        private final TextView categoryNameTextView;
        private final TextView categoryNotesTextView;

        public CategorieViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImageView = itemView.findViewById(R.id.categoryImageView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            categoryNotesTextView = itemView.findViewById(R.id.categoryNotesTextView);
        }

        public void bind(CategorieNote category, List<String> notes) {
            // Setează imaginea, numele și lista de note a categoriei în ViewHolder
            // Aici trebuie să setați resursele și textul corespunzător
            // categoryImageView.setImageResource(R.drawable.ic_category_floral);
            categoryNameTextView.setText(category.toString());
            StringBuilder notesBuilder = new StringBuilder();
            for (String note : notes) {
                notesBuilder.append(note).append("\n");
            }
            // Eliminăm virgula și spațiul final
            String notesText = notesBuilder.substring(0, notesBuilder.length() - 2);
            categoryNotesTextView.setText(notesText);
        }
    }
}