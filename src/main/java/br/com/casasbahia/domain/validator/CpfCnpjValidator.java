package br.com.casasbahia.domain.validator;

import br.com.casasbahia.domain.TipoContratacao;
import br.com.casasbahia.dto.VendedorRequestDTO;

public final class CpfCnpjValidator {

    public static void validar(VendedorRequestDTO vendedor) {
        boolean temCpf = vendedor.getCpf() != null && !vendedor.getCpf().isBlank();
        boolean temCnpj = vendedor.getCnpj() != null && !vendedor.getCnpj().isBlank();

        if (!temCpf && !temCnpj) {
            throw new IllegalArgumentException("CPF ou CNPJ é obrigatório");
        }

        if (temCpf == temCnpj) {
            throw new IllegalArgumentException("Informe apenas CPF ou CNPJ");
        }

        if (vendedor.getTipoContratacao() == TipoContratacao.PESSOA_JURIDICA) {

            if (!temCnpj) {
                throw new IllegalArgumentException("Para PJ é obrigatório informar CNPJ");
            }

            if (!isValidCNPJ(vendedor.getCnpj())) {
                throw new IllegalArgumentException("CNPJ inválido");
            }

        } else {

            if (!temCpf) {
                throw new IllegalArgumentException("Para CLT é obrigatório informar CPF");
            }

            if (!isValidCPF(vendedor.getCpf())) {
                throw new IllegalArgumentException("CPF inválido");
            }
        }
    }


    public static boolean isValidCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }
        int resto = soma % 11;
        int digito1 = (resto < 2) ? 0 : 11 - resto;

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }
        resto = soma % 11;
        int digito2 = (resto < 2) ? 0 : 11 - resto;

        return (cpf.charAt(9) - '0') == digito1
                && (cpf.charAt(10) - '0') == digito2;
    }


    public static boolean isValidCNPJ(String cnpj) {
        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += (cnpj.charAt(i) - '0') * peso1[i];
        }
        int resto = soma % 11;
        int digito1 = (resto < 2) ? 0 : 11 - resto;

        int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += (cnpj.charAt(i) - '0') * peso2[i];
        }
        resto = soma % 11;
        int digito2 = (resto < 2) ? 0 : 11 - resto;

        return (cnpj.charAt(12) - '0') == digito1
                && (cnpj.charAt(13) - '0') == digito2;
    }
}
