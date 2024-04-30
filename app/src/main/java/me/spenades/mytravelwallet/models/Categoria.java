package me.spenades.mytravelwallet.models;

public class Categoria {

    private String categoria;

    private long id;


    public Categoria(long id) {
        this.id = id;
    }


    public Categoria(String categoria) {
        this.categoria = categoria;
    }


    // Constructor para cuando instanciamos desde la BD
    public Categoria(String categoria, long id) {
        this.categoria = categoria;
        this.id = id;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getCategoria() {
        return categoria;
    }


    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }


    @Override
    public String toString() {
        return "Categoria{" +
                ", categoriaNombre='" + categoria + '\'' +
                ", id='" + id +
                "'}";

    }
}
