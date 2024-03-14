package me.spenades.mytravelwallet.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Wallet;

public class WalletController {

    public ArrayList<Map> resultadolist;
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "wallet";


    public WalletController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }


    public int eliminarWallet(long walletId) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(walletId)};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }


    public long nuevoWallet(Wallet wallet) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();

        valoresParaInsertar.put("nombre", wallet.getNombre());
        valoresParaInsertar.put("descripcion", wallet.getDescripcion());
        valoresParaInsertar.put("propietario", wallet.getPropietarioId());
        valoresParaInsertar.put("compartir", wallet.getCompartir());

        return baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
    }


    public int guardarCambios(Wallet walletEditado) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();

        valoresParaActualizar.put("nombre", walletEditado.getNombre());
        valoresParaActualizar.put("descripcion", walletEditado.getDescripcion());
        valoresParaActualizar.put("propietario", walletEditado.getPropietarioId());
        valoresParaActualizar.put("compartir", walletEditado.getCompartir());

        // where id...
        String walletParaActualizar = "id = ?";
        // ... = idWallet

        String[] argumentosParaActualizar = {String.valueOf(walletEditado.getWalletId())};
        return baseDeDatos.update(NOMBRE_TABLA,
                valoresParaActualizar,
                walletParaActualizar,
                argumentosParaActualizar);
    }


    public ArrayList<Map> obtenerWalletsImporte() {
        ArrayList<Wallet> wallets = new ArrayList<>();
        ArrayList<ArrayList> importeTransaccion = new ArrayList<>();
        Map<Long, Double> resultado = new HashMap<>();
        ArrayList<Map> resultadoList = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // String query = "SELECT wallet_id,usuario_id,nombre FROM 'WALLET_USUARIO' INNER JOIN 'USUARIO' ON usuario_id = USUARIO.id WHERE wallet_id
        // = " + walletIdString;

        String query = "SELECT nombre,wallet.descripcion,propietario,compartir,wallet.id,importe FROM 'WALLET' INNER JOIN 'transaccion' ON WALLET" +
                ".id = transaccion.walletId";
        Cursor cursor = baseDeDatos.rawQuery(query, null);

        if (cursor == null) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            System.out.println("ERROR NULL");


        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) {
            System.out.println("ERROR SIN DATOS");
        }


        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos

            String nombreObtenidoDeBD = cursor.getString(0);
            String descripcionObtenidoDeBD = cursor.getString(1);
            long propietarioObtenidaDeBD = cursor.getLong(2);
            int compartirObtenidaDeBD = cursor.getInt(3);
            long walletIdObtenidoDeBD = cursor.getLong(4);
            double importeWalletId = cursor.getDouble(5);


            Wallet walletObtenidaDeBD = new Wallet(nombreObtenidoDeBD, descripcionObtenidoDeBD, propietarioObtenidaDeBD, compartirObtenidaDeBD,
                    walletIdObtenidoDeBD);
            wallets.add(walletObtenidaDeBD);

            // Creamos un diccionario con los wallets repetidos tantas veces como importe de cada transacción
            ArrayList<String> listaImporte = new ArrayList<>();
            listaImporte.add(String.valueOf(walletIdObtenidoDeBD));
            listaImporte.add(String.valueOf(importeWalletId));
            importeTransaccion.add(listaImporte);

        } while (cursor.moveToNext());

        // iteramos los wallets y creamos una lista con cada wallet y el total de la suma de sus transacciones
        ArrayList<Wallet> walletsImporte = new ArrayList<>();
        for (Wallet wallet : wallets) {
            long walletId = wallet.getWalletId();
            double importeViejo = 0.0;
            for (ArrayList lista : importeTransaccion) {
                long walletIdTransaccion = Long.parseLong(lista.get(0).toString());
                if (walletId == walletIdTransaccion) {
                    double importeUnaTransaccion = Double.valueOf(lista.get(1).toString());
                    importeViejo += importeUnaTransaccion;
                }
                resultado.put(walletId, importeViejo);
            }

            resultadoList.add(resultado);
        }

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();

        /*
        // Unimos el Arraylist de wallets y el Map de importe en un Arraylist.
        ArrayList<ArrayList> walletsImportesTotales = new ArrayList<>();
        walletsImportesTotales.add(wallets);
        walletsImportesTotales.add(resultadoList);

         */


        return resultadoList;
    }


    public ArrayList<Wallet> obtenerWallets() {
        ArrayList<Wallet> wallets = new ArrayList<>();
        ArrayList<ArrayList> importeTransaccion = new ArrayList<>();
        Map<Long, Double> resultado = new HashMap<>();
        ArrayList<Map> resultadoList = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        String[] columnasAConsultar = {"nombre", "descripcion", "propietario", "compartir", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,//from wallets
                columnasAConsultar,
                null,
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
            return wallets;

        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) return wallets;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos

            String nombreObtenidoDeBD = cursor.getString(0);
            String descripcionObtenidoDeBD = cursor.getString(1);
            long propietarioObtenidaDeBD = cursor.getLong(2);
            int compartirObtenidaDeBD = cursor.getInt(3);
            long walletIdObtenidoDeBD = cursor.getLong(4);


            Wallet walletObtenidaDeBD = new Wallet(nombreObtenidoDeBD, descripcionObtenidoDeBD, propietarioObtenidaDeBD, compartirObtenidaDeBD,
                    walletIdObtenidoDeBD);
            wallets.add(walletObtenidaDeBD);

        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();

        return wallets;
    }

}