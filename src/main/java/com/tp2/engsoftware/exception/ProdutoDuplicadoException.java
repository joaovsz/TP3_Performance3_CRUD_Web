package com.tp2.engsoftware.exception;

public class ProdutoDuplicadoException extends RuntimeException {

    public ProdutoDuplicadoException(String nome) {
        super("Já existe um produto cadastrado com o nome: " + nome);
    }
}
