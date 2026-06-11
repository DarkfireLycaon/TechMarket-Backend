package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Pedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {


    // Método que ya tenías para el usuario
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(Long usuarioId);

    // NUEVO: Método para obtener todos los pedidos ordenados por fecha
    List<Pedido> findAllByOrderByFechaPedidoDesc();

    // Método para contar pedidos por estado
    long countByEstado(String estado);

    // Método para sumar total ventas
    @Query("SELECT SUM(p.total) FROM Pedido p")
    Double sumarTotalVentas();

    // Consulta para ventas últimos 7 días
    @Query(value = "SELECT DATE(fecha_pedido), SUM(total) " +
            "FROM pedido " +
            "WHERE fecha_pedido >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(fecha_pedido) " +
            "ORDER BY DATE(fecha_pedido) ASC", nativeQuery = true)
    List<Object[]> findVentasUltimos7Dias();

    // NUEVO: Método para categoría más vendida
    // Asegúrate de que las relaciones (Pedido -> PedidoItem -> Producto -> Categoria)
    // coincidan con tus nombres de campos
    @Query("SELECT p.categoria FROM PedidoItem pi JOIN pi.producto p GROUP BY p.categoria ORDER BY COUNT(pi) DESC")
    List<String> findTopCategoria(Pageable pageable);

    List<Pedido> findByEstado(String estado);
}