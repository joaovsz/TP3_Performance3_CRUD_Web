package com.tp2.engsoftware.unit;

import com.tp2.engsoftware.controller.ProdutoWebController;
import com.tp2.engsoftware.exception.ProdutoDuplicadoException;
import com.tp2.engsoftware.exception.ProdutoNotFoundException;
import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoWebController.class)
class ProdutoWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    private Produto criarProduto(Long id, String nome, String descricao, String preco, Integer quantidade) {
        Produto produto = new Produto(nome, descricao, new BigDecimal(preco), quantidade);
        if (id != null) {
            produto.setId(id);
        }
        return produto;
    }

    @Test
    void deveListarProdutos() throws Exception {
        List<Produto> produtos = Arrays.asList(
            criarProduto(1L, "Produto 1", "Descrição do produto 1", "10.00", 5),
            criarProduto(2L, "Produto 2", "Descrição do produto 2", "20.00", 10)
        );
        when(produtoService.listarTodos()).thenReturn(produtos);

        mockMvc.perform(get("/produtos"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/lista"))
            .andExpect(model().attributeExists("produtos"))
            .andExpect(model().attribute("produtos", produtos));

        verify(produtoService).listarTodos();
    }

    @Test
    void deveListarProdutosVazioQuandoNaoHouverProdutos() throws Exception {
        when(produtoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/produtos"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/lista"))
            .andExpect(model().attributeExists("produtos"));

        verify(produtoService).listarTodos();
    }

    @Test
    void deveExibirFormularioParaNovoProduto() throws Exception {
        mockMvc.perform(get("/produtos/novo"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/form"))
            .andExpect(model().attributeExists("produto"));
    }

    @Test
    void deveExibirFormularioParaEditarProduto() throws Exception {
        Produto produto = criarProduto(1L, "Produto Teste", "Descrição teste", "100.00", 10);
        when(produtoService.buscarPorId(1L)).thenReturn(produto);

        mockMvc.perform(get("/produtos/editar/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/form"))
            .andExpect(model().attributeExists("produto"));

        verify(produtoService).buscarPorId(1L);
    }

    @Test
    void deveRedirecionarParaListaQuandoProdutoNaoEncontradoParaEdicao() throws Exception {
        when(produtoService.buscarPorId(999L)).thenThrow(new ProdutoNotFoundException(999L));

        mockMvc.perform(get("/produtos/editar/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/produtos"));

        verify(produtoService).buscarPorId(999L);
    }

    @Test
    void deveCriarNovoProdutoComSucesso() throws Exception {
        Produto produtoSalvo = criarProduto(1L, "Novo Produto", "Descrição do novo produto", "50.00", 15);
        when(produtoService.criar(any(Produto.class))).thenReturn(produtoSalvo);

        mockMvc.perform(post("/produtos")
                .param("nome", "Novo Produto")
                .param("descricao", "Descrição do novo produto")
                .param("preco", "50.00")
                .param("quantidade", "15"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/produtos"))
            .andExpect(flash().attributeExists("sucesso"));

        verify(produtoService).criar(any(Produto.class));
    }

    @Test
    void deveAtualizarProdutoExistenteComSucesso() throws Exception {
        Produto produtoAtualizado = criarProduto(1L, "Produto Editado", "Descrição editada", "150.00", 20);
        when(produtoService.atualizar(eq(1L), any(Produto.class))).thenReturn(produtoAtualizado);

        mockMvc.perform(post("/produtos")
                .param("id", "1")
                .param("nome", "Produto Editado")
                .param("descricao", "Descrição editada")
                .param("preco", "150.00")
                .param("quantidade", "20"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/produtos"))
            .andExpect(flash().attributeExists("sucesso"));

        verify(produtoService).atualizar(eq(1L), any(Produto.class));
    }

    @Test
    void deveRetornarFormularioQuandoHouverErroDeValidacaoNomeVazio() throws Exception {
        mockMvc.perform(post("/produtos")
                .param("nome", "")
                .param("descricao", "Descrição válida aqui")
                .param("preco", "50.00")
                .param("quantidade", "10"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/form"))
            .andExpect(model().attributeHasFieldErrors("produto", "nome"));

        verify(produtoService, never()).criar(any(Produto.class));
        verify(produtoService, never()).atualizar(anyLong(), any(Produto.class));
    }

    @Test
    void deveRetornarFormularioQuandoDescricaoForCurta() throws Exception {
        mockMvc.perform(post("/produtos")
                .param("nome", "Produto Teste")
                .param("descricao", "Curta")
                .param("preco", "50.00")
                .param("quantidade", "10"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/form"))
            .andExpect(model().attributeHasFieldErrors("produto", "descricao"));

        verify(produtoService, never()).criar(any(Produto.class));
    }

    @Test
    void deveRetornarFormularioQuandoPrecoForNegativo() throws Exception {
        mockMvc.perform(post("/produtos")
                .param("nome", "Produto Teste")
                .param("descricao", "Descrição válida do produto")
                .param("preco", "-10.00")
                .param("quantidade", "10"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/form"))
            .andExpect(model().attributeHasFieldErrors("produto", "preco"));

        verify(produtoService, never()).criar(any(Produto.class));
    }

    @Test
    void deveRetornarFormularioQuandoPrecoForZero() throws Exception {
        mockMvc.perform(post("/produtos")
                .param("nome", "Produto Teste")
                .param("descricao", "Descrição válida do produto")
                .param("preco", "0.00")
                .param("quantidade", "10"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/form"))
            .andExpect(model().attributeHasFieldErrors("produto", "preco"));

        verify(produtoService, never()).criar(any(Produto.class));
    }

    @Test
    void deveRetornarFormularioQuandoQuantidadeForNegativa() throws Exception {
        mockMvc.perform(post("/produtos")
                .param("nome", "Produto Teste")
                .param("descricao", "Descrição válida do produto")
                .param("preco", "50.00")
                .param("quantidade", "-5"))
            .andExpect(status().isOk())
            .andExpect(view().name("produtos/form"))
            .andExpect(model().attributeHasFieldErrors("produto", "quantidade"));

        verify(produtoService, never()).criar(any(Produto.class));
    }

    @Test
    void deveRedirecionarComErroQuandoProdutoJaExiste() throws Exception {
        when(produtoService.criar(any(Produto.class)))
            .thenThrow(new ProdutoDuplicadoException("Produto com este nome já existe"));

        mockMvc.perform(post("/produtos")
                .param("nome", "Produto Duplicado")
                .param("descricao", "Descrição do produto duplicado")
                .param("preco", "100.00")
                .param("quantidade", "5"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/produtos"))
            .andExpect(flash().attributeExists("erro"));

        verify(produtoService).criar(any(Produto.class));
    }

    @Test
    void deveDeletarProdutoComSucesso() throws Exception {
        doNothing().when(produtoService).deletar(1L);

        mockMvc.perform(get("/produtos/deletar/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/produtos"))
            .andExpect(flash().attributeExists("sucesso"));

        verify(produtoService).deletar(1L);
    }

    @Test
    void deveRedirecionarComErroQuandoProdutoNaoEncontradoParaDelecao() throws Exception {
        doThrow(new ProdutoNotFoundException(999L))
            .when(produtoService).deletar(999L);

        mockMvc.perform(get("/produtos/deletar/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/produtos"))
            .andExpect(flash().attributeExists("erro"));

        verify(produtoService).deletar(999L);
    }

    @Test
    void deveAceitarPrecoComCasasDecimais() throws Exception {
        when(produtoService.criar(any(Produto.class))).thenReturn(
            criarProduto(1L, "Produto", "Descrição do produto", "99.99", 10)
        );

        mockMvc.perform(post("/produtos")
                .param("nome", "Produto")
                .param("descricao", "Descrição do produto")
                .param("preco", "99.99")
                .param("quantidade", "10"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/produtos"));

        verify(produtoService).criar(any(Produto.class));
    }

    @Test
    void deveAceitarQuantidadeZero() throws Exception {
        when(produtoService.criar(any(Produto.class))).thenReturn(
            criarProduto(1L, "Produto", "Descrição do produto", "50.00", 0)
        );

        mockMvc.perform(post("/produtos")
                .param("nome", "Produto")
                .param("descricao", "Descrição do produto")
                .param("preco", "50.00")
                .param("quantidade", "0"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/produtos"));

        verify(produtoService).criar(any(Produto.class));
    }
}
