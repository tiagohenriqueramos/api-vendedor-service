package br.com.casasbahia.service;

import br.com.casasbahia.dlq.entity.VendedorDlqEntity;
import br.com.casasbahia.domain.Vendedor;
import br.com.casasbahia.domain.factory.MatriculaGenerator;
import br.com.casasbahia.domain.validator.CpfCnpjValidator;
import br.com.casasbahia.dto.*;
import br.com.casasbahia.exception.VendedorNaoEncontradoException;
import br.com.casasbahia.integration.filial.FilialClient;
import br.com.casasbahia.mapper.FilialMapper;
import br.com.casasbahia.mapper.VendedorCadastroEventMapper;
import br.com.casasbahia.mapper.VendedorMapper;
import br.com.casasbahia.messaging.event.VendedorCadastroEvent;
import br.com.casasbahia.messaging.producer.VendedorCadastroProducer;
import br.com.casasbahia.repository.VendedorDlqRepository;
import br.com.casasbahia.repository.VendedorRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Service
public class VendedorService {

    private static final Logger log = LoggerFactory.getLogger(VendedorService.class);

    private final VendedorRepository vendedorRepository;
    private final VendedorDlqRepository vendedorDlqRepository;
    private final FilialClient filialClient;
    private final VendedorCadastroProducer vendedorCadastroProducer;

    public VendedorService(
            VendedorRepository vendedorRepository,
            VendedorDlqRepository vendedorDlqRepository,
            FilialClient filialClient,
            VendedorCadastroProducer vendedorCadastroProducer
    ) {
        this.vendedorRepository = vendedorRepository;
        this.vendedorDlqRepository = vendedorDlqRepository;
        this.filialClient = filialClient;
        this.vendedorCadastroProducer = vendedorCadastroProducer;
    }

    public ProtocoloResponse salvar(VendedorRequestDTO vendedor, String filialId) {

        log.info("Iniciando cadastro de vendedor | email={} | tipoContratacao={}",
                vendedor.getEmail(), vendedor.getTipoContratacao());

        CpfCnpjValidator.validar(vendedor);

        FilialResponseDTO filial = filialClient.buscarPorId(filialId);
        if (filial == null) {
            log.warn("Filial não encontrada | filialId={}", filialId);
            throw new IllegalArgumentException("Filial não encontrada: " + filialId);
        }

        String protocolo = UUID.randomUUID().toString();
        String matricula = MatriculaGenerator.gerar(vendedor.getTipoContratacao());

        log.info("Cadastro validado | protocolo={} | matricula={}", protocolo, matricula);

        VendedorCadastroEvent event =
                VendedorCadastroEventMapper.from(vendedor, protocolo, matricula, filial);

        vendedorCadastroProducer.enviar(event);

        log.info("Evento publicado no RabbitMQ | protocolo={} | exchange=domain.events | routingKey=vendedor.cadastro",
                protocolo);

        return new ProtocoloResponse(protocolo);
    }

    public Vendedor buscarPorId(ObjectId id) {
        log.info("Buscando vendedor por id={}", id);
        return buscarOuFalhar(id);
    }

    public Vendedor buscarPorMatricula(String matricula) {
        log.info("Buscando vendedor por matricula={}", matricula);

        return vendedorRepository.findByMatricula(matricula)
                .orElseThrow(() -> {
                    log.warn("Vendedor não encontrado por matricula={}", matricula);
                    return new VendedorNaoEncontradoException(
                            "Vendedor não encontrado por matricula: " + matricula
                    );
                });
    }

    public VendedorConsultaDTO buscarPorProtocolo(String protocolo) {
        log.info("Consulta por protocolo iniciada | protocolo={}", protocolo);

        return vendedorRepository.findByProtocolo(protocolo)
                .map(vendedor -> {
                    log.info("Vendedor encontrado | protocolo={} | status=PROCESSADO", protocolo);
                    return VendedorConsultaDTO.fromEntity(vendedor);
                })
                .orElseGet(() -> buscarDlq(protocolo));
    }

    private VendedorConsultaDTO buscarDlq(String protocolo) {
        log.warn("Vendedor não encontrado na base principal | buscando DLQ | protocolo={}", protocolo);

        VendedorDlqEntity dlq = vendedorDlqRepository.findByProtocolo(protocolo)
                .orElseThrow(() -> {
                    log.error("Protocolo não encontrado nem em base nem em DLQ | protocolo={}", protocolo);
                    return new VendedorNaoEncontradoException(
                            "Vendedor não encontrado nem em processamento nem em DLQ: " + protocolo
                    );
                });

        log.info("Registro encontrado em DLQ | protocolo={}", protocolo);
        return VendedorConsultaDTO.fromDlq(dlq);
    }

    public Page<VendedorResponseDTO> listar(Pageable pageable) {
        log.info("Listando vendedores | page={} | size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return vendedorRepository.findAll(pageable)
                .map(VendedorMapper::toDTO);
    }

    public Vendedor atualizarVendedor(ObjectId id, Vendedor vendedor, String filialId) {
        log.info("Atualizando vendedor | id={}", id);

        Vendedor existente = buscarOuFalhar(id);

        existente.setNome(vendedor.getNome());
        existente.setDataNascimento(vendedor.getDataNascimento());
        existente.setEmail(vendedor.getEmail());

        FilialResponseDTO filial = filialClient.buscarPorId(filialId);
        if (filial == null) {
            log.warn("Filial não encontrada para atualização | filialId={}", filialId);
            throw new IllegalArgumentException("Filial não encontrada: " + filialId);
        }

        existente.setFilial(FilialMapper.toEntity(filial));

        log.info("Vendedor atualizado com sucesso | id={}", id);
        return vendedorRepository.save(existente);
    }

    public void deletarVendedor(ObjectId id) {
        log.warn("Removendo vendedor | id={}", id);
        vendedorRepository.delete(buscarOuFalhar(id));
    }

    private Vendedor buscarOuFalhar(ObjectId id) {
        return vendedorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Vendedor não encontrado | id={}", id);
                    return new VendedorNaoEncontradoException(
                            "Vendedor não encontrado pelo id: " + id
                    );
                });
    }
}
