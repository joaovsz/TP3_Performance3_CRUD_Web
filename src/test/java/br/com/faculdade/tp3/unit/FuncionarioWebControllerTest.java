package br.com.faculdade.tp3.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import br.com.faculdade.tp3.controller.WebExceptionHandler;
import br.com.faculdade.tp3.controller.rh.FuncionarioWebController;
import br.com.faculdade.tp3.dto.rh.AjusteSalarialPayload;
import br.com.faculdade.tp3.dto.rh.DemissaoPayload;
import br.com.faculdade.tp3.dto.rh.PromocaoPayload;
import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.model.Salario;
import br.com.faculdade.tp3.model.enums.FuncionarioStatus;
import br.com.faculdade.tp3.service.RhService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FuncionarioWebController.class)
@Import(WebExceptionHandler.class)
class FuncionarioWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RhService rhService;

    @Test
    void deveRenderizarLista() throws Exception {
        when(rhService.listarFuncionarios(null, null)).thenReturn(List.of(funcionarioBase()));

        mockMvc.perform(get("/rh/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/lista"))
                .andExpect(model().attributeExists("funcionarios"));
    }

    @Test
    void deveRenderizarListaComFiltros() throws Exception {
        when(rhService.listarFuncionarios("Ana", true)).thenReturn(List.of(funcionarioBase()));

        mockMvc.perform(get("/rh/funcionarios").param("nome", "Ana").param("ativos", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/lista"));
    }

    @Test
    void deveRenderizarFormularioNovo() throws Exception {
        when(rhService.listarDepartamentos()).thenReturn(List.of(new Departamento("TI", "TI")));

        mockMvc.perform(get("/rh/funcionarios/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/form"))
                .andExpect(model().attributeExists("funcionario"));
    }

    @Test
    void deveRenderizarFormularioEdicao() throws Exception {
        when(rhService.buscarFuncionario(1L)).thenReturn(funcionarioBase());
        when(rhService.listarDepartamentos()).thenReturn(List.of(new Departamento("TI", "TI")));

        mockMvc.perform(get("/rh/funcionarios/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/form"));
    }

    @Test
    void deveRetornarFormularioQuandoValidacaoFalha() throws Exception {
        when(rhService.listarDepartamentos()).thenReturn(List.of(new Departamento("TI", "TI")));

        mockMvc.perform(post("/rh/funcionarios/salvar")
                        .param("nome", "A")
                        .param("email", "email-invalido")
                        .param("cpf", "123")
                        .param("cargo", "")
                        .param("departamentoId", "")
                        .param("salarioInicial", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/form"));
    }

    @Test
    void deveContratarComSucessoViaWeb() throws Exception {
        when(rhService.listarDepartamentos()).thenReturn(List.of(new Departamento("TI", "TI")));

        mockMvc.perform(post("/rh/funcionarios/salvar")
                        .param("nome", "Maria Clara")
                        .param("email", "maria@empresa.com")
                        .param("cpf", "12345678901")
                        .param("cargo", "Analista")
                        .param("departamentoId", "1")
                        .param("salarioInicial", "3500.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rh/funcionarios"));

        verify(rhService).contratar(any());
    }

    @Test
    void deveAtualizarCadastroComSucessoViaWeb() throws Exception {
        when(rhService.listarDepartamentos()).thenReturn(List.of(new Departamento("TI", "TI")));

        mockMvc.perform(post("/rh/funcionarios/salvar")
                        .param("id", "1")
                        .param("nome", "Maria Clara")
                        .param("email", "maria@empresa.com")
                        .param("cpf", "12345678901")
                        .param("cargo", "Analista")
                        .param("departamentoId", "1")
                        .param("salarioInicial", "3500.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rh/funcionarios"));

        verify(rhService).atualizarCadastro(eq(1L), any());
    }

    @Test
    void deveRenderizarTelaAumento() throws Exception {
        when(rhService.buscarFuncionario(1L)).thenReturn(funcionarioBase());

        mockMvc.perform(get("/rh/funcionarios/1/aumento"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/aumento"))
                .andExpect(model().attributeExists("payload"));
    }

    @Test
    void deveRetornarTelaAumentoQuandoValidacaoFalha() throws Exception {
        when(rhService.buscarFuncionario(1L)).thenReturn(funcionarioBase());

        mockMvc.perform(post("/rh/funcionarios/1/aumento")
                        .param("percentual", "0")
                        .param("motivo", "x"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/aumento"));
    }

    @Test
    void deveAplicarAumentoViaWeb() throws Exception {
        mockMvc.perform(post("/rh/funcionarios/1/aumento")
                        .param("percentual", "10.00")
                        .param("motivo", "Ajuste anual"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rh/funcionarios"));

        verify(rhService).aumentarSalario(eq(1L), any(AjusteSalarialPayload.class));
    }

    @Test
    void deveRenderizarTelaPromocao() throws Exception {
        when(rhService.buscarFuncionario(1L)).thenReturn(funcionarioBase());

        mockMvc.perform(get("/rh/funcionarios/1/promocao"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/promocao"))
                .andExpect(model().attributeExists("payload"));
    }

    @Test
    void deveRetornarTelaPromocaoQuandoValidacaoFalha() throws Exception {
        when(rhService.buscarFuncionario(1L)).thenReturn(funcionarioBase());

        mockMvc.perform(post("/rh/funcionarios/1/promocao")
                        .param("novoCargo", "A")
                        .param("percentualAumento", "-1")
                        .param("motivo", "x"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/promocao"));
    }

    @Test
    void devePromoverViaWeb() throws Exception {
        mockMvc.perform(post("/rh/funcionarios/1/promocao")
                        .param("novoCargo", "Senior")
                        .param("percentualAumento", "12.50")
                        .param("motivo", "Plano de carreira"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rh/funcionarios"));

        verify(rhService).promover(eq(1L), any(PromocaoPayload.class));
    }

    @Test
    void deveDemitirViaWeb() throws Exception {
        mockMvc.perform(post("/rh/funcionarios/1/demitir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rh/funcionarios"));

        verify(rhService).demitir(eq(1L), any(DemissaoPayload.class));
    }

    @Test
    void deveExcluirDefinitivoViaWeb() throws Exception {
        mockMvc.perform(post("/rh/funcionarios/1/excluir-definitivo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rh/funcionarios"));

        verify(rhService).excluirDefinitivamente(1L);
    }

    @Test
    void deveRenderizarHistoricoMovimentacoes() throws Exception {
        when(rhService.buscarFuncionario(1L)).thenReturn(funcionarioBase());
        when(rhService.listarMovimentacoes(1L)).thenReturn(List.of());

        mockMvc.perform(get("/rh/funcionarios/1/movimentacoes"))
                .andExpect(status().isOk())
                .andExpect(view().name("rh/movimentacoes"))
                .andExpect(model().attributeExists("movimentacoes"));
    }

    private Funcionario funcionarioBase() {
        Departamento departamento = new Departamento("Tecnologia", "TI");
        departamento.setId(1L);

        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("Ana");
        funcionario.setCargo("Dev");
        funcionario.setStatus(FuncionarioStatus.ATIVO);
        funcionario.setDepartamento(departamento);
        funcionario.setDataAdmissao(LocalDate.now());

        Salario salario = new Salario();
        salario.setValorAtual(new BigDecimal("5000.00"));
        funcionario.definirSalario(salario);
        return funcionario;
    }
}
