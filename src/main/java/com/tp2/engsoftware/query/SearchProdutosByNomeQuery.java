package com.tp2.engsoftware.query;

import jakarta.validation.constraints.NotBlank;

/**
 * Query para buscar produtos por nome.
 * Representa a intenção de consultar dados (não modifica estado).
 */
public class SearchProdutosByNomeQuery {

    @NotBlank(message = "Nome para busca é obrigatório")
    private String nome;

    public SearchProdutosByNomeQuery() {
    }

    public SearchProdutosByNomeQuery(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
