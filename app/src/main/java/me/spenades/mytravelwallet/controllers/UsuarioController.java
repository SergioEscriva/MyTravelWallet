package me.spenades.mytravelwallet.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Usuario;

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

        // Recuperamos Valores
        valoresParaInsertar.put("nombre", usuario.getNombre());
        valoresParaInsertar.put("apodo", usuario.getApodo());
        //valoresParaInsertar.put("id", usuario.getId());
        // Agregamos a la BD
        long resultado = baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
        return resultado;
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


    public ArrayList<Usuario> obtenerUsuarios() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // Los usuarios son de toda la app.

        String[] columnasAConsultar = {"nombre", "apodo", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,//from usuario
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

    public ArrayList<Usuario> obtenerUsuarioId(String usuario) {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        String nombre = usuario;

        //String nombre = usuario.getNombre();
        // Los usuarios son de toda la app.
        String selection = "nombre= ?";
        String[] selectionArgs = {nombre};
        String[] columnasAConsultar = {"nombre", "apodo", "id"};

        // Los usuarios son de toda la app.

        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
                columnasAConsultar,
                selection,
                selectionArgs,
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
        //do {
        // El 0 es el número de la columna, como seleccionamos


        String nombreObtenidoDeBD = String.valueOf(cursor.getString(0));
        String apodoObtenidoDeBD = String.valueOf(cursor.getString(1));
        long usuarioIdObtenidoDeBD = (cursor.getLong(2));
        // String participaDb1 = String.valueOf(cursor.getString(0));
        Usuario usuarioObtenidaDeBD = new Usuario(nombreObtenidoDeBD, apodoObtenidoDeBD, usuarioIdObtenidoDeBD);
        usuarios.add(usuarioObtenidaDeBD);

        //} while (cursor.moveToNext());
        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();

        return usuarios;
    }


    public ArrayList<Usuario> obtenerUsuarioNombre(long usuario) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        String id = String.valueOf(usuario);

        // Los usuarios son de toda la app.
        String selection = "id= ?";
        String[] selectionArgs = {id};
        String[] columnasAConsultar = {"nombre", "apodo", "id"};

        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
                columnasAConsultar,
                selection,
                selectionArgs,
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
