package me.spenades.mywallettravel.modelos;

public class Wallet {

    private String nombre;
    private String descripcion;
    private int propietario;
    private int compartir;
    private long WalletId; // El ID del Wallet


    public Wallet(String nombre, String descripcion, int propietario, int compartir) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.propietario = propietario;
        this.compartir = compartir;

    }

    // Constructor para cuando instanciamos desde la BD
    public Wallet(String nombre, String descripcion, int propietario,  int compartir, long WalletId) {
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
    public int getPropietario() {
        return propietario;
    }
    public void setPropietario(int propietario) {
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
                "nombre='" + nombre + '\'' +
                "descripcion='" + descripcion + '\'' +
                "propietario='" + propietario + '\'' +
                "compartir='" + compartir +
                '}';
    }
}
