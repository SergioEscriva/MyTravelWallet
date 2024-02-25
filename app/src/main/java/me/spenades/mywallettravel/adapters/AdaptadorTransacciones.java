package me.spenades.mywallettravel.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import me.spenades.mywallettravel.R;
import me.spenades.mywallettravel.models.Transaccion;

public class AdaptadorTransacciones extends RecyclerView.Adapter<AdaptadorTransacciones.MyViewHolder> {

    private List<Transaccion> listaDeTransaccions;
    public void setListaDeTransacciones(List<Transaccion> listaDeTransaccions) {
        this.listaDeTransaccions = listaDeTransaccions;
    }

    public AdaptadorTransacciones(List<Transaccion> transaccions) {
        this.listaDeTransaccions = transaccions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaTransaccion = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_transaction, viewGroup, false);

        return new MyViewHolder(filaTransaccion);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener  de nuestra lista gracias al índice i
        Transaccion transaccion = listaDeTransaccions.get(i);

        // Obtener los datos de la lista

        String descripcionTransaccion = transaccion.getDescripcion();
        String importeTransaccion = transaccion.getImporte();
        String pagadorTransaccion = transaccion.getPagador();
        String participantesTransaccion = transaccion.getParticipantes();
        String categoriaTransaccion = transaccion.getCategoria();
        int fechaTransaccion = transaccion.getFecha();

        // Y poner a los TextView los datos con setText

        myViewHolder.tvDescripcion.setText(descripcionTransaccion);
        myViewHolder.tvImporte.setText(String.valueOf(importeTransaccion)+ "€") ;
        myViewHolder.tvPagador.setText(pagadorTransaccion);
        myViewHolder.tvParticipantes.setText(String.valueOf(participantesTransaccion));
        myViewHolder.tvCategoria.setText(String.valueOf(categoriaTransaccion));
        myViewHolder.tvFecha.setText(String.valueOf(fechaTransaccion));
    }

    @Override
    public int getItemCount() {
        return listaDeTransaccions.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcion, tvImporte, tvPagador, tvParticipantes, tvCategoria, tvFecha;

        MyViewHolder(View itemView) {
            super(itemView);
            this.tvDescripcion = itemView.findViewById(R.id.tvDescripcionWallet);
            this.tvImporte = itemView.findViewById(R.id.tvImporte);
            this.tvPagador = itemView.findViewById(R.id.tvPagador);
            this.tvParticipantes = itemView.findViewById(R.id.tvParticipantes);
            this.tvCategoria = itemView.findViewById(R.id.tvCategoria);
            this.tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }

}
