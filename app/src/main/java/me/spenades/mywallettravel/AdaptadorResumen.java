package me.spenades.mywallettravel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.spenades.mywallettravel.modelos.Transaccion;

public class AdaptadorResumen extends RecyclerView.Adapter<AdaptadorResumen.MyViewHolder> {

    private List<Transaccion> listaDeTransaccions;

    public void setListaDeTransacciones(List<Transaccion> listaDeTransaccions) {
        this.listaDeTransaccions = listaDeTransaccions;
    }

    public AdaptadorResumen(List<Transaccion> transaccions) {
        this.listaDeTransaccions = transaccions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaResumen = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_resumen, viewGroup, false);
        return new MyViewHolder(filaResumen);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
         Operaciones objOperaciones = new Operaciones();
         int totalTransacciones = objOperaciones.sumaTransacciones(listaDeTransaccions);
            myViewHolder.tvTotal.setText(String.valueOf(totalTransacciones) + "€");
    }

    @Override
    public int getItemCount() {
        return listaDeTransaccions.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTotal ;
        MyViewHolder(View itemView) {
            super(itemView);
            this.tvTotal = itemView.findViewById(R.id.tvTotal);

        }
    }
}
