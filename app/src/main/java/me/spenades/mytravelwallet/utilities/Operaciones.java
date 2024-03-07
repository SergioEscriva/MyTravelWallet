package me.spenades.mytravelwallet.utilities;

import java.util.List;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Transaccion;


public class Operaciones {

    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private List<Transaccion> listaDeTransaccions;

    public String sumaTransacciones(List<Transaccion> listaTransacciones) {

        float total = 0;
        for (Transaccion i : listaTransacciones) {
            total += Float.valueOf(i.getImporte());
        }
        return String.valueOf(total);
    }


}

