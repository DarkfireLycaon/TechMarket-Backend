package com.galvan.inventarios.estrategia;

import com.galvan.inventarios.repositorio.PedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Order(2)
public class EnvioStrategy implements ChatStrategy {

    @Autowired
    private PedidoRepositorio pedidoRepository;

    private static final Pattern ID_PATTERN = Pattern.compile("\\d+"); // Detecta números

    @Override
    public boolean esAplicable(String mensaje) {
        String m = mensaje.toLowerCase();
        return m.contains("envio") || m.contains("pedido") || m.contains("estado");
    }

    @Override
    public String procesar(String mensaje, String email) {
        Matcher matcher = ID_PATTERN.matcher(mensaje);

        if (matcher.find()) {
            Long idPedido = Long.parseLong(matcher.group());
            return pedidoRepository.findById(idPedido)
                    .map(pedido -> {
                        // Verificación de seguridad: ¿Es este pedido del usuario?
                        if (!pedido.getUsuario().getEmail().equals(email)) {
                            return "No tienes permiso para consultar este pedido.";
                        }
                        return "El estado de tu pedido #" + idPedido + " es: **" + pedido.getEstado() + "**" +
                                (pedido.getNumeroSeguimiento() != null ? ". Número de seguimiento: " + pedido.getNumeroSeguimiento() : "");
                    })
                    .orElse("No encontré ningún pedido con el número #" + idPedido);
        }
        return "Para consultarte el estado, por favor escribe el número de tu pedido.";
    }
}