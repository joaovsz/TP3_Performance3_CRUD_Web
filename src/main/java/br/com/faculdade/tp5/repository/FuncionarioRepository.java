package br.com.faculdade.tp5.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.faculdade.tp5.model.Funcionario;
import br.com.faculdade.tp5.model.enums.FuncionarioStatus;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    List<Funcionario> findAllByOrderByNomeAsc();

    List<Funcionario> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Funcionario> findByStatusOrderByNomeAsc(FuncionarioStatus status);

    List<Funcionario> findByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(String nome, FuncionarioStatus status);

    Optional<Funcionario> findByEmailIgnoreCase(String email);

    Optional<Funcionario> findByCpf(String cpf);
}
