package br.com.faculdade.tp5.config;

import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.repository.ProdutoRepository;

import br.com.faculdade.tp5.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp5.model.Departamento;
import br.com.faculdade.tp5.model.Funcionario;
import br.com.faculdade.tp5.repository.DepartamentoRepository;
import br.com.faculdade.tp5.repository.FuncionarioRepository;
import br.com.faculdade.tp5.service.RhService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
            ProdutoRepository produtoRepository,
            RhService rhService
    ) {
        return args -> {
            if (departamentoRepository.count() == 0) {
                departamentoRepository.save(new Departamento("Recursos Humanos", "RH"));
                departamentoRepository.save(new Departamento("Tecnologia da Informação", "TI"));
                departamentoRepository.save(new Departamento("Financeiro", "FIN"));
                departamentoRepository.save(new Departamento("Operações", "OPE"));
            }

            List<Departamento> departamentos = departamentoRepository.findAllByOrderByNomeAsc();
            Map<String, Long> departamentoPorSigla = departamentos
                    .stream()
                    .collect(Collectors.toMap(
                            Departamento::getSigla,
                            Departamento::getId,
                            (atual, ignorar) -> atual
            ));

            validarDepartamentosObrigatorios(departamentoPorSigla);

            if (funcionarioRepository.count() == 0) {
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
            }

            if (produtoRepository.count() == 0) {
                produtoRepository.save(new Produto(
                        "Notebook Pro 14",
                        "Notebook corporativo para equipe de desenvolvimento",
                        new BigDecimal("6499.90"),
                        12
                ));
                produtoRepository.save(new Produto(
                        "Monitor 27\" IPS",
                        "Monitor Full HD para postos de trabalho administrativos",
                        new BigDecimal("1399.00"),
                        20
                ));
                produtoRepository.save(new Produto(
                        "Headset USB",
                        "Headset com microfone para reuniões e suporte remoto",
                        new BigDecimal("289.90"),
                        35
                ));
            }

            List<Funcionario> funcionarios = funcionarioRepository.findAllByOrderByNomeAsc();
            List<Produto> produtos = produtoRepository.findAll();
            validarCargaMinima(departamentos, "departamentos");
            validarCargaMinima(funcionarios, "funcionários");
            validarCargaMinima(produtos, "produtos");
            validarRelacionamentosIntegrados(funcionarios, departamentos);
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

    private void validarCargaMinima(List<?> entidades, String sistema) {
        if (entidades.isEmpty()) {
            throw new IllegalStateException("Não há dados carregados para o sistema de " + sistema + ".");
        }
    }

    private void validarRelacionamentosIntegrados(List<Funcionario> funcionarios, List<Departamento> departamentos) {
        Set<Long> idsDepartamentos = departamentos.stream()
                .map(Departamento::getId)
                .collect(Collectors.toSet());

        boolean inconsistente = funcionarios.stream()
                .anyMatch(funcionario -> funcionario.getDepartamento() == null
                        || funcionario.getDepartamento().getId() == null
                        || !idsDepartamentos.contains(funcionario.getDepartamento().getId()));

        if (inconsistente) {
            throw new IllegalStateException("Dados inconsistentes entre sistemas: funcionário sem departamento válido.");
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
