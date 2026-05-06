package br.com.casasbahia.domain.factory;

import br.com.casasbahia.domain.TipoContratacao;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class MatriculaGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String gerar(TipoContratacao tipoContratacao) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        int randomNumber = RANDOM.nextInt(99);

        return timestamp + randomNumber + "-" + tipoContratacao.getSufixoMatricula();
    }
}

