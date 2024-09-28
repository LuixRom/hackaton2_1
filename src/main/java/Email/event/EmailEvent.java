package Email.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class EmailEvent extends ApplicationEvent {
    private final String email;
    private final String subject;
    private final String nombre;
    private final String nombrePelicula;
    private final String fechaFuncion;
    private final int cantidadEntradas;
    private final double precioTotal;
    private final String qrUrl;

    public EmailEvent(String email, String subject, String nombre, String nombrePelicula, String fechaFuncion, int cantidadEntradas, double precioTotal, String qrUrl) {
        super(email);
        this.email = email;
        this.subject = subject;
        this.nombre = nombre;
        this.nombrePelicula = nombrePelicula;
        this.fechaFuncion = fechaFuncion;
        this.cantidadEntradas = cantidadEntradas;
        this.precioTotal = precioTotal;
        this.qrUrl = qrUrl;
    }
}
