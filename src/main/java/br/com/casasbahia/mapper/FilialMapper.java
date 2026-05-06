package br.com.casasbahia.mapper;

import br.com.casasbahia.domain.Filial;
import br.com.casasbahia.dto.FilialResponseDTO;

public class FilialMapper {

    public static Filial toEntity(FilialResponseDTO dto) {
        Filial filial = new Filial();
        filial.setId(dto.getId());
        filial.setNome(dto.getNome());
        filial.setCnpj(dto.getCnpj());
        filial.setCidade(dto.getCidade());
        filial.setUf(dto.getUf());
        filial.setTipo(dto.getTipo());
        filial.setAtivo(dto.isAtivo());
        filial.setDataCadastro(dto.getDataCadastro());
        filial.setDataUpdate(dto.getDataUpdate());

        return filial;
    }

}
