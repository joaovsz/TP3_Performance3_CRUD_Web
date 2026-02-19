function confirmarAcao(mensagem) {
    return window.confirm(mensagem);
}

function somenteDigitos(valor) {
    return (valor || "").replace(/\D/g, "");
}

function formatarCpf(valor) {
    const digitos = somenteDigitos(valor).slice(0, 11);
    const parte1 = digitos.slice(0, 3);
    const parte2 = digitos.slice(3, 6);
    const parte3 = digitos.slice(6, 9);
    const parte4 = digitos.slice(9, 11);

    if (digitos.length <= 3) {
        return parte1;
    }
    if (digitos.length <= 6) {
        return `${parte1}.${parte2}`;
    }
    if (digitos.length <= 9) {
        return `${parte1}.${parte2}.${parte3}`;
    }
    return `${parte1}.${parte2}.${parte3}-${parte4}`;
}

function centavosParaMoeda(cents) {
    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL"
    }).format(cents / 100);
}

function parseValorParaCentavos(valor) {
    if (!valor) {
        return 0;
    }

    const normalizado = String(valor).trim();
    if (!normalizado) {
        return 0;
    }

    const limpado = normalizado
        .replace(/\s/g, "")
        .replace("R$", "")
        .replace(/[^\d,.-]/g, "");

    if (!limpado) {
        return 0;
    }

    const ultimoSeparador = Math.max(limpado.lastIndexOf(","), limpado.lastIndexOf("."));
    if (ultimoSeparador >= 0) {
        const inteiro = limpado.slice(0, ultimoSeparador).replace(/\D/g, "");
        const fracao = limpado.slice(ultimoSeparador + 1).replace(/\D/g, "");
        const reais = inteiro ? Number(inteiro) : 0;
        const centavos = fracao ? Number(fracao.padEnd(2, "0").slice(0, 2)) : 0;
        return (reais * 100) + centavos;
    }

    const apenasDigitos = limpado.replace(/\D/g, "");
    if (!apenasDigitos) {
        return 0;
    }

    return Number(apenasDigitos) * 100;
}

function estaVazio(valor) {
    return !String(valor || "").trim();
}

function configurarMascaraCpf() {
    const campoCpf = document.getElementById("cpf");
    if (!campoCpf) {
        return;
    }

    campoCpf.value = formatarCpf(campoCpf.value);
    campoCpf.addEventListener("input", () => {
        campoCpf.value = formatarCpf(campoCpf.value);
    });
}

function configurarMascaraSalario() {
    const campoSalario = document.getElementById("salarioInicial");
    const campoSalarioRaw = document.getElementById("salarioInicialRaw");
    if (!campoSalario || !campoSalarioRaw) {
        return;
    }

    const posicionarCursorAntesDosCentavos = () => {
        if (document.activeElement !== campoSalario) {
            return;
        }

        const posicaoVirgula = campoSalario.value.indexOf(",");
        if (posicaoVirgula > 0) {
            requestAnimationFrame(() => {
                campoSalario.setSelectionRange(posicaoVirgula, posicaoVirgula);
            });
        }
    };

    const sincronizarSalario = (formatarCampo) => {
        const centavos = parseValorParaCentavos(campoSalario.value);
        if (estaVazio(campoSalario.value)) {
            campoSalarioRaw.value = "";
            return;
        }

        campoSalarioRaw.value = (centavos / 100).toFixed(2);
        if (formatarCampo) {
            campoSalario.value = centavosParaMoeda(centavos);
        }
    };

    const inicialRaw = parseValorParaCentavos(campoSalarioRaw.value);
    const inicialDisplay = parseValorParaCentavos(campoSalario.value);
    const possuiRawInicial = !estaVazio(campoSalarioRaw.value);
    const possuiDisplayInicial = !estaVazio(campoSalario.value);
    const inicial = possuiRawInicial ? inicialRaw : inicialDisplay;

    if (possuiRawInicial || possuiDisplayInicial) {
        campoSalario.value = centavosParaMoeda(inicial);
        campoSalarioRaw.value = (inicial / 100).toFixed(2);
    } else {
        campoSalario.value = "";
        campoSalarioRaw.value = "";
    }

    campoSalario.addEventListener("input", () => {
        sincronizarSalario(true);
        posicionarCursorAntesDosCentavos();
    });
    campoSalario.addEventListener("focus", posicionarCursorAntesDosCentavos);
    campoSalario.addEventListener("blur", () => sincronizarSalario(true));
}

document.addEventListener("DOMContentLoaded", () => {
    configurarMascaraCpf();
    configurarMascaraSalario();

    const form = document.querySelector("form.stack-form");
    if (!form) {
        return;
    }

    form.addEventListener("submit", () => {
        const campoCpf = document.getElementById("cpf");
        if (campoCpf) {
            campoCpf.value = somenteDigitos(campoCpf.value).slice(0, 11);
        }

        const campoSalario = document.getElementById("salarioInicial");
        const campoSalarioRaw = document.getElementById("salarioInicialRaw");
        if (campoSalario && campoSalarioRaw) {
            const centavos = parseValorParaCentavos(campoSalario.value);
            campoSalarioRaw.value = estaVazio(campoSalario.value) ? "" : (centavos / 100).toFixed(2);
        }
    });
});
