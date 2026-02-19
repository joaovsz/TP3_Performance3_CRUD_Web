package br.com.faculdade.tp3.config;

import br.com.faculdade.tp3.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.repository.DepartamentoRepository;
import br.com.faculdade.tp3.repository.FuncionarioRepository;
import br.com.faculdade.tp3.service.RhService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapDataConfig {

    @Bean
    CommandLineRunner seedDadosIniciais(
            DepartamentoRepository departamentoRepository,
            FuncionarioRepository funcionarioRepository,
            RhService rhService
    ) {
        return args -> {
            if (departamentoRepository.count() == 0) {
                departamentoRepository.save(new Departamento("Recursos Humanos", "RH"));
                departamentoRepository.save(new Departamento("Tecnologia da Informação", "TI"));
                departamentoRepository.save(new Departamento("Financeiro", "FIN"));
                departamentoRepository.save(new Departamento("Operações", "OPE"));
            }
            if (funcionarioRepository.count() > 0) {
                return;
            }

            Map<String, Long> departamentoPorSigla = departamentoRepository.findAllByOrderByNomeAsc()
                    .stream()
                    .collect(Collectors.toMap(
                            Departamento::getSigla,
                            Departamento::getId,
                            (atual, ignorar) -> atual
                    ));

            validarDepartamentosObrigatorios(departamentoPorSigla);

            contratar(
                    rhService,
                    "Mariana Alves",
                    "mariana.alves@empresa.com",
                    "12345678901",
                    "Analista de RH",
                    departamentoPorSigla.get("RH"),
                    "5400.00"
            );

            contratar(
                    rhService,
                    "Rafael Souza",
                    "rafael.souza@empresa.com",
                    "12345678902",
                    "Desenvolvedor Backend",
                    departamentoPorSigla.get("TI"),
                    "8900.00"
            );

            contratar(
                    rhService,
                    "Camila Prado",
                    "camila.prado@empresa.com",
                    "12345678903",
                    "Analista Financeira",
                    departamentoPorSigla.get("FIN"),
                    "7100.00"
            );

            contratar(
                    rhService,
                    "Bruno Martins",
                    "bruno.martins@empresa.com",
                    "12345678904",
                    "Coordenador de Operacoes",
                    departamentoPorSigla.get("OPE"),
                    "9800.00"
            );
        };
    }

    private void validarDepartamentosObrigatorios(Map<String, Long> departamentoPorSigla) {
        if (departamentoPorSigla.get("RH") == null
                || departamentoPorSigla.get("TI") == null
                || departamentoPorSigla.get("FIN") == null
                || departamentoPorSigla.get("OPE") == null) {
            throw new IllegalStateException("Departamentos iniciais obrigatórios não foram carregados.");
        }
    }

    private void contratar(
            RhService rhService,
            String nome,
            String email,
            String cpf,
            String cargo,
            Long departamentoId,
            String salario
    ) {
        FuncionarioPayload payload = new FuncionarioPayload();
        payload.setNome(nome);
        payload.setEmail(email);
        payload.setCpf(cpf);
        payload.setCargo(cargo);
        payload.setDepartamentoId(departamentoId);
        payload.setSalarioInicial(new BigDecimal(salario));
        rhService.contratar(payload);
    }
}
