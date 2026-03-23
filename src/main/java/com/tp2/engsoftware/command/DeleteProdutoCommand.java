package com.tp2.engsoftware.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command para deletar um produto.
 * Representa a intenção de remover um produto (modifica estado).
 */
public class DeleteProdutoCommand {

    @NotNull(message = "ID é obrigatório")
    private Long id;

    public DeleteProdutoCommand() {
    }

    public DeleteProdutoCommand(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
