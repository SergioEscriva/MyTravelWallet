package me.spenades.mytravelwallet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Transaccion;

public class TransaccionesAdapters extends RecyclerView.Adapter<TransaccionesAdapters.MyViewHolder> {
    private List<Transaccion> listaDeTransaccions;

    public TransaccionesAdapters(List<Transaccion> transaccions) {
        this.listaDeTransaccions = transaccions;
    }

    public void setListaDeTransacciones(List<Transaccion> listaDeTransaccions) {
        this.listaDeTransaccions = listaDeTransaccions;
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
        long pagadorIdTransaccion = transaccion.getPagadorId();
        String nombrePagadorTransaccion = transaccion.getNombrePagador();
        //String participantesTransaccion = transaccion.getParticipantes();
        String categoriaTransaccion = transaccion.getCategoria();
        int fechaTransaccion = transaccion.getFecha();
        long transaccionId = transaccion.getId();


        // Y poner a los TextView los datos con setText
        myViewHolder.tvDescripcion.setText(descripcionTransaccion);
        myViewHolder.tvImporte.setText(String.valueOf(importeTransaccion) + "€");
        //myViewHolder.tvPagador.setText(String.valueOf(pagadorTransaccion));

        myViewHolder.tvPagadorId.setText(String.valueOf(pagadorIdTransaccion));
        myViewHolder.tvNombrePagador.setText(String.valueOf(nombrePagadorTransaccion));
        //myViewHolder.tvParticipantes.setText(String.valueOf(participantesTransaccion));
        myViewHolder.tvCategoria.setText(String.valueOf(categoriaTransaccion));
        myViewHolder.tvFecha.setText(String.valueOf(fechaTransaccion));
        myViewHolder.tvTransaccionId.setText(String.valueOf(transaccionId));
    }

    @Override
    public int getItemCount() {
        return listaDeTransaccions.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcion, tvImporte, tvNombrePagador, tvPagadorId, tvParticipantes, tvCategoria, tvFecha, tvTransaccionId;

        MyViewHolder(View itemView) {
            super(itemView);
            this.tvDescripcion = itemView.findViewById(R.id.tvDescripcionWallet);
            this.tvImporte = itemView.findViewById(R.id.tvImporte);
            this.tvNombrePagador = itemView.findViewById(R.id.tvNombrePagador);
            this.tvPagadorId = itemView.findViewById(R.id.tvPagadorId);
            //this.tvParticipantes = itemView.findViewById(R.id.tvParticipantes);
            this.tvCategoria = itemView.findViewById(R.id.tvCategoria);
            this.tvFecha = itemView.findViewById(R.id.tvFecha);
            this.tvTransaccionId = itemView.findViewById(R.id.tvTransaccionId);
        }
    }

}
