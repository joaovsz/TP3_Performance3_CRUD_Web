package br.com.faculdade.tp3.model;

import br.com.faculdade.tp3.model.enums.FuncionarioStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "funcionarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_funcionario_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_funcionario_cpf", columnNames = "cpf")
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_funcionario", length = 20)
@DiscriminatorValue("GERAL")
public class Funcionario implements EntidadeRastreavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, length = 100)
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuncionarioStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @OneToOne(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Salario salario;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(name = "data_demissao")
    private LocalDate dataDemissao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Funcionario() {
    }

    protected Funcionario(
            String nome,
            String email,
            String cpf,
            String cargo,
            Departamento departamento,
            FuncionarioStatus status,
            LocalDate dataAdmissao
    ) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.cargo = cargo;
        this.departamento = departamento;
        this.status = status;
        this.dataAdmissao = dataAdmissao;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public void definirSalario(Salario salario) {
        this.salario = salario;
        salario.setFuncionario(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public FuncionarioStatus getStatus() {
        return status;
    }

    public void setStatus(FuncionarioStatus status) {
        this.status = status;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Salario getSalario() {
        return salario;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public LocalDate getDataDemissao() {
        return dataDemissao;
    }

    public void setDataDemissao(LocalDate dataDemissao) {
        this.dataDemissao = dataDemissao;
    }

    public boolean aceitaPromocaoPara(String novoCargo) {
        return true;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Funcionario that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Builder builder() {
        return new Builder(new Funcionario());
    }

    public static Builder builder(Funcionario destino) {
        return new Builder(destino);
    }

    public static final class Builder {
        private final Funcionario destino;

        private Builder(Funcionario destino) {
            this.destino = destino;
        }

        public Builder nome(String nome) {
            destino.setNome(nome);
            return this;
        }

        public Builder email(String email) {
            destino.setEmail(email);
            return this;
        }

        public Builder cpf(String cpf) {
            destino.setCpf(cpf);
            return this;
        }

        public Builder cargo(String cargo) {
            destino.setCargo(cargo);
            return this;
        }

        public Builder departamento(Departamento departamento) {
            destino.setDepartamento(departamento);
            return this;
        }

        public Builder status(FuncionarioStatus status) {
            destino.setStatus(status);
            return this;
        }

        public Builder dataAdmissao(LocalDate dataAdmissao) {
            destino.setDataAdmissao(dataAdmissao);
            return this;
        }

        public Builder dataDemissao(LocalDate dataDemissao) {
            destino.setDataDemissao(dataDemissao);
            return this;
        }

        public Funcionario build() {
            if (destino.nome == null || destino.email == null || destino.cpf == null || destino.cargo == null) {
                throw new IllegalStateException("Dados obrigatórios do funcionário não foram definidos.");
            }
            if (destino.departamento == null) {
                throw new IllegalStateException("Departamento é obrigatório para montar um funcionário.");
            }
            if (destino.status == null) {
                throw new IllegalStateException("Status do funcionário é obrigatório.");
            }
            if (destino.dataAdmissao == null) {
                throw new IllegalStateException("Data de admissão é obrigatória.");
            }
            return destino;
        }
    }
}
