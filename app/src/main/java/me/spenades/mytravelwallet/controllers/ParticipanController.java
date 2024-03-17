package me.spenades.mytravelwallet.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Usuario;


public class ParticipanController {

    private UsuarioController usuarioController;
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "wallet_usuario";


    public ParticipanController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
        usuarioController = new UsuarioController(contexto);
    }


    public int eliminarMiembro(Miembro miembro) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(miembro.getId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }


    public long nuevoMiembro(Miembro miembro) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();

        // Rescatamos valores necesarios para guardar Miembro
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


    public ArrayList<Miembro> obtenerParticipan(long transaccionId) {

        ArrayList<Usuario> usuarioCompletoLista = new ArrayList<>();
        ArrayList<Miembro> participanFinal = new ArrayList<>();

        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();

        // hacemos busqueda nombre y los que participan del wallet.
        String transaccionIdLong = String.valueOf(transaccionId);
        String participanSql = "SELECT miembros FROM 'TRANSACCION' WHERE id = " + transaccionIdLong;
        Cursor cursor = baseDeDatos.rawQuery(participanSql, null);

        cursor.moveToNext();
        if (cursor == null || cursor.getCount() < 1) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return participanFinal;
        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (! cursor.moveToFirst()) return participanFinal;

        //Recuperamos participan en el pago por su ID y lo iteramos para recuperar su nombre.
        String participaDb1 = String.valueOf(cursor.getString(0));
        String[] participaLista = String.valueOf(participaDb1).split(",");


        for (String usuarioIdDbParticipa : participaLista) {

            Long miembroLong = Long.parseLong(usuarioIdDbParticipa);
            // Formateamos el Id
            //Usuario usuarioIdParticipa = new Usuario(miembroLong);
            // Recuperamos el nombre del id
            usuarioCompletoLista = usuarioController.obtenerUsuarioNombre(miembroLong);
            // Extraemos nombre el id
            Usuario usuarioCompleto = usuarioCompletoLista.get(0);
            String usuarioNombre = usuarioCompleto.getNombre();
            long usuarioId = usuarioCompleto.getId();
            Miembro usuarioObtenidaDeBD = new Miembro(usuarioNombre, usuarioId);
            participanFinal.add(usuarioObtenidaDeBD);

        }
        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        return participanFinal;
    }

}