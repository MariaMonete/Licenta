package com.example.incercarelicenta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incercarelicenta.R;
import com.example.incercarelicenta.clase.Parfum;
import com.example.incercarelicenta.interfete.RecyclerViewInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ParfumAdapter extends RecyclerView.Adapter<ParfumAdapter.ParfumViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private List<Parfum> parfumList;
    private Context context;

    public void setData(List<Parfum> parfumList) {
        this.parfumList = parfumList;
    }

    public ParfumAdapter(List<Parfum> parfumList, Context context,RecyclerViewInterface recyclerViewInterface) {
        this.parfumList = parfumList;
        this.context = context;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public ParfumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parfum, parent, false);
        return new ParfumViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ParfumViewHolder holder, int position) {
        Parfum parfum = parfumList.get(position);

        Picasso.get().load(parfum.getImgUrl()).placeholder(R.drawable.placeholder_image).into(holder.imageView);

        holder.textViewName.setText(parfum.getName());
        holder.textViewBrand.setText(parfum.getBrand());
    }

    @Override
    public int getItemCount() {
        return parfumList.size();
    }

    public static class ParfumViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName, textViewBrand;

        public ParfumViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewBrand = itemView.findViewById(R.id.textViewBrand);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }

                    }
                }
            });

        }


    }
}
