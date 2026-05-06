package br.com.casasbahia.messaging.listener;

import br.com.casasbahia.domain.Vendedor;
import br.com.casasbahia.mapper.VendedorCadastroEventMapper;
import br.com.casasbahia.messaging.config.VendedorMessagingConfig;
import br.com.casasbahia.messaging.event.VendedorCadastroEvent;
import br.com.casasbahia.repository.VendedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class VendedorCadastroListener {

    private static final Logger log = LoggerFactory.getLogger(VendedorCadastroListener.class);
    private final VendedorRepository vendedorRepository;

    public VendedorCadastroListener(VendedorRepository vendedorRepository) {
        this.vendedorRepository = vendedorRepository;
    }
    @RabbitListener(queues = VendedorMessagingConfig.QUEUE)
    public void consumir(VendedorCadastroEvent event) {
        log.info("Consumindo vendedor | protocolo={}", event.getProtocolo());
        Vendedor vendedor = VendedorCadastroEventMapper.toEntity(event);
        vendedorRepository.save(vendedor);
    }
}
