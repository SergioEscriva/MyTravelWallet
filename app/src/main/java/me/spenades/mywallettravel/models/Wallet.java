package me.spenades.mywallettravel.models;

public class Wallet {

    private String nombre;
    private String descripcion;
    private long propietario;
    private int compartir;
    private long WalletId; // El ID del Wallet


    public Wallet(String nombre, String descripcion, long propietario, int compartir) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.propietario = propietario;
        this.compartir = compartir;

    }

    // Constructor para cuando instanciamos desde la BD
    public Wallet(String nombre, String descripcion, long propietario, int compartir, long WalletId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.propietario = propietario;
        this.compartir = compartir;
        this.WalletId = WalletId;
    }

    public long getWalletId() {
        return WalletId;
    }

    public void setWalletId(long walletId) {
        this.WalletId = walletId;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public long getPropietarioId() {
        return propietario;
    }
    public void setPropietarioId(long propietario) {
        this.propietario = propietario;
    }
    public int getCompartir() {
        return compartir;
    }
    public void setCompartir(int compartir) {
        this.compartir = compartir;
    }
    @Override
    public String toString() {
        return "Wallet{" +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", propietario='" + propietario + '\'' +
                ", compartir='" + compartir +
                "'}";
    }
}
