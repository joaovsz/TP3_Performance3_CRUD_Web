package br.com.faculdade.tp3.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.faculdade.tp3.exception.EntradaInvalidaException;
import br.com.faculdade.tp3.exception.SobrecargaSistemaException;
import br.com.faculdade.tp3.exception.TimeoutServicoException;
import br.com.faculdade.tp3.service.FaultSimulationService;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class FaultSimulationServiceTest {

    private final FaultSimulationService service = new FaultSimulationService();

    @Test
    void deveRetornarSucessoQuandoNaoHaTimeout() {
        String resultado = service.simularTimeout(20, 200);

        assertThat(resultado).contains("finalizado");
    }

    @Test
    void deveLancarTimeoutQuandoDelayExcedeLimite() {
        assertThatThrownBy(() -> service.simularTimeout(300, 50))
                .isInstanceOf(TimeoutServicoException.class)
                .hasMessageContaining("Tempo limite");
    }

    @Test
    void deveLancarSobrecargaQuandoRecursoJaEstaOcupado() {
        CompletableFuture<String> primeira = CompletableFuture.supplyAsync(() -> service.simularSobrecarga(250));
        boolean sobrecargaDetectada = false;

        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(20);
                service.simularSobrecarga(10);
            } catch (SobrecargaSistemaException ex) {
                sobrecargaDetectada = true;
                break;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        assertThat(sobrecargaDetectada).isTrue();
        assertThat(primeira.join()).contains("processada");
    }

    @Test
    void deveLancarErroParaParametrosInvalidos() {
        assertThatThrownBy(() -> service.simularTimeout(-1, 10))
                .isInstanceOf(EntradaInvalidaException.class);

        assertThatThrownBy(() -> service.simularTimeout(10, 0))
                .isInstanceOf(EntradaInvalidaException.class);

        assertThatThrownBy(() -> service.simularSobrecarga(-10))
                .isInstanceOf(EntradaInvalidaException.class);
    }
}
