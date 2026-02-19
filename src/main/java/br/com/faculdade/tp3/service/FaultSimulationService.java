package br.com.faculdade.tp3.service;

import br.com.faculdade.tp3.exception.EntradaInvalidaException;
import br.com.faculdade.tp3.exception.SobrecargaSistemaException;
import br.com.faculdade.tp3.exception.TimeoutServicoException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;

@Service
public class FaultSimulationService {

    private static final long MAX_DELAY_MS = 15_000;
    private final Semaphore overloadGuard = new Semaphore(1);

    public String simularTimeout(long delayMs, long timeoutMs) {
        validarDelay(delayMs);
        validarTimeout(timeoutMs);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(delayMs);
            return "Processamento finalizado";
        });

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw new TimeoutServicoException("Tempo limite excedido ao processar a operação.");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new TimeoutServicoException("Processamento interrompido.");
        } catch (ExecutionException ex) {
            throw new RuntimeException("Erro inesperado durante processamento assíncrono.");
        }
    }

    public String simularSobrecarga(long holdMs) {
        validarDelay(holdMs);

        boolean acquired = overloadGuard.tryAcquire();
        if (!acquired) {
            throw new SobrecargaSistemaException("Sistema temporariamente sobrecarregado. Tente novamente em instantes.");
        }

        try {
            sleep(holdMs);
            return "Requisição processada com sucesso";
        } finally {
            overloadGuard.release();
        }
    }

    private void validarDelay(long delayMs) {
        if (delayMs < 0 || delayMs > MAX_DELAY_MS) {
            throw new EntradaInvalidaException("Parâmetro de delay inválido.");
        }
    }

    private void validarTimeout(long timeoutMs) {
        if (timeoutMs <= 0 || timeoutMs > MAX_DELAY_MS) {
            throw new EntradaInvalidaException("Parâmetro de timeout inválido.");
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new TimeoutServicoException("Processamento interrompido.");
        }
    }
}
