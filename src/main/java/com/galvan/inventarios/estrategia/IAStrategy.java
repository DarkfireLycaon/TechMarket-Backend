package com.galvan.inventarios.estrategia;

import com.galvan.inventarios.servicio.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;

public class IAStrategy implements ChatStrategy {
    @Autowired
    private ChatBotService chatBotService;

    @Override
    public boolean esAplicable(String mensaje) {
        // La IA aplica por defecto si ninguna otra regla técnica se activó
        return true;
    }

    @Override
    public Object procesar(String mensaje, String email) {
        return chatBotService.obtenerRespuestaIA(mensaje);
    }
}
