package com.galvan.inventarios.dto;

import java.time.LocalDateTime;

public class PedidoDTO {
    private Long pedidoId;
    private String direccionEnvio;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaPedido;
    // ⬇️ ¡AÑADE ESTOS 3 CAMPOS!
    private Double total;
    private String clienteEmail;
    private String numeroSeguimiento;

    // CONSTRUCTOR ACTUALIZADO
    public PedidoDTO(Long pedidoId, String direccionEnvio, String metodoPago,
                            String estado, LocalDateTime fechaPedido, Double total,
                            String clienteEmail, String numeroSeguimiento) {
        this.pedidoId = pedidoId;
        this.direccionEnvio = direccionEnvio;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.fechaPedido = fechaPedido;
        this.total = total;
        this.clienteEmail = clienteEmail;
        this.numeroSeguimiento = numeroSeguimiento;
    }

    // No olvides generar sus respectivos Getters y Setters aquí abajo...
    public PedidoDTO() {}

    public Long getPedidoId() {
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
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
    public String getClienteEmail() {
        return clienteEmail;
    }
    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }
    public String getNumeroSeguimiento() {
        return numeroSeguimiento;
    }
    public void setNumeroSeguimiento(String numeroSeguimiento) {
        this.numeroSeguimiento = numeroSeguimiento;
    }

}
