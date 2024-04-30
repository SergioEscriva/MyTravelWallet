package me.spenades.mytravelwallet.controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import me.spenades.mytravelwallet.SQLiteDB.AyudanteBaseDeDatos;

public class DemoController {

    private final AyudanteBaseDeDatos ayudanteBaseDeDatos;
    //private String NOMBRE_TABLA = "usuario";


    public DemoController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }


    public void demoIncial() {
        // writable porque vamos a insertar
        SQLiteDatabase db = ayudanteBaseDeDatos.getWritableDatabase();

        /// Creamos un Wallet de Prueba para el primer inicio.
        db.execSQL("INSERT INTO 'CATEGORIA' ('id', 'categoria') VALUES ('1', 'Varios')");
        db.execSQL("INSERT INTO 'main'.'categoria' ('id', 'categoria') VALUES ('2', 'Deudas')");
        db.execSQL("INSERT INTO 'main'.'categoria' ('id', 'categoria') VALUES ('3', 'Gasolina')");
        db.execSQL("INSERT INTO 'main'.'categoria' ('id', 'categoria') VALUES ('4', 'Supermercado')");

        db.execSQL("INSERT INTO 'main'.'transaccion' ('id', 'descripcion', 'importe', 'pagadorId', 'miembros', 'categoriaId', " +
                "'fecha', " +
                "'walletId') VALUES ('1', 'Cola', '8.0', '1', '1,2,3', '1', '25/12/2023', '1')");
        db.execSQL("INSERT INTO 'main'.'transaccion' ('id', 'descripcion', 'importe', 'pagadorId', 'miembros', 'categoriaId', " +
                "'fecha', " +
                "'walletId') VALUES ('2', 'Horchata', '16.50', '2', '1,2,3', '1', '25/12/2023', '1')");
        db.execSQL("INSERT INTO 'main'.'transaccion' ('id', 'descripcion', 'importe', 'pagadorId', 'miembros', 'categoriaId', " +
                "'fecha', " +
                "'walletId') VALUES ('3', 'Logroño', '109.55', '3', '1,2,3', '3', '25/12/2023', '1')");

        db.execSQL("INSERT INTO 'main'.'usuario' ('id', 'nombre', 'apodo') VALUES ('3', 'Javi', 'Javi')");
        db.execSQL("INSERT INTO 'main'.'usuario' ('id', 'nombre', 'apodo') VALUES ('2', 'Hugo', 'Hugo')");
        //db.execSQL(String.format("INSERT INTO 'main'.'usuario' ('id', 'nombre', 'apodo', 'pin') VALUES ('1', 'Usuario1', 'Usuario1', '0000')"));

        db.execSQL("INSERT INTO 'main'.'wallet' ('id', 'nombre', 'descripcion', 'propietario', 'compartir') VALUES ('1', " +
                "'Demo', " +
                "'Wallet de Prueba', '1', '1')");

        db.execSQL("INSERT INTO 'main'.'wallet_usuario' ('id', 'wallet_id', 'usuario_id') VALUES ('3', '1', '3')");

        db.execSQL("INSERT INTO 'main'.'wallet_usuario' ('id', 'wallet_id', 'usuario_id') VALUES ('2', '1', '2')");

        db.execSQL("INSERT INTO 'main'.'wallet_usuario' ('id', 'wallet_id', 'usuario_id') VALUES ('1', '1', '1')");
        db.execSQL("INSERT INTO 'main'.'ayudas' ('id', 'ayuda','ayudaNombre') VALUES ('1','1','inicio')");
        db.execSQL("INSERT INTO 'main'.'ayudas' ('id', 'ayuda','ayudaNombre') VALUES ('2','1','listarWallets')");
        db.execSQL("INSERT INTO 'main'.'ayudas' ('id', 'ayuda','ayudaNombre') VALUES ('3','1','editarWallets')");
        db.execSQL("INSERT INTO 'main'.'ayudas' ('id', 'ayuda','ayudaNombre') VALUES ('4','1','listarTransaciones')");
        db.execSQL("INSERT INTO 'main'.'ayudas' ('id', 'ayuda','ayudaNombre') VALUES ('5','1','saldarDeudas')");

    }

}
