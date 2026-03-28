package br.com.faculdade.tp5.exception;

public class TimeoutServicoException extends RuntimeException {

    public TimeoutServicoException(String mensagem) {
        super(mensagem);
    }
}
