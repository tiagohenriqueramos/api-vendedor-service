package br.com.casasbahia.domain.validator;

import br.com.casasbahia.domain.TipoContratacao;
import br.com.casasbahia.dto.VendedorRequestDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CpfCnpjValidatorTest {

    @Test
    void deveAceitarCpfValidoParaClt() {
        VendedorRequestDTO dto = new VendedorRequestDTO();
        dto.setCpf("42435458800");
        dto.setTipoContratacao(TipoContratacao.CLT);

        assertThatCode(() -> CpfCnpjValidator.validar(dto))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarCpfInvalido() {
        VendedorRequestDTO dto = new VendedorRequestDTO();
        dto.setCpf("11111111111");
        dto.setTipoContratacao(TipoContratacao.CLT);

        assertThatThrownBy(() -> CpfCnpjValidator.validar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF inválido");
    }

    @Test
    void deveAceitarCnpjValidoParaPj() {
        VendedorRequestDTO dto = new VendedorRequestDTO();
        dto.setCnpj("12345678000195");
        dto.setTipoContratacao(TipoContratacao.PESSOA_JURIDICA);

        assertThatCode(() -> CpfCnpjValidator.validar(dto))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarCpfParaPj() {
        VendedorRequestDTO dto = new VendedorRequestDTO();
        dto.setCpf("42435458800");
        dto.setTipoContratacao(TipoContratacao.PESSOA_JURIDICA);

        assertThatThrownBy(() -> CpfCnpjValidator.validar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CNPJ");
    }

    @Test
    void deveRejeitarQuandoNaoInformarCpfNemCnpj() {
        VendedorRequestDTO dto = new VendedorRequestDTO();
        dto.setTipoContratacao(TipoContratacao.CLT);

        assertThatThrownBy(() -> CpfCnpjValidator.validar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF ou CNPJ é obrigatório");
    }

    @Test
    void deveRejeitarQuandoInformarCpfECnpj() {
        VendedorRequestDTO dto = new VendedorRequestDTO();
        dto.setCpf("42435458800");
        dto.setCnpj("12345678000195");
        dto.setTipoContratacao(TipoContratacao.CLT);

        assertThatThrownBy(() -> CpfCnpjValidator.validar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Informe apenas CPF ou CNPJ");
    }
}
