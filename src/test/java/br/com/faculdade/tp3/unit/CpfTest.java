package br.com.faculdade.tp3.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.faculdade.tp3.exception.EntradaInvalidaException;
import br.com.faculdade.tp3.model.Cpf;
import org.junit.jupiter.api.Test;

class CpfTest {

    @Test
    void deveCriarCpfValido() {
        Cpf cpf = new Cpf("12345678901");
        assertThat(cpf.getValor()).isEqualTo("12345678901");
    }

    @Test
    void deveFalharParaCpfInvalido() {
        assertThatThrownBy(() -> new Cpf("ABC"))
                .isInstanceOf(EntradaInvalidaException.class)
                .hasMessageContaining("CPF");
    }

    @Test
    void deveCompararCpfsPeloValor() {
        Cpf cpfA = new Cpf("12345678901");
        Cpf cpfB = new Cpf("12345678901");
        Cpf cpfC = new Cpf("10987654321");

        assertThat(cpfA).isEqualTo(cpfB);
        assertThat(cpfA).hasSameHashCodeAs(cpfB);
        assertThat(cpfA).isNotEqualTo(cpfC);
        assertThat(cpfA).isNotEqualTo(null);
        assertThat(cpfA).isNotEqualTo("12345678901");
    }

    @Test
    void deveRetornarToStringComValorDoCpf() {
        Cpf cpf = new Cpf("12345678901");
        assertThat(cpf.toString()).isEqualTo("12345678901");
    }
}
