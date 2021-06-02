package deti.tqs.webmarket.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class RabbitMqConfig {

    private final String LOCATION_QUEUE = "location.queue";
    private final String LOCATION_EXCHANGE = "location.exchange";
    private final String LOCATION_ROUTING_KEY = "location.routingKey";

    // rabbitMQ cluster specifications
    private final String HOST = "rat-01.rmq2.cloudamqp.com";
    private final String PASSWORD = "URPbqFM6LUS3dJF3su0j2XRZZwBIA-RD";
    private final String PORT = "15672";
    private final String USERNAME = " kartnzpn";

    private final String URI = "amqps://kartnzpn:URPbqFM6LUS3dJF3su0j2XRZZwBIA-RD@rat.rmq2.cloudamqp.com/kartnzpn";

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory(RabbitProperties config) throws Exception {
        var connectionFactory = new CachingConnectionFactory();
        connectionFactory.getRabbitConnectionFactory().setUri(URI);
        return connectionFactory;
    }

    @RabbitListener(queues = "location.queue")
    public void listen(Message in) {
        log.info(in);
    }
}
