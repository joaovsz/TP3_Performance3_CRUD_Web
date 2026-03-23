package com.tp2.engsoftware.query;

import jakarta.validation.constraints.NotNull;

/**
 * Query para buscar um produto por ID.
 * Representa a intenção de consultar dados (não modifica estado).
 */
public class GetProdutoByIdQuery {

    @NotNull(message = "ID é obrigatório")
    private Long id;

    public GetProdutoByIdQuery() {
    }

    public GetProdutoByIdQuery(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
