package me.spenades.mytravelwallet.utilities;

import java.util.List;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Transaccion;


public class Operaciones {

    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private List<Transaccion> listaTransaccions;


    public String sumaTransacciones(List<Transaccion> listaTransacciones) {
        this.listaTransaccions = listaTransacciones;
        float total = 0;
        for (Transaccion i : listaTransacciones) {
            total += Float.valueOf(i.getImporte());
        }
        return String.valueOf(total);
    }


    public String sumaTransaccionesWallet(Long walletId) {
        /*
        float total = 0;
        for (Transaccion i : listaTransaccions) {
            total += Float.valueOf(i.getImporte());
        }
        return String.valueOf(total);

         */
        return String.valueOf(100);
    }


}

