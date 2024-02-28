package me.spenades.mywallettravel.models;

public class Participan {

    private String participan;
    // private String apodo;
    private long walletId;
    private long userId;
    private long id; // El ID de la tabla user

    public Participan(String participan) {

        //this.walletId = walletId;
        this.participan = participan;
    }

    // Constructor para cuando instanciamos desde la BD
    public Participan(long walletId, long userId, String nombre) {

        this.walletId = walletId;
        this.userId = userId;
        this.participan = nombre;
    }


    // Constructor para cuando instanciamos desde la BD
    public Participan(long walletId, long userId, String nombre, long id) {

        this.walletId = walletId;
        this.userId = userId;
        this.participan = nombre;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParticipan() {
        return participan;
    }

    public void setParticipan(String participan) {
        this.participan = participan;
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
    /*
   public String getApodo() {
       return apodo;
   }
   public void setApodo(String apodo) {
       this.apodo = apodo;
   }

    */

    @Override
    public String toString() {
        return "Participan{" +
                ", participan='" + participan + '\'' +
                ", walletId='" + walletId +
                "'}";

    }
}
