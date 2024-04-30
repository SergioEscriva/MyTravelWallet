package me.spenades.mytravelwallet.models;

public class Ayuda {

    private String ayudaNombre;
    private int ayuda;
    private long id; // El ID del Usuario

    public Ayuda(long id) {
        this.id = id;
    }

    public Ayuda(String ayudaNombre) {
        this.ayudaNombre = ayudaNombre;
    }

    public Ayuda(int ayuda) {
        this.ayuda = ayuda;
    }

    public Ayuda(int ayuda, String ayudaNombre) {
        this.ayudaNombre = ayudaNombre;
        this.ayuda = ayuda;
    }

    // Constructor para cuando instanciamos desde la BD
    public Ayuda(int ayuda, String ayudaNombre, long id) {
        this.ayudaNombre = ayudaNombre;
        this.ayuda = ayuda;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAyudaNombre() {
        return ayudaNombre;
    }

    public void setAyudaNombre(String ayudaNombre) {
        this.ayudaNombre = ayudaNombre;
    }

    public int getAyuda() {
        return ayuda;
    }

    public void setAyuda(int ayuda) {
        this.ayuda = ayuda;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                ", ayuda='" + ayuda + '\'' +
                ", ayudaNombre='" + ayudaNombre + '\'' +
                ", id='" + id +
                "'}";

    }

}
