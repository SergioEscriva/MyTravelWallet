package me.spenades.mywallettravel.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mywallettravel.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mywallettravel.models.Usuario;

public class UsuarioController {
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "usuario";

    public UsuarioController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }
    public int eliminarUsuario(Usuario usuario) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(usuario.getId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }

    public long nuevoUsuario(Usuario usuario) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();

        System.out.println("Lista Usuarios" + obtenerUsuarios(0));
        // Recuperamos Valores
        valoresParaInsertar.put("nombre", usuario.getNombre());
        valoresParaInsertar.put("apodo", usuario.getApodo());
        // Agregamos a la BD
        long usuarioId = baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
        System.out.println("UsuarioController " + usuarioId);
        return usuarioId;
    }

    public int guardarCambios(Usuario usuarioEditado) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();

        valoresParaActualizar.put("nombre", usuarioEditado.getNombre());
        valoresParaActualizar.put("apodo", usuarioEditado.getApodo());

        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idUsuario
        String[] argumentosParaActualizar = {String.valueOf(usuarioEditado.getId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }

    public ArrayList<Usuario> obtenerUsuarios(int walletIdSelected) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // Si el wallet recibido es 0 significa que no hay usuarios, y se insta a crearlo.
        // En caso contrario se muestran los Wallets de usuario registrado en la app.

        long walletId = Long.valueOf(walletIdSelected);
        String walletIdInicial = "walletId = " + String.valueOf(walletId + 1);
        String WalletIdAConsultar = (walletIdSelected == 0)? null : walletIdInicial;

        String[] columnasAConsultar = {"nombre", "apodo", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,//from usuario
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
            return usuarios;

        }

        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return usuarios;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos

            String nombreObtenidoDeBD = cursor.getString(0);
            String apodoObtenidoDeBD = cursor.getString(1);
            long usuarioIdObtenidoDeBD = cursor.getLong(2);

            Usuario usuarioObtenidaDeBD = new Usuario(nombreObtenidoDeBD, apodoObtenidoDeBD, usuarioIdObtenidoDeBD);
            usuarios.add(usuarioObtenidaDeBD);

        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        return usuarios;
    }
}