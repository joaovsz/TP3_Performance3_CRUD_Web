package com.tp2.engsoftware.unit;

import com.tp2.engsoftware.exception.ProdutoDuplicadoException;
import com.tp2.engsoftware.exception.ProdutoNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionTest {

    @Test
    @DisplayName("ProdutoNotFoundException deve conter ID no mensagem")
    public void produtoNotFoundExceptionDeveConterIdNaMensagem() {
        Long id = 123L;
        ProdutoNotFoundException exception = new ProdutoNotFoundException(id);

        assertThat(exception.getMessage()).contains("123");
        assertThat(exception.getMessage()).contains("não encontrado");
    }

    @Test
    @DisplayName("ProdutoDuplicadoException deve conter nome na mensagem")
    public void produtoDuplicadoExceptionDeveConterNomeNaMensagem() {
        String nome = "Notebook Dell";
        ProdutoDuplicadoException exception = new ProdutoDuplicadoException(nome);

        assertThat(exception.getMessage()).contains("Notebook Dell");
        assertThat(exception.getMessage()).contains("Já existe");
    }
}
