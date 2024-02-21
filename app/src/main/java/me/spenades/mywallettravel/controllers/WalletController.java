package me.spenades.mywallettravel.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mywallettravel.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mywallettravel.modelos.Wallet;

public class WalletController {
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "wallets";

    public WalletController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }
    public int eliminarWallet(Wallet wallet) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(wallet.getWalletId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }

    public long nuevoWallet(Wallet wallet) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();

        valoresParaInsertar.put("nombre", wallet.getNombre());
        valoresParaInsertar.put("descripcion", wallet.getDescripcion());
        valoresParaInsertar.put("propietario", wallet.getPropietario());
        valoresParaInsertar.put("compartir", wallet.getCompartir());
        return baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
    }

    public int guardarCambios(Wallet walletEditado) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();

        valoresParaActualizar.put("nombre", walletEditado.getNombre());
        valoresParaActualizar.put("descripcion", walletEditado.getDescripcion());
        valoresParaActualizar.put("propietario", walletEditado.getPropietario());
        valoresParaActualizar.put("compartir", walletEditado.getCompartir());

        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idTransaccion
        String[] argumentosParaActualizar = {String.valueOf(walletEditado.getWalletId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }

    public ArrayList<Wallet> obtenerWallets() {
        ArrayList<Wallet> wallets = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        // SELECT todo
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
        if (!cursor.moveToFirst()) return wallets;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos

            String nombreObtenidoDeBD = cursor.getString(0);
            String descripcionObtenidoDeBD = cursor.getString(1);
            int propietarioObtenidaDeBD = cursor.getInt(2);
            int compartirObtenidaDeBD = cursor.getInt(3);
            long walletIdObtenidoDeBD = cursor.getLong(4);

            Wallet walletObtenidaDeBD = new Wallet(nombreObtenidoDeBD, descripcionObtenidoDeBD, propietarioObtenidaDeBD, compartirObtenidaDeBD, walletIdObtenidoDeBD);
            wallets.add(walletObtenidaDeBD);
        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        return wallets;
    }
}