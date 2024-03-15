package me.spenades.mytravelwallet.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Categoria;

public class CategoriaController {

    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "categoria";


    public CategoriaController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }


    public int eliminarCategoria(Categoria categoria) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(categoria.getId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }


    public long nuevaCategoria(Categoria categoria) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();

        // Recuperamos Valores
        valoresParaInsertar.put("categoria", categoria.getCategoria());

        // Agregamos a la BD
        long resultado = baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
        return resultado;
    }


    public int guardarCambios(Categoria categoria) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();

        valoresParaActualizar.put("categoria", categoria.getCategoria());

        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idCategoria
        String[] argumentosParaActualizar = {String.valueOf(categoria.getId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }


    public ArrayList<Categoria> obtenerCategorias() {
        ArrayList<Categoria> categorias = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // Los categorias son de toda la app.

        String[] columnasAConsultar = {"categoria", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,//from categoria
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
            return categorias;

        }

        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) return categorias;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos

            String categoriaObtenidoDeBD = cursor.getString(0);
            long categoriaIdObtenidoDeBD = cursor.getLong(1);

            Categoria categoriaObtenidaDeBD = new Categoria(categoriaObtenidoDeBD, categoriaIdObtenidoDeBD);
            categorias.add(categoriaObtenidaDeBD);

        } while (cursor.moveToNext());
        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        return categorias;
    }


    public ArrayList<Categoria> obtenerCategoriaId(String categoria) {
        ArrayList<Categoria> categorias = new ArrayList<>();

        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        //String categoria = categoria;

        // Los categorias son de toda la app.
        String selection = "categoria= ?";
        String[] selectionArgs = {categoria};
        String[] columnasAConsultar = {"categoria", "id"};

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
            return categorias;

        }

        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) return categorias;
        String categoriaObtenidoDeBD = String.valueOf(cursor.getString(0));
        long categoriaIdObtenidoDeBD = (cursor.getLong(1));
        Categoria categoriaObtenidaDeBD = new Categoria(categoriaObtenidoDeBD, categoriaIdObtenidoDeBD);
        categorias.add(categoriaObtenidaDeBD);

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();

        return categorias;
    }


    public ArrayList<Categoria> obtenerCategoriaNombre(long categoria) {
        ArrayList<Categoria> categorias = new ArrayList<>();

        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        String id = String.valueOf(categoria);

        // Los categorias son de toda la app.
        String selection = "id= ?";
        String[] selectionArgs = {id};
        String[] columnasAConsultar = {"categoria", "id"};

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
            return categorias;

        }

        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) return categorias;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos

            String categoriaObtenidoDeBD = cursor.getString(0);
            long categoriaIdObtenidoDeBD = cursor.getLong(1);

            Categoria categoriaObtenidaDeBD = new Categoria(categoriaObtenidoDeBD, categoriaIdObtenidoDeBD);
            categorias.add(categoriaObtenidaDeBD);

        } while (cursor.moveToNext());
        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();

        return categorias;
    }

}
