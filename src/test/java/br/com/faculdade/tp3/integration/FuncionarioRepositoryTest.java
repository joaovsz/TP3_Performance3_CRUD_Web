package br.com.faculdade.tp3.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.model.Salario;
import br.com.faculdade.tp3.model.enums.FuncionarioStatus;
import br.com.faculdade.tp3.repository.DepartamentoRepository;
import br.com.faculdade.tp3.repository.FuncionarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class FuncionarioRepositoryTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    private Departamento departamento;

    @BeforeEach
    void setUp() {
        departamento = departamentoRepository.save(new Departamento("Tecnologia", "TI"));

        Funcionario ativo = novoFuncionario("Ana", "ana@empresa.com", "12345678901", FuncionarioStatus.ATIVO);
        Funcionario inativo = novoFuncionario("Bruno", "bruno@empresa.com", "12345678902", FuncionarioStatus.INATIVO);

        funcionarioRepository.saveAll(List.of(ativo, inativo));
    }

    @Test
    void deveFiltrarPorStatus() {
        List<Funcionario> ativos = funcionarioRepository.findByStatusOrderByNomeAsc(FuncionarioStatus.ATIVO);

        assertThat(ativos).hasSize(1);
        assertThat(ativos.get(0).getNome()).isEqualTo("Ana");
    }

    @Test
    void deveBuscarPorNomeIgnorandoCase() {
        List<Funcionario> resultado = funcionarioRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc("an");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("ana@empresa.com");
    }

    private Funcionario novoFuncionario(String nome, String email, String cpf, FuncionarioStatus status) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario.setCpf(cpf);
        funcionario.setCargo("Analista");
        funcionario.setStatus(status);
        funcionario.setDepartamento(departamento);
        funcionario.setDataAdmissao(LocalDate.now());

        Salario salario = new Salario();
        salario.setValorAtual(new BigDecimal("3000.00"));
        funcionario.definirSalario(salario);

        return funcionario;
    }
}
