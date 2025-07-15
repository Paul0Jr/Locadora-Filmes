package com.example.Locadora_Filmes.controller;

import com.example.Locadora_Filmes.model.User;
import com.example.Locadora_Filmes.service.ServiceUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ControllerAdmin {

    private final ServiceUser serviceUser;

    public ControllerAdmin(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @GetMapping("/admin")
    public String admin(Authentication authentication, Model model) {
        // Verificar se o usuário tem role de admin
        boolean isAdmin = false;
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
        }
        
        if (!isAdmin) {
            return "redirect:/dashboard";
        }
        
        List<User> usuarios = serviceUser.getAllUsers();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("currentUser", authentication.getName());
        return "admin";
    }

    @PostMapping("/admin/usuario/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceUser.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir usuário: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/usuario/atualizar/{id}")
    public String atualizarUsuario(@PathVariable Long id, 
                                  @RequestParam String name, 
                                  @RequestParam String email,
                                  @RequestParam String role,
                                  RedirectAttributes redirectAttributes) {
        try {
            serviceUser.updateUser(id, name, email, role);
            redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar usuário: " + e.getMessage());
        }
        return "redirect:/admin";
    }
} 