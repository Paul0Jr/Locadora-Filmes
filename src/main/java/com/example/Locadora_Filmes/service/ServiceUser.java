package com.example.Locadora_Filmes.service;

import com.example.Locadora_Filmes.exceptions.NotFoundResource;
import com.example.Locadora_Filmes.model.Movie;
import com.example.Locadora_Filmes.model.User;
import com.example.Locadora_Filmes.repository.RepositoryUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceUser {
    private static RepositoryUser repositoryUser = null;
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

        // Criar usuário
        User user = new User(name.trim(), email.trim().toLowerCase(), passwordEncrypt, "ROLE_USER");
        return repositoryUser.save(user);
    }

    public Optional<User> SearchByEmail(String email) {
        return repositoryUser.findByEmail(email);
    }

    public static User searchId(Long id) {
        return repositoryUser.findById(id)
                .orElseThrow(() -> new NotFoundResource("Usuário de id " + id + " não encontrado!"));
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    //Atualizar no banco
    public User updateUser(Long id, String name, String email, String role) {
        User user = searchId(id);
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        return repositoryUser.save(user);
    }

    public void deleteUser(Long id) {
        if (!repositoryUser.existsById(id)) {
            throw new NotFoundResource("Usuário com id " + id + " inexistente!");
        }
        repositoryUser.deleteById(id);
    }

    public List<User> getAllUsers() {
        return repositoryUser.findAll();
    }
}
