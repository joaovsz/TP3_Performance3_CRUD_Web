package com.tp2.engsoftware.selenium.tests;

import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.repository.ProdutoRepository;
import com.tp2.engsoftware.selenium.pages.ProdutoFormPage;
import com.tp2.engsoftware.selenium.pages.ProdutoListaPage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProdutoCrudSeleniumTest extends BaseSeleniumTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    private ProdutoListaPage listaPage;
    private ProdutoFormPage formPage;

    @BeforeEach
    public void setupPages() {
        super.setup();
        listaPage = new ProdutoListaPage(driver);
        formPage = new ProdutoFormPage(driver);
        produtoRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Deve exibir mensagem quando não há produtos cadastrados")
    public void deveExibirMensagemListaVazia() {
        driver.get(getBaseUrl() + "/produtos");

        assertThat(listaPage.isMensagemVaziaVisivel()).isTrue();
        assertThat(listaPage.isTabelaVisivel()).isFalse();
    }

    @Test
    @Order(2)
    @DisplayName("Deve criar novo produto com sucesso")
    public void deveCriarNovoProduto() {
        driver.get(getBaseUrl() + "/produtos/novo");

        formPage.preencherFormulario(
            "Notebook Dell",
            "Notebook Dell Inspiron 15, Intel Core i5, 8GB RAM",
            "3500.00",
            "10"
        );
        formPage.clicarSalvar();

        assertThat(driver.getCurrentUrl()).contains("/produtos");
        assertThat(listaPage.getMensagemSucesso()).contains("cadastrado com sucesso");
        assertThat(listaPage.produtoExisteNaTabela("Notebook Dell")).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Deve listar todos os produtos cadastrados")
    public void deveListarProdutos() {
        produtoRepository.save(new Produto("Mouse", "Mouse USB com fio", BigDecimal.valueOf(25.90), 50));
        produtoRepository.save(new Produto("Teclado", "Teclado mecânico RGB", BigDecimal.valueOf(350.00), 20));

        driver.get(getBaseUrl() + "/produtos");

        assertThat(listaPage.isTabelaVisivel()).isTrue();
        assertThat(listaPage.getQuantidadeProdutos()).isEqualTo(2);
        assertThat(listaPage.produtoExisteNaTabela("Mouse")).isTrue();
        assertThat(listaPage.produtoExisteNaTabela("Teclado")).isTrue();
    }

    @Test
    @Order(4)
    @DisplayName("Deve editar produto existente")
    public void deveEditarProduto() {
        Produto produto = produtoRepository.save(
            new Produto("Monitor", "Monitor LED 24 polegadas", BigDecimal.valueOf(800.00), 15)
        );

        driver.get(getBaseUrl() + "/produtos/editar/" + produto.getId());

        assertThat(formPage.getValorNome()).isEqualTo("Monitor");

        formPage.preencherFormulario(
            "Monitor LG",
            "Monitor LG LED 27 polegadas Full HD",
            "1200.00",
            "8"
        );
        formPage.clicarSalvar();

        assertThat(driver.getCurrentUrl()).contains("/produtos");
        assertThat(listaPage.getMensagemSucesso()).contains("atualizado com sucesso");
        assertThat(listaPage.produtoExisteNaTabela("Monitor LG")).isTrue();
        assertThat(listaPage.getPrecoProduto("Monitor LG")).contains("1.200,00");
    }

    @Test
    @Order(5)
    @DisplayName("Deve deletar produto existente")
    public void deveDeletarProduto() {
        Produto produto = produtoRepository.save(
            new Produto("Webcam", "Webcam HD 1080p", BigDecimal.valueOf(250.00), 30)
        );

        driver.get(getBaseUrl() + "/produtos");

        int quantidadeInicial = listaPage.getQuantidadeProdutos();
        listaPage.clicarDeletarProduto(produto.getId());

        assertThat(driver.getCurrentUrl()).contains("/produtos");
        assertThat(listaPage.getMensagemSucesso()).contains("deletado com sucesso");

        if (quantidadeInicial > 1) {
            assertThat(listaPage.getQuantidadeProdutos()).isEqualTo(quantidadeInicial - 1);
        } else {
            assertThat(listaPage.isMensagemVaziaVisivel()).isTrue();
        }
    }

    @Test
    @Order(6)
    @DisplayName("Não deve criar produto com nome duplicado")
    public void naoDeveCriarProdutoComNomeDuplicado() {
        produtoRepository.save(
            new Produto("Impressora", "Impressora HP LaserJet", BigDecimal.valueOf(1500.00), 5)
        );

        driver.get(getBaseUrl() + "/produtos/novo");

        formPage.preencherFormulario(
            "Impressora",
            "Outra impressora com mesmo nome",
            "1800.00",
            "3"
        );
        formPage.clicarSalvar();

        assertThat(listaPage.getMensagemErro()).contains("Já existe um produto");
    }

    @Test
    @Order(7)
    @DisplayName("Deve navegar do formulário de volta para lista")
    public void deveVoltarParaLista() {
        driver.get(getBaseUrl() + "/produtos/novo");

        formPage.clicarVoltar();

        assertThat(driver.getCurrentUrl()).endsWith("/produtos");
    }

    @Test
    @Order(8)
    @DisplayName("Deve cancelar edição e voltar para lista")
    public void deveCancelarEdicao() {
        Produto produto = produtoRepository.save(
            new Produto("Scanner", "Scanner de mesa", BigDecimal.valueOf(600.00), 12)
        );

        driver.get(getBaseUrl() + "/produtos/editar/" + produto.getId());

        formPage.preencherFormulario(
            "Scanner Modificado",
            "Descrição modificada mas será cancelada",
            "700.00",
            "10"
        );
        formPage.clicarCancelar();

        assertThat(driver.getCurrentUrl()).endsWith("/produtos");

        Produto produtoNaoModificado = produtoRepository.findById(produto.getId()).get();
        assertThat(produtoNaoModificado.getNome()).isEqualTo("Scanner");
    }
}
