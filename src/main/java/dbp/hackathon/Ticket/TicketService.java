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

        // Crear el ticket sin el código QR todavía
        Ticket ticket = new Ticket();
        ticket.setEstudiante(estudiante);
        ticket.setFuncion(funcion);
        ticket.setCantidad(cantidad);
        ticket.setEstado(Estado.VENDIDO);
        ticket.setFechaCompra(LocalDateTime.now());

        // Guardar el ticket para obtener un ID asignado
        Ticket savedTicket = ticketRepository.save(ticket);

        // Ahora que el ticket tiene un ID, generar el código QR
        String qrUrl = generateQrCode(savedTicket.getId());
        savedTicket.setQr(qrUrl);

        // Guardar nuevamente el ticket con el QR asignado
        ticketRepository.save(savedTicket);

        // Publicar el evento de correo o cualquier otra lógica adicional
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
        ticketRepository.save(ticket);

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
