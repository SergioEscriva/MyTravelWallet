package me.spenades.mywallettravel.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.spenades.mywallettravel.R;
import me.spenades.mywallettravel.models.Usuario;


public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.MyViewHolder> {

    private List<Usuario> listaDeUsuarios;

    public AdaptadorUsuarios(List<Usuario> usuario) {
        this.listaDeUsuarios = usuario;
    }

    public void setListaDeUsuarios(List<Usuario> listaDeUsuarios) {
        this.listaDeUsuarios = listaDeUsuarios;
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
        Usuario usuario = listaDeUsuarios.get(i);

        // Obtener los datos de la lista
        String nombreUsuario = usuario.getNombre();
        String apodoUsuario = usuario.getApodo();
        Long UsuarioId = usuario.getId();

        // Y poner a los TextView los datos con setText
        myViewHolder.tvUsuario.setText(nombreUsuario);
        // myViewHolder.tvApodo.setText(apodoUsuario);

    }

    @Override
    public int getItemCount() {
        return listaDeUsuarios.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario;

        MyViewHolder(View itemView) {
            super(itemView);
            // this.tvUsuario = itemView.findViewById(R.id.tvUsuario);
        }
    }
}
