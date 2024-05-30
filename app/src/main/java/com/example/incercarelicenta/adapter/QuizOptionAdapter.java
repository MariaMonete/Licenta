// QuizOptionAdapter.java
package com.example.incercarelicenta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.incercarelicenta.R;
import com.example.incercarelicenta.interfete.RecyclerViewInterface;

public class QuizOptionAdapter extends RecyclerView.Adapter<QuizOptionAdapter.QuizOptionViewHolder> {

    private final List<String> options;
    private final List<Integer> imageIds;
    private final RecyclerViewInterface recyclerViewInterface;
    private int selectedPosition = -1;

    public QuizOptionAdapter(List<String> options, List<Integer> imageIds, RecyclerViewInterface recyclerViewInterface) {
        this.options = options;
        this.imageIds = imageIds;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public QuizOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new QuizOptionViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizOptionViewHolder holder, int position) {
        holder.optionTextView.setText(options.get(position));
        holder.optionImageView.setImageResource(imageIds.get(position));

        holder.itemView.setBackgroundResource(selectedPosition == position ? R.drawable.selected_background : 0);

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
            recyclerViewInterface.onItemClick(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public String getSelectedOption() {
        return selectedPosition != -1 ? options.get(selectedPosition) : null;
    }

    static class QuizOptionViewHolder extends RecyclerView.ViewHolder {
        ImageView optionImageView;
        TextView optionTextView;

        QuizOptionViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            optionImageView = itemView.findViewById(R.id.optionImageView);
            optionTextView = itemView.findViewById(R.id.optionTextView);
        }
    }
}
