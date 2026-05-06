package br.com.casasbahia.service;

import br.com.casasbahia.dlq.entity.VendedorDlqEntity;
import br.com.casasbahia.domain.TipoContratacao;
import br.com.casasbahia.domain.Vendedor;
import br.com.casasbahia.dto.*;
import br.com.casasbahia.exception.VendedorNaoEncontradoException;
import br.com.casasbahia.integration.filial.FilialClient;
import br.com.casasbahia.messaging.producer.VendedorCadastroProducer;
import br.com.casasbahia.repository.VendedorDlqRepository;
import br.com.casasbahia.repository.VendedorRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendedorServiceTest {

    @Mock
    VendedorRepository vendedorRepository;

    @Mock
    VendedorDlqRepository vendedorDlqRepository;

    @Mock
    FilialClient filialClient;

    @Mock
    VendedorCadastroProducer producer;

    @InjectMocks
    VendedorService service;

    @Test
    void devePublicarEventoERetornarProtocolo() {
        VendedorRequestDTO dto = new VendedorRequestDTO();
        dto.setNome("Tiago Ramos");
        dto.setCpf("42435458800");
        dto.setEmail("tiago@casasbahia.com.br");
        dto.setTipoContratacao(TipoContratacao.CLT);
        dto.setFilialId("1");

        FilialResponseDTO filial = new FilialResponseDTO();
        filial.setId("1");
        filial.setNome("Filial Centro");

        when(filialClient.buscarPorId("1")).thenReturn(filial);

        ProtocoloResponse response = service.salvar(dto, "1");

        assertThat(response).isNotNull();
        assertThat(response.getProtocolo()).isNotBlank();

        verify(producer).enviar(any());
        verifyNoInteractions(vendedorRepository);
    }

    @Test
    void deveRetornarVendedorQuandoProcessado() {
        String protocolo = "proto-123";

        Vendedor vendedor = new Vendedor();
        vendedor.setId(new ObjectId("6960541336914a3250448cb5"));
        vendedor.setProtocolo(protocolo);
        vendedor.setNome("Tiago Ramos");

        when(vendedorRepository.findByProtocolo(protocolo))
                .thenReturn(Optional.of(vendedor));

        VendedorConsultaDTO response = service.buscarPorProtocolo(protocolo);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("PROCESSADO");
        assertThat(response.getVendedor()).isNotNull();
        assertThat(response.getVendedor().getNome()).isEqualTo("Tiago Ramos");

        verifyNoInteractions(vendedorDlqRepository);
    }


    @Test
    void deveRetornarErroQuandoEstiverNaDlq() {
        String protocolo = "proto-erro";

        when(vendedorRepository.findByProtocolo(protocolo))
                .thenReturn(Optional.empty());

        VendedorDlqEntity dlq = new VendedorDlqEntity();
        dlq.setProtocolo(protocolo);
        dlq.setPayload("{json}");
        dlq.setError("Erro no processamento");

        when(vendedorDlqRepository.findByProtocolo(protocolo))
                .thenReturn(Optional.of(dlq));

        VendedorConsultaDTO response = service.buscarPorProtocolo(protocolo);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("ERRO");
        assertThat(response.getErro()).isEqualTo("Erro no processamento");
        assertThat(response.getVendedor()).isNull();
    }

    @Test
    void deveBuscarVendedorPorId() {
        ObjectId id = new ObjectId();
        Vendedor vendedor = new Vendedor();
        vendedor.setId(id);
        vendedor.setNome("Tiago Ramos");

        when(vendedorRepository.findById(id)).thenReturn(Optional.of(vendedor));

        Vendedor response = service.buscarPorId(id);

        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("Tiago Ramos");
    }

    @Test
    void deveLancarExcecaoQuandoBuscarPorIdInexistente() {
        ObjectId id = new ObjectId();

        when(vendedorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(id))
                .isInstanceOf(VendedorNaoEncontradoException.class)
                .hasMessageContaining("Vendedor não encontrado pelo id");
    }

    @Test
    void deveBuscarVendedorPorMatricula() {
        String matricula = "MAT-123";
        Vendedor vendedor = new Vendedor();
        vendedor.setMatricula(matricula);

        when(vendedorRepository.findByMatricula(matricula))
                .thenReturn(Optional.of(vendedor));

        Vendedor response = service.buscarPorMatricula(matricula);

        assertThat(response).isNotNull();
        assertThat(response.getMatricula()).isEqualTo(matricula);
    }

    @Test
    void deveLancarExcecaoQuandoBuscarPorMatriculaInexistente() {
        String matricula = "MAT-404";

        when(vendedorRepository.findByMatricula(matricula))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorMatricula(matricula))
                .isInstanceOf(VendedorNaoEncontradoException.class)
                .hasMessageContaining("Vendedor não encontrado por matricula");
    }

    @Test
    void deveListarVendedoresPaginado() {
        Vendedor vendedor = new Vendedor();
        vendedor.setNome("Tiago Ramos");
        vendedor.setId(new ObjectId("6960541336914a3250448cb5"));

        Page<Vendedor> page = new PageImpl<>(List.of(vendedor));
        PageRequest pageable = PageRequest.of(0, 10);

        when(vendedorRepository.findAll(pageable)).thenReturn(page);

        Page<VendedorResponseDTO> response = service.listar(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void deveAtualizarVendedorComSucesso() {
        ObjectId id = new ObjectId();

        Vendedor existente = new Vendedor();
        existente.setId(id);

        Vendedor atualizado = new Vendedor();
        atualizado.setNome("Novo Nome");
        atualizado.setEmail("novo@email.com");
        atualizado.setDataNascimento(LocalDate.of(1990, 1, 1));

        FilialResponseDTO filial = new FilialResponseDTO();
        filial.setId("1");
        filial.setNome("Filial Centro");

        when(vendedorRepository.findById(id)).thenReturn(Optional.of(existente));
        when(filialClient.buscarPorId("1")).thenReturn(filial);
        when(vendedorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Vendedor response = service.atualizarVendedor(id, atualizado, "1");

        assertThat(response.getNome()).isEqualTo("Novo Nome");
        assertThat(response.getEmail()).isEqualTo("novo@email.com");
    }

    @Test
    void deveLancarErroQuandoAtualizarComFilialInexistente() {
        ObjectId id = new ObjectId();

        Vendedor existente = new Vendedor();
        Vendedor atualizado = new Vendedor();

        when(vendedorRepository.findById(id)).thenReturn(Optional.of(existente));
        when(filialClient.buscarPorId("99")).thenReturn(null);

        assertThatThrownBy(() -> service.atualizarVendedor(id, atualizado, "99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Filial não encontrada");
    }

    @Test
    void deveDeletarVendedor() {
        ObjectId id = new ObjectId();
        Vendedor vendedor = new Vendedor();
        vendedor.setId(id);

        when(vendedorRepository.findById(id)).thenReturn(Optional.of(vendedor));

        service.deletarVendedor(id);

        verify(vendedorRepository).delete(vendedor);
    }

    @Test
    void deveLancarErroAoDeletarVendedorInexistente() {
        ObjectId id = new ObjectId();

        when(vendedorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletarVendedor(id))
                .isInstanceOf(VendedorNaoEncontradoException.class);
    }

}
