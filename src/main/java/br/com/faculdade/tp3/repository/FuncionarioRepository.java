package br.com.faculdade.tp3.repository;

import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.model.enums.FuncionarioStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    List<Funcionario> findAllByOrderByNomeAsc();

    List<Funcionario> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Funcionario> findByStatusOrderByNomeAsc(FuncionarioStatus status);

    List<Funcionario> findByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(String nome, FuncionarioStatus status);

    Optional<Funcionario> findByEmailIgnoreCase(String email);

    Optional<Funcionario> findByCpf(String cpf);
}
