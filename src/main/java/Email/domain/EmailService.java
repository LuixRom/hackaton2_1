package Email.domain;

import dbp.hackathon.Ticket.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMessage(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }
    public void sendTicketConfirmationEmail(String to, Ticket ticket, String qrCodeUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirmación de tu Ticket");

        // Detalles del ticket y el QR
        String content = "Hola, " + ticket.getEstudiante().getName() +
                "\n\nGracias por tu compra de entradas para " + ticket.getFuncion().getNombre() +
                ".\n\nAquí tienes tu código QR para acceder al evento:\n" + qrCodeUrl +
                "\n\nFecha de compra: " + ticket.getFechaCompra() +
                "\n\nCantidad de entradas: " + ticket.getCantidad();

        message.setText(content);
        javaMailSender.send(message);
    }
}
