package com.tp2.engsoftware.unit;

import com.tp2.engsoftware.exception.ProdutoDuplicadoException;
import com.tp2.engsoftware.exception.ProdutoNotFoundException;
import com.tp2.engsoftware.handler.ProdutoCommandHandler;
import com.tp2.engsoftware.handler.ProdutoQueryHandler;
import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.repository.ProdutoRepository;
import com.tp2.engsoftware.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    private ProdutoCommandHandler commandHandler;
    private ProdutoQueryHandler queryHandler;
    private ProdutoService service;

    private Produto produto;

    @BeforeEach
    public void setup() {
        commandHandler = new ProdutoCommandHandler(repository);
        queryHandler = new ProdutoQueryHandler(repository);
        service = new ProdutoService(commandHandler, queryHandler);
        
        produto = new Produto("Notebook", "Notebook Dell", BigDecimal.valueOf(3500.00), 10);
        produto.setId(1L);
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    public void deveListarTodosProdutos() {
        List<Produto> produtos = Arrays.asList(produto, new Produto("Mouse", "Mouse USB", BigDecimal.valueOf(25.90), 50));
        when(repository.findAll()).thenReturn(produtos);

        List<Produto> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(produto);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar produto por ID existente")
    public void deveBuscarProdutoPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        Produto resultado = service.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Notebook");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto inexistente")
    public void deveLancarExcecaoAoBuscarProdutoInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
            .isInstanceOf(ProdutoNotFoundException.class)
            .hasMessageContaining("999");

        verify(repository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar produtos por nome")
    public void deveBuscarProdutosPorNome() {
        List<Produto> produtos = Arrays.asList(produto);
        when(repository.findByNomeContainingIgnoreCase("Note")).thenReturn(produtos);

        List<Produto> resultado = service.buscarPorNome("Note");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).contains("Notebook");
        verify(repository, times(1)).findByNomeContainingIgnoreCase("Note");
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    public void deveCriarProduto() {
        Produto novoProduto = new Produto("Teclado", "Teclado Mecânico", BigDecimal.valueOf(350.00), 20);
        when(repository.existsByNomeIgnoreCase("Teclado")).thenReturn(false);
        when(repository.save(novoProduto)).thenReturn(novoProduto);

        Produto resultado = service.criar(novoProduto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Teclado");
        verify(repository, times(1)).save(novoProduto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com nome duplicado")
    public void deveLancarExcecaoAoCriarProdutoDuplicado() {
        when(repository.existsByNomeIgnoreCase("Notebook")).thenReturn(true);
        when(repository.findByNomeContainingIgnoreCase("Notebook")).thenReturn(Arrays.asList(produto));

        Produto produtoDuplicado = new Produto("Notebook", "Outro notebook", BigDecimal.valueOf(4000.00), 5);

        assertThatThrownBy(() -> service.criar(produtoDuplicado))
            .isInstanceOf(ProdutoDuplicadoException.class)
            .hasMessageContaining("Notebook");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar produto existente")
    public void deveAtualizarProduto() {
        Produto produtoAtualizado = new Produto("Notebook HP", "Notebook HP ProBook", BigDecimal.valueOf(4000.00), 8);

        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.existsByNomeIgnoreCase("Notebook HP")).thenReturn(false);
        when(repository.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = service.atualizar(1L, produtoAtualizado);

        assertThat(resultado.getNome()).isEqualTo("Notebook HP");
        verify(repository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    public void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Produto produtoAtualizado = new Produto("Nome", "Descrição atualizada", BigDecimal.valueOf(100.00), 5);

        assertThatThrownBy(() -> service.atualizar(999L, produtoAtualizado))
            .isInstanceOf(ProdutoNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar produto existente")
    public void deveDeletarProduto() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        doNothing().when(repository).delete(produto);

        service.deletar(1L);

        verify(repository, times(1)).delete(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto inexistente")
    public void deveLancarExcecaoAoDeletarProdutoInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletar(999L))
            .isInstanceOf(ProdutoNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve permitir atualizar produto mantendo o mesmo nome")
    public void devePermitirAtualizarProdutoComMesmoNome() {
        Produto produtoAtualizado = new Produto("Notebook", "Nova descrição", BigDecimal.valueOf(3600.00), 12);

        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.existsByNomeIgnoreCase("Notebook")).thenReturn(true);
        when(repository.findByNomeContainingIgnoreCase("Notebook")).thenReturn(Arrays.asList(produto));
        when(repository.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = service.atualizar(1L, produtoAtualizado);

        assertThat(resultado).isNotNull();
        verify(repository, times(1)).save(any(Produto.class));
    }
}
