package br.com.faculdade.tp3.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Locale;

@Entity
@DiscriminatorValue("TEC")
public class FuncionarioTecnico extends Funcionario {

    @Override
    public boolean aceitaPromocaoPara(String novoCargo) {
        if (novoCargo == null) {
            return false;
        }
        String cargoNormalizado = novoCargo.toLowerCase(Locale.ROOT);
        return cargoNormalizado.contains("analista")
                || cargoNormalizado.contains("desenvolvedor")
                || cargoNormalizado.contains("engenheiro")
                || cargoNormalizado.contains("qa")
                || cargoNormalizado.contains("arquiteto")
                || cargoNormalizado.contains("tech")
                || cargoNormalizado.contains("lead")
                || cargoNormalizado.contains("coordenador")
                || cargoNormalizado.contains("gerente");
    }
}
