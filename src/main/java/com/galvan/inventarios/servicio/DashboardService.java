package com.galvan.inventarios.servicio;
import com.galvan.inventarios.dto.DashboardDTO;
import com.galvan.inventarios.repositorio.PedidoRepositorio;
import com.galvan.inventarios.repositorio.ProductoRepositorio;
import com.galvan.inventarios.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired private ProductoRepositorio productoRepositorio;
    @Autowired private PedidoRepositorio pedidoRepositorio; // Asumiendo que tienes este
    @Autowired private UsuarioRepositorio usuarioRepositorio;

    public DashboardDTO obtenerDatosDashboard() {
        // 1. Total Ventas (suma de un campo 'total' en tu tabla pedidos)
        Double total = pedidoRepositorio.sumarTotalVentas();

        // 2. Pedidos Pendientes
        Long pendientes = pedidoRepositorio.countByEstado("PENDIENTE");

        // 3. Stock bajo (ej: menos de 5 unidades)
        Long bajoStock = productoRepositorio.countByStockLessThan(5);

        // 4. Usuarios registrados
        Long totalUsuarios = usuarioRepositorio.count();

        // 5. Categoría más vendida (debes tener este método en tu repositorio)
         List<String> categoria = pedidoRepositorio.findTopCategoria(PageRequest.of(0, 1));

        return new DashboardDTO(total, pendientes, bajoStock, categoria, totalUsuarios);
    }
}