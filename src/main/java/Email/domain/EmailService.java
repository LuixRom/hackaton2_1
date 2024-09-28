package Email.domain;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendHtmlMessage(String to, String subject, String nombre, String nombrePelicula, String fechaFuncion, int cantidadEntradas, double precioTotal, String qrUrl) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);

            // Cuerpo del correo en HTML
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>¡Gracias por tu compra!</title>
                </head>
                <body>
                  <h1>¡Gracias por tu compra!</h1>
                  <p>¡Hola {{nombre}}! Te informamos que tu compra ha sido exitosa. A continuación, te presentamos los detalles de tu compra:</p>
                  <ul>
                    <li>Nombre de la película: {{nombrePelicula}}</li>
                    <li>Fecha de la función: {{fechaFuncion}}</li>
                    <li>Cantidad de entradas: {{cantidadEntradas}}</li>
                    <li>Precio total: {{precioTotal}}</li>
                    <li>Código QR: <img src="{{qr}}"></li>
                  </ul>
                  <p>¡No olvides llevar tu código QR impreso o en tu dispositivo móvil para poder ingresar a la función! ¡Te esperamos!</p>
                </body>
                </html>
                """;

            // Reemplazar los placeholders con los valores
            htmlContent = htmlContent.replace("{{nombre}}", nombre)
                    .replace("{{nombrePelicula}}", nombrePelicula)
                    .replace("{{fechaFuncion}}", fechaFuncion)
                    .replace("{{cantidadEntradas}}", String.valueOf(cantidadEntradas))
                    .replace("{{precioTotal}}", String.format("%.2f", precioTotal))
                    .replace("{{qr}}", qrUrl);

            helper.setText(htmlContent, true);

            // Enviar el correo
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
