package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.*;
import com.galvan.inventarios.repositorio.CarritoRepositorio;
import com.galvan.inventarios.repositorio.ProductoRepositorio;  // ← Usar ProductoRepositorio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepositorio carritoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;  // ← Cambiado

    // Obtener carrito
    public Carrito obtenerCarrito(Usuario usuario) {
        return carritoRepositorio.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    nuevo.setTotal(0.0);
                    return carritoRepositorio.save(nuevo);
                });
    }

    // Agregar producto
    @Transactional
    public Carrito agregarProducto(Usuario usuario, Integer productoId, Integer cantidad) {
        Carrito carrito = obtenerCarrito(usuario);
        Producto producto = productoRepositorio.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si ya existe
        CarritoItem itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProducto().getIdProducto().equals(productoId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            itemExistente.setSubtotal(itemExistente.getPrecioUnitario() * itemExistente.getCantidad());
        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecioVenta());
            nuevoItem.setSubtotal(producto.getPrecioVenta() * cantidad);
            carrito.getItems().add(nuevoItem);
        }

        // Actualizar total
        double total = carrito.getItems().stream()
                .mapToDouble(CarritoItem::getSubtotal)
                .sum();
        carrito.setTotal(total);

        return carritoRepositorio.save(carrito);
    }

    // Eliminar producto
    @Transactional
    public Carrito eliminarProducto(Usuario usuario, Integer productoId) {
        Carrito carrito = obtenerCarrito(usuario);
        carrito.getItems().removeIf(item -> item.getProducto().getIdProducto().equals(productoId));

        double total = carrito.getItems().stream()
                .mapToDouble(CarritoItem::getSubtotal)
                .sum();
        carrito.setTotal(total);

        return carritoRepositorio.save(carrito);
    }

    // Actualizar cantidad
    @Transactional
    public Carrito actualizarCantidad(Usuario usuario, Integer productoId, Integer cantidad) {
        Carrito carrito = obtenerCarrito(usuario);

        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getProducto().getIdProducto().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (cantidad <= 0) {
            carrito.getItems().remove(item);
        } else {
            item.setCantidad(cantidad);
            item.setSubtotal(item.getPrecioUnitario() * cantidad);
        }

        double total = carrito.getItems().stream()
                .mapToDouble(CarritoItem::getSubtotal)
                .sum();
        carrito.setTotal(total);

        return carritoRepositorio.save(carrito);
    }

    // Vaciar carrito
    public void vaciarCarrito(Usuario usuario) {
        Carrito carrito = obtenerCarrito(usuario);
        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepositorio.save(carrito);
    }
}