package me.spenades.mytravelwallet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Miembro;


public class PagadoresAdapters extends RecyclerView.Adapter<PagadoresAdapters.MyViewHolder> {

    private List<Miembro> listaDeMiembros;
    private Map<Long, Double> listaDePagado;


    public PagadoresAdapters(List<Miembro> miembro, Map<Long, Double> pagado) {
        this.listaDeMiembros = miembro;
        this.listaDePagado = pagado;
    }


    public void setListaDeMiembros(List<Miembro> listaDeMiembros, Map<Long, Double> pagado) {
        this.listaDeMiembros = listaDeMiembros;
        this.listaDePagado = listaDePagado;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaWallet = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_pagador, viewGroup, false);
        return new MyViewHolder(filaWallet);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener la de nuestra lista gracias al índice i
        Miembro miembro = listaDeMiembros.get(i);

        double importes = listaDePagado.get(miembro.getUserId());

        // Obtener los datos de la lista
        String nombre = miembro.getNombre();
        double importe = importes;

        // Y poner a los TextView los datos con setText
        myViewHolder.cbPagador.setText(String.valueOf(nombre));
        myViewHolder.cbAPagado.setText("lleva pagado: " + String.valueOf(importe) + "€");
    }


    @Override
    public int getItemCount() {
        return listaDeMiembros.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cbPagador, cbPagadorId, cbAPagado;


        MyViewHolder(View itemView) {
            super(itemView);
            this.cbPagador = itemView.findViewById(R.id.cbPagador);
            this.cbAPagado = itemView.findViewById(R.id.cbAPagado);
            this.cbPagadorId = itemView.findViewById(R.id.cbPagadorId);
        }
    }
}
