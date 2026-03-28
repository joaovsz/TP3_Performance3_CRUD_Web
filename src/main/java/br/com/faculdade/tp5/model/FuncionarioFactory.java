package br.com.faculdade.tp5.model;

import java.time.LocalDate;
import java.util.Locale;

import br.com.faculdade.tp5.model.enums.FuncionarioStatus;

public final class FuncionarioFactory {

    private FuncionarioFactory() {
    }

    public static Funcionario criarPorCargo(
            String nome,
            String email,
            String cpf,
            String cargo,
            Departamento departamento,
            LocalDate dataAdmissao
    ) {
        Funcionario funcionario = instanciarPorCargo(cargo);
        return Funcionario.builder(funcionario)
                .nome(nome)
                .email(email)
                .cpf(cpf)
                .cargo(cargo)
                .departamento(departamento)
                .status(FuncionarioStatus.ATIVO)
                .dataAdmissao(dataAdmissao)
                .build();
    }

    private static Funcionario instanciarPorCargo(String cargo) {
        String cargoNormalizado = cargo == null ? "" : cargo.toLowerCase(Locale.ROOT);

        if (cargoNormalizado.contains("gerente")
                || cargoNormalizado.contains("coordenador")
                || cargoNormalizado.contains("lead")
                || cargoNormalizado.contains("líder")
                || cargoNormalizado.contains("lider")
                || cargoNormalizado.contains("diretor")) {
            return new FuncionarioLideranca();
        }

        if (cargoNormalizado.contains("analista")
                || cargoNormalizado.contains("desenvolvedor")
                || cargoNormalizado.contains("engenheiro")
                || cargoNormalizado.contains("arquiteto")
                || cargoNormalizado.contains("qa")
                || cargoNormalizado.contains("tech")) {
            return new FuncionarioTecnico();
        }

        return new FuncionarioAdministrativo();
    }
}
