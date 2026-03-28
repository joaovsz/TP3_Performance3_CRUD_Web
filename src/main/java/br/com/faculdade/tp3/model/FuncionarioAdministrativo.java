package br.com.faculdade.tp3.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Locale;

@Entity
@DiscriminatorValue("ADMIN")
public class FuncionarioAdministrativo extends Funcionario {

    @Override
    public boolean aceitaPromocaoPara(String novoCargo) {
        if (novoCargo == null) {
            return false;
        }
        String cargoNormalizado = novoCargo.toLowerCase(Locale.ROOT);
        return !cargoNormalizado.contains("cto")
                && !cargoNormalizado.contains("ceo")
                && !cargoNormalizado.contains("diretor");
    }
}
