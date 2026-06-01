package com.galvan.inventarios.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();

    private Double total = 0.0;


    // Constructor vacío
    public Carrito(long id, Usuario usuario,  List<CarritoItem> items, Double total) {
        this.id = id;
        this.usuario = usuario;
        this.items = items;
        this.total = total;

    }
    // Getters y Setters
    public Carrito() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public List<CarritoItem> getItems() {
        return items;
    }
    public void setItems(List<CarritoItem> items) {
        this.items = items;

    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
}