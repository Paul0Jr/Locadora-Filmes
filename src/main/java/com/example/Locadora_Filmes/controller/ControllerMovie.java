package com.example.Locadora_Filmes.controller;

import org.springframework.ui.Model;
import com.example.Locadora_Filmes.model.Movie;
import com.example.Locadora_Filmes.repository.RepositoryMovie;
import com.example.Locadora_Filmes.service.ServiceMovie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ControllerMovie {

    @GetMapping("/filmes")
    public String filmes(Model model) {
       model.addAttribute("filmes", serviceMovie.listMovies());
        return "filmes";
    }

    private final ServiceMovie serviceMovie;
    public ControllerMovie(ServiceMovie serviceMovie) {
        this.serviceMovie = serviceMovie;
    }



    @PostMapping("/filmes/adicionar")
    public String addMovie(@RequestParam String nome,
                           @RequestParam String diretor,
                           @RequestParam int lancamento,
                           @RequestParam String genero,
                           @RequestParam String classificacao,
                           @RequestParam double preco) {
        Movie movie = serviceMovie.saveMovie(nome, diretor, lancamento, genero, classificacao, preco);
        return "redirect:/filmes";
    }

    @PostMapping("/filmes/deletar/{id}")
    public String deleteMovie(@PathVariable Long id) {
        serviceMovie.deleteMovie(id);
        return "redirect:/filmes";
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<?> searchMovie(@PathVariable Long id) {
        Movie movie = serviceMovie.searchId(id);
        return ResponseEntity.ok(movie);
    } */

   /*
   @PostMapping
    public Movie AddMovie(@RequestBody Movie movie) {
        return serviceMovie.saveMovie(movie);
    } */

    /*@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        serviceMovie.deleteMovie(id);
        return ResponseEntity.noContent().build();
    } */

}
