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


    public Producto buscarProductoPorId(Long idProducto) {
        Producto producto = this.productoRepositorio.findById(idProducto).orElse(null);
        return producto;
    }

    @Override
    public Producto guardarProducto(Producto producto) {
      return this.productoRepositorio.save(producto);
    }

    @Override
    public void eliminarProducto(Long idProducto) {
     this.productoRepositorio.deleteById(idProducto);
    }

    // Cambia de findByUsuarioId a findByUsuarioId (debe coincidir con ProductoRepositorio)
    public List<Producto> obtenerProductosPorUsuario(Long usuarioId) {
        return productoRepositorio.findByUsuarioId(usuarioId);
    }
}
