package br.com.casasbahia.domain;

public enum TipoContratacao {

    OUTSOURCING("OUT"),
    CLT("CLT"),
    PESSOA_JURIDICA("PJ");

    private final String sufixoMatricula;

    TipoContratacao(String sufixoMatricula) {
        this.sufixoMatricula = sufixoMatricula;
    }

    public String getSufixoMatricula() {
        return sufixoMatricula;
    }
}
