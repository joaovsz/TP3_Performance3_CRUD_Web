package br.com.faculdade.tp3.repository;

import br.com.faculdade.tp3.model.Departamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    List<Departamento> findAllByOrderByNomeAsc();
}
