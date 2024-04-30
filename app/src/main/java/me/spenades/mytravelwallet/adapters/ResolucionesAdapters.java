package me.spenades.mytravelwallet.adapters;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        Operaciones operaciones = new Operaciones();
        String numeroString = String.valueOf(solucionFinal.get(4));
        double numeroDouble = Double.parseDouble(numeroString);
        double numeroAbs = Math.abs(numeroDouble);
        String numeroLimpio = operaciones.dosDecimalesDoubleString(numeroAbs);
        Spanned solucion;


        // Componemos la frase a Mostrar
        //nombres en negrita  https://es.stackoverflow.com/questions/109343/texto-en-negrita-desde-un-json/109404
        // en color http://www.uv.es/jac/guia/texcolej.htm
        //
        solucion = Html.fromHtml("· <b>" + solucionFinal.get(1) + "</b> debe <FONT COLOR=#E91E63>" + numeroLimpio + "€</FONT>" + " a <b>" + solucionFinal.get(3) + "</b>");


        myViewHolder.tvResolver.setText(solucion);
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
