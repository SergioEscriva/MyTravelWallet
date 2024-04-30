package me.spenades.mytravelwallet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Miembro;


public class MiembrosAdapters extends RecyclerView.Adapter<MiembrosAdapters.MyViewHolder> {

    private List<Miembro> listaDeMiembros;


    public MiembrosAdapters(List<Miembro> miembro) {
        this.listaDeMiembros = miembro;
    }


    public void setListaDeMiembros(List<Miembro> listaDeMiembros) {
        this.listaDeMiembros = listaDeMiembros;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaWallet = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_miembro, viewGroup, false);
        return new MyViewHolder(filaWallet);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener la de nuestra lista gracias al índice i
        Miembro miembro = listaDeMiembros.get(i);

        // Obtener los datos de la lista
        String nombre = miembro.getNombre();

        // Y poner a los TextView los datos con setText
        myViewHolder.tvMiembro.setText(String.valueOf(nombre));


    }


    @Override
    public int getItemCount() {
        return listaDeMiembros.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMiembro, tvMiembroId;


        MyViewHolder(View itemView) {
            super(itemView);
            this.tvMiembro = itemView.findViewById(R.id.cbMiembro);
            this.tvMiembroId = itemView.findViewById(R.id.cbMiembroId);

        }
    }
}
