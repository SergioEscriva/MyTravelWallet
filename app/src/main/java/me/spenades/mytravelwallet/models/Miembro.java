package me.spenades.mytravelwallet.models;

public class Miembro {

    private String nombre;
    private long walletId;
    private long userId;
    private long id;


    public Miembro(long walletId, String nombre) {

        this.walletId = walletId;
        this.nombre = nombre;
    }


    public Miembro(String nombre, long userId) {

        this.userId = userId;
        this.nombre = nombre;
    }


    public Miembro(long walletId, long userId, String nombre) {

        this.walletId = walletId;
        this.userId = userId;
        this.nombre = nombre;
    }


    // Constructor para cuando instanciamos desde la BD
    public Miembro(long walletId, long userId, String nombre, long id) {

        this.walletId = walletId;
        this.userId = userId;
        this.nombre = nombre;
        this.id = id;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public long getUserId() {
        return userId;
    }


    public void setUserId(long userId) {
        this.userId = userId;
    }


    public long getWalletId() {
        return walletId;
    }


    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }


    @Override
    public String toString() {
        return "Miembro{" +
                ", nombre='" + nombre + '\'' +
                ", walletId='" + walletId + '\'' +
                ", userId='" + userId +
                "'}";

    }
}
