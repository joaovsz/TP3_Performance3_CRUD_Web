package br.com.faculdade.tp5.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.faculdade.tp3.model.Salario;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SalarioTest {

    @Test
    void deveAplicarAumentoComArredondamento() {
        Salario salario = new Salario();
        salario.setValorAtual(new BigDecimal("5000.00"));

        BigDecimal novoSalario = salario.aplicarAumento(new BigDecimal("10.00"));

        assertThat(novoSalario).isEqualTo(new BigDecimal("5500.00"));
        assertThat(salario.getValorAtual()).isEqualTo(new BigDecimal("5500.00"));
    }

    @Test
    void deveFalharQuandoPercentualNaoEhPositivo() {
        Salario salario = new Salario();
        salario.setValorAtual(new BigDecimal("5000.00"));

        assertThatThrownBy(() -> salario.aplicarAumento(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveFalharQuandoSalarioAtualNaoFoiDefinido() {
        Salario salario = new Salario();

        assertThatThrownBy(() -> salario.aplicarAumento(new BigDecimal("10.00")))
                .isInstanceOf(IllegalStateException.class);
    }
}
