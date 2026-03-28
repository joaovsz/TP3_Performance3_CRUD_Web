package br.com.faculdade.tp5.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.faculdade.tp5.model.MovimentacaoRh;

public interface MovimentacaoRhRepository extends JpaRepository<MovimentacaoRh, Long> {

    List<MovimentacaoRh> findByFuncionarioIdOrderByMovimentadoEmDesc(Long funcionarioId);

    void deleteByFuncionarioId(Long funcionarioId);
}
