package br.com.faculdade.tp3.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.faculdade.tp3.dto.rh.AjusteSalarialPayload;
import br.com.faculdade.tp3.dto.rh.DemissaoPayload;
import br.com.faculdade.tp3.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp3.dto.rh.PromocaoPayload;
import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.repository.DepartamentoRepository;
import br.com.faculdade.tp3.repository.FuncionarioRepository;
import br.com.faculdade.tp3.repository.MovimentacaoRhRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RhApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private MovimentacaoRhRepository movimentacaoRhRepository;

    private Long departamentoId;

    @BeforeEach
    void setUp() {
        movimentacaoRhRepository.deleteAll();
        funcionarioRepository.deleteAll();
        if (departamentoRepository.count() == 0) {
            departamentoRepository.save(new Departamento("Tecnologia", "TI"));
        }
        departamentoId = departamentoRepository.findAll().get(0).getId();
    }

    @Test
    void deveExecutarFluxoCompletoDeRh() throws Exception {
        FuncionarioPayload contratar = funcionarioPayload("0001", "Dev Júnior");
        MvcResult resultadoContratacao = mockMvc.perform(post("/api/rh/funcionarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(contratar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andReturn();

        JsonNode json = objectMapper.readTree(resultadoContratacao.getResponse().getContentAsString());
        long id = json.get("id").asLong();

        AjusteSalarialPayload aumento = new AjusteSalarialPayload();
        aumento.setPercentual(new BigDecimal("10.00"));
        aumento.setMotivo("Ajuste de mercado");

        mockMvc.perform(post("/api/rh/funcionarios/{id}/aumento-salarial", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(aumento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salario.valorAtual").value(4400.00));

        PromocaoPayload promocao = new PromocaoPayload();
        promocao.setNovoCargo("Dev Pleno");
        promocao.setPercentualAumento(new BigDecimal("15.00"));
        promocao.setMotivo("Destaque em entregas");

        mockMvc.perform(post("/api/rh/funcionarios/{id}/promover", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(promocao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cargo").value("Dev Pleno"));

        DemissaoPayload demissao = new DemissaoPayload();
        demissao.setMotivo("Encerramento do contrato");

        mockMvc.perform(post("/api/rh/funcionarios/{id}/demitir", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(demissao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));

        mockMvc.perform(get("/api/rh/funcionarios/{id}/movimentacoes", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").exists());
    }

    @Test
    void deveRetornarErroValidacaoParaEntradaInvalida() throws Exception {
        FuncionarioPayload invalido = funcionarioPayload("0002", "D");
        invalido.setCpf("123");

        mockMvc.perform(post("/api/rh/funcionarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fields.cpf").exists());
    }

    @Test
    void deveFiltrarFuncionariosAtivos() throws Exception {
        mockMvc.perform(post("/api/rh/funcionarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(funcionarioPayload("0003", "Analista"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/rh/funcionarios").param("ativos", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ATIVO"));
    }

    @Test
    void deveSimularTimeoutComRespostaSegura() throws Exception {
        mockMvc.perform(get("/api/rh/diagnostico/timeout")
                        .param("delayMs", "400")
                        .param("timeoutMs", "50"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.message").value("Tempo limite excedido ao processar a operação."));
    }

    @Test
    void deveSimularSobrecargaEmConcorrencia() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<Integer> primeira = executor.submit(() ->
                    mockMvc.perform(get("/api/rh/diagnostico/sobrecarga").param("holdMs", "250"))
                            .andReturn().getResponse().getStatus());

            Thread.sleep(30);

            Future<Integer> segunda = executor.submit(() ->
                    mockMvc.perform(get("/api/rh/diagnostico/sobrecarga").param("holdMs", "250"))
                            .andReturn().getResponse().getStatus());

            List<Integer> status = List.of(primeira.get(), segunda.get());
            assertThat(status).contains(200, 503);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void deveAtualizarCadastroViaPut() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/rh/funcionarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(funcionarioPayload("0004", "QA"))))
                .andExpect(status().isCreated())
                .andReturn();

        long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        FuncionarioPayload update = funcionarioPayload("0004", "QA Senior");
        update.setId(id);
        update.setNome("Juliana Lima");
        update.setSalarioInicial(new BigDecimal("0.00"));

        mockMvc.perform(put("/api/rh/funcionarios/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Juliana Lima"));
    }

    private FuncionarioPayload funcionarioPayload(String sufixo, String cargo) {
        FuncionarioPayload payload = new FuncionarioPayload();
        payload.setNome("Juliana " + sufixo);
        payload.setEmail("juliana" + sufixo + "@empresa.com");
        payload.setCpf("1234567" + sufixo);
        payload.setCargo(cargo);
        payload.setDepartamentoId(departamentoId);
        payload.setSalarioInicial(new BigDecimal("4000.00"));
        return payload;
    }
}
