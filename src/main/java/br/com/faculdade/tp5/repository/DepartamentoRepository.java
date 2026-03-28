package br.com.faculdade.tp5.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.faculdade.tp5.model.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    List<Departamento> findAllByOrderByNomeAsc();
}
