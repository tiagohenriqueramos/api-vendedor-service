package br.com.casasbahia.mapper;

import br.com.casasbahia.domain.TipoContratacao;
import br.com.casasbahia.domain.Vendedor;
import br.com.casasbahia.dto.FilialResponseDTO;
import br.com.casasbahia.dto.VendedorRequestDTO;
import br.com.casasbahia.messaging.event.VendedorCadastroEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class VendedorCadastroEventMapper {

    private VendedorCadastroEventMapper() {
    }

    public static VendedorCadastroEvent from(
            VendedorRequestDTO dto,
            String protocolo,
            String matricula,
            FilialResponseDTO filial
    ) {
        VendedorCadastroEvent event = new VendedorCadastroEvent();

        event.setProtocolo(protocolo);
        event.setMatricula(matricula);
        event.setNome(dto.getNome());
        event.setCpf(dto.getCpf());
        event.setCnpj(dto.getCnpj());
        event.setEmail(dto.getEmail());
        event.setTipoContratacao(dto.getTipoContratacao().name());
        event.setFilial(filial);
        event.setDataNascimento(dto.getDataNascimento());
        event.setDataAdmissao(LocalDate.now());
        event.setCriadoEm(LocalDateTime.now());

        return event;
    }

    public static Vendedor toEntity(VendedorCadastroEvent event) {
        Vendedor vendedor = new Vendedor();

        vendedor.setProtocolo(event.getProtocolo());
        vendedor.setMatricula(event.getMatricula());
        vendedor.setNome(event.getNome());
        vendedor.setCpf(event.getCpf());
        vendedor.setCnpj(event.getCnpj());
        vendedor.setEmail(event.getEmail());
        vendedor.setTipoContratacao(TipoContratacao.valueOf(event.getTipoContratacao()));
        vendedor.setDataNascimento(event.getDataNascimento());
        vendedor.setDataAdmissao(event.getDataAdmissao());
        vendedor.setFilial(FilialMapper.toEntity(event.getFilial()));

        return vendedor;
    }
}
