package com.tp2.engsoftware.controller;

import com.tp2.engsoftware.exception.ProdutoDuplicadoException;
import com.tp2.engsoftware.exception.ProdutoNotFoundException;
import com.tp2.engsoftware.model.Produto;
import com.tp2.engsoftware.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoWebController {

    private final ProdutoService service;

    public ProdutoWebController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", service.listarTodos());
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String mostrarFormularioNovo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        try {
            Produto produto = service.buscarPorId(id);
            model.addAttribute("produto", produto);
            return "produtos/form";
        } catch (ProdutoNotFoundException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "redirect:/produtos";
        }
    }

    @PostMapping
    public String salvar(
            @Valid @ModelAttribute Produto produto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "produtos/form";
        }

        try {
            if (produto.getId() == null) {
                service.criar(produto);
                redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!");
            } else {
                service.atualizar(produto.getId(), produto);
                redirectAttributes.addFlashAttribute("sucesso", "Produto atualizado com sucesso!");
            }
            return "redirect:/produtos";
        } catch (ProdutoDuplicadoException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/produtos";
        }
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto deletado com sucesso!");
        } catch (ProdutoNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/produtos";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erro", "Erro ao processar requisição: " + ex.getMessage());
        return "redirect:/produtos";
    }
}
