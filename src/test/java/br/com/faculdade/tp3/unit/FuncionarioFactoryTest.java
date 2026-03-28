package br.com.faculdade.tp3.unit;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.model.FuncionarioAdministrativo;
import br.com.faculdade.tp3.model.FuncionarioFactory;
import br.com.faculdade.tp3.model.FuncionarioLideranca;
import br.com.faculdade.tp3.model.FuncionarioTecnico;
import br.com.faculdade.tp3.model.enums.FuncionarioStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FuncionarioFactoryTest {

    private final Departamento departamento = new Departamento("Tecnologia", "TI");

    @Test
    void deveCriarFuncionarioTecnicoPorCargo() {
        Funcionario funcionario = FuncionarioFactory.criarPorCargo(
                "Ana",
                "ana@empresa.com",
                "12345678901",
                "Desenvolvedora Backend",
                departamento,
                LocalDate.of(2026, 1, 10)
        );

        assertThat(funcionario).isInstanceOf(FuncionarioTecnico.class);
        assertThat(funcionario.getStatus()).isEqualTo(FuncionarioStatus.ATIVO);
    }

    @Test
    void deveCriarFuncionarioDeLiderancaPorCargo() {
        Funcionario funcionario = FuncionarioFactory.criarPorCargo(
                "Carlos",
                "carlos@empresa.com",
                "10987654321",
                "Gerente de Engenharia",
                departamento,
                LocalDate.of(2026, 2, 1)
        );

        assertThat(funcionario).isInstanceOf(FuncionarioLideranca.class);
    }

    @Test
    void deveCriarFuncionarioAdministrativoComoPadrao() {
        Funcionario funcionario = FuncionarioFactory.criarPorCargo(
                "Marina",
                "marina@empresa.com",
                "11223344556",
                "Assistente Financeira",
                departamento,
                LocalDate.of(2026, 3, 1)
        );

        assertThat(funcionario).isInstanceOf(FuncionarioAdministrativo.class);
    }
}
