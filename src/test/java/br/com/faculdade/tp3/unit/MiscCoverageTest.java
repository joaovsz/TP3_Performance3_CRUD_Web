package br.com.faculdade.tp3.unit;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.faculdade.tp3.controller.HomeController;
import br.com.faculdade.tp3.controller.WebExceptionHandler;
import br.com.faculdade.tp3.exception.RecursoNaoEncontradoException;
import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.model.MovimentacaoRh;
import br.com.faculdade.tp3.model.Salario;
import br.com.faculdade.tp3.model.enums.FuncionarioStatus;
import br.com.faculdade.tp3.model.enums.TipoMovimentacaoRh;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

class MiscCoverageTest {

    @Test
    void deveCobrirHomeControllerEWebExceptionHandler() {
        HomeController homeController = new HomeController();
        assertThat(homeController.home()).isEqualTo("redirect:/rh/funcionarios");

        WebExceptionHandler handler = new WebExceptionHandler();
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        String destino = handler.handleWebError(new RuntimeException("x"), redirect);

        assertThat(destino).isEqualTo("redirect:/rh/funcionarios");
        assertThat(redirect.getFlashAttributes()).containsKey("erro");
    }

    @Test
    void deveCobrirModelosBasicos() {
        Departamento departamento = new Departamento("Financeiro", "FIN");
        departamento.setId(1L);

        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("Joao");
        funcionario.setEmail("joao@empresa.com");
        funcionario.setCpf("12345678901");
        funcionario.setCargo("Analista");
        funcionario.setStatus(FuncionarioStatus.ATIVO);
        funcionario.setDepartamento(departamento);
        funcionario.setDataAdmissao(LocalDate.now());

        Salario salario = new Salario();
        salario.setId(5L);
        salario.setValorAtual(new BigDecimal("2500.00"));
        salario.setFuncionario(funcionario);
        salario.updateTimestamp();
        funcionario.definirSalario(salario);

        funcionario.prePersist();
        funcionario.preUpdate();

        MovimentacaoRh movimentacao = new MovimentacaoRh();
        movimentacao.setFuncionario(funcionario);
        movimentacao.setTipo(TipoMovimentacaoRh.CONTRATACAO);
        movimentacao.setDescricao("Contratacao");
        movimentacao.setSalarioAnterior(BigDecimal.ZERO);
        movimentacao.setSalarioNovo(new BigDecimal("2500.00"));
        movimentacao.prePersist();

        assertThat(funcionario.getNome()).isEqualTo("Joao");
        assertThat(funcionario.getSalario().getValorAtual()).isEqualTo(new BigDecimal("2500.00"));
        assertThat(funcionario.getCriadoEm()).isNotNull();
        assertThat(funcionario.getAtualizadoEm()).isNotNull();
        assertThat(salario.getAtualizadoEm()).isNotNull();
        assertThat(movimentacao.getMovimentadoEm()).isNotNull();

        Funcionario outro = new Funcionario();
        outro.setId(1L);
        assertThat(funcionario).isEqualTo(outro);
        assertThat(funcionario).isNotEqualTo(new Object());

        Departamento outroDep = new Departamento();
        outroDep.setId(1L);
        assertThat(departamento).isEqualTo(outroDep);
        assertThat(departamento).isNotEqualTo(new Object());

        Salario outroSal = new Salario();
        outroSal.setId(5L);
        assertThat(salario).isEqualTo(outroSal);
        assertThat(salario).isNotEqualTo(new Object());

        assertThat(funcionario.hashCode()).isNotZero();
        assertThat(departamento.hashCode()).isNotZero();
        assertThat(salario.hashCode()).isNotZero();
    }

    @Test
    void deveCobrirExceptionRecursoNaoEncontrado() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Nao encontrado");
        assertThat(ex.getMessage()).isEqualTo("Nao encontrado");
    }
}
