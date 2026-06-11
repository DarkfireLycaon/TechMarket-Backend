package com.galvan.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    private Double totalVentas;
    private Long pedidosPendientes;
    private Long productosBajoStock;
    private List<String> categoriaTop;
    private Long usuariosRegistrados;
}