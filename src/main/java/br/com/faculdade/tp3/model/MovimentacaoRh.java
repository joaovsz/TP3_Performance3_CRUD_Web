package br.com.faculdade.tp3.model;

import br.com.faculdade.tp3.model.enums.TipoMovimentacaoRh;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_rh")
public class MovimentacaoRh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    @JsonIgnore
    private Funcionario funcionario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoMovimentacaoRh tipo;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(name = "salario_anterior", precision = 12, scale = 2)
    private BigDecimal salarioAnterior;

    @Column(name = "salario_novo", precision = 12, scale = 2)
    private BigDecimal salarioNovo;

    @Column(name = "movimentado_em", nullable = false)
    private LocalDateTime movimentadoEm;

    @PrePersist
    public void prePersist() {
        movimentadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public TipoMovimentacaoRh getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacaoRh tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSalarioAnterior() {
        return salarioAnterior;
    }

    public void setSalarioAnterior(BigDecimal salarioAnterior) {
        this.salarioAnterior = salarioAnterior;
    }

    public BigDecimal getSalarioNovo() {
        return salarioNovo;
    }

    public void setSalarioNovo(BigDecimal salarioNovo) {
        this.salarioNovo = salarioNovo;
    }

    public LocalDateTime getMovimentadoEm() {
        return movimentadoEm;
    }
}
