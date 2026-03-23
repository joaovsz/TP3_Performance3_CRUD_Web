package com.tp2.engsoftware.integration;

import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    @BeforeEach
    public void setup() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar produto no banco de dados")
    public void deveSalvarProduto() {
        Produto produto = new Produto("Mouse", "Mouse USB com fio e sensor óptico", BigDecimal.valueOf(25.90), 50);

        Produto produtoSalvo = repository.save(produto);

        assertThat(produtoSalvo.getId()).isNotNull();
        assertThat(produtoSalvo.getNome()).isEqualTo("Mouse");
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    public void deveBuscarProdutoPorId() {
        Produto produto = repository.save(
            new Produto("Teclado", "Teclado Mecânico", BigDecimal.valueOf(350.00), 20)
        );

        Optional<Produto> encontrado = repository.findById(produto.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Teclado");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar ID inexistente")
    public void deveRetornarVazioParaIdInexistente() {
        Optional<Produto> encontrado = repository.findById(999L);

        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar produtos por nome ignorando case")
    public void deveBuscarProdutosPorNome() {
        repository.save(new Produto("Notebook Dell", "Notebook Dell Inspiron 15 com processador Intel", BigDecimal.valueOf(3500.00), 10));
        repository.save(new Produto("Notebook HP", "Notebook HP ProBook 450 G8", BigDecimal.valueOf(4000.00), 8));
        repository.save(new Produto("Mouse", "Mouse USB com fio e sensor óptico", BigDecimal.valueOf(25.90), 50));

        List<Produto> resultado = repository.findByNomeContainingIgnoreCase("notebook");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(p -> p.getNome().toLowerCase().contains("notebook"));
    }

    @Test
    @DisplayName("Deve verificar se existe produto por nome")
    public void deveVerificarExistenciaPorNome() {
        repository.save(new Produto("Monitor", "Monitor LED", BigDecimal.valueOf(800.00), 15));

        boolean existe = repository.existsByNomeIgnoreCase("monitor");
        boolean naoExiste = repository.existsByNomeIgnoreCase("webcam");

        assertThat(existe).isTrue();
        assertThat(naoExiste).isFalse();
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    public void deveListarTodosProdutos() {
        repository.save(new Produto("Produto 1", "Descrição do produto 1", BigDecimal.valueOf(100.00), 10));
        repository.save(new Produto("Produto 2", "Descrição do produto 2", BigDecimal.valueOf(200.00), 20));
        repository.save(new Produto("Produto 3", "Descrição do produto 3", BigDecimal.valueOf(300.00), 30));

        List<Produto> produtos = repository.findAll();

        assertThat(produtos).hasSize(3);
    }

    @Test
    @DisplayName("Deve atualizar produto existente")
    public void deveAtualizarProduto() {
        Produto produto = repository.save(
            new Produto("Scanner", "Scanner de mesa", BigDecimal.valueOf(600.00), 12)
        );

        produto.setNome("Scanner HP");
        produto.setPreco(BigDecimal.valueOf(750.00));
        Produto atualizado = repository.save(produto);

        assertThat(atualizado.getNome()).isEqualTo("Scanner HP");
        assertThat(atualizado.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(750.00));
    }

    @Test
    @DisplayName("Deve deletar produto")
    public void deveDeletarProduto() {
        Produto produto = repository.save(
            new Produto("Headset", "Headset gamer", BigDecimal.valueOf(300.00), 15)
        );

        repository.delete(produto);

        Optional<Produto> encontrado = repository.findById(produto.getId());
        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve contar produtos corretamente")
    public void deveContarProdutos() {
        repository.save(new Produto("Produto 1", "Descrição 1", BigDecimal.valueOf(100.00), 10));
        repository.save(new Produto("Produto 2", "Descrição 2", BigDecimal.valueOf(200.00), 20));

        long count = repository.count();

        assertThat(count).isEqualTo(2);
    }
}
