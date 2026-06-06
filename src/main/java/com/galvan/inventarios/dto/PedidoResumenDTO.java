package com.galvan.inventarios.dto;

import java.time.LocalDateTime;

public class PedidoResumenDTO {
    private Long pedidoId;
    private String direccionEnvio;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaPedido;

    // Constructores, Getters y Setters
    // Asegúrate de que el constructor sea así:
    public PedidoResumenDTO(Long pedidoId, String direccionEnvio, String metodoPago, String estado, LocalDateTime fechaPedido) {
        this.pedidoId = pedidoId; // Long
        this.direccionEnvio = direccionEnvio; // String
        this.metodoPago = metodoPago; // String
        this.estado = estado; // String
        this.fechaPedido = fechaPedido; // LocalDateTime
    }

    // Getters...
    public long getPedidoId() {
        return pedidoId;
    }
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
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
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }
    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

}