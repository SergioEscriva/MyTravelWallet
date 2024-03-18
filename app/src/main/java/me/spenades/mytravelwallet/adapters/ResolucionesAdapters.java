package me.spenades.mytravelwallet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.utilities.Operaciones;


public class ResolucionesAdapters extends RecyclerView.Adapter<ResolucionesAdapters.MyViewHolder> {


    private List<List> listaDeResoluciones;


    public ResolucionesAdapters(List<List> resoluciones) {
        this.listaDeResoluciones = resoluciones;
    }


    public void setListaDeResoluciones(List<List> listaDeResoluciones) {
        this.listaDeResoluciones = listaDeResoluciones;
    }


    public void setImporteWallet() {
        this.listaDeResoluciones = listaDeResoluciones;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaResolucion = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_resoluciones, viewGroup, false);
        return new MyViewHolder(filaResolucion);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener la resolución de la lista gracias al índice i
        List<List> solucionFinal = listaDeResoluciones.get(i);

        // Limpiamos el importe
        Operaciones numeroDecimal = new Operaciones();
        String numeroString = String.valueOf(solucionFinal.get(4));
        double numeroDouble = Double.parseDouble(numeroString);
        double numeroLimpio = numeroDecimal.bigDecimal(numeroDouble);
        double numeroDecimalConvertido = numeroLimpio;
        double importe = Math.abs(numeroDecimalConvertido);
        String solucion = new String();

        // Componemos la frase a Mostrar
        solucion = ("· " + solucionFinal.get(1) + " debe " + String.valueOf(importe) + "€" + " a " + solucionFinal.get(3));

        myViewHolder.tvResolver.setText(String.valueOf(solucion));
    }


    @Override
    public int getItemCount() {
        return listaDeResoluciones.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvResolver;


        MyViewHolder(View itemView) {
            super(itemView);
            this.tvResolver = itemView.findViewById(R.id.tvResolver);
        }
    }

}
