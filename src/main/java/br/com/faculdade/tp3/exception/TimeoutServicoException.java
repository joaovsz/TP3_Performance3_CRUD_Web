package br.com.faculdade.tp3.exception;

public class TimeoutServicoException extends RuntimeException {

    public TimeoutServicoException(String mensagem) {
        super(mensagem);
    }
}
