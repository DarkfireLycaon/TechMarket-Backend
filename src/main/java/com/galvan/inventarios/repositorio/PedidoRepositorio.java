package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Pedido;
import com.galvan.inventarios.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {

    // Buscar pedidos por usuario
    List<Pedido> findByUsuario(Usuario usuario);

    // Buscar pedidos por estado
    List<Pedido> findByEstado(String estado);

    // Buscar pedidos por rango de fechas
    List<Pedido> findByFechaPedidoBetween(LocalDateTime inicio, LocalDateTime fin);

    // Buscar pedidos de un usuario por estado
    List<Pedido> findByUsuarioAndEstado(Usuario usuario, String estado);

    // Buscar pedido por número de seguimiento
    Pedido findByNumeroSeguimiento(String numeroSeguimiento);

    // Contar pedidos por estado
    Long countByEstado(String estado);

    // Buscar pedidos recientes (últimos 30 días)
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido >= :fecha ORDER BY p.fechaPedido DESC")
    List<Pedido> findPedidosRecientes(@Param("fecha") LocalDateTime fecha);

    // Buscar pedidos con total mayor a un valor
    List<Pedido> findByTotalGreaterThan(Double total);

    // Buscar pedidos de un usuario con ordenamiento
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(Usuario usuario);

    // Buscar pedidos por método de pago
    List<Pedido> findByMetodoPago(String metodoPago);

    // Obtener el total de ventas en un período
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.fechaPedido BETWEEN :inicio AND :fin AND p.estado = 'ENTREGADO'")
    Double getTotalVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Obtener los productos más vendidos
    @Query(value = "SELECT p.id_producto, p.nombre, SUM(pi.cantidad) as total_vendido " +
            "FROM pedido_item pi " +
            "JOIN producto p ON pi.producto_id = p.id_producto " +
            "JOIN pedidos ped ON pi.pedido_id = ped.id " +
            "WHERE ped.estado = 'ENTREGADO' " +
            "GROUP BY p.id_producto, p.nombre " +
            "ORDER BY total_vendido DESC LIMIT 10", nativeQuery = true)
    List<Object[]> findProductosMasVendidos();
}