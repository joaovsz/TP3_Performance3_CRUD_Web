package br.com.faculdade.tp3.dto.rh;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class AjusteSalarialPayload {

    @NotNull(message = "Percentual é obrigatório")
    @DecimalMin(value = "0.01", message = "Percentual deve ser maior que zero")
    @Digits(integer = 4, fraction = 2, message = "Percentual inválido")
    private BigDecimal percentual;

    @NotBlank(message = "Motivo é obrigatório")
    @Size(min = 5, max = 255, message = "Motivo deve ter entre 5 e 255 caracteres")
    private String motivo;

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
