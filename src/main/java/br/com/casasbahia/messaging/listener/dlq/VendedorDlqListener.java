package br.com.casasbahia.messaging.listener.dlq;

import br.com.casasbahia.dlq.entity.VendedorDlqEntity;
import br.com.casasbahia.messaging.config.VendedorMessagingConfig;
import br.com.casasbahia.repository.VendedorDlqRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class VendedorDlqListener {

    private final VendedorDlqRepository repository;
    private final ObjectMapper objectMapper;

    public VendedorDlqListener(VendedorDlqRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = VendedorMessagingConfig.QUEUE_DLQ)
    public void consumirDlq(
            Message message,
            @Header(AmqpHeaders.RECEIVED_EXCHANGE) String exchange,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey,
            @Header(AmqpHeaders.CONSUMER_QUEUE) String queue
    ) {
        try {
            String payload = new String(
                    message.getBody(),
                    StandardCharsets.UTF_8
            );

            JsonNode root = objectMapper.readTree(payload);
            String protocolo = root.path("protocolo").asText(null);

            VendedorDlqEntity entity = new VendedorDlqEntity();
            entity.setProtocolo(protocolo);
            entity.setExchange(exchange);
            entity.setRoutingKey(routingKey);
            entity.setQueue(queue);
            entity.setPayload(payload);
            entity.setReceivedAt(LocalDateTime.now());

            entity.setError(
                    message.getMessageProperties()
                            .getHeaders()
                            .getOrDefault("x-death", "unknown")
                            .toString()
            );

            repository.save(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
