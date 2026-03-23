package com.tp2.engsoftware.integration;

import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProdutoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/produtos deve retornar lista vazia quando não há produtos")
    public void deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/api/produtos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/produtos deve retornar todos os produtos")
    public void deveRetornarTodosProdutos() throws Exception {
        repository.save(new Produto("Mouse", "Mouse USB com fio e sensor óptico", BigDecimal.valueOf(25.90), 50));
        repository.save(new Produto("Teclado", "Teclado Mecânico RGB com switches", BigDecimal.valueOf(350.00), 20));

        mockMvc.perform(get("/api/produtos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome", is("Mouse")))
            .andExpect(jsonPath("$[1].nome", is("Teclado")));
    }

    @Test
    @DisplayName("GET /api/produtos/{id} deve retornar produto por ID")
    public void deveRetornarProdutoPorId() throws Exception {
        Produto produto = repository.save(
            new Produto("Monitor", "Monitor LED 24\"", BigDecimal.valueOf(800.00), 15)
        );

        mockMvc.perform(get("/api/produtos/" + produto.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Monitor")))
            .andExpect(jsonPath("$.preco", is(800.00)));
    }

    @Test
    @DisplayName("GET /api/produtos/{id} deve retornar 404 para produto inexistente")
    public void deveRetornar404ParaProdutoInexistente() throws Exception {
        mockMvc.perform(get("/api/produtos/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", containsString("não encontrado")));
    }

    @Test
    @DisplayName("POST /api/produtos deve criar novo produto")
    public void deveCriarNovoProduto() throws Exception {
        Produto produto = new Produto("Webcam", "Webcam HD 1080p", BigDecimal.valueOf(250.00), 30);

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.nome", is("Webcam")))
            .andExpect(jsonPath("$.preco", is(250.00)));
    }

    @Test
    @DisplayName("POST /api/produtos deve retornar 400 para dados inválidos")
    public void deveRetornar400ParaDadosInvalidos() throws Exception {
        Produto produto = new Produto("AB", "Desc", BigDecimal.valueOf(-10.00), -5);

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", aMapWithSize(greaterThan(0))));
    }

    @Test
    @DisplayName("POST /api/produtos deve retornar 409 para nome duplicado")
    public void deveRetornar409ParaNomeDuplicado() throws Exception {
        repository.save(new Produto("Impressora", "Impressora HP", BigDecimal.valueOf(1500.00), 5));

        Produto produtoDuplicado = new Produto("Impressora", "Outra descrição", BigDecimal.valueOf(1800.00), 3);

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoDuplicado)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error", containsString("Já existe")));
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} deve atualizar produto existente")
    public void deveAtualizarProduto() throws Exception {
        Produto produto = repository.save(
            new Produto("Scanner", "Scanner de mesa", BigDecimal.valueOf(600.00), 12)
        );

        Produto produtoAtualizado = new Produto("Scanner HP", "Scanner HP de alta resolução", BigDecimal.valueOf(750.00), 10);

        mockMvc.perform(put("/api/produtos/" + produto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoAtualizado)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Scanner HP")))
            .andExpect(jsonPath("$.preco", is(750.00)));
    }

    @Test
    @DisplayName("DELETE /api/produtos/{id} deve deletar produto existente")
    public void deveDeletarProduto() throws Exception {
        Produto produto = repository.save(
            new Produto("Headset", "Headset gamer", BigDecimal.valueOf(300.00), 15)
        );

        mockMvc.perform(delete("/api/produtos/" + produto.getId()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/produtos/" + produto.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/produtos/buscar deve buscar produtos por nome")
    public void deveBuscarProdutosPorNome() throws Exception {
        repository.save(new Produto("Notebook Dell", "Notebook Dell Inspiron 15 com processador Intel", BigDecimal.valueOf(3500.00), 10));
        repository.save(new Produto("Notebook HP", "Notebook HP ProBook 450 G8", BigDecimal.valueOf(4000.00), 8));
        repository.save(new Produto("Mouse", "Mouse USB com fio e sensor óptico", BigDecimal.valueOf(25.90), 50));

        mockMvc.perform(get("/api/produtos/buscar?nome=Notebook"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome", containsString("Notebook")))
            .andExpect(jsonPath("$[1].nome", containsString("Notebook")));
    }
}
