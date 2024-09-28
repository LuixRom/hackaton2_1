package Email.event;

import Email.domain.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {
    @Autowired
    private EmailService emailService;

    @EventListener
    @Async
    public void handleHelloEmailEvent(EmailEvent event) {
        emailService.sendMessage(event.getEmail(), event.getSubject(), event.getContent());
    }
}