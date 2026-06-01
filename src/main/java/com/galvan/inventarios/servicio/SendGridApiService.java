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

    // Método existente
    public void enviarCorreo(String para, String asunto, String contenido) {
        Email from = new Email("sistema@inventarios.com");
        Email to = new Email(para);
        Content content = new Content("text/plain", contenido);
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

    // Método para confirmación de registro
    public void enviarCorreoConfirmacion(String email, String nombre) {
        String asunto = "Bienvenido a Inventarios";
        String contenido = "Hola " + nombre + ",\n\nGracias por registrarte en el sistema de inventarios.\n\nSaludos!";
        enviarCorreo(email, asunto, contenido);
    }

    // Método para recuperación de contraseña
    public void enviarCorreoRecuperacion(String email, String token) {
        String asunto = "Recuperación de contraseña";
        String contenido = "Hola,\n\nPara recuperar tu contraseña, haz clic en el siguiente enlace:\n\n" +
                "http://localhost:8080/reset-password?token=" + token + "\n\n" +
                "Si no solicitaste este cambio, ignora este correo.\n\nSaludos!";
        enviarCorreo(email, asunto, contenido);
    }
}