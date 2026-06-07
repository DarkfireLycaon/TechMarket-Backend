package com.galvan.inventarios.repositorio;
import com.galvan.inventarios.modelo.Historial;
import com.galvan.inventarios.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialRepositorio extends JpaRepository<Historial, Long> {
    // Busca los registros del usuario, ordenados por fecha, limitado a los últimos 3
    List<Historial> findTop3ByUsuarioOrderByFechaVisitaDesc(Usuario usuario);
}