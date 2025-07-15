package com.example.Locadora_Filmes.controller;

import com.example.Locadora_Filmes.model.User;
import com.example.Locadora_Filmes.service.ServiceUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControllerAdmin {

    private final ServiceUser serviceUser;

    public ControllerAdmin(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        model.addAttribute("user", serviceUser.getAllUsers());
        return "admin";
    }

    @GetMapping("/admin/usuario/{id}")
    @ResponseBody
    public User getUserDetailsJson(@PathVariable Long id) {
        return ServiceUser.searchId(id);
    }

    @PostMapping("/admin/usuario/atualizar/{id}")
    public String updateUser(@PathVariable Long id,
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

    @PostMapping("/admin/deletar/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceUser.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Usuário deletado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao deletar usuário: " + e.getMessage());
        }
        return "redirect:/admin";
    }

}
