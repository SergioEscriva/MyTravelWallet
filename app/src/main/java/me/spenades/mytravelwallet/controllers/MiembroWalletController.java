package me.spenades.mytravelwallet.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Miembro;


public class MiembroWalletController {

    private UsuarioAppController usuarioAppController;
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "wallet_usuario";


    public MiembroWalletController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
        usuarioAppController = new UsuarioAppController(contexto);
    }


    public int eliminarMiembro(Miembro miembro) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        long usuarioId = miembro.getUserId();
        String usuarioIdString = String.valueOf(usuarioId);
        long walletId = miembro.getWalletId();
        String walletIdString = String.valueOf(walletId);

        String query =
                "DELETE" +
                " FROM 'WALLET_USUARIO'" +
                " WHERE wallet_id = " + walletIdString +
                " AND" +
                " usuario_id = "
                + usuarioIdString;
        Cursor cursor = baseDeDatos.rawQuery(query, null);
        int respuesta = cursor.getCount();
        cursor.close();
        return respuesta;
    }


    public long nuevoMiembro(Miembro miembro) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();

        // Rescatamos valores necesarios para guardar Miembro TODO
        String walletId = String.valueOf(miembro.getWalletId());
        String usuarioId = String.valueOf(miembro.getUserId());

        // Buscamos si existe el Miembro en esa tabla
        String existeMiembro = "SELECT * from 'WALLET_USUARIO' WHERE wallet_id = " + walletId + " AND usuario_id = " + usuarioId;
        Cursor cursorMiembro = baseDeDatos.rawQuery(existeMiembro, null);
        long miembroExisteDb = cursorMiembro.getCount();

        // Si no existe se añade
        long miembroYaExiste = 1;
        if (miembroExisteDb == 0) {
            miembroYaExiste = 0;
            String guardarMiembro = "INSERT INTO 'WALLET_USUARIO' (wallet_id,usuario_id) VALUES (" + walletId + "," + usuarioId + ")";
            Cursor cursorMiembros = baseDeDatos.rawQuery(guardarMiembro, null);
            miembroYaExiste = cursorMiembros.getCount();
            cursorMiembros.close();
        }
        // Fin del ciclo. Cerramos cursor
        cursorMiembro.close();
        return miembroYaExiste;
    }


    public int guardarCambios(Miembro miembroEditado) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();

        valoresParaActualizar.put("walletId", miembroEditado.getWalletId());
        valoresParaActualizar.put("userId", miembroEditado.getUserId());

        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idMiembro
        String[] argumentosParaActualizar = {String.valueOf(miembroEditado.getId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }


    public ArrayList<Miembro> obtenerMiembros(long walletId) {
        ArrayList<Miembro> miembros = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // hacemos un inner join para extraer los nombres por la id     wallet_id,usuario_id,nombre
        String walletIdString = String.valueOf(walletId);
        String query = "SELECT wallet_id,usuario_id,nombre FROM 'WALLET_USUARIO' INNER JOIN 'USUARIO' ON usuario_id = USUARIO.id WHERE wallet_id = "
                + walletIdString;
        Cursor cursor = baseDeDatos.rawQuery(query, null);

        if (cursor == null) {
            return miembros;
        }

        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) return miembros;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            long wallet_idObtenidoDeBD = cursor.getLong(0);
            long user_idObtenidoDeBD = cursor.getLong(1);
            String nombreObtenidoDeBD = cursor.getString(2);

            Miembro miembroObtenidaDeBD = new Miembro(wallet_idObtenidoDeBD, user_idObtenidoDeBD, nombreObtenidoDeBD);
            miembros.add(miembroObtenidaDeBD);

        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();

        return miembros;

    }
}