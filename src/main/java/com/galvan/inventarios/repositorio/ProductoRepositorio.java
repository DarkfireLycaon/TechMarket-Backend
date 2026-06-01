package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {
    List<Producto> findByDisponibleTrue();
    List<Producto> findByCategoriaAndDisponibleTrue(String categoria);
    List<Producto> findByUsuarioId(Long usuarioId);

}
