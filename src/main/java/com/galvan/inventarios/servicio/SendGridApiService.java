package com.galvan.inventarios.servicio;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendGridApiService {
    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    // Método genérico para enviar emails
    public void enviarCorreo(String para, String asunto, String contenido, boolean esHtml) {
        Email from = new Email("sistema@inventarios.com");
        Email to = new Email(para);
        // Ajustamos el tipo de contenido según si es HTML o no
        Content content = new Content(esHtml ? "text/html" : "text/plain", contenido);
        Mail mail = new Mail(from, asunto, to, content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Correo enviado. Código: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
        }
    }

    // ✅ CORRECCIÓN: Ahora recibe el parámetro 'token'
    public void enviarCorreoConfirmacion(String email, String nombre, String token) {
        String asunto = "Bienvenido a Inventarios";
        // Usamos el token que llega por parámetro
        String urlConfirmacion = "https://techmarket-backend-6iqj.onrender.com/auth/confirmar?token=" + token;

        String contenido = "Hola " + nombre + ",<br><br>Haz clic aquí para activar tu cuenta:<br>" +
                "<a href='" + urlConfirmacion + "'>Activar cuenta</a>";

        // Enviamos 'true' para indicar que es HTML
        enviarCorreo(email, asunto, contenido, true);
    }

    // Método para recuperación
    public void enviarCorreoRecuperacion(String email, String token) {
        String asunto = "Recuperación de contraseña";
        // Corregido: apunte a tu URL de producción en Render
        String urlRecuperacion = "https://techmarket-frontend.onrender.com/reset-password?token=" + token;

        String contenido = "Hola,\n\nPara recuperar tu contraseña, haz clic en el siguiente enlace:\n\n" +
                urlRecuperacion + "\n\nSaludos!";

        enviarCorreo(email, asunto, contenido, false);
    }
}