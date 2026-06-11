package com.galvan.inventarios.dto;

public class AuthResponse {
    private String token;
    private String nombre;
    private boolean isAdmin; // <-- NUEVO

    // Constructor
    public AuthResponse(String token, String nombre, boolean isAdmin) {
        this.token = token;
        this.nombre = nombre;
        this.isAdmin = isAdmin;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public String getNombre() { return nombre; }
    public boolean isAdmin() { return isAdmin; }
}