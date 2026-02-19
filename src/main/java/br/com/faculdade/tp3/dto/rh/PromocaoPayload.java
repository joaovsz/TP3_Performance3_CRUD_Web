package br.com.faculdade.tp3.dto.rh;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class PromocaoPayload {

    @NotBlank(message = "Novo cargo é obrigatório")
    @Size(min = 2, max = 100, message = "Novo cargo deve ter entre 2 e 100 caracteres")
    private String novoCargo;

    @NotNull(message = "Percentual de aumento é obrigatório")
    @DecimalMin(value = "0.00", message = "Percentual não pode ser negativo")
    @Digits(integer = 4, fraction = 2, message = "Percentual inválido")
    private BigDecimal percentualAumento;

    @NotBlank(message = "Motivo é obrigatório")
    @Size(min = 5, max = 255, message = "Motivo deve ter entre 5 e 255 caracteres")
    private String motivo;

    public String getNovoCargo() {
        return novoCargo;
    }

    public void setNovoCargo(String novoCargo) {
        this.novoCargo = novoCargo;
    }

    public BigDecimal getPercentualAumento() {
        return percentualAumento;
    }

    public void setPercentualAumento(BigDecimal percentualAumento) {
        this.percentualAumento = percentualAumento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
