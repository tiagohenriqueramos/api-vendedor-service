package br.com.casasbahia.mapper;

import br.com.casasbahia.domain.Vendedor;
import br.com.casasbahia.dto.VendedorRequestDTO;
import br.com.casasbahia.dto.VendedorResponseDTO;
public class VendedorMapper {

    public static Vendedor toEntity(VendedorRequestDTO dto) {
        Vendedor vendedor = new Vendedor();
        vendedor.setNome(dto.getNome());
        vendedor.setDataNascimento(dto.getDataNascimento());
        vendedor.setCpf(dto.getCpf());
        vendedor.setCnpj(dto.getCnpj());
        vendedor.setEmail(dto.getEmail());
        vendedor.setTipoContratacao(dto.getTipoContratacao());
        vendedor.setDataAdmissao(dto.getDataAdmissao());
        vendedor.setProtocolo(dto.getProtocolo());
        vendedor.setDataAdmissao(dto.getDataAdmissao());
        return vendedor;
    }

    public static VendedorResponseDTO toDTO(Vendedor vendedor) {
        VendedorResponseDTO dto = new VendedorResponseDTO();
        dto.setId(vendedor.getId().toHexString());
        dto.setMatricula(vendedor.getMatricula());
        dto.setNome(vendedor.getNome());
        dto.setDataNascimento(vendedor.getDataNascimento());
        dto.setCpf(vendedor.getCpf());
        dto.setCnpj(vendedor.getCnpj());
        dto.setEmail(vendedor.getEmail());
        dto.setTipoContratacao(vendedor.getTipoContratacao());
        dto.setFilial(vendedor.getFilial());
        dto.setDataAdmissao(vendedor.getDataAdmissao());
        dto.setProtocolo(vendedor.getProtocolo());
        return dto;
    }
}

