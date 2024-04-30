package me.spenades.mytravelwallet.models;

public class Transaccion {

    private String descripcion;
    private String importe;
    private long pagadorId;
    private String nombrePagador;
    private String miembros;
    private String categoria;
    private long categoriaId;
    private String fecha;
    private long walletId;
    private long id; // El ID de la Transacción
    private float total;


    public Transaccion(String descripcion, String importe, long pagadorId, String miembros, long categoriaId, String fecha, long walletId) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagadorId = pagadorId;
        this.miembros = miembros;
        this.categoriaId = categoriaId;
        this.fecha = fecha;
        this.walletId = walletId;
    }


    // Constructor para cuando instanciamos desde la BD
    public Transaccion(String descripcion, String importe, long pagadorId, String nombrePagador, String miembros, long categoriaId,
                       String categoria,
                       String fecha, long walletId, long id) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagadorId = pagadorId;
        this.nombrePagador = nombrePagador;
        this.miembros = miembros;
        this.categoriaId = categoriaId;
        this.categoria = categoria;
        this.fecha = fecha;
        this.walletId = walletId;
        this.id = id;
    }


    public Transaccion(String descripcion, String importe, long pagadorId, String miembros, long categoriaId, String fecha, long walletId,
                       long id) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagadorId = pagadorId;
        this.miembros = miembros;
        this.categoriaId = categoriaId;
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


    public String getMiembros() {
        return miembros;
    }


    public void setMiembros(String miembros) {
        this.miembros = miembros;
    }


    public String getCategoria() {
        return categoria;
    }


    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }


    public long getCategoriaId() {
        return categoriaId;
    }


    public void setCategoriaId(long categoriaId) {
        this.categoriaId = categoriaId;
    }


    public String getFecha() {
        return fecha;
    }


    public void setFecha(String fecha) {
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
                ", miembros='" + miembros + '\'' +
                ", categoriaId='" + categoriaId + '\'' +
                ", fecha='" + fecha + '\'' +
                ", walletId='" + walletId +
                "'}";
    }

}