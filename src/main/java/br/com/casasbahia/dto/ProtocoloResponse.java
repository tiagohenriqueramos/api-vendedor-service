package br.com.casasbahia.dto;

public class ProtocoloResponse {

    private String protocolo;
    private String mensagem;

    public ProtocoloResponse(String protocolo) {
        this.protocolo = protocolo;
        this.mensagem = "Cadastro recebido com sucesso";
    }

    public String getProtocolo() {
        return protocolo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
