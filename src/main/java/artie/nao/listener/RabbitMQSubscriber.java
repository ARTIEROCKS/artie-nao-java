package artie.nao.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSubscriber {

    @RabbitListener(queues = "${artie.webservices.interventions.queue}")
    public void receiveMessage(String message) {
        // Procesa el mensaje recibido
        System.out.println("Mensaje recibido: " + message);
    }
}
