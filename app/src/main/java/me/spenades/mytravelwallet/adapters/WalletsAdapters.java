package me.spenades.mytravelwallet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Wallet;
import me.spenades.mytravelwallet.utilities.DeudaUtility;
import me.spenades.mytravelwallet.utilities.Operaciones;

public class WalletsAdapters extends RecyclerView.Adapter<WalletsAdapters.MyViewHolder> {

    private List<Wallet> listaDeWallets;
    private ArrayList<Map> listaDeImportes;


    public WalletsAdapters(List<Wallet> wallets) {
        this.listaDeWallets = wallets;
    }


    public void setListaDeWallets(List<Wallet> listaDeWallets, ArrayList<Map> listaDeImportes) {
        this.listaDeWallets = listaDeWallets;
        this.listaDeImportes = listaDeImportes;
    }


    public void setImporteWallet() {
        this.listaDeWallets = listaDeWallets;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaWallet =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_wallet,
                        viewGroup, false);
        return new MyViewHolder(filaWallet);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Operaciones operaciones = new Operaciones();
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
        myViewHolder.tvImporteW.setText(String.valueOf("0.0€"));


        // importes Totales de los Wallets
        Map importe = listaDeImportes.get(0);
        String importeSumado = String.valueOf(importe.get(walletId));

        // Si no tiene transacciones añade 0.0
        if (importeSumado == "null") importeSumado = "0.0";
        String importeSumadoLimpio = operaciones.dosDecimalesStringString(importeSumado);
        DeudaUtility deudaUtility = new DeudaUtility();
        String importeTotal = deudaUtility.importeFormateado(importeSumadoLimpio);
        myViewHolder.tvImporteW.setText(String.format(importeTotal));
    }


    @Override
    public int getItemCount() {
        return listaDeWallets.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvDescripcion, tvWalletId, tvImporteW;

        MyViewHolder(View itemView) {
            super(itemView);
            this.tvNombre = itemView.findViewById(R.id.tvNombreWallet);
            this.tvDescripcion = itemView.findViewById(R.id.tvDescripcionWallet);
            this.tvWalletId = itemView.findViewById(R.id.tvWalletId);
            this.tvImporteW = itemView.findViewById(R.id.tvImporteW);
        }
    }
}
