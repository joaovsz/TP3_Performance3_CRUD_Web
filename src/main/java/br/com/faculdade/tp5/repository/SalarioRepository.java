package br.com.faculdade.tp5.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.faculdade.tp5.model.Salario;

public interface SalarioRepository extends JpaRepository<Salario, Long> {
}
