package me.spenades.mywallettravel.utilities;

import java.util.List;

import me.spenades.mywallettravel.models.Transaccion;

public class Operaciones {
        private List<Transaccion> listaDeTransaccions;
        public String sumaTransacciones(List<Transaccion> listaTransacciones){

            float total = 0;
            for (Transaccion i : listaTransacciones){
                total += Float.valueOf(i.getImporte());
            }
            return String.valueOf(total);
        }
}

