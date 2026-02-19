package br.com.faculdade.tp3.repository;

import br.com.faculdade.tp3.model.Salario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalarioRepository extends JpaRepository<Salario, Long> {
}
