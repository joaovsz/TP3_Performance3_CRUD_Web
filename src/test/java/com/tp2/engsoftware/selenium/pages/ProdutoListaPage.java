package com.tp2.engsoftware.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProdutoListaPage extends BasePage {

    @FindBy(id = "btn-novo-produto")
    private WebElement btnNovoProduto;

    @FindBy(id = "tabela-produtos")
    private WebElement tabelaProdutos;

    @FindBy(id = "msg-vazio")
    private WebElement msgVazio;

    @FindBy(id = "msg-sucesso")
    private WebElement msgSucesso;

    @FindBy(id = "msg-erro")
    private WebElement msgErro;

    public ProdutoListaPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void acessarPagina() {
        navigateTo("/produtos");
    }

    public void clicarNovoProduto() {
        waitForElementToBeClickable(btnNovoProduto);
        btnNovoProduto.click();
    }

    public boolean isTabelaVisivel() {
        try {
            return tabelaProdutos.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMensagemVaziaVisivel() {
        try {
            return msgVazio.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getQuantidadeProdutos() {
        if (!isTabelaVisivel()) {
            return 0;
        }
        List<WebElement> linhas = tabelaProdutos.findElements(By.cssSelector("tbody tr"));
        return linhas.size();
    }

    public String getMensagemSucesso() {
        try {
            waitForElement(msgSucesso);
            return msgSucesso.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getMensagemErro() {
        try {
            waitForElement(msgErro);
            return msgErro.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void clicarEditarProduto(Long id) {
        WebElement btnEditar = driver.findElement(
            By.cssSelector("a.btn-editar[data-id='" + id + "']")
        );
        waitForElementToBeClickable(btnEditar);
        btnEditar.click();
    }

    public void clicarDeletarProduto(Long id) {
        WebElement btnDeletar = driver.findElement(
            By.cssSelector("a.btn-deletar[data-id='" + id + "']")
        );
        waitForElementToBeClickable(btnDeletar);
        btnDeletar.click();
        driver.switchTo().alert().accept();
    }

    public boolean produtoExisteNaTabela(String nome) {
        if (!isTabelaVisivel()) {
            return false;
        }
        List<WebElement> produtos = driver.findElements(By.className("produto-nome"));
        return produtos.stream().anyMatch(p -> p.getText().equals(nome));
    }

    public String getPrecoProduto(String nome) {
        if (!isTabelaVisivel()) {
            return "";
        }
        List<WebElement> linhas = tabelaProdutos.findElements(By.cssSelector("tbody tr"));
        for (WebElement linha : linhas) {
            String nomeProduto = linha.findElement(By.className("produto-nome")).getText();
            if (nomeProduto.equals(nome)) {
                return linha.findElement(By.className("produto-preco")).getText();
            }
        }
        return "";
    }
}
