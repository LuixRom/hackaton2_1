package dbp.hackathon.Ticket;

import Email.event.EmailEvent;
import dbp.hackathon.Estudiante.Estudiante;
import dbp.hackathon.Estudiante.EstudianteRepository;
import dbp.hackathon.Funcion.Funcion;
import dbp.hackathon.Funcion.FuncionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private FuncionRepository funcionRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;



    public Ticket createTicket(Long estudianteId, Long funcionId, Integer cantidad) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
        Funcion funcion = funcionRepository.findById(funcionId).orElse(null);
        if (estudiante == null || funcion == null) {
            throw new IllegalStateException("Estudiante or Funcion not found!");
        }


        Ticket ticket = new Ticket();
        ticket.setEstudiante(estudiante);
        ticket.setFuncion(funcion);
        ticket.setCantidad(cantidad);
        ticket.setEstado(Estado.VENDIDO);
        ticket.setFechaCompra(LocalDateTime.now());



        String qrUrl = generateQrCode(ticket.getId());
        ticket.setQr(qrUrl);


        Ticket savedTicket = ticketRepository.save(ticket);


        publishEmailEvent(estudiante, funcion, savedTicket, qrUrl);

        return savedTicket;
    }

    private String generateQrCode(Long ticketId) {
        try {
            return "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + URLEncoder.encode(ticketId.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error al generar el código QR", e);
        }
    }


    private void publishEmailEvent(Estudiante estudiante, Funcion funcion, Ticket ticket, String qrUrl) {
        String subject = "¡Gracias por tu compra!";
        String nombreEstudiante = estudiante.getName();
        String nombrePelicula = funcion.getNombre();
        String fechaFuncion = funcion.getFecha().toString();
        int cantidadEntradas = ticket.getCantidad();
        double precioTotal = funcion.getPrecio() * cantidadEntradas;

        // Crear el evento de correo electrónico
        EmailEvent emailEvent = new EmailEvent(
                estudiante.getEmail(),
                subject,
                nombreEstudiante,
                nombrePelicula,
                fechaFuncion,
                cantidadEntradas,
                precioTotal,
                qrUrl
        );


        eventPublisher.publishEvent(emailEvent);
=======
        String qrCodeUrl = qrService.generateQRCode(ticket.getId().toString());
        ticket.setQr(qrCodeUrl); // Asignar el QR al ticket

        // Guardar el ticket en la base de datos
        Ticket savedTicket = ticketRepository.save(ticket);

        // Enviar correo de confirmación con el QR
        emailService.sendTicketConfirmationEmail(savedTicket.getEstudiante().getEmail(), savedTicket, qrCodeUrl);

        return savedTicket;
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }

    public Iterable<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Iterable<Ticket> findByEstudianteId(Long estudianteId) {
        return ticketRepository.findByEstudianteId(estudianteId);
    }

    public void changeState(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            throw new IllegalStateException("Ticket not found!");
        }
        ticket.setEstado(Estado.CANJEADO);
        ticketRepository.save(ticket);
    }

    public double calcularRecaudacionPorFuncion(Long funcionId) {
        List<Ticket> tickets = ticketRepository.findByFuncionId(funcionId);
        return tickets.stream()
                .mapToDouble(ticket -> ticket.getFuncion().getPrecio() * ticket.getCantidad())
                .sum();
    }
}
