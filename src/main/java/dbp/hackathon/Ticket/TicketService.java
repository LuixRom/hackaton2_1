package dbp.hackathon.Ticket;

import Email.domain.EmailService;
import dbp.hackathon.Estudiante.Estudiante;
import dbp.hackathon.Estudiante.EstudianteRepository;
import dbp.hackathon.Funcion.Funcion;
import dbp.hackathon.Funcion.FuncionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private EmailService emailService;

    @Autowired
    private QrService qrService;

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
        ticket.setQr("GENERATED-QR-CODE");

        String qrCodeUrl = qrService.generateQRCode(ticket.getId().toString());
        ticket.setQr(qrCodeUrl);

        Ticket savedTicket = ticketRepository.save(ticket);

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
