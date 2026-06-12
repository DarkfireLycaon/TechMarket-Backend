package com.galvan.inventarios.controlador;



import com.galvan.inventarios.router.ChatRouter;
import com.galvan.inventarios.servicio.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatBotController {

    @Autowired
    private ChatRouter chatRouter; // Ahora inyectamos el Router, no el servicio directo

    @GetMapping("/consulta")
    public ResponseEntity<Object> responderConsulta(@RequestParam(name = "nombre") String nombre) {
        System.out.println("🔍 Recibida consulta: '" + nombre + "'");

        String emailUsuario = "usuario@ejemplo.com";
        Object respuesta = chatRouter.manejarMensaje(nombre, emailUsuario);

        // Spring detectará automáticamente si 'respuesta' es String o List
        // y lo serializará correctamente a JSON.
        return ResponseEntity.ok(respuesta);
    }
}
