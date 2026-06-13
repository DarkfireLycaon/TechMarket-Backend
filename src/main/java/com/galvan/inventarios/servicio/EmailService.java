package com.galvan.inventarios.servicio;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${app.url.base}")
    private String urlBase;

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoHTML(String email, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("Confirma tu cuenta");
        helper.setText(html, true); // El 'true' indica que es HTML
        mailSender.send(message);
    }
    public void enviarCorreoConfirmacion(String email, String nombre, String token) {
        try {
            String urlConfirmacion = urlBase + "/auth/confirmar?token=" + token;
            String htmlContenido = "<h1>Bienvenido a Inventarios, " + nombre + "</h1>"
                    + "<p>Haz clic en el siguiente botón para activar tu cuenta:</p>"
                    + "<a href=\"" + urlConfirmacion + "\" style=\"padding:10px 20px; background-color:#28a745; color:white; border-radius:5px; text-decoration:none;\">Confirmar Cuenta</a>";

            // Llamamos al método que sí envía HTML
            enviarCorreoHTML(email, htmlContenido);

            System.out.println("✅ ÉXITO: Correo HTML enviado a " + email);
        } catch (Exception e) {
            System.err.println("❌ ERROR AL ENVIAR CORREO HTML:");
            e.printStackTrace();
        }
    }


}