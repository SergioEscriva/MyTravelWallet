package me.spenades.mytravelwallet.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Miembro;


public class GastosTotalesAdapters extends RecyclerView.Adapter<GastosTotalesAdapters.MyViewHolder> {


    private ArrayList<String> listaDeGastos;
    private List<Miembro> listaDeMiembros;


    public GastosTotalesAdapters(ArrayList<String> listaDeGastos, List<Miembro> listaDeMiembros) {
        this.listaDeGastos = listaDeGastos;
        this.listaDeMiembros = listaDeMiembros;
    }


    public void setListaDeResoluciones(ArrayList<String> listaDeGastos, List<Miembro> listaDeMiembros) {
        this.listaDeGastos = listaDeGastos;
        this.listaDeMiembros = listaDeMiembros;
    }


    public void setImporteWallet() {
        this.listaDeGastos = listaDeGastos;
        this.listaDeMiembros = listaDeMiembros;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaResolucion = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_gastos, viewGroup, false);
        return new MyViewHolder(filaResolucion);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String miembroGasto = listaDeGastos.get(i);

        myViewHolder.tvGastosTotales.setText(miembroGasto);
    }


    @Override
    public int getItemCount() {
        return listaDeGastos.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvGastosTotales;


        MyViewHolder(View itemView) {
            super(itemView);
            this.tvGastosTotales = itemView.findViewById(R.id.tvGastosTotales);
        }
    }

}
