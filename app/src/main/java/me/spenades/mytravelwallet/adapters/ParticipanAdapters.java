package me.spenades.mytravelwallet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.AgregarTransaccionActivity;
import me.spenades.mytravelwallet.EditarTransaccionesActivity;
import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Participante;


public class ParticipanAdapters extends RecyclerView.Adapter<ParticipanAdapters.MyViewHolder> {

    List<String> listaParticipa = new ArrayList<>();
    List<String> listaNoParticipa = new ArrayList<>();
    private List<Participante> listaDeParticipantes;
    private List<Participante> listaDeParticipan;


    public ParticipanAdapters(List<Participante> participan, List<Participante> participantes) {
        this.listaDeParticipantes = participantes;
        this.listaDeParticipan = participan;

    }


    public void setListaDeParticipan(List<Participante> listaDeParticipan,
                                     List<Participante> listaDeParticipantes) {
        this.listaDeParticipantes = listaDeParticipantes;
        this.listaDeParticipan = listaDeParticipan;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaWallet =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_participa,
                        viewGroup, false);

        return new MyViewHolder(filaWallet);

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener la de nuestra lista gracias al índice i
        Participante participan = listaDeParticipantes.get(i);

        // Obtener los datos de la lista
        long walletId = participan.getWalletId();
        long userId = participan.getUserId();

        //long participanteId = participan.getId();
        String nombre = participan.getNombre();
        boolean paticipaOno = participaExiste(userId);
        myViewHolder.cbParticipa.setText(nombre);

        // Se envía para iterar si existe como Participa en la transacción
        myViewHolder.cbParticipa.setChecked(paticipaOno);

        // Añadimos a la lista final los que participan inicialmente
        if (paticipaOno == true) {
            listaParticipa.add(String.valueOf(userId));
        }

        // Listener del Checkbox de participan.
        myViewHolder.cbParticipa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Según se clickea se añaden o borran de la lista final los que participan
                Boolean checkBoxStateParticipa = myViewHolder.cbParticipa.isChecked();
                if (checkBoxStateParticipa == true) {
                    listaParticipa.add(String.valueOf(userId));
                } else {
                    listaParticipa.remove(String.valueOf(userId));
                }
                // Enviamos el resultado a EditarTransacciones
                EditarTransaccionesActivity editarTransaccionesActivity =
                        new EditarTransaccionesActivity();
                editarTransaccionesActivity.paticipanCheck(listaParticipa);
                AgregarTransaccionActivity agregarTransaccionActivity =
                        new AgregarTransaccionActivity();
                agregarTransaccionActivity.paticipanCheck(listaParticipa);
            }

        });

    }


    @Override
    public int getItemCount() {
        return listaDeParticipantes.size();

    }


    // TODO SACAR A UNA CLASE
    public boolean participaExiste(Long participanteId) {

        // Si existe se añade el Check
        for (Participante participa : listaDeParticipan) {
            if (participa.getUserId() == participanteId) {
                return true;
            }
        }
        return false;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbParticipa;


        MyViewHolder(View itemView) {
            super(itemView);
            this.cbParticipa = (CheckBox) itemView.findViewById(R.id.cbParticipa);
        }
    }

}
