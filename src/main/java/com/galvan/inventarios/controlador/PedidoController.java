package com.galvan.inventarios.controlador;

import com.galvan.inventarios.dto.PedidoDTO;
import com.galvan.inventarios.dto.PedidoResumenDTO;
import com.galvan.inventarios.modelo.Pedido;
import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.repositorio.UsuarioRepositorio;
import com.galvan.inventarios.servicio.PedidoServicio;
import com.galvan.inventarios.servicio.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.galvan.inventarios.servicio.PaypalServicio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


 @RestController
 @RequestMapping("/api/pedidos")
 @CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    @Autowired
    private PedidoServicio pedidoService;

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @Autowired
    private ProductoService productoService;

    // Crear pedido
    @PostMapping("/crear")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        try {
            Usuario usuario = obtenerUsuarioActual();
            Pedido pedido = pedidoService.crearPedido(usuario, pedidoDTO);

            // Devolver solo los datos necesarios, no el objeto Pedido completo
            Map<String, Object> response = new java.util.LinkedHashMap<>();
            response.put("mensaje", "Pedido creado exitosamente");
            response.put("pedidoId", pedido.getId());
            response.put("numeroSeguimiento", pedido.getNumeroSeguimiento());
            response.put("total", pedido.getTotal());
            response.put("fecha", pedido.getFechaPedido());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // Obtener mis pedidos
    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> obtenerMisPedidos() {
        Usuario usuario = obtenerUsuarioActual();
        // AHORA: Llamamos al servicio que devuelve el DTO (a prueba de bucles circulares)
        List<PedidoDTO> pedidos = pedidoService.obtenerMisPedidos(usuario.getId());

        return ResponseEntity.ok(pedidos);
    }



     // Y corrige también el detalle de pedido (si devuelves 'pedido' completo, habrá recursividad)
     @GetMapping("/{pedidoId}")
     public ResponseEntity<?> obtenerPedido(@PathVariable Long pedidoId) {
         // Si quieres evitar el error, mapea esto a un DTO detallado
         return ResponseEntity.ok(pedidoService.obtenerPedidoDTO(pedidoId));
     }

    // Cancelar pedido
    @PutMapping("/cancelar/{pedidoId}")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long pedidoId) {
        try {
            pedidoService.actualizarEstado(pedidoId, "CANCELADO");

            // Respuesta simple y segura
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pedido cancelado exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========== ENDPOINTS PARA ADMINISTRADORES ==========

     // Ejemplo para todos los endpoints, no solo mis-pedidos
     @GetMapping("/admin/todos")
     public ResponseEntity<List<PedidoDTO>> obtenerTodosPedidos() {
         // Debes crear este método en PedidoServicio igual que hiciste con obtenerMisPedidos
         return ResponseEntity.ok(pedidoService.obtenerTodosPedidosResumen());
     }

    @PutMapping("/admin/estado/{pedidoId}")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long pedidoId,
            @RequestBody Map<String, String> request) {
        try {
            String nuevoEstado = request.get("estado");
            Pedido pedido = pedidoService.actualizarEstado(pedidoId, nuevoEstado);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Estado actualizado a: " + nuevoEstado,
                    "pedido", pedido
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/estado/{estado}")
    public ResponseEntity<?> obtenerPedidosPorEstado(@PathVariable String estado) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    private Usuario obtenerUsuarioActual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
     @Autowired
     private PaypalServicio paypalServicio;

     // Endpoint 1: Iniciar Pago en PayPal
     @PostMapping("/{pedidoId}/pagar-paypal")
     public ResponseEntity<?> iniciarPagoPaypal(@PathVariable Long pedidoId) {
         try {
             Pedido pedido = pedidoService.obtenerPedido(pedidoId);

             // Verificación de seguridad
             Usuario usuarioActual = obtenerUsuarioActual();
             if (!pedido.getUsuario().getId().equals(usuarioActual.getId())) {
                 return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
             }

             // Crear orden en PayPal
             com.paypal.orders.Order paypalOrder = paypalServicio.crearOrdenPaypal(pedido.getTotal(), pedido.getId());

             // Buscar la URL a la que Angular debe redirigir al usuario para que pague
             String approveUrl = paypalOrder.links().stream()
                     .filter(link -> link.rel().equals("approve"))
                     .findFirst()
                     .orElseThrow(() -> new RuntimeException("No se encontró la URL de aprobación"))
                     .href();

             return ResponseEntity.ok(Map.of(
                     "paypalOrderId", paypalOrder.id(),
                     "redirectUrl", approveUrl
             ));
         } catch (Exception e) {
             return ResponseEntity.badRequest().body(Map.of("error", "Error con PayPal: " + e.getMessage()));
         }
     }

     // Endpoint 2: Confirmar y capturar el dinero tras el pago del cliente
     @PostMapping("/capturar-paypal")
     public ResponseEntity<?> capturarPagoPaypal(@RequestBody Map<String, String> body) {
         try {
             String token = body.get("token"); // ID de la orden de PayPal recibido de Angular
             Long pedidoId = Long.parseLong(body.get("pedidoId"));

             // Capturar el dinero desde el servidor de PayPal
             com.paypal.orders.Order order = paypalServicio.capturarPago(token);

             if ("COMPLETED".equals(order.status())) {
                 // Actualizar tu base de datos mediante tu servicio existente
                 pedidoService.actualizarEstado(pedidoId, "PAGADO");

                 // Opcional: registrar el método de pago explícito si no venía en el DTO
                 Pedido pedido = pedidoService.obtenerPedido(pedidoId);
                 pedido.setMetodoPago("PAYPAL");
                 // Nota: Asegúrate de guardar los cambios del pedido si tu método actualizarEstado no lo hace ya por dentro

                 return ResponseEntity.ok(Map.of("mensaje", "Pago procesado y pedido completado con éxito"));
             } else {
                 return ResponseEntity.badRequest().body(Map.of("error", "El estado del pago en PayPal no es COMPLETED"));
             }
         } catch (Exception e) {
             return ResponseEntity.badRequest().body(Map.of("error", "Fallo al capturar pago: " + e.getMessage()));
         }
     }
     public Map<String, Object> obtenerResumenVentas() {
         Map<String, Object> resumen = new HashMap<>();

         // 1. Total ventas (Suma de los totales de pedidos)
         Double total = pedidoService.sumarTotalVentas();
         resumen.put("totalVentas", total != null ? total : 0.0);

         // 2. Cantidad total de pedidos
         resumen.put("cantidadPedidos", pedidoService.count());

         // 3. Ventas por día (últimos 7 días) - Esto requiere una lógica de agrupación
         // Aquí podrías llamar a un método que retorne una lista de objetos con fecha y suma
         resumen.put("ventasUltimos7Dias", pedidoService.findVentasUltimos7Dias());

         return resumen;
     }

}

