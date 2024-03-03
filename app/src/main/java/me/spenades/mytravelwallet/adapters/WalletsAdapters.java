package me.spenades.mytravelwallet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Wallet;

public class WalletsAdapters extends RecyclerView.Adapter<WalletsAdapters.MyViewHolder> {

    private List<Wallet> listaDeWallets;

    public WalletsAdapters(List<Wallet> wallets) {
        this.listaDeWallets = wallets;
    }

    public void setListaDeWallets(List<Wallet> listaDeWallets) {
        this.listaDeWallets = listaDeWallets;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaWallet = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_wallet, viewGroup, false);
        return new MyViewHolder(filaWallet);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener la de nuestra lista gracias al índice i
        Wallet wallet = listaDeWallets.get(i);

        // Obtener los datos de la lista
        String nombreWallet = wallet.getNombre();
        String descripcionWallet = wallet.getDescripcion();
        long walletId = wallet.getWalletId();
        int compartirWallet = wallet.getCompartir();

        // Y poner a los TextView los datos con setText
        myViewHolder.tvNombre.setText(nombreWallet);
        myViewHolder.tvDescripcion.setText(descripcionWallet);
        myViewHolder.tvWalletId.setText(String.valueOf(walletId));
        //myViewHolder.tvPropietarioId.setText(String.valueOf(propietarioIdWallet));

        boolean checkbox_Compartir = (compartirWallet == 1) ? true : false;


    }

    @Override
    public int getItemCount() {
        return listaDeWallets.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPropietarioId, tvWalletId;
        CheckBox checkbox_Compartir;

        MyViewHolder(View itemView) {
            super(itemView);
            this.tvNombre = itemView.findViewById(R.id.tvNombreWallet);
            this.tvDescripcion = itemView.findViewById(R.id.tvDescripcionWallet);
            this.tvWalletId = itemView.findViewById(R.id.tvWalletId);
            //this.tvPropietarioId = itemView.findViewById(R.id.tvPropietarioId);
            this.checkbox_Compartir = (CheckBox) itemView.findViewById(R.id.checkBox_Compartir);
        }
    }
}
