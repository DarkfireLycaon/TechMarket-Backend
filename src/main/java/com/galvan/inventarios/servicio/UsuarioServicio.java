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
    @Autowired
    private SendGridApiService sendGridApiService;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario) {
        // 1. Encriptar password
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // 2. Generar token único para la activación (UUID largo para mayor seguridad)
        String tokenActivacion = UUID.randomUUID().toString();
        usuario.setCodigoConfirmacion(tokenActivacion);
        usuario.setEnabled(false);

        // 3. Guardar usuario
        Usuario guardado = usuarioRepositorio.save(usuario);

        // 4. Enviar correo usando el token generado
        // Asegúrate de que este método en tu SendGridApiService use la URL:
        // "https://techmarket-backend-6iqj.onrender.com/auth/confirmar?token=" + token
        sendGridApiService.enviarCorreoConfirmacion(usuario.getEmail(),usuario.getNombre(), tokenActivacion);

        return guardado;
    }

    public void generarTokenRecuperacion(String email) {
        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
        usuarioRepositorio.save(usuario);

        // Enviar correo de recuperación
        sendGridApiService.enviarCorreoRecuperacion(usuario.getEmail(), token);
    }

    // Método para confirmación vía URL (el que estás usando)
    public boolean confirmarToken(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCodigoConfirmacion(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEnabled(true);
            usuario.setCodigoConfirmacion(null); // Limpiamos el token tras usarlo
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

}