package br.com.casasbahia.messaging.listener;

import br.com.casasbahia.domain.TipoContratacao;
import br.com.casasbahia.domain.Vendedor;
import br.com.casasbahia.dto.FilialResponseDTO;
import br.com.casasbahia.messaging.event.VendedorCadastroEvent;
import br.com.casasbahia.repository.VendedorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VendedorCadastroListenerTest {

    @Mock
    VendedorRepository repository;

    @InjectMocks
    VendedorCadastroListener listener;

    @Test
    void devePersistirVendedorAoConsumirEvento() {

        VendedorCadastroEvent event = new VendedorCadastroEvent();
        event.setProtocolo("proto-123");
        event.setMatricula("123-CLT");
        event.setNome("Tiago Ramos");
        event.setCpf("42435458800");
        event.setEmail("tiago@casasbahia.com.br");
        event.setTipoContratacao("CLT");
        event.setDataNascimento(LocalDate.of(1992, 11, 28));

        FilialResponseDTO filial = new FilialResponseDTO();
        filial.setId("1");
        filial.setNome("Filial Centro");

        event.setFilial(filial);

        listener.consumir(event);

        ArgumentCaptor<Vendedor> captor = ArgumentCaptor.forClass(Vendedor.class);
        verify(repository).save(captor.capture());

        Vendedor vendedorSalvo = captor.getValue();

        assertThat(vendedorSalvo.getProtocolo()).isEqualTo("proto-123");
        assertThat(vendedorSalvo.getMatricula()).isEqualTo("123-CLT");
        assertThat(vendedorSalvo.getNome()).isEqualTo("Tiago Ramos");
        assertThat(vendedorSalvo.getTipoContratacao()).isEqualTo(TipoContratacao.CLT);
    }
}
