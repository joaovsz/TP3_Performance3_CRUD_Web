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
import org.springframework.stereotype.Service;

import java.util.List;

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
        return queryHandler.handle(new ListAllProdutosQuery());
    }

    public Produto buscarPorId(Long id) {
        return queryHandler.handle(new GetProdutoByIdQuery(id));
    }

    public List<Produto> buscarPorNome(String nome) {
        return queryHandler.handle(new SearchProdutosByNomeQuery(nome));
    }

    // ========== COMMANDS (Modificam Estado) ==========

    public Produto criar(Produto produto) {
        CreateProdutoCommand command = new CreateProdutoCommand(
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidade()
        );
        return commandHandler.handle(command);
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
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
        DeleteProdutoCommand command = new DeleteProdutoCommand(id);
        commandHandler.handle(command);
    }
}
