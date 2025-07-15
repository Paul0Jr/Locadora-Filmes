package com.example.Locadora_Filmes.service;

import com.example.Locadora_Filmes.model.User;
import com.example.Locadora_Filmes.repository.RepositoryUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final RepositoryUser repositoryUser;

    public CustomUserDetailsService(RepositoryUser repositoryUser) {
        this.repositoryUser = repositoryUser;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("=== CUSTOM USER DETAILS SERVICE ===");
        logger.info("Tentando carregar usuário com email: {}", email);
        
        try {
            User user = repositoryUser.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

            logger.info("Usuário encontrado: {} (ID: {})", user.getName(), user.getId());
            logger.info("Senha do usuário (hash): {}", user.getPassword().substring(0, Math.min(20, user.getPassword().length())) + "...");

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
            );
            
            logger.info("UserDetails criado com sucesso para: {}", userDetails.getUsername());
            logger.info("Authorities: {}", userDetails.getAuthorities());
            logger.info("Account non-expired: {}", userDetails.isAccountNonExpired());
            logger.info("Account non-locked: {}", userDetails.isAccountNonLocked());
            logger.info("Credentials non-expired: {}", userDetails.isCredentialsNonExpired());
            logger.info("Enabled: {}", userDetails.isEnabled());
            
            return userDetails;
        } catch (UsernameNotFoundException e) {
            logger.warn("Usuário não encontrado: {}", email);
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao carregar usuário com email {}: {}", email, e.getMessage(), e);
            throw new UsernameNotFoundException("Erro interno ao carregar usuário", e);
        }
    }
} 