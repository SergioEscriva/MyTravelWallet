package me.spenades.mytravelwallet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.utilities.Operaciones;


public class GastosTotalesAdapters extends RecyclerView.Adapter<GastosTotalesAdapters.MyViewHolder> {


    private Map<Long, Double> listaDeGastos;
    private List<Miembro> listaDeMiembros;


    public GastosTotalesAdapters(Map<Long, Double> listaDeGastos, List<Miembro> listaDeMiembros) {
        this.listaDeGastos = listaDeGastos;
        this.listaDeMiembros = listaDeMiembros;
    }


    public void setListaDeResoluciones(Map<Long, Double> listaDeGastos, List<Miembro> listaDeMiembros) {
        this.listaDeGastos = listaDeGastos;
        this.listaDeMiembros = listaDeMiembros;
    }


    public void setImporteWallet() {
        this.listaDeGastos = listaDeGastos;
        this.listaDeMiembros = listaDeMiembros;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaResolucion = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_gastos, viewGroup, false);
        return new MyViewHolder(filaResolucion);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        List<String> gastosTotales = new ArrayList<>();
        // Obtenemos de la listaDeGastos los ids, y los iteramos con la listaDeMiembros, para obtener el nombre.
        ArrayList<String> miembrosGastos = new ArrayList<>();
        for (Long miembroIdGasto : listaDeGastos.keySet()) {
            double importe = listaDeGastos.get(miembroIdGasto);
            for (Miembro solucionFinal : listaDeMiembros) {
                long miembroId = solucionFinal.getUserId();
                if (miembroIdGasto == miembroId) {
                    String miembro = new String();
                    String importeString = new String();
                    miembro = solucionFinal.getNombre();
                    // Limpiamos decimales del importe
                    Operaciones operaciones = new Operaciones();
                    double importeLimpio = operaciones.bigDecimal(importe);
                    importeString = String.valueOf(importeLimpio);
                    //System.out.println(miembro + importeString);
                    String miembroGastoString = miembro + " tendría que pagar " + "[TOTAL]€" + "\nComo ha pagado " + importeString + "€\nTiene un " +
                            "Saldo de" +
                            " " + "[-Total]€";
                    miembrosGastos.add(miembroGastoString);
                }
            }

        }
        System.out.println(miembrosGastos.get(i) + " " + i);
        String miembroGasto = miembrosGastos.get(i);
        myViewHolder.tvGastosTotales.setText(miembroGasto);
    }


    @Override
    public int getItemCount() {
        return listaDeGastos.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvGastosTotales;


        MyViewHolder(View itemView) {
            super(itemView);
            this.tvGastosTotales = itemView.findViewById(R.id.tvGastosTotales);
        }
    }

}
