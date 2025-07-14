package com.example.Locadora_Filmes.controller;

import com.example.Locadora_Filmes.model.User;
import com.example.Locadora_Filmes.service.ServiceUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ControllerUser {

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/")
    public String RedirectLogin() {
        return "redirect:/login";
    }

    private final ServiceUser serviceUser;

    public ControllerUser(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, 
                          @RequestParam String email, 
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam(required = false) String terms,
                          RedirectAttributes redirectAttributes) {
        
        try {
            // Validações básicas
            if (name == null || name.trim().isEmpty() || name.trim().length() < 2) {
                redirectAttributes.addFlashAttribute("error", "Nome deve ter pelo menos 2 caracteres.");
                return "redirect:/registro?error=true";
            }

            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email é obrigatório.");
                return "redirect:/registro?error=true";
            }

            if (password == null || password.trim().isEmpty() || password.length() < 8) {
                redirectAttributes.addFlashAttribute("error", "Senha deve ter pelo menos 8 caracteres.");
                return "redirect:/registro?error=true";
            }

            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Confirmação de senha é obrigatória.");
                return "redirect:/registro?error=true";
            }

            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "As senhas não coincidem.");
                return "redirect:/registro?error=true";
            }

            // Verificar se o email já existe
            Optional<User> existingUser = serviceUser.SearchByEmail(email.trim().toLowerCase());
            if (existingUser.isPresent()) {
                return "redirect:/registro?emailExists=true";
            }

            // Criar o usuário
            User newUser = serviceUser.UserRegister(name.trim(), email.trim(), password);
            
            if (newUser != null && newUser.getId() > 0) {
                redirectAttributes.addFlashAttribute("success", "Conta criada com sucesso! Você pode fazer login agora.");
                return "redirect:/login?success=true";
            } else {
                redirectAttributes.addFlashAttribute("error", "Erro ao criar conta. Tente novamente.");
                return "redirect:/registro?error=true";
            }

        } catch (RuntimeException e) {
            // Captura erros específicos do serviço
            if (e.getMessage().contains("Email já está cadastrado")) {
                return "redirect:/registro?emailExists=true";
            }
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro?error=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro interno do servidor. Tente novamente.");
            return "redirect:/registro?error=true";
        }
    }
}
