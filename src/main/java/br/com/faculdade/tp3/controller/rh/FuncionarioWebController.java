package br.com.faculdade.tp3.controller.rh;

import br.com.faculdade.tp3.dto.rh.AjusteSalarialPayload;
import br.com.faculdade.tp3.dto.rh.DemissaoPayload;
import br.com.faculdade.tp3.dto.rh.FuncionarioPayload;
import br.com.faculdade.tp3.dto.rh.PromocaoPayload;
import br.com.faculdade.tp3.exception.EntradaInvalidaException;
import br.com.faculdade.tp3.exception.RecursoDuplicadoException;
import br.com.faculdade.tp3.model.Funcionario;
import br.com.faculdade.tp3.service.RhService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rh/funcionarios")
public class FuncionarioWebController {

    private final RhService rhService;

    public FuncionarioWebController(RhService rhService) {
        this.rhService = rhService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativos,
            Model model
    ) {
        model.addAttribute("funcionarios", rhService.listarFuncionarios(nome, ativos));
        model.addAttribute("filtroNome", nome == null ? "" : nome);
        model.addAttribute("filtroAtivos", ativos);
        return "rh/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        FuncionarioPayload payload = new FuncionarioPayload();
        popularModeloFormulario(model, payload);
        return "rh/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Funcionario funcionario = rhService.buscarFuncionario(id);
        FuncionarioPayload payload = new FuncionarioPayload();
        payload.setId(funcionario.getId());
        payload.setNome(funcionario.getNome());
        payload.setEmail(funcionario.getEmail());
        payload.setCpf(funcionario.getCpf());
        payload.setCargo(funcionario.getCargo());
        payload.setDepartamentoId(funcionario.getDepartamento().getId());
        payload.setSalarioInicial(funcionario.getSalario().getValorAtual());

        popularModeloFormulario(model, payload);
        return "rh/form";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("funcionario") FuncionarioPayload payload,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            popularModeloFormulario(model, payload);
            return "rh/form";
        }

        try {
            if (payload.getId() == null) {
                rhService.contratar(payload);
                redirectAttributes.addFlashAttribute("sucesso", "Funcionário contratado com sucesso.");
            } else {
                rhService.atualizarCadastro(payload.getId(), payload);
                redirectAttributes.addFlashAttribute("sucesso", "Cadastro atualizado com sucesso.");
            }
        } catch (RecursoDuplicadoException | EntradaInvalidaException ex) {
            popularModeloFormulario(model, payload);
            model.addAttribute("erro", ex.getMessage());
            return "rh/form";
        }

        return "redirect:/rh/funcionarios";
    }

    @GetMapping("/{id}/aumento")
    public String telaAumento(@PathVariable Long id, Model model) {
        model.addAttribute("funcionario", rhService.buscarFuncionario(id));
        model.addAttribute("payload", new AjusteSalarialPayload());
        return "rh/aumento";
    }

    @PostMapping("/{id}/aumento")
    public String aumentar(
            @PathVariable Long id,
            @Valid @ModelAttribute("payload") AjusteSalarialPayload payload,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("funcionario", rhService.buscarFuncionario(id));
            return "rh/aumento";
        }

        rhService.aumentarSalario(id, payload);
        redirectAttributes.addFlashAttribute("sucesso", "Aumento aplicado com sucesso.");
        return "redirect:/rh/funcionarios";
    }

    @GetMapping("/{id}/promocao")
    public String telaPromocao(@PathVariable Long id, Model model) {
        model.addAttribute("funcionario", rhService.buscarFuncionario(id));
        model.addAttribute("payload", new PromocaoPayload());
        return "rh/promocao";
    }

    @PostMapping("/{id}/promocao")
    public String promover(
            @PathVariable Long id,
            @Valid @ModelAttribute("payload") PromocaoPayload payload,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("funcionario", rhService.buscarFuncionario(id));
            return "rh/promocao";
        }

        rhService.promover(id, payload);
        redirectAttributes.addFlashAttribute("sucesso", "Promoção aplicada com sucesso.");
        return "redirect:/rh/funcionarios";
    }

    @PostMapping("/{id}/demitir")
    public String demitir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        DemissaoPayload payload = new DemissaoPayload();
        payload.setMotivo("Desligamento via painel de RH");
        rhService.demitir(id, payload);
        redirectAttributes.addFlashAttribute("sucesso", "Funcionário demitido com sucesso.");
        return "redirect:/rh/funcionarios";
    }

    @PostMapping("/{id}/excluir-definitivo")
    public String excluirDefinitivo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rhService.excluirDefinitivamente(id);
        redirectAttributes.addFlashAttribute("sucesso", "Registro excluído definitivamente.");
        return "redirect:/rh/funcionarios";
    }

    @GetMapping("/{id}/movimentacoes")
    public String movimentacoes(@PathVariable Long id, Model model) {
        model.addAttribute("funcionario", rhService.buscarFuncionario(id));
        model.addAttribute("movimentacoes", rhService.listarMovimentacoes(id));
        return "rh/movimentacoes";
    }

    private void popularModeloFormulario(Model model, FuncionarioPayload payload) {
        model.addAttribute("funcionario", payload);
        model.addAttribute("departamentos", rhService.listarDepartamentos());
        model.addAttribute("tituloForm", payload.getId() == null ? "Contratar funcionário" : "Editar cadastro");
    }
}
