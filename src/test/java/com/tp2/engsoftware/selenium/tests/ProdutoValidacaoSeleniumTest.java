package com.tp2.engsoftware.selenium.tests;

import com.tp2.engsoftware.repository.ProdutoRepository;
import com.tp2.engsoftware.selenium.pages.ProdutoFormPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ProdutoValidacaoSeleniumTest extends BaseSeleniumTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    private ProdutoFormPage formPage;

    @BeforeEach
    public void setupPages() {
        super.setup();
        formPage = new ProdutoFormPage(driver);
        produtoRepository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource({
        "AB, Descrição válida com mais de dez caracteres, 100.00, 5",
        ", Descrição válida com mais de dez caracteres, 100.00, 5",
        "A, Descrição válida com mais de dez caracteres, 100.00, 5"
    })
    @DisplayName("Não deve aceitar nome inválido")
    public void naoDeveAceitarNomeInvalido(String nome, String descricao, String preco, String quantidade) {
        driver.get(getBaseUrl() + "/produtos/novo");

        if (nome != null) {
            formPage.preencherFormulario(nome, descricao, preco, quantidade);
        } else {
            formPage.limparNome();
            formPage.preencherFormulario("", descricao, preco, quantidade);
        }

        formPage.clicarSalvar();

        String url = driver.getCurrentUrl();
        assertThat(url).doesNotContain("/produtos");
        assertThat(url).contains("/produtos/novo");
    }

    @ParameterizedTest
    @CsvSource({
        "Produto Teste, Curta, 100.00, 5",
        "Produto Teste, , 100.00, 5",
        "Produto Teste, 123456789, 100.00, 5"
    })
    @DisplayName("Não deve aceitar descrição inválida")
    public void naoDeveAceitarDescricaoInvalida(String nome, String descricao, String preco, String quantidade) {
        driver.get(getBaseUrl() + "/produtos/novo");

        if (descricao != null) {
            formPage.preencherFormulario(nome, descricao, preco, quantidade);
        } else {
            formPage.preencherFormulario(nome, "", preco, quantidade);
        }

        formPage.clicarSalvar();

        String url = driver.getCurrentUrl();
        assertThat(url).contains("/produtos/novo");
    }

    @ParameterizedTest
    @CsvSource({
        "Produto Válido, Descrição válida com mais de dez caracteres, 0, 5",
        "Produto Válido, Descrição válida com mais de dez caracteres, -10.50, 5",
        "Produto Válido, Descrição válida com mais de dez caracteres, , 5"
    })
    @DisplayName("Não deve aceitar preço inválido")
    public void naoDeveAceitarPrecoInvalido(String nome, String descricao, String preco, String quantidade) {
        driver.get(getBaseUrl() + "/produtos/novo");

        formPage.preencherFormulario(nome, descricao, preco != null ? preco : "", quantidade);
        formPage.clicarSalvar();

        String url = driver.getCurrentUrl();
        assertThat(url).contains("/produtos/novo");
    }

    @ParameterizedTest
    @CsvSource({
        "Mouse Gamer, Mouse RGB com 7 botões programáveis, 150.00, 10",
        "Teclado Mecânico, Teclado com switches blue e iluminação RGB, 450.00, 25",
        "Headset, Headset gamer com som surround 7.1, 300.00, 15",
        "SSD 500GB, Unidade de estado sólido NVMe de 500GB, 400.00, 30",
        "Placa de Vídeo, GPU dedicada com 8GB VRAM, 2500.00, 5"
    })
    @DisplayName("Deve criar produtos com diferentes dados válidos")
    public void deveCriarProdutosComDadosValidos(String nome, String descricao, String preco, String quantidade) {
        driver.get(getBaseUrl() + "/produtos/novo");

        formPage.preencherFormulario(nome, descricao, preco, quantidade);
        formPage.clicarSalvar();

        assertThat(driver.getCurrentUrl()).endsWith("/produtos");
    }

    @ParameterizedTest
    @CsvSource({
        "Produto 1, Descrição do produto número um para teste, 10.50, 1",
        "Produto 2, Descrição do produto número dois para teste, 99.99, 999",
        "Produto 3, Descrição do produto número três para teste, 0.01, 0"
    })
    @DisplayName("Deve aceitar valores limite válidos")
    public void deveAceitarValoresLimiteValidos(String nome, String descricao, String preco, String quantidade) {
        driver.get(getBaseUrl() + "/produtos/novo");

        formPage.preencherFormulario(nome, descricao, preco, quantidade);
        formPage.clicarSalvar();

        assertThat(driver.getCurrentUrl()).endsWith("/produtos");
    }
}
