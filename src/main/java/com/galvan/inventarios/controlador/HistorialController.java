package com.galvan.inventarios.controlador;

import com.galvan.inventarios.modelo.*;
import com.galvan.inventarios.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private HistorialRepositorio historialRepo;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio; // Necesario para buscar al usuario

    @Autowired
    private ProductoRepositorio productoRepositorio; // Necesario para obtener el producto

    @PostMapping("/visita/{productoId}")
    public ResponseEntity<?> registrarVisita(@PathVariable Long productoId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepositorio.findByEmail(username).orElseThrow(); // Asumiendo que usas email como identificador
        Producto producto = productoRepositorio.findById(productoId).orElseThrow();

        Historial historial = new Historial();
        historial.setUsuario(usuario);
        historial.setProducto(producto);
        historial.setFechaVisita(LocalDateTime.now());

        historialRepo.save(historial);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ultimos")
    public List<Producto> getUltimos() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepositorio.findByEmail(username).orElseThrow();

        return historialRepo.findTop3ByUsuarioOrderByFechaVisitaDesc(usuario)
                .stream()
                .map(Historial::getProducto)
                .distinct() // ¡Importante! Evita repetir el mismo producto si lo vio varias veces
                .limit(3)   // Aseguramos el límite
                .collect(Collectors.toList());
    }
}