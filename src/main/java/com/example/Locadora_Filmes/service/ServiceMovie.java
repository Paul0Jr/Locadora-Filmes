package com.example.Locadora_Filmes.service;

import com.example.Locadora_Filmes.exceptions.NotFoundResource;
import com.example.Locadora_Filmes.model.Movie;
import com.example.Locadora_Filmes.repository.RepositoryMovie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceMovie {

    private static RepositoryMovie repositoryMovie = null;
    public ServiceMovie(RepositoryMovie repositoryMovie) {
        this.repositoryMovie = repositoryMovie;
    }

    //Listagem
    public static List<Movie> listMovies() {
        return repositoryMovie.findAll();
    }

    //Procurar no banco
    public static Movie searchId(Long id) {
        return repositoryMovie.findById(id)
                .orElseThrow(() -> new NotFoundResource("Filme de id " + id + " não encontrado!"));
    }

    //Adicionar no banco
    public Movie saveMovie(String nome, String diretor, int lancamento, String genero, String classificacao, double preco, String imagem, String descricao) {
        // Verificar se já existe um filme com o mesmo nome e ano
        Optional<Movie> existingMovie = repositoryMovie.findByNomeAndLancamento(nome.trim(), lancamento);
        if (existingMovie.isPresent()) {
            throw new RuntimeException("Já existe um filme com o nome '" + nome + "' lançado em " + lancamento + ".");
        }
        
        Movie movie = new Movie(nome, diretor, lancamento, genero, classificacao, preco, imagem, descricao);
        return repositoryMovie.save(movie);
    }

    //Atualizar no banco
    public Movie updateMovie(Long id, String nome, String diretor, int lancamento, String genero, String classificacao, double preco, String imagem) {
        Movie movie = searchId(id);
        
        // Verificar se já existe outro filme com o mesmo nome e ano (excluindo o filme atual)
        Optional<Movie> existingMovie = repositoryMovie.findByNomeAndLancamento(nome.trim(), lancamento);
        if (existingMovie.isPresent() && existingMovie.get().getId() != id) {
            throw new RuntimeException("Já existe um filme com o nome '" + nome + "' lançado em " + lancamento + ".");
        }

        movie.setNome(nome);
        movie.setDiretor(diretor);
        movie.setLancamento(lancamento);
        movie.setGenero(genero);
        movie.setClassificacao(classificacao);
        movie.setPreco(preco);
        movie.setImagem(imagem);
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