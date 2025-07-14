package com.example.Locadora_Filmes.service;

import com.example.Locadora_Filmes.exceptions.NotFoundResource;
import com.example.Locadora_Filmes.model.Movie;
import com.example.Locadora_Filmes.repository.RepositoryMovie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceMovie {

    private RepositoryMovie repositoryMovie;
    public ServiceMovie(RepositoryMovie repositoryMovie) {
        this.repositoryMovie = repositoryMovie;
    }

    //Listagem
    public List<Movie> listMovies() {
        return repositoryMovie.findAll();
    }

    //Procurar no banco
    public Movie searchId(Long id) {
        return repositoryMovie.findById(id)
                .orElseThrow(() -> new NotFoundResource("Filme de id " + id + " não encontrado!"));
    }

    //Adicionar no banco
    public Movie saveMovie(String nome, String diretor, int lancamento, String genero, String classificacao, double preco) {
        Movie movie = new Movie(nome, diretor, lancamento, genero, classificacao, preco);
        return repositoryMovie.save(movie);
    }

    //Deletar no banco
    public void deleteMovie(Long id) {
        if (!repositoryMovie.existsById(id)) {
            throw new NotFoundResource("Filme com id " + id + " inexistente!");
        }
        repositoryMovie.deleteById(id);
    }
}