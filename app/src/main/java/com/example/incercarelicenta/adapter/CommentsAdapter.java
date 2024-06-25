package com.example.incercarelicenta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incercarelicenta.R;
import com.example.incercarelicenta.clase.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private String currentUserId;
    private boolean isModerator=false;
    private OnCommentClickListener listener;

    public interface OnCommentClickListener {
        void onEditClick(Comment comment);
        void onDeleteClick(Comment comment);
    }

    public CommentsAdapter(List<Comment> comments, String currentUserId, boolean isModerator, OnCommentClickListener listener) {
        this.comments = comments;
        this.currentUserId = currentUserId;
        this.isModerator = isModerator;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.username.setText(comment.getUsername());
        holder.text.setText(comment.getText());

        if (comment.getUserId().equals(currentUserId) || isModerator) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.editButton.setOnClickListener(v -> listener.onEditClick(comment));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(comment));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView username, text;
        ImageButton editButton, deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.textViewUsername);
            text = itemView.findViewById(R.id.textViewText);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}


