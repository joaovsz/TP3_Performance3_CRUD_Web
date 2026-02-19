package br.com.faculdade.tp3.controller.rh;

import br.com.faculdade.tp3.dto.rh.AjusteSalarialPayload;
import br.com.faculdade.tp3.dto.rh.DemissaoPayload;
import br.com.faculdade.tp3.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp3.dto.rh.PromocaoPayload;
import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.model.MovimentacaoRh;
import br.com.faculdade.tp3.service.RhService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rh")
public class RhApiController {

    private final RhService rhService;

    public RhApiController(RhService rhService) {
        this.rhService = rhService;
    }

    @GetMapping("/funcionarios")
    public ResponseEntity<List<Funcionario>> listarFuncionarios(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativos
    ) {
        return ResponseEntity.ok(rhService.listarFuncionarios(nome, ativos));
    }

    @GetMapping("/funcionarios/{id}")
    public ResponseEntity<Funcionario> buscarFuncionario(@PathVariable Long id) {
        return ResponseEntity.ok(rhService.buscarFuncionario(id));
    }

    @GetMapping("/funcionarios/{id}/movimentacoes")
    public ResponseEntity<List<MovimentacaoRh>> listarMovimentacoes(@PathVariable Long id) {
        return ResponseEntity.ok(rhService.listarMovimentacoes(id));
    }

    @PostMapping("/funcionarios")
    public ResponseEntity<Funcionario> contratar(@Valid @RequestBody FuncionarioPayload payload) {
        Funcionario funcionario = rhService.contratar(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
    }

    @PutMapping("/funcionarios/{id}")
    public ResponseEntity<Funcionario> atualizar(@PathVariable Long id, @Valid @RequestBody FuncionarioPayload payload) {
        return ResponseEntity.ok(rhService.atualizarCadastro(id, payload));
    }

    @PostMapping("/funcionarios/{id}/aumento-salarial")
    public ResponseEntity<Funcionario> aumentarSalario(
            @PathVariable Long id,
            @Valid @RequestBody AjusteSalarialPayload payload
    ) {
        return ResponseEntity.ok(rhService.aumentarSalario(id, payload));
    }

    @PostMapping("/funcionarios/{id}/promover")
    public ResponseEntity<Funcionario> promover(
            @PathVariable Long id,
            @Valid @RequestBody PromocaoPayload payload
    ) {
        return ResponseEntity.ok(rhService.promover(id, payload));
    }

    @PostMapping("/funcionarios/{id}/demitir")
    public ResponseEntity<Funcionario> demitir(
            @PathVariable Long id,
            @Valid @RequestBody DemissaoPayload payload
    ) {
        return ResponseEntity.ok(rhService.demitir(id, payload));
    }

    @DeleteMapping("/funcionarios/{id}")
    public ResponseEntity<Void> excluirDefinitivo(@PathVariable Long id) {
        rhService.excluirDefinitivamente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/departamentos")
    public ResponseEntity<List<Departamento>> listarDepartamentos() {
        return ResponseEntity.ok(rhService.listarDepartamentos());
    }
}
