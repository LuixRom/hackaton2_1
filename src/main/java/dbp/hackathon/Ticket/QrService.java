package dbp.hackathon.Ticket;

import org.springframework.stereotype.Service;

@Service
public class QrService {

    public String generateQRCode(String data) {
        // Tamaño y contenido del QR
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + data;

        return qrUrl; // Retornar la URL del QR
    }
}