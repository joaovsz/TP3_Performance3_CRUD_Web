package com.tp2.engsoftware.unit;

import com.tp2.engsoftware.model.Produto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ProdutoTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve criar produto válido")
    public void deveCriarProdutoValido() {
        Produto produto = new Produto("Notebook", "Notebook Dell Inspiron 15", BigDecimal.valueOf(3500.00), 10);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Não deve aceitar nome vazio")
    public void naoDeveAceitarNomeVazio() {
        Produto produto = new Produto("", "Descrição válida", BigDecimal.valueOf(100.00), 5);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Nome"));
    }

    @Test
    @DisplayName("Não deve aceitar nome muito curto")
    public void naoDeveAceitarNomeMuitoCurto() {
        Produto produto = new Produto("AB", "Descrição válida", BigDecimal.valueOf(100.00), 5);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Não deve aceitar descrição vazia")
    public void naoDeveAceitarDescricaoVazia() {
        Produto produto = new Produto("Nome Válido", "", BigDecimal.valueOf(100.00), 5);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Não deve aceitar descrição muito curta")
    public void naoDeveAceitarDescricaoMuitoCurta() {
        Produto produto = new Produto("Nome Válido", "Curta", BigDecimal.valueOf(100.00), 5);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Não deve aceitar preço negativo")
    public void naoDeveAceitarPrecoNegativo() {
        Produto produto = new Produto("Nome Válido", "Descrição válida com mais de dez caracteres", BigDecimal.valueOf(-10.00), 5);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Não deve aceitar preço zero")
    public void naoDeveAceitarPrecoZero() {
        Produto produto = new Produto("Nome Válido", "Descrição válida com mais de dez caracteres", BigDecimal.ZERO, 5);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Não deve aceitar quantidade negativa")
    public void naoDeveAceitarQuantidadeNegativa() {
        Produto produto = new Produto("Nome Válido", "Descrição válida com mais de dez caracteres", BigDecimal.valueOf(100.00), -1);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve aceitar quantidade zero")
    public void deveAceitarQuantidadeZero() {
        Produto produto = new Produto("Nome Válido", "Descrição válida com mais de dez caracteres", BigDecimal.valueOf(100.00), 0);

        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve testar equals e hashCode")
    public void deveTestarEqualsEHashCode() {
        Produto produto1 = new Produto("Produto", "Descrição do produto", BigDecimal.valueOf(100.00), 10);
        produto1.setId(1L);

        Produto produto2 = new Produto("Outro", "Outra descrição", BigDecimal.valueOf(200.00), 20);
        produto2.setId(1L);

        Produto produto3 = new Produto("Produto", "Descrição do produto", BigDecimal.valueOf(100.00), 10);
        produto3.setId(2L);

        assertThat(produto1).isEqualTo(produto2);
        assertThat(produto1).isNotEqualTo(produto3);
        assertThat(produto1.hashCode()).isEqualTo(produto2.hashCode());
    }

    @Test
    @DisplayName("Deve testar toString")
    public void deveTestarToString() {
        Produto produto = new Produto("Notebook", "Notebook Dell", BigDecimal.valueOf(3500.00), 10);
        produto.setId(1L);

        String toString = produto.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("nome='Notebook'");
        assertThat(toString).contains("preco=3500");
        assertThat(toString).contains("quantidade=10");
    }

    @Test
    @DisplayName("Deve testar getters e setters")
    public void deveTestarGettersESetters() {
        Produto produto = new Produto();

        produto.setId(1L);
        produto.setNome("Mouse");
        produto.setDescricao("Mouse USB com fio");
        produto.setPreco(BigDecimal.valueOf(25.90));
        produto.setQuantidade(50);

        assertThat(produto.getId()).isEqualTo(1L);
        assertThat(produto.getNome()).isEqualTo("Mouse");
        assertThat(produto.getDescricao()).isEqualTo("Mouse USB com fio");
        assertThat(produto.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(25.90));
        assertThat(produto.getQuantidade()).isEqualTo(50);
    }
}
