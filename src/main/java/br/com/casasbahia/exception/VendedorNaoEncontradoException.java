package br.com.casasbahia.exception;

public class VendedorNaoEncontradoException extends RuntimeException {

    public VendedorNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
