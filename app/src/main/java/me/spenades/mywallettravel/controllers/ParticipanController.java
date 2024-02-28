package me.spenades.mywallettravel.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mywallettravel.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mywallettravel.models.Participan;
import me.spenades.mywallettravel.models.Participante;


public class ParticipanController {
    private UsuarioController usuarioController;
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "wallet_usuario";

    public ParticipanController(Context contexto) {
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
        String existeParticipante = "SELECT * from 'WALLET_USUARIO' WHERE wallet_id = " + walletId + " AND usuario_id = " + usuarioId;
        Cursor cursorParticipante = baseDeDatos.rawQuery(existeParticipante, null);
        long participanteExisteDb = cursorParticipante.getCount();

        // Si no existe se añade
        long participanteYaExiste = 1;
        if (participanteExisteDb == 0) {
            participanteYaExiste = 0;
            String guardarParticipante = "INSERT INTO 'WALLET_USUARIO' (wallet_id,usuario_id) VALUES (" + walletId + "," + usuarioId + ")";
            Cursor cursorParticipantes = baseDeDatos.rawQuery(guardarParticipante, null);
            participanteYaExiste = cursorParticipantes.getCount();
            cursorParticipantes.close();
        }
        // Fin del ciclo. Cerramos cursor
        cursorParticipante.close();
        return participanteYaExiste;
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

    public ArrayList<Participan> obtenerParticipan(long walletId) {
        //public Cursor obtenerParticipantes(int walletIdSelected) {
        ArrayList<Participan> participan = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // hacemos busqueda nombre y particpantes del wallet.
        String walletIdString = "1"; //String.valueOf(walletId);
        String query = "SELECT participantes FROM TRANSACCION WHERE id = " + walletIdString;
        Cursor cursor = baseDeDatos.rawQuery(query, null);
        //String lista = cursor.getString(0);
        //System.out.println("Participan: Listo: " + lista);


        if (cursor == null) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return participan;

        }

        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return participan;

        // En caso de que sí haya, iteramos y vamos agregando
        do {
            // El 0 es el número de la columna, como seleccionamos

            //long nombreObtenidoDeBD = cursor.getLong(0);
            String apodoObtenidoDeBD = cursor.getString(0);


            Participan usuarioObtenidaDeBD = new Participan(apodoObtenidoDeBD);
            participan.add(usuarioObtenidaDeBD);

        } while (cursor.moveToNext());
        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        System.out.println(participan);
        return participan;







        /*
        // hacemos un inner join para extraer los nombres por la id     wallet_id,usuario_id,nombre
        //String walletIdString = String.valueOf(walletId);
        //String query = "SELECT wallet_id,usuario_id,nombre FROM 'WALLET_USUARIO' INNER JOIN 'USUARIO' ON usuario_id = USUARIO.id WHERE wallet_id = " + walletIdString;
        String query = "SELECT wallet_id,usuario_id,nombre FROM 'WALLET_USUARIO' INNER JOIN 'USUARIO' ON usuario_id = USUARIO.id WHERE wallet_id = " + walletIdString;

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

    public ArrayList<Usuario> obtenerUsuariosId(Usuario usuario) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        String nombre = usuario.getNombre();
        // Los usuarios son de toda la app.
        String selection = "nombre= ?";
        String[] selectionArgs = {nombre};
        String[] columnasAConsultar = {"nombre", "apodo", "id"};

        // Los usuarios son de toda la app.

        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,//from usuario
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

            return usuarios;
            */  /*
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

    */
    }

}