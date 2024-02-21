package me.spenades.mywallettravel.modelos;

public class Transaccion {

    private String descripcion;
    private int importe;
    private String pagador;
    private int participantes;
    private String categoria;
    private int fecha;
    private int walletId; // El ID del Wallet
    private long transaccionId; // El ID de la Transacción
    private int total;

    public Transaccion(String descripcion, int importe, String pagador, int participantes, String categoria, int fecha, int walletId) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagador = pagador;
        this.participantes = participantes;
        this.categoria = categoria;
        this.fecha = fecha;
        this.walletId = walletId;

    }

    // Constructor para cuando instanciamos desde la BD
    public Transaccion(String descripcion, int importe, String pagador, int participantes, String categoria, int fecha, int walletId, long transaccionId) {
        this.descripcion = descripcion;
        this.importe = importe;
        this.pagador = pagador;
        this.participantes = participantes;
        this.categoria = categoria;
        this.fecha = fecha;
        this.walletId = walletId;
        this.transaccionId = transaccionId;
    }

    public long getTransaccionId() {
        return transaccionId;
    }

    public void setTransaccionId(long transaccionId) {
        this.transaccionId = transaccionId;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public int getImporte() {
        return importe;
    }
    public void setImporte(int importe) {
        this.importe = importe;
    }
    public String getPagador() {
        return pagador;
    }
    public void setPagador(String pagador) {
        this.pagador = pagador;
    }
    public int getParticipantes() {
        return participantes;
    }
    public void setParticipantes(int participantes) {
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
    public int getWalletId() {
        return walletId;
    }
    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }


    @Override
    public String toString() {
        return "Transaccion{" +
                "descripcion='" + descripcion + '\'' +
                "importe='" + importe + '\'' +
                "pagador='" + pagador + '\'' +
                "participantes='" + participantes + '\'' +
                "categoria='" + categoria + '\'' +
                "fecha='" + fecha + '\'' +
                "walletId=" + walletId +
                '}';
    }
}
