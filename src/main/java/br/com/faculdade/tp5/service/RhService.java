package br.com.faculdade.tp5.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.faculdade.tp5.dto.rh.AjusteSalarialPayload;
import br.com.faculdade.tp5.dto.rh.DemissaoPayload;
import br.com.faculdade.tp5.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp5.dto.rh.PromocaoPayload;
import br.com.faculdade.tp5.exception.EntradaInvalidaException;
import br.com.faculdade.tp5.exception.RecursoDuplicadoException;
import br.com.faculdade.tp5.exception.RecursoNaoEncontradoException;
import br.com.faculdade.tp5.model.Cpf;
import br.com.faculdade.tp5.model.Departamento;
import br.com.faculdade.tp5.model.Funcionario;
import br.com.faculdade.tp5.model.FuncionarioFactory;
import br.com.faculdade.tp5.model.MovimentacaoRh;
import br.com.faculdade.tp5.model.Salario;
import br.com.faculdade.tp5.model.enums.FuncionarioStatus;
import br.com.faculdade.tp5.model.enums.TipoMovimentacaoRh;
import br.com.faculdade.tp5.repository.DepartamentoRepository;
import br.com.faculdade.tp5.repository.FuncionarioRepository;
import br.com.faculdade.tp5.repository.MovimentacaoRhRepository;
import br.com.faculdade.tp5.util.RhValidator;

@Service
public class RhService {

    private final FuncionarioRepository funcionarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final MovimentacaoRhRepository movimentacaoRhRepository;

