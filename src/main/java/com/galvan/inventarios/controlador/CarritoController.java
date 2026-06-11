package com.galvan.inventarios.controlador;

import com.galvan.inventarios.modelo.Carrito;
import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.repositorio.UsuarioRepositorio;  // ← Cambiar a UsuarioRepositorio
import com.galvan.inventarios.servicio.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "http://localhost:4200")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;  // ← Cambiar a UsuarioRepositorio

    @GetMapping
    public ResponseEntity<?> obtenerCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        Carrito carrito = carritoService.obtenerCarrito(usuario);
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(@RequestBody Map<String, Object> request) {
        try {
            Usuario usuario = obtenerUsuarioActual();
            Integer productoId = (Integer) request.get("productoId");
            Integer cantidad = (Integer) request.get("cantidad");

            if (cantidad == null || cantidad <= 0) cantidad = 1;

            Carrito carrito = carritoService.agregarProducto(usuario, productoId, cantidad);
            return ResponseEntity.ok(Map.of("mensaje", "Producto agregado", "carrito", carrito));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer productoId) {
        try {
            Usuario usuario = obtenerUsuarioActual();
            Carrito carrito = carritoService.eliminarProducto(usuario, productoId);
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado", "carrito", carrito));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<?> actualizarCantidad(@PathVariable Integer productoId, @RequestBody Map<String, Integer> request) {
        try {
            Usuario usuario = obtenerUsuarioActual();
            Integer cantidad = request.get("cantidad");
            Carrito carrito = carritoService.actualizarCantidad(usuario, productoId, cantidad);
            return ResponseEntity.ok(Map.of("mensaje", "Carrito actualizado", "carrito", carrito));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<?> vaciarCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        carritoService.vaciarCarrito(usuario);
        return ResponseEntity.ok(Map.of("mensaje", "Carrito vaciado"));
    }

    private Usuario obtenerUsuarioActual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}