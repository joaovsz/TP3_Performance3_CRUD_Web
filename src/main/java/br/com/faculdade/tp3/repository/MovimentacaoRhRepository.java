package br.com.faculdade.tp3.repository;

import br.com.faculdade.tp3.model.MovimentacaoRh;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRhRepository extends JpaRepository<MovimentacaoRh, Long> {

    List<MovimentacaoRh> findByFuncionarioIdOrderByMovimentadoEmDesc(Long funcionarioId);

    void deleteByFuncionarioId(Long funcionarioId);
}
