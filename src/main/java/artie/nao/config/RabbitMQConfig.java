package artie.nao.config;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${artie.webservices.interventions.queue}")
    private String queueName;

    @Bean
    public Queue pedagogicalInterventionsQueue() {
        return new Queue(queueName, true, false, false);
    }
}
