package com.galvan.inventarios.router;

import com.galvan.inventarios.estrategia.ChatStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRouter {

    private final List<ChatStrategy> estrategias;

    // Spring inyecta automáticamente TODAS las clases que implementan ChatStrategy
    public ChatRouter(List<ChatStrategy> estrategias) {
        // Ordenamos las estrategias según la prioridad (si implementan Comparable)
        // O simplemente las filtramos con cuidado.
        this.estrategias = estrategias;
    }

    public Object manejarMensaje(String mensaje, String email) {
        return estrategias.stream()
                .filter(e -> e.esAplicable(mensaje))
                .findFirst()
                .map(e -> e.procesar(mensaje, email))
                .orElse("Lo siento, no logré entender tu solicitud.");
    }
    }