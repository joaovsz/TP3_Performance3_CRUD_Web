package br.com.faculdade.tp5.model;

import java.util.Objects;
import java.util.regex.Pattern;

import br.com.faculdade.tp5.exception.EntradaInvalidaException;

public final class Cpf {

    private static final Pattern CPF_PATTERN = Pattern.compile("^[0-9]{11}$");

    private final String valor;

    public Cpf(String valor) {
        if (valor == null || !CPF_PATTERN.matcher(valor).matches()) {
            throw new EntradaInvalidaException("CPF deve conter exatamente 11 dígitos.");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cpf cpf)) {
            return false;
        }
        return Objects.equals(valor, cpf.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
