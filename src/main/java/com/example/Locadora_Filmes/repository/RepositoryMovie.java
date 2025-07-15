package com.example.Locadora_Filmes.repository;

import com.example.Locadora_Filmes.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryMovie extends JpaRepository<Movie, Long> {
    
    @Query("SELECT m FROM Movie m WHERE LOWER(m.nome) = LOWER(:nome) AND m.lancamento = :lancamento")
    Optional<Movie> findByNomeAndLancamento(@Param("nome") String nome, @Param("lancamento") int lancamento);
}
