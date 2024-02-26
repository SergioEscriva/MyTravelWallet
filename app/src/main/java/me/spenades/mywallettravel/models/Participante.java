package me.spenades.mywallettravel.models;

public class Participante {

    private String nombre;
   // private String apodo;
    private long walletId;
    private long userId;
    private long id; // El ID de la tabla user wallet


    public Participante(long walletId, long userId, String nombre) {
       // this.apodo = apodo;
        this.walletId = walletId;
        this.userId = userId;
        this.nombre = nombre;
    }

    public Participante(long walletId, String nombre) {
        // this.apodo = apodo;
        //this.userId = userId;
        this.walletId = walletId;
        this.nombre = nombre;
    }


    // Constructor para cuando instanciamos desde la BD
    public Participante(long walletId, long userId, String nombre, long id) {
        // this.apodo = apodo;
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
    /*
   public String getApodo() {
       return apodo;
   }
   public void setApodo(String apodo) {
       this.apodo = apodo;
   }

    */

     @Override
    public String toString(){
         return "Participante{" +
                 ", nombre='" + nombre + '\'' +
                 ", walletId='" + walletId + '\'' +
                 ", userId='" + userId +
                 "'}";

    }
}
