package me.spenades.mywallettravel.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mywallettravel.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mywallettravel.modelos.Transaccion;
import me.spenades.mywallettravel.modelos.Wallet;

public class TransaccionController {
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "transacciones";

    public TransaccionController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }


    public int eliminarTransaccion(Transaccion transaccion) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(transaccion.getTransaccionId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }

    public long nuevaTransaccion(Transaccion transaccion) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();
        valoresParaInsertar.put("descripcion", transaccion.getDescripcion());
        valoresParaInsertar.put("importe", transaccion.getImporte());
        valoresParaInsertar.put("pagador", transaccion.getPagador());
        valoresParaInsertar.put("participantes", transaccion.getParticipantes());
        valoresParaInsertar.put("categoria", transaccion.getCategoria());
        valoresParaInsertar.put("fecha", transaccion.getFecha());
        valoresParaInsertar.put("walletId", transaccion.getWalletId()+1);

        return baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
    }

    public int guardarCambios(Transaccion transaccionEditada) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();

        valoresParaActualizar.put("descripcion", transaccionEditada.getDescripcion());
        valoresParaActualizar.put("importe", transaccionEditada.getImporte());
        valoresParaActualizar.put("pagador", transaccionEditada.getPagador());
        valoresParaActualizar.put("participantes", transaccionEditada.getParticipantes());
        valoresParaActualizar.put("categoria", transaccionEditada.getCategoria());
        valoresParaActualizar.put("fecha", transaccionEditada.getFecha());
        valoresParaActualizar.put("walletId", transaccionEditada.getWalletId());
        System.out.println("QQQQQQQQQQ" + valoresParaActualizar);
        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idTransaccion
        String[] argumentosParaActualizar = {String.valueOf(transaccionEditada.getTransaccionId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }

    public ArrayList<Transaccion> obtenerTransacciones(int walletIdSelected) {
        ArrayList<Transaccion> transaccions = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        long walletId = walletIdSelected;
        String WalletIdAConsultar = "walletId = " + String.valueOf(walletId +1);
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        // SELECT id del Wallet
        String[] columnasAConsultar = {"descripcion", "importe", "pagador", "participantes", "categoria", "fecha", "walletId", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA, // Transacciones
                columnasAConsultar,
                WalletIdAConsultar,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return transaccions;

        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return transaccions;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos

            String descripcionObtenidoDeBD = cursor.getString(0);
            int importeObtenidaDeBD = cursor.getInt(1);
            String pagadorObtenidoDeBD = cursor.getString(2);
            int participantesObtenidaDeBD = cursor.getInt(3);
            String categoriaObtenidaDeBD = cursor.getString(4);
            int fechaObtenidoDeBD = cursor.getInt(5);
            int walletIdObtenidoDeBD = cursor.getInt(6);
            long idTransaccion = cursor.getLong(7);

            Transaccion transaccionObtenidaDeBD = new Transaccion(descripcionObtenidoDeBD, importeObtenidaDeBD, pagadorObtenidoDeBD, participantesObtenidaDeBD, categoriaObtenidaDeBD, fechaObtenidoDeBD, walletIdObtenidoDeBD, idTransaccion);
            transaccions.add(transaccionObtenidaDeBD);
        } while (cursor.moveToNext());
            System.out.println(transaccions);
        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        return transaccions;
    }
}