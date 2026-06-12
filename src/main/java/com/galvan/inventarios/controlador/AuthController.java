package com.galvan.inventarios.controlador;

import com.galvan.inventarios.modelo.ResetPasswordDTO;
import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.galvan.inventarios.config.JwtUtils;
import com.galvan.inventarios.repositorio.UsuarioRepositorio;
import java.net.URI;
import java.security.Principal; // <--- OBLIGATORIO PARA IDENTIFICAR AL TOKEN JWT

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private UsuarioServicio usuarioService;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ NUEVO ENDPOINT: Envía los datos guardados del perfil al checkout de Angular
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No estás autenticado o el token expiró"));
            }

            // Sacamos el email que Spring extrajo del JWT de manera segura
            String email = principal.getName();

            return usuarioRepositorio.findByEmail(email)
                    .map(usuario -> ResponseEntity.ok((Object) usuario))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Usuario inexistente")));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al recuperar datos: " + e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            if (usuarioRepositorio.existsByEmail(usuario.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("mensaje", "El correo ya está registrado"));
            }

            // Guardar el usuario completo en la Base de Datos
            usuarioService.registrar(usuario);

            return ResponseEntity
                    .ok()
                    .body(Map.of(
                            "mensaje", "Usuario registrado con éxito. Revisa tu correo para confirmar.",
                            "email", usuario.getEmail()
                    ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error en el servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        return usuarioRepositorio.findByEmail(loginRequest.getEmail())
                .map(usuario -> {
                    if (!usuario.isEnabled()) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Debes confirmar tu cuenta primero."));
                    }

                    if (passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                        String token = jwtUtils.generarToken(usuario.getEmail());

                        // Creamos la respuesta con todos los datos requeridos por Angular
                        Map<String, Object> respuestaExito = new HashMap<>();
                        respuestaExito.put("token", token);
                        respuestaExito.put("nombre", usuario.getNombre());
                        respuestaExito.put("isAdmin", usuario.getIsAdmin()); // Mapea el booleano real de la BD

                        return ResponseEntity.ok(respuestaExito);
                    }

                    return ResponseEntity.status(401).body(Collections.singletonMap("error", "Credenciales inválidas."));
                })
                .orElse(ResponseEntity.status(401).body(Collections.singletonMap("error", "Usuario no encontrado.")));
    }

    @PostMapping("/olvide-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            usuarioService.generarTokenRecuperacion(email);
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, se ha enviado un enlace."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("mensaje", "Proceso de recuperación iniciado."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody ResetPasswordDTO data) {
        try {
            usuarioService.actualizarPasswordConToken(data.getToken(), data.getNuevaPassword());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada con éxito."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/confirmar")
    public ResponseEntity<?> confirmarCuenta(@RequestParam String token) {
        boolean activado = usuarioService.confirmarToken(token);
        if (activado) {
            // Redirige al frontend tras la activación
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:4200/login?activado=true"))
                    .build();
        }
        return ResponseEntity.badRequest().body("Token inválido o expirado");
    }
    // Agrega este endpoint dentro de tu AuthController.java

    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(@RequestBody Usuario datosActualizados, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("mensaje", "No estás autenticado o tu sesión expiró"));
            }

            // 1. Extraemos el email seguro del token
            String email = principal.getName();

            // 2. Buscamos al usuario actual en la base de datos
            return usuarioRepositorio.findByEmail(email)
                    .map(usuarioExistente -> {
                        // 3. Modificamos únicamente los campos permitidos
                        usuarioExistente.setNombre(datosActualizados.getNombre());
                        usuarioExistente.setTelefono(datosActualizados.getTelefono());
                        usuarioExistente.setCiudad(datosActualizados.getCiudad());
                        usuarioExistente.setDireccion(datosActualizados.getDireccion());
                        usuarioExistente.setCodigoPostal(datosActualizados.getCodigoPostal());

                        // 4. Guardamos los cambios en la base de datos
                        usuarioRepositorio.save(usuarioExistente);

                        return ResponseEntity.ok(Map.of("mensaje", "Perfil actualizado con éxito"));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("mensaje", "Usuario no encontrado en el sistema")));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno al actualizar: " + e.getMessage()));
        }
    }
}