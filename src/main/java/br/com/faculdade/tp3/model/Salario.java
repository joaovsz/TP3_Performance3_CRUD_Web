package br.com.faculdade.tp3.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "salarios")
public class Salario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false, unique = true)
    @JsonBackReference
    private Funcionario funcionario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorAtual;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        atualizadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public BigDecimal aplicarAumento(BigDecimal percentual) {
        if (percentual == null || percentual.signum() <= 0) {
            throw new IllegalArgumentException("Percentual de aumento deve ser positivo.");
        }
        if (valorAtual == null) {
            throw new IllegalStateException("Salário atual não está definido.");
        }

        BigDecimal fator = percentual.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        valorAtual = valorAtual
                .multiply(BigDecimal.ONE.add(fator))
                .setScale(2, RoundingMode.HALF_UP);
        return valorAtual;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Salario salario)) {
            return false;
        }
        return Objects.equals(id, salario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
