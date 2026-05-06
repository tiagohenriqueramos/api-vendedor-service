package br.com.casasbahia.messaging.producer;

import br.com.casasbahia.messaging.config.VendedorMessagingConfig;
import br.com.casasbahia.messaging.event.VendedorCadastroEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class VendedorCadastroProducer {

    private static final Logger log =
            LoggerFactory.getLogger(VendedorCadastroProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public VendedorCadastroProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviar(VendedorCadastroEvent event) {
        log.info(
                "Publicando evento de cadastro de vendedor | protocolo={} | email={}",
                event.getProtocolo(),
                event.getEmail()
        );

        rabbitTemplate.convertAndSend(
                VendedorMessagingConfig.EXCHANGE,
                VendedorMessagingConfig.ROUTING_KEY,
                event
        );
    }
}
