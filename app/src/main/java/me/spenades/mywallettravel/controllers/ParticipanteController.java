package me.spenades.mywallettravel.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

import me.spenades.mywallettravel.EditarWalletActivity;
import me.spenades.mywallettravel.SQLiteDB.AyudanteBaseDeDatos;
import me.spenades.mywallettravel.adapters.AdaptadorTransacciones;
import me.spenades.mywallettravel.models.Participante;
import me.spenades.mywallettravel.controllers.UsuarioController;
import me.spenades.mywallettravel.models.Transaccion;
import me.spenades.mywallettravel.models.Usuario;


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

        // Rescatamos valores necesarios para guardar Participante
        String walletId = String.valueOf(participante.getWalletId());
        String nombreUsuario = String.valueOf(participante.getNombre());
       // long walletIdLong = Long.valueOf(walletId);
        long usuarioId = Long.valueOf(participante.getUserId());

        // Comprobamos si el Nombre de Usuario existe
        //Lo ingresamos y nos devuelve la posición será el ID y Existe pues
        Usuario nuevoUsuario = new Usuario(nombreUsuario,nombreUsuario);
        long usuarioIdExiste = usuarioController.nuevoUsuario(nuevoUsuario);

        if (usuarioIdExiste == -1) {
            // Como existe no lo agregamos  como usuario pero si como participante y rescatamos el Id que ya tiene
            usuarioIdExiste = usuarioId + 1;
            System.out.println("Existe" + usuarioIdExiste);
        }

        String guardarParticipante = "INSERT INTO 'WALLET_USUARIO' (wallet_id,usuario_id) VALUES (" + walletId +"," + usuarioIdExiste + ")";
        Cursor cursorParticipantes = baseDeDatos.rawQuery(guardarParticipante, null);
        long participanteExiste = cursorParticipantes.getCount();

        return participanteExiste;
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

        // hacemos un inner para extraer los nombres por la id
        //long walletId = Long.valueOf(walletIdSelected);
        //String WalletIdAConsultar = "wallet_id = " + String.valueOf(walletId + 1);
        String walletIdString =  String.valueOf(walletId + 1);
        String query ="SELECT wallet_id,usuario_id,nombre FROM WALLET_USUARIO INNER JOIN USUARIO ON usuario_id=USUARIO.id WHERE wallet_id =" + walletIdString;
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
        System.out.println("Lectura DB " + participantes);
        return participantes;

    }
}