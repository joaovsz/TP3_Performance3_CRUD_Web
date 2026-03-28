package br.com.faculdade.tp5.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import br.com.faculdade.tp5.exception.EntradaInvalidaException;
import br.com.faculdade.tp5.model.Cpf;
import br.com.faculdade.tp5.util.RhValidator;

class RhValidatorTest {

    @Test
    void deveSanitizarEmailEmMinusculo() {
        String email = RhValidator.sanitizarEmail("  ANA.SOUZA@EMPRESA.COM ");
        assertThat(email).isEqualTo("ana.souza@empresa.com");
    }

    @Test
    void deveRetornarCpfComoValueObject() {
        Cpf cpf = RhValidator.sanitizarCpf("12345678901");
        assertThat(cpf.getValor()).isEqualTo("12345678901");
    }

    @Test
    void deveFalharQuandoCpfEhInvalido() {
        assertThatThrownBy(() -> RhValidator.sanitizarCpf("123"))
                .isInstanceOf(EntradaInvalidaException.class)
                .hasMessageContaining("CPF");
    }

    @Test
    void deveFalharQuandoTextoHumanoContemScript() {
        assertThatThrownBy(() -> RhValidator.sanitizarTextoHumano("DROP TABLE funcionarios", "Nome", true, 3, 120))
                .isInstanceOf(EntradaInvalidaException.class)
                .hasMessageContaining("malicioso");
    }
}
