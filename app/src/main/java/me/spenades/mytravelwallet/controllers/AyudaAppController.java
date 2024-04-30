package me.spenades.mytravelwallet.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Ayuda;

public class AyudaAppController {

    private final AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private final String NOMBRE_TABLA = "ayudas";


    public AyudaAppController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }


    public int modificarAyuda(Ayuda ayudaEditada) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();

        valoresParaActualizar.put("ayuda", ayudaEditada.getAyuda());
        valoresParaActualizar.put("ayudaNombre", ayudaEditada.getAyudaNombre());

        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idUsuario
        String[] argumentosParaActualizar = {String.valueOf(ayudaEditada.getId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }


    public ArrayList<Ayuda> obtenerAyuda() {
        ArrayList<Ayuda> ayuda = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // Los usuarios son de toda la app.
        String[] columnasAConsultar = {"ayuda", "ayudaNombre", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
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
            return ayuda;

        }

        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) return ayuda;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos
            int ayudaObtenidoDeBD = cursor.getInt(0);
            String ayudaNombreObtenidoDeBD = cursor.getString(1);
            long ayudaIdObtenidoDeBD = cursor.getLong(2);

            Ayuda ayudaObtenidaDeBD = new Ayuda(ayudaObtenidoDeBD, ayudaNombreObtenidoDeBD, ayudaIdObtenidoDeBD);
            ayuda.add(ayudaObtenidaDeBD);

        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        return ayuda;
    }

}