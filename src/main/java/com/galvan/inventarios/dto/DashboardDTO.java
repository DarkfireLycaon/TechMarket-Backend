package com.galvan.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data

@NoArgsConstructor
public class DashboardDTO {
    private Double totalVentas;
    private Long pedidosPendientes;
    private Long productosBajoStock;
    private List<String> categoriaTop;
    private Long usuariosRegistrados;

    public DashboardDTO(Double d, Long l1, Long l2, List<String> list, Long l3) {
        // Asigna los valores a tus atributos aquí
        // Ejemplo: this.precio = d; this.cantidad = l1; ...
    }
}