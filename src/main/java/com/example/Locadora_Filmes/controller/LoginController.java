package com.example.Locadora_Filmes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "accessDenied", required = false) String accessDenied,
                       Model model) {
        
        logger.info("=== LOGIN PAGE ACCESS ===");
        logger.info("Error parameter: {}", error);
        logger.info("Logout parameter: {}", logout);
        logger.info("AccessDenied parameter: {}", accessDenied);

        if (error != null) {
            logger.warn("ERROR DETECTED - Adding error message to model");
            model.addAttribute("errorMessage", "Email ou senha incorretos. Tente novamente.");
        }

        if (logout != null) {
            logger.info("LOGOUT DETECTED - Adding success message to model");
            model.addAttribute("successMessage", "Você foi desconectado com sucesso.");
        }

        if (accessDenied != null) {
            logger.warn("ACCESS DENIED DETECTED - Adding error message to model");
            model.addAttribute("errorMessage", "Acesso negado. Você não tem permissão para acessar esta página.");
        }

        return "login";
    }
} 