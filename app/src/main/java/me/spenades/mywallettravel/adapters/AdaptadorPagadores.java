package me.spenades.mywallettravel.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import me.spenades.mywallettravel.R;
import me.spenades.mywallettravel.models.Participante;


public class AdaptadorPagadores extends RecyclerView.Adapter<AdaptadorPagadores.MyViewHolder> {

    private List<Participante> listaDeParticipantes;

    public void setListaDeParticipantes(List<Participante> listaDeParticipantes) {
        this.listaDeParticipantes = listaDeParticipantes;
    }

    public AdaptadorPagadores(List<Participante> participante) {
        this.listaDeParticipantes = participante;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaWallet = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_participantes, viewGroup, false);
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
        myViewHolder.tvParticipante.setText(String.valueOf(nombre));
       // myViewHolder.tvApodo.setText(apodoParticipante);

    }

    @Override
    public int getItemCount() {
        return listaDeParticipantes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvParticipante;
        CheckBox checkbox_Compartir;
        MyViewHolder(View itemView) {
            super(itemView);
            this.tvParticipante = itemView.findViewById(R.id.tvParticipante);
        }
    }
}
