package me.spenades.mytravelwallet.models;

public class Transaccion {

    private String descripcion;
    private String importe;
    private long pagadorId;
    private String nombrePagador;
    private String participantes;
    private String categoria;
    private int fecha;
    private long walletId; // El ID del Wallet
    //private long walletName; // El nombre del Wallet
    private long id; // El ID de la Transacción
    private float total;


    public Transaccion(String descripcion, String importe, long pagadorId, String participantes, String categoria, int fecha, long walletId) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagadorId = pagadorId;
        this.participantes = participantes;
        this.categoria = categoria;
        this.fecha = fecha;
        this.walletId = walletId;
    }
/*
    public Transaccion(String descripcion, String importe, long pagadorId, String nombrePagador, String participantes, String categoria, int fecha, long walletId) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagadorId = pagadorId;
        this.nombrePagador = nombrePagador;
        this.participantes = participantes;
        this.categoria = categoria;
        this.fecha = fecha;
        this.walletId = walletId;
    }

 */

    // Constructor para cuando instanciamos desde la BD
    public Transaccion(String descripcion, String importe, long pagadorId, String nombrePagador, String participantes, String categoria, int fecha, long walletId, long id) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagadorId = pagadorId;
        this.nombrePagador = nombrePagador;
        this.participantes = participantes;
        this.categoria = categoria;
        this.fecha = fecha;
        this.walletId = walletId;
        this.id = id;
    }

    public Transaccion(String descripcion, String importe, long pagadorId, String participantes, String categoria, int fecha, long walletId, long id) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagadorId = pagadorId;
        //this.nombrePagador = nombrePagador;
        this.participantes = participantes;
        this.categoria = categoria;
        this.fecha = fecha;
        this.walletId = walletId;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public long getPagadorId() {
        return pagadorId;
    }

    public void setPagadorId(long pagadorId) {
        this.pagadorId = pagadorId;
    }

    public String getNombrePagador() {
        return nombrePagador;
    }

    public void setNombrePagador(String nombrePagador) {
        this.nombrePagador = nombrePagador;
    }

    public String getParticipantes() {
        return participantes;
    }

    public void setParticipantes(String participantes) {
        this.participantes = participantes;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getFecha() {
        return fecha;
    }

    public void setFecha(int fecha) {
        this.fecha = fecha;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }


    @Override
    public String toString() {
        return "Transaccion{" +
                ", descripcion='" + descripcion + '\'' +
                ", importe='" + importe + '\'' +
                ", pagadorId='" + pagadorId + '\'' +
                ", participantes='" + participantes + '\'' +
                ", categoria='" + categoria + '\'' +
                ", fecha='" + fecha + '\'' +
                ", walletId='" + walletId +
                "'}";
    }

}