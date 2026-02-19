package br.com.faculdade.tp3.fuzz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import br.com.faculdade.tp3.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.repository.DepartamentoRepository;
import br.com.faculdade.tp3.repository.FuncionarioRepository;
import br.com.faculdade.tp3.repository.MovimentacaoRhRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RhApiFuzzTest {

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

    private static final SecureRandom RANDOM = new SecureRandom();

    @BeforeEach
    void setUp() {
        movimentacaoRhRepository.deleteAll();
        funcionarioRepository.deleteAll();
        if (departamentoRepository.count() == 0) {
            departamentoRepository.save(new Departamento("Tecnologia", "TI"));
        }
        departamentoId = departamentoRepository.findAll().get(0).getId();
    }

    @ParameterizedTest
    @MethodSource("nomesMaliciosos")
    void deveBloquearNomesMaliciososSemVazarDetalhes(String entrada) throws Exception {
        FuncionarioPayload payload = payloadValido();
        payload.setNome(entrada);

        MockHttpServletResponse response = mockMvc.perform(post("/api/rh/funcionarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isBetween(400, 499);
        assertThat(response.getContentAsString())
                .doesNotContain("Exception")
                .doesNotContain("java.")
                .doesNotContain("org.springframework");
    }

    @Test
    void fuzzAleatorioDeveEvitarErros500() throws Exception {
        for (int i = 0; i < 40; i++) {
            FuncionarioPayload payload = payloadValido();
            payload.setNome(textoAleatorio(1 + RANDOM.nextInt(60)));
            payload.setEmail(textoAleatorio(3 + RANDOM.nextInt(30)) + "@x.com");
            payload.setCpf(String.format("%011d", RANDOM.nextInt(999999999)));
            payload.setCargo(textoAleatorio(1 + RANDOM.nextInt(35)));

            MockHttpServletResponse response = mockMvc.perform(post("/api/rh/funcionarios")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(payload)))
                    .andReturn()
                    .getResponse();

            assertThat(response.getStatus()).isLessThan(500);
            assertThat(response.getContentAsString())
                    .doesNotContain("at br.com.faculdade")
                    .doesNotContain("java.lang");
        }
    }

    private static Stream<String> nomesMaliciosos() {
        return Stream.of(
                "<script>alert('x')</script>",
                "' OR '1'='1",
                "../../etc/passwd",
                "\u0000\u0007\u001b",
                "SELECT * FROM funcionarios",
                "{\"$gt\":\"\"}"
        );
    }

    private FuncionarioPayload payloadValido() {
        FuncionarioPayload payload = new FuncionarioPayload();
        payload.setNome("Valido Nome");
        payload.setEmail("valido@empresa.com");
        payload.setCpf("12345678901");
        payload.setCargo("Analista");
        payload.setDepartamentoId(departamentoId);
        payload.setSalarioInicial(new BigDecimal("3000.00"));
        return payload;
    }

    private static String textoAleatorio(int tamanho) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+=-[]{};:'\",.<>?/\\| ";
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString().trim();
    }
}
