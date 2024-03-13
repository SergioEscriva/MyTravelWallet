package me.spenades.mytravelwallet.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Transaccion;

public class TransaccionController {

    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "transaccion";


    public TransaccionController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }


    public int eliminarTransaccion(Transaccion transaccion) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(transaccion.getId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }


    public long nuevaTransaccion(Transaccion transaccion) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();
        valoresParaInsertar.put("descripcion", transaccion.getDescripcion());
        valoresParaInsertar.put("importe", transaccion.getImporte());
        valoresParaInsertar.put("pagadorId", transaccion.getPagadorId());
        valoresParaInsertar.put("participantes", transaccion.getParticipantes());
        valoresParaInsertar.put("categoria", transaccion.getCategoria());
        valoresParaInsertar.put("fecha", transaccion.getFecha());
        valoresParaInsertar.put("walletId", transaccion.getWalletId());
        long transaccionId = baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
        return transaccionId;
    }


    //descripcion text, importe real, pagador int, participantes text, categoria txt, fecha int,
    // walletId int
    public int guardarCambios(Transaccion transaccionEditada) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();
        valoresParaActualizar.put("descripcion", transaccionEditada.getDescripcion());
        valoresParaActualizar.put("importe", transaccionEditada.getImporte());
        valoresParaActualizar.put("pagadorId", transaccionEditada.getPagadorId());
        valoresParaActualizar.put("participantes", transaccionEditada.getParticipantes());
        valoresParaActualizar.put("categoria", transaccionEditada.getCategoria());
        valoresParaActualizar.put("fecha", transaccionEditada.getFecha());
        valoresParaActualizar.put("walletId", transaccionEditada.getWalletId());

        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idTransaccion
        String[] argumentosParaActualizar = {String.valueOf(transaccionEditada.getId())};

        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar,
                argumentosParaActualizar);
    }


    public ArrayList<Transaccion> obtenerTransacciones(long walletIdSelected) {
        ArrayList<Transaccion> transaccions = new ArrayList<>();

        // readable porque no vamos a modificar, solamente leer
        long walletId = walletIdSelected;
        String WalletIdAConsultar = String.valueOf(walletId);

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        String query = "SELECT descripcion,importe,pagadorId,participantes,categoria,fecha," +
                "walletId,TRANSACCION.id,nombre FROM 'TRANSACCION' INNER JOIN 'USUARIO' ON " +
                "pagadorId = USUARIO.id WHERE walletId = " + WalletIdAConsultar;

        Cursor cursor = baseDeDatos.rawQuery(query, null);


        if (cursor == null) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return transaccions;
        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) return transaccions;

        // En caso de que sí haya, iteramos y vamos agregando
        do {

            String descripcionObtenidoDeBD = cursor.getString(0);
            String importeObtenidaDeBD = cursor.getString(1);
            long pagadorIdObtenidoDeBD = cursor.getLong(2);
            String participantesObtenidaDeBD = cursor.getString(3);
            String categoriaObtenidaDeBD = cursor.getString(4);
            int fechaObtenidoDeBD = cursor.getInt(5);
            long walletIdObtenidoDeBD = cursor.getInt(6);
            long idTransaccion = cursor.getLong(7);
            String nombrePagador = cursor.getString(8);

            Transaccion transaccionObtenidaDeBD = new Transaccion(descripcionObtenidoDeBD,
                    importeObtenidaDeBD, pagadorIdObtenidoDeBD, nombrePagador,
                    participantesObtenidaDeBD, categoriaObtenidaDeBD, fechaObtenidoDeBD,
                    walletIdObtenidoDeBD, idTransaccion);
            transaccions.add(transaccionObtenidaDeBD);
        } while (cursor.moveToNext());
        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        return transaccions;
    }


}