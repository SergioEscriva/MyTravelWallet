package me.spenades.mytravelwallet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.activities.AgregarTransaccionActivity;
import me.spenades.mytravelwallet.activities.EditarTransaccionesActivity;
import me.spenades.mytravelwallet.models.Miembro;


public class ParticipanAdapters extends RecyclerView.Adapter<ParticipanAdapters.MyViewHolder> {

    List<String> listaParticipa = new ArrayList<>();

    private List<Miembro> listaDeMiembros;
    private List<Miembro> listaDeParticipan;
    private Boolean addEdit;


    public ParticipanAdapters(List<Miembro> participan, List<Miembro> miembros, Boolean addEdit) {
        this.listaDeMiembros = miembros;
        this.listaDeParticipan = participan;
        this.addEdit = addEdit;

    }


    public void setListaDeParticipan(List<Miembro> listaDeParticipan,
                                     List<Miembro> listaDeMiembros, Boolean addEdit) {
        this.listaDeMiembros = listaDeMiembros;
        this.listaDeParticipan = listaDeParticipan;
        this.addEdit = addEdit;
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
        Miembro participan = listaDeMiembros.get(i);

        // Obtener los datos de la lista
        long userId = participan.getUserId();
        String nombre = participan.getNombre();
        boolean paticipaOno = participaExiste(userId);
        myViewHolder.cbParticipa.setText(nombre);

        // Se envía para iterar si existe como Participa en la transacción
        myViewHolder.cbParticipa.setChecked(paticipaOno);

        // Añadimos a la lista final los que participan inicialmente
        if (paticipaOno == true) {
            listaParticipa.add(String.valueOf(userId));
        }

        // Enviamos a Agregar o Editar Transacción según sea la llamada.

        if (addEdit == true) {

            //Enviamos el resultado a Agregar
            AgregarTransaccionActivity agregarTransaccionActivity =
                    new AgregarTransaccionActivity();
            agregarTransaccionActivity.paticipanCheck(listaParticipa);
        } else {

            //Enviamos el resultado a EditarTransacciones
            EditarTransaccionesActivity editarTransaccionesActivity =
                    new EditarTransaccionesActivity();
            editarTransaccionesActivity.paticipanCheck(listaParticipa);
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

                    // no deja que se quede sin paticipantes.
                    if (listaParticipa.size() < 2) {
                        myViewHolder.cbParticipa.setChecked(true);
                    } else
                        listaParticipa.remove(String.valueOf(userId));
                }


                if (addEdit == true) {

                    //Enviamos el resultado a Agregar
                    AgregarTransaccionActivity agregarTransaccionActivity =
                            new AgregarTransaccionActivity();
                    agregarTransaccionActivity.paticipanCheck(listaParticipa);
                } else {

                    //Enviamos el resultado a EditarTransacciones
                    EditarTransaccionesActivity editarTransaccionesActivity =
                            new EditarTransaccionesActivity();
                    editarTransaccionesActivity.paticipanCheck(listaParticipa);
                }
            }

        });

    }


    @Override
    public int getItemCount() {
        return listaDeMiembros.size();

    }


    public boolean participaExiste(Long miembroId) {

        // Si existe se añade el Check
        for (Miembro participa : listaDeParticipan) {
            if (participa.getUserId() == miembroId) {
                return true;
            }
        }
        return false;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbParticipa;
        TextView tvDivison, etTransaccionImporte;

        MyViewHolder(View itemView) {
            super(itemView);
            this.cbParticipa = itemView.findViewById(R.id.cbParticipa);
            this.tvDivison = itemView.findViewById(R.id.tvDivision);
            this.etTransaccionImporte = itemView.findViewById(R.id.etTransaccionImporte);
        }
    }

}
