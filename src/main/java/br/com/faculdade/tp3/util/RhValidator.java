package br.com.faculdade.tp3.util;

import br.com.faculdade.tp3.exception.EntradaInvalidaException;
import br.com.faculdade.tp3.model.Cpf;
import java.util.Locale;
import java.util.regex.Pattern;

public final class RhValidator {

    private static final Pattern CONTROL_PATTERN = Pattern.compile(".*[\\p{Cntrl}&&[^\\r\\n\\t]].*");
    private static final Pattern HUMAN_TEXT_PATTERN = Pattern.compile("^[\\p{L}0-9 .,'-]+$");
    private static final Pattern MALICIOUS_PATTERN = Pattern.compile(
            ".*(<|>|\\{|\\}|\\$\\{|--|;|/\\*|\\*/|\\bselect\\b|\\binsert\\b|\\bdelete\\b|\\bdrop\\b).*",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern CPF_PATTERN = Pattern.compile("^[0-9]{11}$");

    private RhValidator() {
    }

    public static String sanitizarTexto(String valor, String campo, boolean obrigatorio, int min, int max) {
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

    public static String sanitizarTextoHumano(String valor, String campo, boolean obrigatorio, int min, int max) {
        String normalizado = sanitizarTexto(valor, campo, obrigatorio, min, max);
        if (!normalizado.isEmpty() && !HUMAN_TEXT_PATTERN.matcher(normalizado).matches()) {
            throw new EntradaInvalidaException(campo + " contém caracteres não permitidos.");
        }
        return normalizado;
    }

    public static String sanitizarEmail(String email) {
        String normalizado = sanitizarTexto(email, "Email", true, 5, 160).toLowerCase(Locale.ROOT);
        if (!normalizado.contains("@") || normalizado.startsWith("@") || normalizado.endsWith("@")) {
            throw new EntradaInvalidaException("Email inválido.");
        }
        return normalizado;
    }

    public static Cpf sanitizarCpf(String cpf) {
        String normalizado = sanitizarTexto(cpf, "CPF", true, 11, 11);
        if (!CPF_PATTERN.matcher(normalizado).matches()) {
            throw new EntradaInvalidaException("CPF deve conter exatamente 11 dígitos.");
        }
        return new Cpf(normalizado);
    }
}
