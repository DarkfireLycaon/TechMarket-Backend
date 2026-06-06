package com.galvan.inventarios.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    private LocalDateTime fechaPedido;
    private Double total;
    private String estado; // PENDIENTE, PAGADO, ENVIADO, ENTREGADO, CANCELADO
    private String direccionEnvio;
    private String metodoPago;
    private String numeroSeguimiento;


    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<PedidoItem> items = new ArrayList<>();

    // Getters y Setters
    public Pedido() {}

    public Pedido(long id, Usuario usuario, LocalDateTime fechaPedido, String estado, String direccionEnvio, String metodoPago, String numeroSeguimiento ) {
     this.id = id;
     this.usuario = usuario;
     this.fechaPedido = fechaPedido;
     this.estado = estado;
     this.direccionEnvio = direccionEnvio;
     this.metodoPago = metodoPago;
     this.total = 0.0;
     this.items = new ArrayList<>();
     this.numeroSeguimiento = numeroSeguimiento;

    }
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
    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }
    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getDireccionEnvio() {
        return direccionEnvio;

    }
    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }
    public String getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    public List<PedidoItem> getItems() {
        return items;
    }
    public void setItems(List<PedidoItem> items) {
        this.items = items;
    }
    public String getNumeroSeguimiento() { return numeroSeguimiento; }
    public void setNumeroSeguimiento(String numeroSeguimiento) { this.numeroSeguimiento = numeroSeguimiento; }}


