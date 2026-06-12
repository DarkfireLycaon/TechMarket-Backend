package com.galvan.inventarios.estrategia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvan.inventarios.dto.ComparativaDTO;
import com.galvan.inventarios.modelo.Producto;
import com.galvan.inventarios.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(1)
public class ComparadorStrategy implements ChatStrategy {

    @Autowired
    private ProductoRepositorio productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean esAplicable(String mensaje) {
        String m = mensaje.toLowerCase();
        boolean esComparacion = m.contains("comparar") || m.contains("vs") ||
                m.contains("diferencia entre") || m.contains(" y ");

        if (!esComparacion) return false;

        // Mejor: Buscar solo los productos que tengan alguna palabra clave del mensaje
        // Si el mensaje es "iPhone vs Samsung", buscamos productos que tengan "iphone" o "samsung"
        // Esto es mucho más ligero que un findAll()
        return true; // Simplifica aquí y deja que el método 'procesar' valide la existencia real
    }

    @Override
    public Object procesar(String mensaje, String email) {
        // 1. Limpiamos el mensaje: quitamos conectores innecesarios
        String limpio = mensaje.toLowerCase()
                .replace("quiero comparar el", "")
                .replace("quiero comparar", "")
                .replace("comparar", "");

        // 2. Separamos usando los conectores
        String[] separadores = {" y ", " vs ", " con "};
        String[] partes = null;

        for (String sep : separadores) {
            if (limpio.contains(sep)) {
                partes = limpio.split(sep);
                break;
            }
        }

        if (partes == null || partes.length < 2) {
            return "No entiendo la comparación. Prueba escribir: 'iPhone vs Samsung'";
        }

        // 3. Búsqueda tolerante
        Producto p1 = buscarProductoTolerante(partes[0].trim());
        Producto p2 = buscarProductoTolerante(partes[1].trim());

        if (p1 == null || p2 == null) {
            return "No encontré uno de los productos. Prueba con marcas como 'iPhone', 'Samsung' o 'Xiaomi'.";
        }

        return generarComparativaDTO(p1, p2);
    }

    // NUEVO: Método de búsqueda tolerante
    private Producto buscarProductoTolerante(String nombre) {
        // Obtenemos todos los productos (o idealmente usa una búsqueda de texto completo en BD)
        List<Producto> todos = productoRepository.findAll();

        return todos.stream()
                .filter(p -> {
                    String nombreBD = p.getNombre().toLowerCase();
                    String busqueda = nombre.toLowerCase();
                    // Acepta si el nombre contiene la palabra clave o viceversa
                    return nombreBD.contains(busqueda) || busqueda.contains(nombreBD.split(" ")[0]);
                })
                .findFirst()
                .orElse(null);
    }
    private ComparativaDTO generarComparativaDTO(Producto p1, Producto p2) {
        return new ComparativaDTO(p1, p2);
    }
}