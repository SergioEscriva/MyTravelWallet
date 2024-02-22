package me.spenades.mywallettravel.utilities;

import java.util.List;

import me.spenades.mywallettravel.models.Transaccion;

public class Operaciones {
        private List<Transaccion> listaDeTransaccions;
        public int sumaTransacciones(List<Transaccion> listaTransacciones){

            int total = 0;
            for (Transaccion i : listaTransacciones){
                total += i.getImporte();
            }
            return total;
        }
}

