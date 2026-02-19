package br.com.faculdade.tp3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleWebError(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erro", "Não foi possível concluir a operação. Revise os dados e tente novamente.");
        return "redirect:/rh/funcionarios";
    }
}
