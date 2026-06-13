package com.galvan.inventarios.servicio;



import com.galvan.inventarios.dto.ProductoChatDTO;
import com.galvan.inventarios.modelo.Producto;

import com.galvan.inventarios.repositorio.ProductoRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;



import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatBotService {

    @Autowired

    private ProductoRepositorio productoRepository;

    // Esta interfaz la inyecta automáticamente LangChain4j con tu configuración
    @Autowired
    private ChatLanguageModel chatModel;

    public String obtenerRespuestaIA(String pregunta) {
        // 1. Obtenemos un resumen de productos para que la IA sepa qué hay
        String inventarioResumen = productoRepository.findAll().stream()
                .map(p -> p.getNombre() + " (Stock: " + p.getStock() + ")")
                .limit(20) // Limitamos a 20 productos para no saturar la IA
                .reduce((a, b) -> a + ", " + b)
                .orElse("No hay productos disponibles.");

        // 2. Construimos el prompt (instrucciones)
        String prompt = "Actúa como un asistente de inventario amable. " +
                "Basado en este inventario: [" + inventarioResumen + "], " +
                "responde a la siguiente pregunta del cliente: " + pregunta;

        // 3. Enviamos a Gemini y devolvemos la respuesta
        return chatModel.generate(prompt);
    }

    public String consultarStock(String entradaUsuario) {

        // 1. Limpiamos: extraemos palabras clave relevantes (o simplificamos la búsqueda)
        // Aquí podrías usar una lista de marcas conocidas o simplemente buscar
        // por la palabra clave del producto.
        String nombreProducto = extraerPalabraClave(entradaUsuario);

        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase(nombreProducto);

        if (productos.isEmpty()) {
            return "No encontré '" + nombreProducto + "'. Pero tenemos estos modelos disponibles: " +
                    productoRepository.findAll().stream()
                            .filter(p -> p.getStock() > 0)
                            .limit(3)
                            .map(Producto::getNombre)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("Por ahora no tenemos stock en ningún producto.");
        }



        if (productos.size() > 5) {

            return "Encontré muchos productos (" + productos.size() + "). ¿Podrías ser más específico?";

        }



        // Si hay pocos, los listamos todos

        StringBuilder respuesta = new StringBuilder("Encontré estos productos: ");

        for (Producto p : productos) {

            respuesta.append("\n- ").append(p.getNombre()).append(": ")

                    .append(p.getStock() > 0 ? p.getStock() + " en stock." : "Agotado.");

        }

        return respuesta.toString();

    }
    // Método auxiliar para limpiar la basura del mensaje
    private String extraerPalabraClave(String entrada) {
        String m = entrada.toLowerCase();
        if (m.contains("iphone")) return "iPhone";
        if (m.contains("samsung")) return "Samsung";
        if (m.contains("xiaomi")) return "Xiaomi";
        return entrada; // Si no es ninguna, intenta buscar el texto original
    }
    public List<ProductoChatDTO> buscarProductosParaChat(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(p -> {
                    ProductoChatDTO dto = new ProductoChatDTO();
                    dto.id = p.getIdProducto(); // ¡Aquí está el ID para el enlace!
                    dto.nombre = p.getNombre();
                    dto.stockInfo = p.getStock() > 0 ? "Disponible" : "Agotado";
                    return dto;
                }).collect(Collectors.toList());
    }
}

