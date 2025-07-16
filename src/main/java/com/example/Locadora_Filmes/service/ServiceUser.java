package com.example.Locadora_Filmes.service;

import com.example.Locadora_Filmes.exceptions.NotFoundResource;
import com.example.Locadora_Filmes.model.User;
import com.example.Locadora_Filmes.repository.RepositoryUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceUser {
    private final RepositoryUser repositoryUser;
    private final PasswordEncoder passwordEncoder;

    public ServiceUser(RepositoryUser repositoryUser, PasswordEncoder passwordEncoder) {
        this.repositoryUser = repositoryUser;
        this.passwordEncoder = passwordEncoder;
    }

    public User UserLogin(String email) throws NotFoundResource {
        User user = repositoryUser.findByEmail(email)
                .orElseThrow(() -> new NotFoundResource("Usuário não encontrado com o e-mail: " + email));
        return user;
    }

    public User UserRegister(String name, String email, String password) {

        // Verificar se o email já existe
        if (repositoryUser.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email já está cadastrado: " + email);
        }

        // Validar dados
        if (name == null || name.trim().isEmpty() || name.trim().length() < 2) {
            throw new RuntimeException("Nome deve ter pelo menos 2 caracteres");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email é obrigatório");
        }

        if (password == null || password.trim().isEmpty() || password.length() < 8) {
            throw new RuntimeException("Senha deve ter pelo menos 8 caracteres");
        }

        // Criptografar senha
        String passwordEncrypt = passwordEncoder.encode(password);

        // Criar usuário com role padrão
        User user = new User(name.trim(), email.trim().toLowerCase(), passwordEncrypt, "ROLE_USER");
        return repositoryUser.save(user);
    }

    public Optional<User> SearchByEmail(String email) {
        return repositoryUser.findByEmail(email);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public List<User> getAllUsers() {
        return repositoryUser.findAll();
    }

    public void deleteUser(Long id) {
        if (!repositoryUser.existsById(id)) {
            throw new RuntimeException("Usuário com id " + id + " não encontrado!");
        }
        repositoryUser.deleteById(id);
    }

    public User updateUser(Long id, String name, String email, String role) {
        User user = repositoryUser.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com id " + id + " não encontrado!"));

        // Verificar se o email já existe (excluindo o usuário atual)
        if (!email.equals(user.getEmail())) {
            Optional<User> existingUser = repositoryUser.findByEmail(email);
            if (existingUser.isPresent()) {
                throw new RuntimeException("Email já está cadastrado: " + email);
            }
        }

        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        return repositoryUser.save(user);
    }
}
