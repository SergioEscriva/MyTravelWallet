package me.spenades.mytravelwallet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Usuario;


public class UsuariosAdapters extends RecyclerView.Adapter<UsuariosAdapters.MyViewHolder> {

    private List<Usuario> listaDeUsuarios;

    public UsuariosAdapters(List<Usuario> usuario) {
        this.listaDeUsuarios = usuario;
    }

    public void setListaDeUsuarios(List<Usuario> listaDeUsuarios) {
        this.listaDeUsuarios = listaDeUsuarios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaWallet = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_participante, viewGroup, false);
        return new MyViewHolder(filaWallet);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener la de nuestra lista gracias al índice i
        Usuario usuario = listaDeUsuarios.get(i);

        // Obtener los datos de la lista
        String nombreUsuario = usuario.getNombre();
        String apodoUsuario = usuario.getApodo();
        long usuarioId = usuario.getId();

        // Y poner a los TextView los datos con setText
        myViewHolder.tvUsuario.setText(nombreUsuario);
        myViewHolder.tvUsuarioId.setText(String.valueOf(usuarioId));

    }

    @Override
    public int getItemCount() {
        return listaDeUsuarios.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvUsuarioId;

        MyViewHolder(View itemView) {
            super(itemView);
            //this.tvUsuario = itemView.findViewById(R.id.tvUsuario);
            //this.tvUsuarioId = itemView.findViewById(R.id.tvUsuarioId);
        }
    }
}
