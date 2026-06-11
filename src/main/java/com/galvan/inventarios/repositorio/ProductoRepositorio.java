package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {
    List<Producto> findByDisponibleTrue();
    List<Producto> findByCategoriaAndDisponibleTrue(String categoria);
    List<Producto> findByUsuarioId(Long usuarioId);
    @Query("SELECT p FROM Producto p WHERE p.usuario.id = :usuarioId")
    List<Producto> listarProductosPorUsuario(@Param("usuarioId") Long usuarioId);
    List<Producto> findByEsOfertaTrue();
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    Long countByStockLessThan(Integer stock);
}
