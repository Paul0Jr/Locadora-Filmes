package com.example.Locadora_Filmes.repository;

import com.example.Locadora_Filmes.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryMovie extends JpaRepository<Movie, Long> {
}
