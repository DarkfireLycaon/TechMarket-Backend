package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Carrito;
import com.galvan.inventarios.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepositorio extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuario(Usuario usuario);
}