package com.galvan.inventarios.servicio;

import com.galvan.inventarios.dto.PedidoDTO;
import com.galvan.inventarios.modelo.*;
import com.galvan.inventarios.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.galvan.inventarios.dto.PedidoResumenDTO;
import java.util.stream.Collectors;

@Service
public class PedidoServicio {

    @Autowired
    private PedidoRepositorio pedidoRepository;

    @Autowired
    private CarritoRepositorio carritoRepository;

    @Autowired
    private ProductoRepositorio productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear pedido a partir del carrito
    @Transactional
    public Pedido crearPedido(Usuario usuario, PedidoDTO pedidoDTO) {
        // Obtener carrito del usuario
        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito vacío"));

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("No hay productos en el carrito");
        }

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setDireccionEnvio(pedidoDTO.getDireccionEnvio());
        pedido.setMetodoPago(pedidoDTO.getMetodoPago());
        pedido.setNumeroSeguimiento(generarNumeroSeguimiento());

        double total = 0.0;

        // Convertir items del carrito a items del pedido
        for (CarritoItem carritoItem : carrito.getItems()) {
            Producto producto = carritoItem.getProducto();

            // Verificar stock
            if (producto.getStock() < carritoItem.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            // Reducir stock
            producto.reducirStock(carritoItem.getCantidad());
            productoRepository.save(producto);

            // Crear item del pedido
            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedido);
            pedidoItem.setProducto(producto);
            pedidoItem.setCantidad(carritoItem.getCantidad());
            pedidoItem.setPrecioUnitario(carritoItem.getPrecioUnitario());
            pedidoItem.setSubtotal(carritoItem.getSubtotal());

            pedido.getItems().add(pedidoItem);
            total += carritoItem.getSubtotal();
        }

        pedido.setTotal(total);

        // Guardar pedido
        Pedido nuevoPedido = pedidoRepository.save(pedido);

        // Vaciar carrito
        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);

        return nuevoPedido;
    }


    public List<Pedido> obtenerPedidosUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuario.getId());
    }

    // Obtener pedido por ID
    public Pedido obtenerPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    // Actualizar estado del pedido (para administradores)
    @Transactional
    public Pedido actualizarEstado(Long pedidoId, String nuevoEstado) {
        Pedido pedido = obtenerPedido(pedidoId);
        pedido.setEstado(nuevoEstado);

        // Si el pedido se cancela, devolver stock
        if (nuevoEstado.equals("CANCELADO") && !pedido.getEstado().equals("CANCELADO")) {
            for (PedidoItem item : pedido.getItems()) {
                Producto producto = item.getProducto();
                producto.aumentarStock(item.getCantidad());
                productoRepository.save(producto);
            }
        }

        return pedidoRepository.save(pedido);
    }

    // Generar número de seguimiento único
    private String generarNumeroSeguimiento() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Obtener todos los pedidos (admin)
    public List<Pedido> obtenerTodosPedidos() {
        return pedidoRepository.findAll();
    }

    // Obtener pedidos por estado
    public List<Pedido> obtenerPedidosPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }
    // Ejemplo en tu Servicio de Pedidos
    public List<PedidoResumenDTO> obtenerMisPedidos(Long usuarioId) {
        // 1. Obtenemos las entidades desde el repo
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId);

        // 2. Transformamos a DTO (esto es lo que evita el error de truncamiento de JSON)
        return pedidos.stream()
                .map(p -> new PedidoResumenDTO(
                        p.getId(),
                        p.getDireccionEnvio(),
                        p.getMetodoPago(),
                        p.getEstado(),
                        p.getFechaPedido()
                ))
                .collect(Collectors.toList());
    }
    // 1. Para el cliente (solo ve sus pedidos y datos resumidos)
    public List<PedidoResumenDTO> obtenerPedidosPorUsuarioId(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId)
                .stream()
                .map(this::convertirAResumen)
                .collect(Collectors.toList());
    }

    // 2. Para el Admin (ve todos los pedidos, quizás con más detalle)
    public List<PedidoResumenDTO> obtenerTodosPedidosResumen() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc()
                .stream()
                .map(this::convertirAResumen)
                .collect(Collectors.toList());
    }

    // Método privado para mantener el código limpio (evita repetir el map)
    private PedidoResumenDTO convertirAResumen(Pedido p) {
        return new PedidoResumenDTO(
                p.getId(),
                p.getDireccionEnvio(),
                p.getMetodoPago(),
                p.getEstado(),
                p.getFechaPedido()
        );
    }
    // En PedidoServicio.java

    public PedidoResumenDTO obtenerPedidoDTO(Long pedidoId) {
        // 1. Buscamos la entidad original
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // 2. Convertimos a DTO manualmente para evitar la recursividad
        return new PedidoResumenDTO(
                pedido.getId(),
                pedido.getDireccionEnvio(),
                pedido.getMetodoPago(),
                pedido.getEstado(),
                pedido.getFechaPedido()
        );
    }
}

