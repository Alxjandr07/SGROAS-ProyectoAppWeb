package ec.edu.uteq.sgroas.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Envio de correos con el codigo de confirmacion.
 * Si no hay SMTP configurado (app.smtp.host vacio) funciona en MODO CONSOLA:
 * el codigo se imprime en el log para poder probar los flujos en desarrollo.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.smtp.host:}")
    private String host;

    @Value("${app.smtp.port:587}")
    private int port;

    @Value("${app.smtp.username:}")
    private String username;

    @Value("${app.smtp.password:}")
    private String password;

    @Value("${app.mail.from:no-reply@sgroas.com}")
    private String from;

    public boolean configurado() {
        return host != null && !host.isBlank();
    }

    public void enviarCodigoVerificacion(String para, String nombre, String codigo) {
        String html = plantillaCodigo(
                "Confirma tu cuenta",
                "Hola " + nombre + ", usa este codigo para activar tu cuenta en SGROAS:",
                codigo,
                "El codigo expira en 10 minutos. Si no creaste esta cuenta, ignora este mensaje."
        );
        enviar(para, "SGROAS · Codigo de verificacion de cuenta", html, codigo);
    }

    public void enviarCodigoRestablecimiento(String para, String codigo) {
        String html = plantillaCodigo(
                "Restablece tu contrasena",
                "Recibimos una solicitud para restablecer tu contrasena de SGROAS. Usa este codigo:",
                codigo,
                "El codigo expira en 10 minutos y solo puede usarse una vez. Si no fuiste tu, ignora este mensaje."
        );
        enviar(para, "SGROAS · Codigo para restablecer contrasena", html, codigo);
    }

    private void enviar(String para, String asunto, String html, String codigo) {
        if (!configurado()) {
            log.warn("[MODO CONSOLA] SMTP sin configurar. Codigo para {} ({}): {}", para, asunto, codigo);
            return;
        }
        try {
            JavaMailSender sender = crearSender();
            MimeMessage mensaje = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, false, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(html, true);
            sender.send(mensaje);
            log.info("Correo '{}' enviado a {}", asunto, para);
        } catch (Exception e) {
            log.error("No se pudo enviar el correo a {}: {}", para, e.getMessage());
            throw new IllegalStateException(
                    "No se pudo enviar el correo de confirmacion. Revisa la configuracion SMTP del servidor.");
        }
    }

    private JavaMailSender crearSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        if (username != null && !username.isBlank()) {
            sender.setUsername(username);
            sender.setPassword(password);
        }
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", Boolean.toString(username != null && !username.isBlank()));
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        return sender;
    }

    private String plantillaCodigo(String titulo, String intro, String codigo, String pie) {
        return """
                <!doctype html>
                <html lang="es">
                <body style="margin:0;padding:24px;background:#f4f5f7;font-family:Arial,Helvetica,sans-serif;">
                  <div style="max-width:480px;margin:0 auto;background:#ffffff;border-radius:12px;padding:32px;text-align:center;">
                    <h2 style="margin:0 0 8px;color:#1a2233;">SGROAS</h2>
                    <p style="margin:0;color:#6b7280;font-size:14px;">%s</p>
                    <p style="margin:16px 0 0;color:#374151;font-size:15px;">%s</p>
                    <div style="margin:24px auto;display:inline-block;background:#eef2ff;border:1px solid #c7d2fe;
                                border-radius:10px;padding:14px 28px;font-size:32px;letter-spacing:10px;
                                font-weight:bold;color:#1d4ed8;">%s</div>
                    <p style="margin:0;color:#9ca3af;font-size:13px;">%s</p>
                  </div>
                </body>
                </html>
                """.formatted(titulo, intro, codigo, pie);
    }
}
