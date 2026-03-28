package br.com.faculdade.tp3.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LIDER")
public class FuncionarioLideranca extends Funcionario {

    @Override
    public boolean aceitaPromocaoPara(String novoCargo) {
        return novoCargo != null && !novoCargo.isBlank();
    }
}
