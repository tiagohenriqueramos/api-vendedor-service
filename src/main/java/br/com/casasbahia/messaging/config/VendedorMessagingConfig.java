package br.com.casasbahia.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VendedorMessagingConfig {

    public static final String QUEUE = "vendedor.cadastro.queue";
    public static final String QUEUE_DLQ = "vendedor.cadastro.queue-dlq";

    public static final String EXCHANGE = "domain.events";
    public static final String DLX = "domain.dlx";

    public static final String ROUTING_KEY = "vendedor.cadastro";
    public static final String DLQ_ROUTING_KEY = "vendedor.cadastro.dlq";

    @Bean
    public Queue vendedorCadastroQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue vendedorDlqCadastroQueue() {
        return QueueBuilder.durable(QUEUE_DLQ).build();
    }

    @Bean
    public DirectExchange domainExchange() {
        return ExchangeBuilder
                .directExchange(EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(DLX)
                .durable(true)
                .build();
    }

    @Bean
    public Binding vendedorBinding(
            Queue vendedorCadastroQueue,
            DirectExchange domainExchange
    ) {
        return BindingBuilder
                .bind(vendedorCadastroQueue)
                .to(domainExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding vendedorBindingDlx(
            Queue vendedorDlqCadastroQueue,
            DirectExchange deadLetterExchange
    ) {
        return BindingBuilder
                .bind(vendedorDlqCadastroQueue)
                .to(deadLetterExchange)
                .with(DLQ_ROUTING_KEY);
    }
}