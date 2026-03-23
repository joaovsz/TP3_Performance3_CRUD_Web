package com.tp2.engsoftware.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ProdutoFormPage extends BasePage {

    @FindBy(id = "nome")
    private WebElement inputNome;

    @FindBy(id = "descricao")
    private WebElement inputDescricao;

    @FindBy(id = "preco")
    private WebElement inputPreco;

    @FindBy(id = "quantidade")
    private WebElement inputQuantidade;

    @FindBy(id = "btn-salvar")
    private WebElement btnSalvar;

    @FindBy(id = "btn-cancelar")
    private WebElement btnCancelar;

    @FindBy(id = "btn-voltar")
    private WebElement btnVoltar;

    @FindBy(id = "erro-nome")
    private WebElement erroNome;

    @FindBy(id = "erro-descricao")
    private WebElement erroDescricao;

    @FindBy(id = "erro-preco")
    private WebElement erroPreco;

    @FindBy(id = "erro-quantidade")
    private WebElement erroQuantidade;

    public ProdutoFormPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void acessarPaginaNovo() {
        navigateTo("/produtos/novo");
    }

    public void acessarPaginaEdicao(Long id) {
        navigateTo("/produtos/editar/" + id);
    }

    public void preencherFormulario(String nome, String descricao, String preco, String quantidade) {
        waitForElement(inputNome);
        inputNome.clear();
        inputNome.sendKeys(nome);

        inputDescricao.clear();
        inputDescricao.sendKeys(descricao);

        inputPreco.clear();
        inputPreco.sendKeys(preco);

        inputQuantidade.clear();
        inputQuantidade.sendKeys(quantidade);
    }

    public void clicarSalvar() {
        waitForElementToBeClickable(btnSalvar);
        btnSalvar.click();
    }

    public void clicarCancelar() {
        waitForElementToBeClickable(btnCancelar);
        btnCancelar.click();
    }

    public void clicarVoltar() {
        waitForElementToBeClickable(btnVoltar);
        btnVoltar.click();
    }

    public String getValorNome() {
        return inputNome.getAttribute("value");
    }

    public String getValorDescricao() {
        return inputDescricao.getAttribute("value");
    }

    public String getValorPreco() {
        return inputPreco.getAttribute("value");
    }

    public String getValorQuantidade() {
        return inputQuantidade.getAttribute("value");
    }

    public boolean hasErroNome() {
        try {
            return erroNome.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasErroDescricao() {
        try {
            return erroDescricao.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasErroPreco() {
        try {
            return erroPreco.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasErroQuantidade() {
        try {
            return erroQuantidade.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void limparCampo(WebElement element) {
        element.clear();
    }

    public void limparNome() {
        inputNome.clear();
    }

    public void limparDescricao() {
        inputDescricao.clear();
    }

    public void limparPreco() {
        inputPreco.clear();
    }

    public void limparQuantidade() {
        inputQuantidade.clear();
    }
}
