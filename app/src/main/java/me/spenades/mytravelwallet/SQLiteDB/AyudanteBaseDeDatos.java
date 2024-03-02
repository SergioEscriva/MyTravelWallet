package me.spenades.mytravelwallet.SQLiteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Crear Base de Datos Interna
public class AyudanteBaseDeDatos extends SQLiteOpenHelper {
    private static final String
            NOMBRE_BASE_DE_DATOS = "walletsDB",
            NOMBRE_TABLA_WALLETS = "wallet",
            NOMBRE_TABLA_USUARIOS = "usuario",
            NOMBRE_TABLA_CATEGORIAS = "categoria",
            NOMBRE_TABLA_WALLETS_USUARIOS = "wallet_usuario",
            NOMBRE_TABLA_TRANSACCIONES = "transaccion";


    private static final int VERSION_BASE_DE_DATOS = 1;

    public AyudanteBaseDeDatos(Context context) {
        super(context,
                NOMBRE_BASE_DE_DATOS,
                null,
                VERSION_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, nombre text unique, descripcion text, propietario int, compartir inte)", NOMBRE_TABLA_WALLETS));

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, descripcion text, importe real, pagador int, participantes text, categoria txt, fecha int, walletId int)", NOMBRE_TABLA_TRANSACCIONES));

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, nombre text unique, apodo text)", NOMBRE_TABLA_USUARIOS));

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, wallet_id int, usuario_id int)", NOMBRE_TABLA_WALLETS_USUARIOS));

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, categoria text unique)", NOMBRE_TABLA_CATEGORIAS));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
