package com.galvan.inventarios.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "producto")  // ← Agrega esto para evitar problemas con nombres
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    private String nombre;  // ← Agrega este campo (lo necesitas para mostrar en la tienda)
    private String descripcion;
    private Double precioCompra;
    private Double precioVenta;
    private Integer stock;
    private String imagenUrl;
    private String categoria; // ELECTRONICA, HOGAR, TELEFONIA, INFORMATICA, GAMING
    private String marca;
    private Boolean disponible = true;

    // Nuevos campos para el marketplace
    private Double calificacion;  // Promedio de calificaciones (0-5)
    private Integer totalVendidos; // Contador de ventas
    private Boolean destacado = false; // Producto destacado en home

    @OneToMany(mappedBy = "producto")
    @JsonIgnore
    private List<CarritoItem> carritoItems;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "producto_proveedor",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "proveedor_id")
    )
    @JsonIgnore
    private List<Proveedor> proveedores;

    // Constructor vacío
    public Producto() {
        this.totalVendidos = 0;
        this.calificacion = 0.0;
    }

    // Constructor personalizado
    public Producto(String nombre, String descripcion, Double precioVenta, Integer stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.totalVendidos = 0;
        this.calificacion = 0.0;
        this.disponible = true;
    }

    // Getters y Setters completos
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
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

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public Integer getTotalVendidos() {
        return totalVendidos;
    }

    public void setTotalVendidos(Integer totalVendidos) {
        this.totalVendidos = totalVendidos;
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public List<Proveedor> getProveedores() {
        return proveedores;
    }

    public void setProveedores(List<Proveedor> proveedores) {
        this.proveedores = proveedores;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Método para reducir stock cuando se compra
    public void reducirStock(int cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
            this.totalVendidos += cantidad;
            if (this.stock == 0) {
                this.disponible = false;
            }
        }
    }

    // Método para aumentar stock
    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
        if (this.stock > 0 && !this.disponible) {
            this.disponible = true;
        }
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precioVenta +
                ", stock=" + stock +
                '}';
    }
}