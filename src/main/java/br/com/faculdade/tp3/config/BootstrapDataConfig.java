package br.com.faculdade.tp3.config;

import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.repository.DepartamentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapDataConfig {

    @Bean
    CommandLineRunner seedDepartamentos(DepartamentoRepository departamentoRepository) {
        return args -> {
            if (departamentoRepository.count() == 0) {
                departamentoRepository.save(new Departamento("Recursos Humanos", "RH"));
                departamentoRepository.save(new Departamento("Tecnologia da Informação", "TI"));
                departamentoRepository.save(new Departamento("Financeiro", "FIN"));
                departamentoRepository.save(new Departamento("Operações", "OPE"));
            }
        };
    }
}
