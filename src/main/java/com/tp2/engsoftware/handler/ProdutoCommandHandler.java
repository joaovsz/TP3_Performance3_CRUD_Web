package com.tp2.engsoftware.handler;

import com.tp2.engsoftware.command.CreateProdutoCommand;
import com.tp2.engsoftware.command.DeleteProdutoCommand;
import com.tp2.engsoftware.command.UpdateProdutoCommand;
import com.tp2.engsoftware.exception.ProdutoDuplicadoException;
import com.tp2.engsoftware.exception.ProdutoNotFoundException;
import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.repository.ProdutoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handler responsável por processar Commands (operações que modificam estado).
 * Implementa o padrão CQS (Command Query Separation).
 */
@Component
public class ProdutoCommandHandler {

    private final ProdutoRepository repository;

    public ProdutoCommandHandler(ProdutoRepository repository) {
        this.repository = repository;
    }

    /**
     * Processa comando de criação de produto.
     * @param command Dados para criação
     * @return Produto criado
     */
    @Transactional
    public Produto handle(CreateProdutoCommand command) {
        validarNomeDuplicado(command.getNome(), null);

        Produto produto = new Produto();
        produto.setNome(command.getNome());
        produto.setDescricao(command.getDescricao());
        produto.setPreco(command.getPreco());
        produto.setQuantidade(command.getQuantidade());

        return repository.save(produto);
    }

    /**
     * Processa comando de atualização de produto.
     * @param command Dados para atualização
     * @return Produto atualizado
     */
    @Transactional
    public Produto handle(UpdateProdutoCommand command) {
        Produto produtoExistente = repository.findById(command.getId())
                .orElseThrow(() -> new ProdutoNotFoundException(command.getId()));

        validarNomeDuplicado(command.getNome(), command.getId());

        produtoExistente.setNome(command.getNome());
        produtoExistente.setDescricao(command.getDescricao());
        produtoExistente.setPreco(command.getPreco());
        produtoExistente.setQuantidade(command.getQuantidade());

        return repository.save(produtoExistente);
    }

    /**
     * Processa comando de deleção de produto.
     * @param command ID do produto a deletar
     */
    @Transactional
    public void handle(DeleteProdutoCommand command) {
        Produto produto = repository.findById(command.getId())
                .orElseThrow(() -> new ProdutoNotFoundException(command.getId()));
        repository.delete(produto);
    }

    private void validarNomeDuplicado(String nome, Long idExcluir) {
        if (repository.existsByNomeIgnoreCase(nome)) {
            List<Produto> produtos = repository.findByNomeContainingIgnoreCase(nome);
            boolean isDuplicado = produtos.stream()
                    .anyMatch(p -> p.getNome().equalsIgnoreCase(nome) &&
                                   !p.getId().equals(idExcluir));
            if (isDuplicado) {
                throw new ProdutoDuplicadoException(nome);
            }
        }
    }
}
