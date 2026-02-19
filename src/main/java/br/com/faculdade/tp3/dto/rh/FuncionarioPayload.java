package br.com.faculdade.tp3.dto.rh;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class FuncionarioPayload {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 160, message = "Email deve ter no máximo 160 caracteres")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter 11 dígitos numéricos")
    private String cpf;

    @NotBlank(message = "Cargo é obrigatório")
    @Size(min = 2, max = 100, message = "Cargo deve ter entre 2 e 100 caracteres")
    private String cargo;

    @NotNull(message = "Departamento é obrigatório")
    private Long departamentoId;

    @DecimalMin(value = "0.00", message = "Salário inicial não pode ser negativo")
    @Digits(integer = 10, fraction = 2, message = "Salário inicial inválido")
    private BigDecimal salarioInicial;

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

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public BigDecimal getSalarioInicial() {
        return salarioInicial;
    }

    public void setSalarioInicial(BigDecimal salarioInicial) {
        this.salarioInicial = salarioInicial;
    }
}
