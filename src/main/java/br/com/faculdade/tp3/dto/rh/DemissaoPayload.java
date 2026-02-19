package br.com.faculdade.tp3.dto.rh;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DemissaoPayload {

    @NotBlank(message = "Motivo da demissão é obrigatório")
    @Size(min = 5, max = 255, message = "Motivo deve ter entre 5 e 255 caracteres")
    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
