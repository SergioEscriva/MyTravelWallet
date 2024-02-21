package me.spenades.mywallettravel.SQLiteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
        //Crear Base de Datos Interna
public class AyudanteBaseDeDatos extends SQLiteOpenHelper {
    private static final String
            DB_PATH = "/data/data/me.spenades.mywallettravel/databases/",
            NOMBRE_BASE_DE_DATOS = "walletsDB",
            NOMBRE_TABLA_WALLETS = "wallets",
            NOMBRE_TABLA_TRANSACCIONES = "transacciones";

    private static final int VERSION_BASE_DE_DATOS = 1;

    public AyudanteBaseDeDatos(Context context) {
        super(context, NOMBRE_BASE_DE_DATOS, null, VERSION_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, nombre text, descripcion text, propietario int, compartir int)", NOMBRE_TABLA_WALLETS));

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, descripcion text, importe int, pagador txt, participantes int, categoria txt, fecha int, walletId int)", NOMBRE_TABLA_TRANSACCIONES));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
