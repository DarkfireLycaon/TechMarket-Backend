package com.galvan.inventarios.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // --- NUEVOS ATRIBUTOS INTEGRADOS ---
    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String city; // Mapeado en español como 'ciudad' en el getter/setter para Angular

    private String direccion; // Opcional (Permite null en BD)

    private String codigoPostal; // Opcional (Permite null en BD)
    // ------------------------------------

    private boolean enabled = false;
    private String codigoConfirmacion;
    private String resetToken;
    private LocalDateTime tokenExpiration;

    public Usuario() {}

    public Usuario(String nombre, String email, String password, String codigoConfirmacion, String telefono, String ciudad) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.codigoConfirmacion = codigoConfirmacion;
        this.telefono = telefono;
        this.city = ciudad;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getCodigoConfirmacion() {
        return codigoConfirmacion;
    }
    public void setCodigoConfirmacion(String codigoConfirmacion) {
        this.codigoConfirmacion = codigoConfirmacion;
    }
    public String getResetToken() {
        return resetToken;
    }
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    public LocalDateTime getTokenExpiration() {
        return tokenExpiration;
    }
    public void setTokenExpiration(LocalDateTime tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters de los nuevos campos
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getCiudad() {
        return city;
    }
    public void setCiudad(String ciudad) {
        this.city = ciudad;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getCodigoPostal() {
        return codigoPostal;
    }
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
}