package com.tp2.engsoftware.handler;

import com.tp2.engsoftware.exception.ProdutoNotFoundException;
import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.query.GetProdutoByIdQuery;
import com.tp2.engsoftware.query.ListAllProdutosQuery;
import com.tp2.engsoftware.query.SearchProdutosByNomeQuery;
import com.tp2.engsoftware.repository.ProdutoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler responsável por processar Queries (operações read-only).
 * Implementa o padrão CQS (Command Query Separation).
 */
@Component
@Transactional(readOnly = true)
public class ProdutoQueryHandler {

    private final ProdutoRepository repository;

    public ProdutoQueryHandler(ProdutoRepository repository) {
        this.repository = repository;
    }

    /**
     * Processa query de listagem de todos os produtos.
     * @param query Query sem parâmetros
     * @return Lista de todos os produtos
     */
    public List<Produto> handle(ListAllProdutosQuery query) {
        return repository.findAll();
    }

    /**
     * Processa query de busca por ID.
     * @param query ID do produto
     * @return Produto encontrado
     * @throws ProdutoNotFoundException se produto não existir
     */
    public Produto handle(GetProdutoByIdQuery query) {
        return repository.findById(query.getId())
                .orElseThrow(() -> new ProdutoNotFoundException(query.getId()));
    }

    /**
     * Processa query de busca por nome.
     * @param query Nome (ou parte do nome) para busca
     * @return Lista de produtos que contêm o nome
     */
    public List<Produto> handle(SearchProdutosByNomeQuery query) {
        return repository.findByNomeContainingIgnoreCase(query.getNome());
    }
}
