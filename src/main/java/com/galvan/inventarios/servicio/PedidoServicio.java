package com.galvan.inventarios.servicio;

import com.galvan.inventarios.dto.PedidoDTO;
import com.galvan.inventarios.modelo.*;
import com.galvan.inventarios.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    // ==========================================================================
    // LÓGICA DE NEGOCIO (ENTIDADES PURAS)
    // ==========================================================================

    /**
     * Crear pedido a partir del carrito de compras actual del usuario
     */
    @Transactional
    public Pedido crearPedido(Usuario usuario, PedidoDTO pedidoDTO) {
        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito vacío"));

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("No hay productos en el carrito");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setDireccionEnvio(pedidoDTO.getDireccionEnvio());
        pedido.setMetodoPago(pedidoDTO.getMetodoPago());
        pedido.setNumeroSeguimiento(generarNumeroSeguimiento());

        double total = 0.0;

        for (CarritoItem carritoItem : carrito.getItems()) {
            Producto producto = carritoItem.getProducto();

            if (producto.getStock() < carritoItem.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            producto.reducirStock(carritoItem.getCantidad());
            productoRepository.save(producto);

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
        Pedido nuevoPedido = pedidoRepository.save(pedido);

        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);

        return nuevoPedido;
    }

    public List<Pedido> obtenerPedidosUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuario.getId());
    }

    public Pedido obtenerPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public List<Pedido> obtenerPedidosPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    /**
     * Actualizar estado del pedido (Devuelve stock si pasa a CANCELADO)
     */
    @Transactional
    public Pedido actualizarEstado(Long pedidoId, String nuevoEstado) {
        Pedido pedido = obtenerPedido(pedidoId);
        pedido.setEstado(nuevoEstado);

        if (nuevoEstado.equals("CANCELADO") && !pedido.getEstado().equals("CANCELADO")) {
            for (PedidoItem item : pedido.getItems()) {
                Producto producto = item.getProducto();
                producto.aumentarStock(item.getCantidad());
                productoRepository.save(producto);
            }
        }

        return pedidoRepository.save(pedido);
    }

    private String generarNumeroSeguimiento() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    // ==========================================================================
    // LÓGICA DE TRANSFERENCIA DE DATOS (MAPPING A DTOs PARA ANGULAR)
    // ==========================================================================

    /**
     * Convertidor Centralizado: Mapea la entidad al DTO con los 8 campos requeridos
     */
    private PedidoDTO convertirAResumen(Pedido p) {
        return new PedidoDTO(
                p.getId(),
                p.getDireccionEnvio(),
                p.getMetodoPago(),
                p.getEstado(),
                p.getFechaPedido(),
                p.getTotal(), // ✅ Agregado el total cobrado
                p.getUsuario() != null ? p.getUsuario().getEmail() : "Usuario General", // ✅ Agregado el email del cliente de forma segura
                p.getNumeroSeguimiento() // ✅ Agregado el número de tracking
        );
    }

    /**
     * Obtiene los pedidos resumidos de un cliente específico
     */
    public List<PedidoDTO> obtenerMisPedidos(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId)
                .stream()
                .map(this::convertirAResumen)
                .collect(Collectors.toList());
    }

    /**
     * Alias compatible por si lo usas en otros controladores
     */
    public List<PedidoDTO> obtenerPedidosPorUsuarioId(Long usuarioId) {
        return obtenerMisPedidos(usuarioId);
    }

    /**
     * Obtiene el listado global de pedidos formateado a DTO para el Panel de Administrador
     */
    public List<PedidoDTO> obtenerTodosPedidosResumen() {
        // Nota: Si tu repositorio no tiene 'findAllByOrderByFechaPedidoDesc', usa 'findAll()'
        return pedidoRepository.findAll()
                .stream()
                .map(this::convertirAResumen)
                .collect(Collectors.toList());
    }

    /**
     * Busca un único pedido mapeado directamente a DTO (Evita bucles infinitos en detalles)
     */
    public PedidoDTO obtenerPedidoDTO(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        return convertirAResumen(pedido);
    }
    /**
     * Genera el resumen para el Dashboard de Administrador
     */
    public Map<String, Object> obtenerResumenParaDashboard() {
        Map<String, Object> resumen = new HashMap<>();

        // 1. Total ventas acumuladas
        Double totalVentas = pedidoRepository.sumarTotalVentas();
        resumen.put("totalVentas", totalVentas != null ? totalVentas : 0.0);

        // 2. Pedidos pendientes
        resumen.put("pedidosPendientes", pedidoRepository.countByEstado("PENDIENTE"));

        // 3. Stock crítico (lo delegamos al repositorio de productos)
        resumen.put("productosBajoStock", productoRepository.countByStockLessThan(5));

        // 4. Usuarios totales
        resumen.put("usuariosRegistrados", usuarioRepository.count());

        // 5. Categoría más vendida (usando Pageable para obtener el primero)
        // Dentro de tu metodo obtenerResumenParaDashboard()
        List<String> topCat = pedidoRepository.findTopCategoria(PageRequest.of(0, 1));
        resumen.put("categoriaTop", !topCat.isEmpty() ? topCat.get(0) : "Sin ventas");

        // 6. Tendencia semanal
        resumen.put("ventasUltimos7Dias", pedidoRepository.findVentasUltimos7Dias());

        return resumen;
    }
    // 1. Total de ventas
    public Double sumarTotalVentas() {
        Double total = pedidoRepository.sumarTotalVentas();
        return total != null ? total : 0.0;
    }

    // 2. Cantidad total de pedidos
    public long count() {
        return pedidoRepository.count();
    }

    // 3. Ventas por día (últimos 7 días)
    public List<Object[]> findVentasUltimos7Dias() {
        return pedidoRepository.findVentasUltimos7Dias();
    }
}