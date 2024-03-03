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
import me.spenades.mytravelwallet.utilities.Operaciones;

public class ResumenAdapters extends RecyclerView.Adapter<ResumenAdapters.MyViewHolder> {

    private List<Transaccion> listaDeTransaccions;
    private long walletId;

    public ResumenAdapters(List<Transaccion> transaccions, long walletId) {
        this.listaDeTransaccions = transaccions;
        this.walletId = walletId;
    }

    public void setListaDeTransacciones(List<Transaccion> listaDeTransaccions, long walletId) {
        this.listaDeTransaccions = listaDeTransaccions;
        this.walletId = walletId;
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
        String totalTransacciones = objOperaciones.sumaTransacciones(listaDeTransaccions);
        myViewHolder.tvTotal.setText(String.valueOf(totalTransacciones) + "€");
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTotal;

        MyViewHolder(View itemView) {
            super(itemView);
            this.tvTotal = itemView.findViewById(R.id.tvTotal);
            //tvTotal = itemView.findViewById(R.id.tvNombreWallet);
        }
    }
}
