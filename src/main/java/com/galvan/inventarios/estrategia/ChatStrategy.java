package com.galvan.inventarios.estrategia;

public interface ChatStrategy {
    boolean esAplicable(String mensaje); // ¿Este módulo sabe responder esto?
    Object procesar(String mensaje, String email); // Ejecuta la lógica
}