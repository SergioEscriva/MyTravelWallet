package me.spenades.mywallettravel.models;

public class Usuario {

    private String nombre;
    private String apodo;
    private long id; // El ID del Usuario

    public Usuario(long id) {
        this.id = id;
    }

    public Usuario(String nombre, String apodo) {
        this.nombre = nombre;
        this.apodo = apodo;
    }

    // Constructor para cuando instanciamos desde la BD
    public Usuario(String nombre, String apodo, long id) {
        this.nombre = nombre;
        this.apodo = apodo;
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

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                ", nombre='" + nombre + '\'' +
                ", apodo='" + apodo +
                "'}";

    }
}