    public RhService(
            FuncionarioRepository funcionarioRepository,
            DepartamentoRepository departamentoRepository,
            MovimentacaoRhRepository movimentacaoRhRepository
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.departamentoRepository = departamentoRepository;
        this.movimentacaoRhRepository = movimentacaoRhRepository;
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarFuncionarios(String nome, Boolean somenteAtivos) {
        FuncionarioStatus status = resolverStatus(somenteAtivos);

        if (nome == null || nome.isBlank()) {
            if (status == null) {
                return listaImutavel(funcionarioRepository.findAllByOrderByNomeAsc());
            }
            return listaImutavel(funcionarioRepository.findByStatusOrderByNomeAsc(status));
        }

        String termo = RhValidator.sanitizarTextoHumano(nome, "Filtro de nome", false, 1, 120);
        if (status != null) {
            return listaImutavel(funcionarioRepository.findByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(termo, status));
        }
        return listaImutavel(funcionarioRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(termo));
    }

    @Transactional(readOnly = true)
    public Funcionario buscarFuncionario(Long id) {
        validarId(id);
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoRh> listarMovimentacoes(Long funcionarioId) {
        validarId(funcionarioId);
        return listaImutavel(movimentacaoRhRepository.findByFuncionarioIdOrderByMovimentadoEmDesc(funcionarioId));
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarDepartamentos() {
        return listaImutavel(departamentoRepository.findAllByOrderByNomeAsc());
    }

    @Transactional
    public Funcionario contratar(FuncionarioPayload payload) {
        EntradaFuncionario entrada = normalizarFuncionario(payload, null, true);
        validarChavesUnicas(entrada.email(), entrada.cpf().getValor(), null);
        Departamento departamento = buscarDepartamento(entrada.departamentoId());

        Funcionario funcionario = FuncionarioFactory.criarPorCargo(
                entrada.nome(),
                entrada.email(),
                entrada.cpf().getValor(),
                entrada.cargo(),
                departamento,
                LocalDate.now()
        );

        Salario salario = new Salario();
        salario.setValorAtual(entrada.salario());
        funcionario.definirSalario(salario);

        Funcionario salvo = salvarComTratamento(funcionario);
        registrarMovimentacao(
                salvo,
                TipoMovimentacaoRh.CONTRATACAO,
                "Contratação realizada",
                null,
                entrada.salario()
        );
        return salvo;
    }

    @Transactional
    public Funcionario atualizarCadastro(Long id, FuncionarioPayload payload) {
        validarId(id);
        EntradaFuncionario entrada = normalizarFuncionario(payload, id, false);

        Funcionario funcionario = buscarFuncionario(id);
        validarChavesUnicas(entrada.email(), entrada.cpf().getValor(), id);

        funcionario.setNome(entrada.nome());
        funcionario.setEmail(entrada.email());
        funcionario.setCpf(entrada.cpf().getValor());
        funcionario.setCargo(entrada.cargo());
        funcionario.setDepartamento(buscarDepartamento(entrada.departamentoId()));

        Funcionario salvo = salvarComTratamento(funcionario);
        registrarMovimentacao(
                salvo,
                TipoMovimentacaoRh.ATUALIZACAO_CADASTRAL,
                "Atualização cadastral",
                salvo.getSalario().getValorAtual(),
                salvo.getSalario().getValorAtual()
        );
        return salvo;
    }

    @Transactional
    public Funcionario aumentarSalario(Long id, AjusteSalarialPayload payload) {
        validarId(id);
        if (payload == null) {
            throw new EntradaInvalidaException("Dados de aumento salarial são obrigatórios.");
        }

        BigDecimal percentual = validarPercentual(payload.getPercentual(), "Percentual de aumento");
        String motivo = RhValidator.sanitizarTexto(payload.getMotivo(), "Motivo", true, 5, 255);

        Funcionario funcionario = buscarFuncionario(id);
        validarFuncionarioAtivo(funcionario);

        BigDecimal salarioAnterior = funcionario.getSalario().getValorAtual();
        BigDecimal salarioNovo = funcionario.getSalario().aplicarAumento(percentual);
        Funcionario salvo = salvarComTratamento(funcionario);

        registrarMovimentacao(
                salvo,
                TipoMovimentacaoRh.AUMENTO_SALARIAL,
                "Aumento salarial: " + motivo,
                salarioAnterior,
                salarioNovo
        );
        return salvo;
    }

    @Transactional
    public Funcionario promover(Long id, PromocaoPayload payload) {
        validarId(id);
        if (payload == null) {
            throw new EntradaInvalidaException("Dados de promoção são obrigatórios.");
        }

        String novoCargo = RhValidator.sanitizarTextoHumano(payload.getNovoCargo(), "Novo cargo", true, 2, 100);
        String motivo = RhValidator.sanitizarTexto(payload.getMotivo(), "Motivo", true, 5, 255);
        BigDecimal percentual = validarPercentual(payload.getPercentualAumento(), "Percentual da promoção");

        Funcionario funcionario = buscarFuncionario(id);
        validarFuncionarioAtivo(funcionario);
        if (!funcionario.aceitaPromocaoPara(novoCargo)) {
            throw new EntradaInvalidaException("Promoção incompatível com o tipo atual de funcionário.");
        }

        BigDecimal salarioAnterior = funcionario.getSalario().getValorAtual();
        BigDecimal salarioNovo = funcionario.getSalario().aplicarAumento(percentual);

        funcionario.setCargo(novoCargo);

        Funcionario salvo = salvarComTratamento(funcionario);
        registrarMovimentacao(
                salvo,
                TipoMovimentacaoRh.PROMOCAO,
                "Promoção: " + motivo,
                salarioAnterior,
                salarioNovo
        );
        return salvo;
    }

    @Transactional
    public Funcionario demitir(Long id, DemissaoPayload payload) {
        validarId(id);
        if (payload == null) {
            throw new EntradaInvalidaException("Dados de demissão são obrigatórios.");
        }

        String motivo = RhValidator.sanitizarTexto(payload.getMotivo(), "Motivo da demissão", true, 5, 255);

        Funcionario funcionario = buscarFuncionario(id);
        if (funcionario.getStatus() == FuncionarioStatus.INATIVO) {
            throw new EntradaInvalidaException("Funcionário já está inativo.");
        }

        funcionario.setStatus(FuncionarioStatus.INATIVO);
        funcionario.setDataDemissao(LocalDate.now());

        Funcionario salvo = salvarComTratamento(funcionario);
        BigDecimal salarioAtual = salvo.getSalario().getValorAtual();
        registrarMovimentacao(
                salvo,
                TipoMovimentacaoRh.DEMISSAO,
                "Demissão: " + motivo,
                salarioAtual,
                salarioAtual
        );
        return salvo;
    }

    @Transactional
    public void excluirDefinitivamente(Long id) {
        Funcionario funcionario = buscarFuncionario(id);
        movimentacaoRhRepository.deleteByFuncionarioId(id);
        funcionarioRepository.delete(funcionario);
    }

    private void validarChavesUnicas(String email, String cpf, Long idAtual) {
        funcionarioRepository.findByEmailIgnoreCase(email).ifPresent(existente -> {
            if (!existente.getId().equals(idAtual)) {
                throw new RecursoDuplicadoException("Já existe funcionário com o email informado.");
            }
        });

        funcionarioRepository.findByCpf(cpf).ifPresent(existente -> {
            if (!existente.getId().equals(idAtual)) {
                throw new RecursoDuplicadoException("Já existe funcionário com o CPF informado.");
            }
        });
    }

    private Departamento buscarDepartamento(Long id) {
        validarId(id);
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Departamento não encontrado."));
    }

    private Funcionario salvarComTratamento(Funcionario funcionario) {
        try {
            return funcionarioRepository.save(funcionario);
        } catch (DataIntegrityViolationException ex) {
            throw new RecursoDuplicadoException("Violação de integridade de dados no cadastro do funcionário.");
        }
    }

    private void registrarMovimentacao(
            Funcionario funcionario,
            TipoMovimentacaoRh tipo,
            String descricao,
            BigDecimal salarioAnterior,
            BigDecimal salarioNovo
    ) {
        MovimentacaoRh movimentacao = new MovimentacaoRh();
        movimentacao.setFuncionario(funcionario);
        movimentacao.setTipo(tipo);
        movimentacao.setDescricao(descricao);
        movimentacao.setSalarioAnterior(salarioAnterior);
        movimentacao.setSalarioNovo(salarioNovo);
        movimentacaoRhRepository.save(movimentacao);
    }

    private FuncionarioStatus resolverStatus(Boolean somenteAtivos) {
        if (somenteAtivos == null) {
            return null;
        }
        return somenteAtivos ? FuncionarioStatus.ATIVO : FuncionarioStatus.INATIVO;
    }

    private void validarFuncionarioAtivo(Funcionario funcionario) {
        if (funcionario.getStatus() != FuncionarioStatus.ATIVO) {
            throw new EntradaInvalidaException("Operação permitida apenas para funcionários ativos.");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new EntradaInvalidaException("ID inválido. Informe um valor positivo.");
        }
    }

    private EntradaFuncionario normalizarFuncionario(FuncionarioPayload payload, Long idRota, boolean contratar) {
        if (payload == null) {
            throw new EntradaInvalidaException("Dados do funcionário são obrigatórios.");
        }

        if (idRota != null && payload.getId() != null && !idRota.equals(payload.getId())) {
            throw new EntradaInvalidaException("ID do corpo não confere com o ID da rota.");
        }

        String nome = RhValidator.sanitizarTextoHumano(payload.getNome(), "Nome", true, 3, 120);
        String email = RhValidator.sanitizarEmail(payload.getEmail());
        Cpf cpf = RhValidator.sanitizarCpf(payload.getCpf());
        String cargo = RhValidator.sanitizarTextoHumano(payload.getCargo(), "Cargo", true, 2, 100);
        Long departamentoId = payload.getDepartamentoId();
        if (departamentoId == null) {
            throw new EntradaInvalidaException("Departamento é obrigatório.");
        }

        BigDecimal salario = null;
        if (contratar) {
            salario = sanitizarSalario(payload.getSalarioInicial(), "Salário inicial");
        }

        return new EntradaFuncionario(nome, email, cpf, cargo, departamentoId, salario);
    }

    private BigDecimal sanitizarSalario(BigDecimal salario, String campo) {
        if (salario == null) {
            throw new EntradaInvalidaException(campo + " é obrigatório.");
        }
        if (salario.scale() > 2) {
            throw new EntradaInvalidaException(campo + " deve ter no máximo 2 casas decimais.");
        }
        if (salario.signum() < 0) {
            throw new EntradaInvalidaException(campo + " não pode ser negativo.");
        }
        return salario.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal validarPercentual(BigDecimal percentual, String campo) {
        BigDecimal valor = sanitizarSalario(percentual, campo);
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || valor.compareTo(BigDecimal.valueOf(300)) > 0) {
            throw new EntradaInvalidaException(campo + " deve estar entre 0.01 e 300.00.");
        }
        return valor;
    }

    private <T> List<T> listaImutavel(List<T> itens) {
        if (itens == null || itens.isEmpty()) {
            return List.of();
        }
        return List.copyOf(itens);
    }

    private record EntradaFuncionario(
            String nome,
            String email,
            Cpf cpf,
            String cargo,
            Long departamentoId,
            BigDecimal salario
    ) {
    }
}
