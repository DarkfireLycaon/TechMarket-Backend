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
    private ChatBotService chatBotService; // Asegúrate de tener esta inyección

    @Autowired
    private ChatRouter chatRouter; // Ahora inyectamos el Router, no el servicio directo

    @GetMapping("/consulta")
    public ResponseEntity<Object> responderConsulta(@RequestParam(name = "nombre") String pregunta) {
        // Si no tienes el email del usuario en la petición, pasa un valor nulo o "invitado"
        String emailUsuario = "invitado@sistema.com";

        Object respuesta = chatRouter.gestionarPregunta(pregunta, emailUsuario);
        return ResponseEntity.ok(respuesta);
    }
}
