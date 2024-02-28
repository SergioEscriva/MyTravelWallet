package me.spenades.mywallettravel.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mywallettravel.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mywallettravel.models.Participante;


public class ParticipanteController {
    private UsuarioController usuarioController;
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "wallet_usuario";

    public ParticipanteController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
        usuarioController = new UsuarioController(contexto);
    }

    public int eliminarParticipante(Participante participante) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(participante.getId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }

    public long nuevoParticipante(Participante participante) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();

        // Rescatamos valores necesarios para guardar Participante TODO
        String walletId = String.valueOf(participante.getWalletId());
        String usuarioId = String.valueOf(participante.getUserId());

        // Buscamos si existe el Participante en esa tabla
        String existeParticipante = "SELECT count(*) from 'WALLET_USUARIO' WHERE wallet_id = " + walletId + " AND usuario_id = " + usuarioId;
        Cursor cursorParticipante = baseDeDatos.rawQuery(existeParticipante, null);
        long existe = cursorParticipante.getCount();
        cursorParticipante.close();
        System.out.println(cursorParticipante);

        // Si no existe se añade
        long participanteAgregado = 0;
        if (existe == 0) {
            String guardarParticipante = "INSERT INTO 'WALLET_USUARIO' (wallet_id,usuario_id) VALUES (" + walletId + "," + usuarioId + ")";
            Cursor cursorParticipantes = baseDeDatos.rawQuery(guardarParticipante, null);
            participanteAgregado = cursorParticipantes.getCount();
            cursorParticipantes.close();
        }
        // Fin del ciclo. Cerramos cursor y regresamos la lista

        return participanteAgregado;
    }

    public int guardarCambios(Participante participanteEditado) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();

        valoresParaActualizar.put("walletId", participanteEditado.getWalletId());
        valoresParaActualizar.put("userId", participanteEditado.getUserId());

        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idParticipante
        String[] argumentosParaActualizar = {String.valueOf(participanteEditado.getId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }

    public ArrayList<Participante> obtenerParticipantes(long walletId) {
        //public Cursor obtenerParticipantes(int walletIdSelected) {
        ArrayList<Participante> participantes = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // hacemos un inner join para extraer los nombres por la id
        String walletIdString = String.valueOf(walletId);
        String query = "SELECT wallet_id,usuario_id,nombre FROM WALLET_USUARIO INNER JOIN USUARIO ON usuario_id=USUARIO.id WHERE wallet_id = " + walletIdString;
        Cursor cursor = baseDeDatos.rawQuery(query, null);

        if (cursor == null) {
            return participantes;
        }

        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return participantes;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            long wallet_idObtenidoDeBD = cursor.getLong(0);
            long user_idObtenidoDeBD = cursor.getLong(1);
            String nombreObtenidoDeBD = cursor.getString(2);

            Participante participanteObtenidaDeBD = new Participante(wallet_idObtenidoDeBD, user_idObtenidoDeBD, nombreObtenidoDeBD);
            participantes.add(participanteObtenidaDeBD);

        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        System.out.println(participantes);
        return participantes;

    }
}