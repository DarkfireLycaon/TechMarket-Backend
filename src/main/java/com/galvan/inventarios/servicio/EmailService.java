package com.galvan.inventarios.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.host:NOT_CONFIGURED}")
    private String mailHost;

    public void enviarCorreoConfirmacion(String email, String nombre, String token) {
        System.out.println("\n========== ENVÍO DE CORREO ==========");
        System.out.println("Destino: " + email);
        System.out.println("Usuario: " + nombre);
        System.out.println("Token: " + token);

        if (mailSender == null) {
            System.out.println("⚠️ MailSender no configurado - Modo desarrollo");
            System.out.println("Token de confirmación (usa este para activar): " + token);
            System.out.println("Link: http://localhost:8080/auth/confirmar?token=" + token);
            return;
        }

        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(email);
            mensaje.setSubject("Confirma tu cuenta - Sistema de Inventarios");
            mensaje.setText(String.format(
                    "Hola %s,\n\n" +
                            "Gracias por registrarte.\n\n" +
                            "Para confirmar tu cuenta, haz clic en:\n" +
                            "http://localhost:8080/auth/confirmar?token=%s\n\n" +
                            "Saludos!",
                    nombre, token
            ));
            mensaje.setFrom("noreply@inventarios.com");

            mailSender.send(mensaje);
            System.out.println("✅ Correo enviado a: " + email);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar: " + e.getMessage());
        }
        System.out.println("=====================================\n");
    }
}