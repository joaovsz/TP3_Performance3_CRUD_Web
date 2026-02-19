package br.com.faculdade.tp3.service;

import br.com.faculdade.tp3.dto.rh.AjusteSalarialPayload;
import br.com.faculdade.tp3.dto.rh.DemissaoPayload;
import br.com.faculdade.tp3.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp3.dto.rh.PromocaoPayload;
import br.com.faculdade.tp3.exception.EntradaInvalidaException;
import br.com.faculdade.tp3.exception.RecursoDuplicadoException;
import br.com.faculdade.tp3.exception.RecursoNaoEncontradoException;
import br.com.faculdade.tp3.model.Departamento;
import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.model.MovimentacaoRh;
import br.com.faculdade.tp3.model.Salario;
import br.com.faculdade.tp3.model.enums.FuncionarioStatus;
import br.com.faculdade.tp3.model.enums.TipoMovimentacaoRh;
import br.com.faculdade.tp3.repository.DepartamentoRepository;
import br.com.faculdade.tp3.repository.FuncionarioRepository;
import br.com.faculdade.tp3.repository.MovimentacaoRhRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RhService {

    private static final Pattern CONTROL_PATTERN = Pattern.compile(".*[\\p{Cntrl}&&[^\\r\\n\\t]].*");
    private static final Pattern HUMAN_TEXT_PATTERN = Pattern.compile("^[\\p{L}0-9 .,'-]+$");
    private static final Pattern MALICIOUS_PATTERN = Pattern.compile(
            ".*(<|>|\\{|\\}|\\$\\{|--|;|/\\*|\\*/|\\bselect\\b|\\binsert\\b|\\bdelete\\b|\\bdrop\\b).*",
            Pattern.CASE_INSENSITIVE
    );

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
                return funcionarioRepository.findAllByOrderByNomeAsc();
            }
            return funcionarioRepository.findByStatusOrderByNomeAsc(status);
        }

        String termo = sanitizarTextoHumano(nome, "Filtro de nome", false, 1, 120);
        if (status == null) {
            return funcionarioRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(termo);
        }
        return funcionarioRepository.findByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(termo, status);
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
        return movimentacaoRhRepository.findByFuncionarioIdOrderByMovimentadoEmDesc(funcionarioId);
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarDepartamentos() {
        return departamentoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public Funcionario contratar(FuncionarioPayload payload) {
        EntradaFuncionario entrada = normalizarFuncionario(payload, null, true);
        validarChavesUnicas(entrada.email(), entrada.cpf(), null);
        Departamento departamento = buscarDepartamento(entrada.departamentoId());

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(entrada.nome());
        funcionario.setEmail(entrada.email());
        funcionario.setCpf(entrada.cpf());
        funcionario.setCargo(entrada.cargo());
        funcionario.setDepartamento(departamento);
        funcionario.setStatus(FuncionarioStatus.ATIVO);
        funcionario.setDataAdmissao(LocalDate.now());

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
        validarChavesUnicas(entrada.email(), entrada.cpf(), id);

        funcionario.setNome(entrada.nome());
        funcionario.setEmail(entrada.email());
        funcionario.setCpf(entrada.cpf());
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
        String motivo = sanitizarTexto(payload.getMotivo(), "Motivo", true, 5, 255);

        Funcionario funcionario = buscarFuncionario(id);
        validarFuncionarioAtivo(funcionario);

        BigDecimal salarioAnterior = funcionario.getSalario().getValorAtual();
        BigDecimal fator = percentual.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal salarioNovo = salarioAnterior
                .multiply(BigDecimal.ONE.add(fator))
                .setScale(2, RoundingMode.HALF_UP);

        funcionario.getSalario().setValorAtual(salarioNovo);
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

        String novoCargo = sanitizarTextoHumano(payload.getNovoCargo(), "Novo cargo", true, 2, 100);
        String motivo = sanitizarTexto(payload.getMotivo(), "Motivo", true, 5, 255);
        BigDecimal percentual = validarPercentual(payload.getPercentualAumento(), "Percentual da promoção");

        Funcionario funcionario = buscarFuncionario(id);
        validarFuncionarioAtivo(funcionario);

        BigDecimal salarioAnterior = funcionario.getSalario().getValorAtual();
        BigDecimal fator = percentual.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal salarioNovo = salarioAnterior
                .multiply(BigDecimal.ONE.add(fator))
                .setScale(2, RoundingMode.HALF_UP);

        funcionario.setCargo(novoCargo);
        funcionario.getSalario().setValorAtual(salarioNovo);

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

        String motivo = sanitizarTexto(payload.getMotivo(), "Motivo da demissão", true, 5, 255);

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

        String nome = sanitizarTextoHumano(payload.getNome(), "Nome", true, 3, 120);
        String email = sanitizarEmail(payload.getEmail());
        String cpf = sanitizarCpf(payload.getCpf());
        String cargo = sanitizarTextoHumano(payload.getCargo(), "Cargo", true, 2, 100);
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

    private String sanitizarTexto(String valor, String campo, boolean obrigatorio, int min, int max) {
        if (valor == null) {
            if (obrigatorio) {
                throw new EntradaInvalidaException(campo + " é obrigatório.");
            }
            return "";
        }

        String normalizado = valor.trim();
        if (obrigatorio && normalizado.isEmpty()) {
            throw new EntradaInvalidaException(campo + " é obrigatório.");
        }

        if (!normalizado.isEmpty() && (normalizado.length() < min || normalizado.length() > max)) {
            throw new EntradaInvalidaException(campo + " deve ter entre " + min + " e " + max + " caracteres.");
        }

        if (CONTROL_PATTERN.matcher(normalizado).matches()) {
            throw new EntradaInvalidaException(campo + " contém caracteres inválidos.");
        }

        if (MALICIOUS_PATTERN.matcher(normalizado).matches()) {
            throw new EntradaInvalidaException(campo + " contém conteúdo potencialmente malicioso.");
        }

        return normalizado;
    }

    private String sanitizarTextoHumano(String valor, String campo, boolean obrigatorio, int min, int max) {
        String normalizado = sanitizarTexto(valor, campo, obrigatorio, min, max);
        if (!normalizado.isEmpty() && !HUMAN_TEXT_PATTERN.matcher(normalizado).matches()) {
            throw new EntradaInvalidaException(campo + " contém caracteres não permitidos.");
        }
        return normalizado;
    }

    private String sanitizarEmail(String email) {
        String normalizado = sanitizarTexto(email, "Email", true, 5, 160).toLowerCase(Locale.ROOT);
        if (!normalizado.contains("@") || normalizado.startsWith("@") || normalizado.endsWith("@")) {
            throw new EntradaInvalidaException("Email inválido.");
        }
        return normalizado;
    }

    private String sanitizarCpf(String cpf) {
        String normalizado = sanitizarTexto(cpf, "CPF", true, 11, 11);
        if (!normalizado.matches("^[0-9]{11}$")) {
            throw new EntradaInvalidaException("CPF deve conter exatamente 11 dígitos.");
        }
        return normalizado;
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

    private record EntradaFuncionario(
            String nome,
            String email,
            String cpf,
            String cargo,
            Long departamentoId,
            BigDecimal salario
    ) {
    }
}
