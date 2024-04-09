package com.example.incercarelicenta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incercarelicenta.R;
import com.example.incercarelicenta.interfete.RecyclerViewInterface;
import com.example.incercarelicenta.interfete.RecyclerViewInterfaceString;

import java.util.List;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {

    private final RecyclerViewInterfaceString recyclerViewInterface;
    private List<String> noteLista;

    public NotaAdapter(RecyclerViewInterfaceString recyclerViewInterface, List<String> noteLista){
        this.recyclerViewInterface = recyclerViewInterface;
        this.noteLista = noteLista;
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.child_item,
                        parent, false);

        return new NotaViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        String nota
                = noteLista.get(position);

        holder
                .NotaItemTitle
                .setText(nota);
    }

    @Override
    public int getItemCount() {
        return noteLista.size();
    }


    class NotaViewHolder
            extends RecyclerView.ViewHolder {

        TextView NotaItemTitle;

        NotaViewHolder(View itemView,RecyclerViewInterfaceString recyclerViewInterface)
        {
            super(itemView);
            NotaItemTitle
                    = itemView.findViewById(
                    R.id.child_item_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface !=null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(NotaItemTitle.getText().toString());
                        }

                    }
                }
            });
        }
    }
}
