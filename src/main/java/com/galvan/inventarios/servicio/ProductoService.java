package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Producto;
import com.galvan.inventarios.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ProductoService implements IProductoServicio {
    @Autowired
    private ProductoRepositorio productoRepositorio;
    @Override
    public List<Producto> listarProductos() {
         return this.productoRepositorio.findAll();
    }


    public Producto buscarProductoPorId(Integer idProducto) {
        Producto producto = this.productoRepositorio.findById(idProducto).orElse(null);
        return producto;
    }

    @Override
    public Producto guardarProducto(Producto producto) {
      return this.productoRepositorio.save(producto);
    }

    @Override
    public void eliminarProducto(Integer idProducto) {
     this.productoRepositorio.deleteById(idProducto);
    }

    // Cambia de findByUsuarioId a findByUsuarioId (debe coincidir con ProductoRepositorio)
    public List<Producto> obtenerProductosPorUsuario(Long usuarioId) {
        return productoRepositorio.findByUsuarioId(usuarioId);
    }
    public List<Producto> getOfertas() {
        return productoRepositorio.findByEsOfertaTrue();
    }
    public Producto actualizarEstadoOferta(Integer id, Boolean esOferta, Double precioOferta) {
        Producto producto = productoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setEsOferta(esOferta);
        // Solo actualizamos el precio si realmente es una oferta
        if (Boolean.TRUE.equals(esOferta)) {
            producto.setPrecioOferta(precioOferta);
        } else {
            producto.setPrecioOferta(null);
        }

        return productoRepositorio.save(producto);
    }
    // En ProductoService.java (puedes usarlo para público)
    public List<Producto> listarPorCategoriaPublico(String categoria) {
        // Nota: NO llamamos a lógica que requiera usuario logueado
        return productoRepositorio.findByCategoriaAndDisponibleTrue(categoria);
    }

    public List<Producto> buscarPorNombre(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of(); // Devuelve lista vacía si el término está vacío
        }
        return productoRepositorio.findByNombreContainingIgnoreCase(query);
    }
}
