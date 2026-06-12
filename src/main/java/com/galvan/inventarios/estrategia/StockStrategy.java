package com.galvan.inventarios.estrategia;

import com.galvan.inventarios.servicio.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(3)
public class StockStrategy implements ChatStrategy {
    @Autowired
    private ChatBotService chatBotService;

    @Override
    public boolean esAplicable(String mensaje) {
        // Si el mensaje no es una comparativa o un pedido,
        // asumimos que es una consulta de producto.
        return true;
    }

    @Override
    public Object procesar(String mensaje, String email) {
        // Buscamos los productos estructurados
        var productos = chatBotService.buscarProductosParaChat(mensaje);

        // Si encontró resultados, devolvemos la lista de DTOs (esto será JSON en el frontend)
        if (!productos.isEmpty() && productos.size() <= 5) {
            return productos;
        }

        // Si no encontró nada o hay demasiados, devolvemos el texto explicativo
        return chatBotService.consultarStock(mensaje);
    }
}