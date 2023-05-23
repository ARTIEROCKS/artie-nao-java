package artie.nao.listener;

import artie.nao.service.NaoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSubscriber {

    @Autowired
    private NaoService naoService;

    @RabbitListener(queues = "${artie.webservices.interventions.queue}")
    public void receiveMessage(String message) {
        // Procesa el mensaje recibido
        System.out.println("Mensaje recibido: " + message);
        try {
            naoService.executeBMLE(message);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
