package me.spenades.mywallettravel.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import me.spenades.mywallettravel.R;
import me.spenades.mywallettravel.models.Participante;


public class AdaptadorParticipan extends RecyclerView.Adapter<AdaptadorParticipan.MyViewHolder> {

    private List<Participante> listaDeParticipantes;

    public AdaptadorParticipan(List<Participante> participante) {
        this.listaDeParticipantes = participante;
    }

    public void setListaDeParticipantes(List<Participante> listaDeParticipantes) {
        this.listaDeParticipantes = listaDeParticipantes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaWallet = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_participan, viewGroup, false);
        return new MyViewHolder(filaWallet);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener la de nuestra lista gracias al índice i
        Participante participante = listaDeParticipantes.get(i);

        // Obtener los datos de la lista
        long walletId = participante.getWalletId();
        long userId = participante.getUserId();
        long ParticipanteId = participante.getId();
        String nombre = participante.getNombre();

        // Y poner a los TextView los datos con setText
        myViewHolder.cbParticipante.setText(String.valueOf(nombre));
        // myViewHolder.tvApodo.setText(apodoParticipante);

    }

    @Override
    public int getItemCount() {
        return listaDeParticipantes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbParticipante;

        MyViewHolder(View itemView) {
            super(itemView);
            this.cbParticipante = (CheckBox) itemView.findViewById(R.id.cbParticipante);
            // this.cbParticipante = itemView.findViewById(R.id.cbParticipante);
        }
    }
}
