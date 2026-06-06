package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Pedido;
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

    // Otros métodos...
    List<Pedido> findByEstado(String estado);


}