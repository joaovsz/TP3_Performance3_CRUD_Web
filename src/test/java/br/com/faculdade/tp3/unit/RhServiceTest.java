package br.com.faculdade.tp3.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.faculdade.tp3.dto.rh.AjusteSalarialPayload;
import br.com.faculdade.tp3.dto.rh.DemissaoPayload;
import br.com.faculdade.tp3.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp3.dto.rh.PromocaoPayload;
import br.com.faculdade.tp3.exception.EntradaInvalidaException;
import br.com.faculdade.tp3.exception.RecursoDuplicadoException;
import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.model.MovimentacaoRh;
import br.com.faculdade.tp3.model.Salario;
import br.com.faculdade.tp3.model.enums.FuncionarioStatus;
import br.com.faculdade.tp3.repository.DepartamentoRepository;
import br.com.faculdade.tp3.repository.FuncionarioRepository;
import br.com.faculdade.tp3.repository.MovimentacaoRhRepository;
import br.com.faculdade.tp3.service.RhService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RhServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private MovimentacaoRhRepository movimentacaoRhRepository;

    @InjectMocks
    private RhService rhService;

    @Captor
    private ArgumentCaptor<Funcionario> funcionarioCaptor;

    private Departamento departamento;

    @BeforeEach
    void setUp() {
        departamento = new Departamento("Tecnologia", "TI");
        departamento.setId(10L);
    }

    @Test
    void deveContratarFuncionarioComSucesso() {
        FuncionarioPayload payload = payloadBase();

        when(funcionarioRepository.findByEmailIgnoreCase(payload.getEmail())).thenReturn(Optional.empty());
        when(funcionarioRepository.findByCpf(payload.getCpf())).thenReturn(Optional.empty());
        when(departamentoRepository.findById(10L)).thenReturn(Optional.of(departamento));
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> {
            Funcionario f = invocation.getArgument(0);
            f.setId(1L);
            return f;
        });
        when(movimentacaoRhRepository.save(any(MovimentacaoRh.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario resultado = rhService.contratar(payload);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getStatus()).isEqualTo(FuncionarioStatus.ATIVO);
        assertThat(resultado.getSalario().getValorAtual()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(resultado.getDataAdmissao()).isEqualTo(LocalDate.now());

        verify(funcionarioRepository).save(funcionarioCaptor.capture());
        assertThat(funcionarioCaptor.getValue().getDepartamento().getId()).isEqualTo(10L);
        verify(movimentacaoRhRepository).save(any(MovimentacaoRh.class));
    }

    @Test
    void deveFalharAoContratarComEmailDuplicado() {
        FuncionarioPayload payload = payloadBase();

        Funcionario existente = new Funcionario();
        existente.setId(999L);

        when(funcionarioRepository.findByEmailIgnoreCase(payload.getEmail())).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> rhService.contratar(payload))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("email");

        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    void deveAtualizarCadastroComSucesso() {
        Funcionario existente = funcionarioBase();

        FuncionarioPayload payload = payloadBase();
        payload.setNome("Ana Maria Souza");
        payload.setCargo("Tech Lead");

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(funcionarioRepository.findByEmailIgnoreCase("ana.souza@empresa.com")).thenReturn(Optional.of(existente));
        when(funcionarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(existente));
        when(departamentoRepository.findById(10L)).thenReturn(Optional.of(departamento));
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movimentacaoRhRepository.save(any(MovimentacaoRh.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario atualizado = rhService.atualizarCadastro(1L, payload);

        assertThat(atualizado.getNome()).isEqualTo("Ana Maria Souza");
        assertThat(atualizado.getCargo()).isEqualTo("Tech Lead");
    }

    @Test
    void deveFalharQuandoIdDoCorpoDivergeDoIdDaRota() {
        FuncionarioPayload payload = payloadBase();
        payload.setId(7L);

        assertThatThrownBy(() -> rhService.atualizarCadastro(1L, payload))
                .isInstanceOf(EntradaInvalidaException.class)
                .hasMessageContaining("ID do corpo");
    }

    @Test
    void deveAplicarAumentoSalarial() {
        Funcionario funcionario = funcionarioBase();

        AjusteSalarialPayload payload = new AjusteSalarialPayload();
        payload.setPercentual(new BigDecimal("10.00"));
        payload.setMotivo("Ajuste anual");

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movimentacaoRhRepository.save(any(MovimentacaoRh.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario atualizado = rhService.aumentarSalario(1L, payload);

        assertThat(atualizado.getSalario().getValorAtual()).isEqualTo(new BigDecimal("5500.00"));
    }

    @Test
    void deveFalharPromocaoParaFuncionarioInativo() {
        Funcionario funcionario = funcionarioBase();
        funcionario.setStatus(FuncionarioStatus.INATIVO);

        PromocaoPayload payload = new PromocaoPayload();
        payload.setNovoCargo("Gerente de Engenharia");
        payload.setPercentualAumento(new BigDecimal("15.00"));
        payload.setMotivo("Plano de carreira");

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        assertThatThrownBy(() -> rhService.promover(1L, payload))
                .isInstanceOf(EntradaInvalidaException.class)
                .hasMessageContaining("ativos");
    }

    @Test
    void deveDemitirFuncionarioAtivo() {
        Funcionario funcionario = funcionarioBase();

        DemissaoPayload payload = new DemissaoPayload();
        payload.setMotivo("Reestruturação interna");

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movimentacaoRhRepository.save(any(MovimentacaoRh.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario demitido = rhService.demitir(1L, payload);

        assertThat(demitido.getStatus()).isEqualTo(FuncionarioStatus.INATIVO);
        assertThat(demitido.getDataDemissao()).isEqualTo(LocalDate.now());
    }

    @Test
    void deveFalharQuandoFuncionarioJaEstaInativoNaDemissao() {
        Funcionario funcionario = funcionarioBase();
        funcionario.setStatus(FuncionarioStatus.INATIVO);

        DemissaoPayload payload = new DemissaoPayload();
        payload.setMotivo("Sem justa causa");

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        assertThatThrownBy(() -> rhService.demitir(1L, payload))
                .isInstanceOf(EntradaInvalidaException.class)
                .hasMessageContaining("já está inativo");
    }

    @Test
    void deveListarComFiltroDeAtivos() {
        Funcionario funcionario = funcionarioBase();
        when(funcionarioRepository.findByStatusOrderByNomeAsc(FuncionarioStatus.ATIVO)).thenReturn(List.of(funcionario));

        List<Funcionario> ativos = rhService.listarFuncionarios(null, true);

        assertThat(ativos).hasSize(1);
        verify(funcionarioRepository).findByStatusOrderByNomeAsc(FuncionarioStatus.ATIVO);
    }

    @Test
    void deveFalharQuandoCpfInvalidoNaContratacao() {
        FuncionarioPayload payload = payloadBase();
        payload.setCpf("123");

        assertThatThrownBy(() -> rhService.contratar(payload))
                .isInstanceOf(EntradaInvalidaException.class)
                .hasMessageContaining("CPF");
    }

    private FuncionarioPayload payloadBase() {
        FuncionarioPayload payload = new FuncionarioPayload();
        payload.setNome("Ana Souza");
        payload.setEmail("ana.souza@empresa.com");
        payload.setCpf("12345678901");
        payload.setCargo("Desenvolvedora");
        payload.setDepartamentoId(10L);
        payload.setSalarioInicial(new BigDecimal("5000.00"));
        return payload;
    }

    private Funcionario funcionarioBase() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("Ana Souza");
        funcionario.setEmail("ana.souza@empresa.com");
        funcionario.setCpf("12345678901");
        funcionario.setCargo("Desenvolvedora");
        funcionario.setStatus(FuncionarioStatus.ATIVO);
        funcionario.setDepartamento(departamento);
        funcionario.setDataAdmissao(LocalDate.now());

        Salario salario = new Salario();
        salario.setValorAtual(new BigDecimal("5000.00"));
        funcionario.definirSalario(salario);

        return funcionario;
    }
}
