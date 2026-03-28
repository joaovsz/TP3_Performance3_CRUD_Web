package com.tp2.engsoftware.service;

import com.tp2.engsoftware.command.CreateProdutoCommand;
import com.tp2.engsoftware.command.DeleteProdutoCommand;
import com.tp2.engsoftware.command.UpdateProdutoCommand;
import com.tp2.engsoftware.handler.ProdutoCommandHandler;
import com.tp2.engsoftware.handler.ProdutoQueryHandler;
import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.query.GetProdutoByIdQuery;
import com.tp2.engsoftware.query.ListAllProdutosQuery;
import com.tp2.engsoftware.query.SearchProdutosByNomeQuery;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service que coordena operações de produto usando CQS.
 * Delega Commands para ProdutoCommandHandler e Queries para ProdutoQueryHandler.
 */
@Service
public class ProdutoService {

    private final ProdutoCommandHandler commandHandler;
    private final ProdutoQueryHandler queryHandler;

    public ProdutoService(ProdutoCommandHandler commandHandler, ProdutoQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    // ========== QUERIES (Read-Only) ==========

    public List<Produto> listarTodos() {
        return listaImutavel(queryHandler.handle(new ListAllProdutosQuery()));
    }

    public Produto buscarPorId(Long id) {
        validarId(id);
        return queryHandler.handle(new GetProdutoByIdQuery(id));
    }

    public List<Produto> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return listarTodos();
        }
        return listaImutavel(queryHandler.handle(new SearchProdutosByNomeQuery(nome.trim())));
    }

    // ========== COMMANDS (Modificam Estado) ==========

    public Produto criar(Produto produto) {
        validarProduto(produto);
        CreateProdutoCommand command = new CreateProdutoCommand(
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidade()
        );
        return commandHandler.handle(command);
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
        validarId(id);
        validarProduto(produtoAtualizado);
        UpdateProdutoCommand command = new UpdateProdutoCommand(
                id,
                produtoAtualizado.getNome(),
                produtoAtualizado.getDescricao(),
                produtoAtualizado.getPreco(),
                produtoAtualizado.getQuantidade()
        );
        return commandHandler.handle(command);
    }

    public void deletar(Long id) {
        validarId(id);
        DeleteProdutoCommand command = new DeleteProdutoCommand(id);
        commandHandler.handle(command);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do produto deve ser positivo.");
        }
    }

    private void validarProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório.");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório.");
        }
        if (produto.getDescricao() == null || produto.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Descrição do produto é obrigatória.");
        }
        BigDecimal preco = produto.getPreco();
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço do produto deve ser maior que zero.");
        }
        Integer quantidade = produto.getQuantidade();
        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException("Quantidade do produto não pode ser negativa.");
        }
    }

    private List<Produto> listaImutavel(List<Produto> produtos) {
        if (produtos == null || produtos.isEmpty()) {
            return List.of();
        }
        return List.copyOf(produtos);
    }
}
