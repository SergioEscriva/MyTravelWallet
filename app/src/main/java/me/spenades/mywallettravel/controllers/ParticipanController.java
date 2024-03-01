package me.spenades.mywallettravel.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mywallettravel.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mywallettravel.models.Participante;
import me.spenades.mywallettravel.models.Usuario;


public class ParticipanController {
    private UsuarioController usuarioController;
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "wallet_usuario";
    //private String NOMBRE_TABLA_TRANSACCION = "transaccion";

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

        // Rescatamos valores necesarios para guardar Participante
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

    public ArrayList<Participante> obtenerParticipan(long transaccionId) {

        ArrayList<Usuario> usuarioCompletoLista = new ArrayList<>();
        ArrayList<Participante> participanFinal = new ArrayList<>();

        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // hacemos busqueda nombre y los que participan del wallet.
        String transaccionIdLong = String.valueOf(transaccionId);
        String participanSql = "SELECT participantes FROM 'TRANSACCION' WHERE id = " + transaccionIdLong;
        Cursor cursor = baseDeDatos.rawQuery(participanSql, null);

        System.out.println("ListaCrear Otra " + cursor.getCount());
        //Cursor cursor1 = cursor.getColumnNames();
        //cursor.moveToFirst();
        cursor.moveToNext();
        if (cursor == null || cursor.getCount() < 1) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return participanFinal;
        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return participanFinal;

        //Recuperamos participan en el pago por su ID y lo iteramos para recuperar su nombre.

        String participaDb1 = String.valueOf(cursor.getString(0));
        String[] participaLista = String.valueOf(participaDb1).split(",");

        for (String usuarioIdDbParticipa : participaLista) {


            Long participanteLong = Long.parseLong(usuarioIdDbParticipa);
            // Formateamos el Id
            Usuario usuarioIdParticipa = new Usuario(participanteLong);
            // Recuperamos el nombre del id
            usuarioCompletoLista = usuarioController.obtenerUsuarioNombre(usuarioIdParticipa);
            // Extraemos nombre el id
            Usuario usuarioCompleto = usuarioCompletoLista.get(0);
            String usuarioNombre = usuarioCompleto.getNombre();
            long usuarioId = usuarioCompleto.getId();
            Participante usuarioObtenidaDeBD = new Participante(usuarioNombre, usuarioId);
            participanFinal.add(usuarioObtenidaDeBD);

        }
        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        System.out.println("ÑÑÑÑÑÑÑÑÑÑ" + participanFinal);
        return participanFinal;
    }

}