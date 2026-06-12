package com.galvan.inventarios.servicio;



import com.galvan.inventarios.dto.ProductoChatDTO;
import com.galvan.inventarios.modelo.Producto;

import com.galvan.inventarios.repositorio.ProductoRepositorio;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;



import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatBotService {

    @Autowired

    private ProductoRepositorio productoRepository;



    public String consultarStock(String nombreProducto) {

        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase(nombreProducto);

        if (productos.isEmpty()) {
            // Sugerencia inteligente: Listar 3 productos aleatorios o destacados
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

