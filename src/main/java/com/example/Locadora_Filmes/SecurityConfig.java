package com.example.Locadora_Filmes;

import com.example.Locadora_Filmes.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configurando Spring Security...");
        
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/login", "/registro", "/register", "/simple-login").permitAll()
                        .requestMatchers("/api/test/**", "/api/debug/**", "/api/test-login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .failureHandler((request, response, exception) -> {
                            logger.error("=== FALHA NA AUTENTICAÇÃO ===");
                            logger.error("Exception: {}", exception.getClass().getSimpleName());
                            logger.error("Message: {}", exception.getMessage());
                            logger.error("Request URI: {}", request.getRequestURI());
                            logger.error("Request method: {}", request.getMethod());
                            logger.error("Email parameter: {}", request.getParameter("email"));
                            logger.error("Password parameter: {}", request.getParameter("password") != null ? "[PRESENTE]" : "[AUSENTE]");
                            response.sendRedirect("/login?error=true");
                        })
                        .successHandler((request, response, authentication) -> {
                            logger.info("=== LOGIN BEM-SUCEDIDO ===");
                            logger.info("Usuário: {}", authentication.getName());
                            logger.info("Authorities: {}", authentication.getAuthorities());
                            logger.info("Request URI: {}", request.getRequestURI());
                            logger.info("Redirecionando para: /dashboard");
                            response.sendRedirect("/dashboard");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/login?accessDenied=true")
                )
                .authenticationProvider(authenticationProvider());

        logger.info("Spring Security configurado com sucesso");
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        logger.info("Configurando DaoAuthenticationProvider...");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        logger.info("DaoAuthenticationProvider configurado");
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


