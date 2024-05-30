package com.example.incercarelicenta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incercarelicenta.R;
import com.example.incercarelicenta.clase.Categorie;
import com.example.incercarelicenta.interfete.RecyclerViewInterfaceString;

import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ParentViewHolder> {

    private final RecyclerViewInterfaceString recyclerViewInterface;
    private RecyclerView.RecycledViewPool
            viewPool
            = new RecyclerView
            .RecycledViewPool();
    private List<Categorie> itemList;
    private final Map<String, Integer> categoryImageMap;

    public CategoryAdapter(RecyclerViewInterfaceString recyclerViewInterface, List<Categorie> itemList, Map<String, Integer> categoryImageMap)
    {
        this.recyclerViewInterface = recyclerViewInterface;
        this.itemList = itemList;
        this.categoryImageMap = categoryImageMap;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.parent_item,
                        parent, false);

        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {

        Categorie categorii = itemList.get(position);



        holder
                .ParentItemTitle
                .setText(categorii.getParentItemTitle());

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(
                holder
                        .ChildRecyclerView
                        .getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);


        layoutManager
                .setInitialPrefetchItemCount(
                        categorii
                                .getNoteList()
                                .size());


        NotaAdapter notaAdapter
                = new NotaAdapter(
                recyclerViewInterface, categorii
                .getNoteList());
        holder
                .ChildRecyclerView
                .setLayoutManager(layoutManager);
        holder
                .ChildRecyclerView
                .setAdapter(notaAdapter);
        holder
                .ChildRecyclerView
                .setRecycledViewPool(viewPool);

        if(categoryImageMap.containsKey(categorii.getParentItemTitle())){
            int imageResource = categoryImageMap.get(categorii.getParentItemTitle());
            holder.categoryImageView.setImageResource(imageResource);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ParentViewHolder
            extends RecyclerView.ViewHolder {

        private final ImageView categoryImageView;
        private TextView ParentItemTitle;
        private RecyclerView ChildRecyclerView;

        ParentViewHolder(final View itemView)
        {
            super(itemView);

            categoryImageView = itemView.findViewById(R.id.categoryImageViewIncercare);

            ParentItemTitle
                    = itemView
                    .findViewById(
                            R.id.parent_item_title_incercare);
            ChildRecyclerView
                    = itemView
                    .findViewById(
                            R.id.child_recyclerview_incercare);
        }
    }
}
