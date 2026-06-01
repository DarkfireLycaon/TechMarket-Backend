package com.galvan.inventarios.controlador;

import com.galvan.inventarios.modelo.Producto;
import com.galvan.inventarios.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicController {

    @Autowired
    private ProductoRepositorio productoRepositorio;  // ← Inyectar como instancia

    @GetMapping("/productos")
    public List<Producto> listarProductos() {
        return productoRepositorio.findByDisponibleTrue();  // ← usar this o la instancia
    }

    @GetMapping("/productos/categoria/{categoria}")
    public List<Producto> listarPorCategoria(@PathVariable String categoria) {
        return productoRepositorio.findByCategoriaAndDisponibleTrue(categoria);
    }

    @GetMapping("/productos/{id}")
    public Producto obtenerProducto(@PathVariable Integer id) {
        return productoRepositorio.findById(id).orElse(null);
    }
}