package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UsuarioServicio {

    @Autowired // 👈 ESTO FALTABA
    private SendGridApiService sendGridApiService;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // EmailService ya no es necesario, lo reemplazamos con SendGridApiService
     @Autowired
     private EmailService emailService; // 👈 ELIMINADO

    public Usuario registrar(Usuario usuario) {
        // 1. Encriptar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // 2. Generar código único para la URL (usamos un UUID largo para que sea seguro)
        String tokenActivacion = UUID.randomUUID().toString();
        usuario.setCodigoConfirmacion(tokenActivacion); // Guardamos el token aquí
        usuario.setEnabled(false);

        Usuario guardado = usuarioRepositorio.save(usuario);

        // 3. Enviar el correo CON ESTE TOKEN
        // Asegúrate de que tu SendGridApiService construya la URL así:
        // "https://techmarket-backend-6iqj.onrender.com/auth/confirmar?token=" + tokenActivacion
        sendGridApiService.enviarCorreoConfirmacion(usuario.getEmail(), tokenActivacion);

        return guardado;
    }

    public void generarTokenRecuperacion(String email) {
        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
        usuarioRepositorio.save(usuario);

        // ✅ Enviar correo de recuperación TAMBIÉN con SendGrid
        sendGridApiService.enviarCorreoRecuperacion(usuario.getEmail(), token); // Necesitas crear este método
    }

    public boolean confirmarCuenta(String email, String codigo) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (codigo.equals(usuario.getCodigoConfirmacion())) {
                usuario.setEnabled(true);
                usuario.setCodigoConfirmacion(null);
                usuarioRepositorio.save(usuario);
                return true;
            }
        }
        return false;
    }

    public boolean confirmarToken(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCodigoConfirmacion(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEnabled(true);
            usuario.setCodigoConfirmacion(null);
            usuarioRepositorio.save(usuario);
            return true;
        }
        return false;
    }

    public void actualizarPasswordConToken(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepositorio.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (usuario.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace ha expirado. Solicita uno nuevo.");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setResetToken(null);
        usuario.setTokenExpiration(null);
        usuarioRepositorio.save(usuario);
    }

    // Los métodos generarHtmlConfirmacion y generarHtmlRecuperacion
    // ahora están en SendGridApiService, así que los eliminamos de aquí
}