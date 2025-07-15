package com.example.Locadora_Filmes.controller;

import org.springframework.ui.Model;
import com.example.Locadora_Filmes.model.Movie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.Locadora_Filmes.service.ServiceMovie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ControllerMovie {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping("/filmes")
    public String filmes(Model model, 
                        @RequestParam(value = "success", required = false) String success,
                        @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("filmes", serviceMovie.listMovies());
        
        if (success != null) {
            model.addAttribute("success", "Filme atualizado com sucesso!");
        }
        
        if (error != null) {
            model.addAttribute("error", error);
        }
        
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
                           @RequestParam double preco,
                           @RequestParam String descricao,
                           @RequestParam("imagemFile") MultipartFile imagemFile,
                           RedirectAttributes redirectAttributes) {

        if (imagemFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor, selecione um arquivo de imagem.");
            return "redirect:/filmes";
        }

        // Validar tipo de arquivo
        String originalFilename = imagemFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nome do arquivo inválido.");
            return "redirect:/filmes";
        }

        String contentType = imagemFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            redirectAttributes.addFlashAttribute("error", "Por favor, selecione apenas arquivos de imagem (JPG, PNG, GIF, etc.).");
            return "redirect:/filmes";
        }

        try {
            // Gera um nome de arquivo único para evitar conflitos
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

            if (!fileExtension.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                redirectAttributes.addFlashAttribute("error", "Formato de imagem não suportado. Use JPG, PNG, GIF, BMP ou WebP.");
                return "redirect:/filmes";
            }
            
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.write(filePath, imagemFile.getBytes());

            // Salva o caminho relativo da imagem
            String urlImagem = "/uploads/" + uniqueFilename;

            System.out.println("=== DEBUG UPLOAD ===");
            System.out.println("Nome original: " + originalFilename);
            System.out.println("Tipo de conteúdo: " + contentType);
            System.out.println("Extensão: " + fileExtension);
            System.out.println("Nome único: " + uniqueFilename);
            System.out.println("Caminho salvo: " + urlImagem);
            System.out.println("Arquivo salvo em: " + filePath.toAbsolutePath());
            System.out.println("===================");

            serviceMovie.saveMovie(nome, diretor, lancamento, genero, classificacao, preco, urlImagem, descricao);
            redirectAttributes.addFlashAttribute("success", "Filme adicionado com sucesso!");

        } catch (RuntimeException e) {
            // Captura erros específicos do serviço (incluindo duplicidade)
            if (e.getMessage().contains("Já existe um filme")) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", "Erro ao adicionar filme: " + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Ocorreu um erro ao fazer o upload da imagem: " + e.getMessage());
        }
        System.out.println("Arquivo alterado!");
        return "redirect:/filmes";
    }

    @GetMapping("/filmes/{id}")
    @ResponseBody
    public Movie getMovieDetails(@PathVariable Long id) {
        return serviceMovie.searchId(id);
    }

    @PostMapping("/filmes/atualizar/{id}")
    public String updateMovie(@PathVariable Long id,
                             @RequestParam String nome,
                             @RequestParam String diretor,
                             @RequestParam int lancamento,
                             @RequestParam String genero,
                             @RequestParam String classificacao,
                             @RequestParam double preco,
                             @RequestParam(value = "imagemFile", required = false) MultipartFile imagemFile,
                             RedirectAttributes redirectAttributes) {

        try {
            Movie existingMovie = serviceMovie.searchId(id);
            String urlImagem = existingMovie.getImagem(); // Mantém a imagem atual por padrão

            // Se uma nova imagem foi enviada, processa ela
            if (imagemFile != null && !imagemFile.isEmpty()) {
                // Validar tipo de arquivo
                String originalFilename = imagemFile.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Nome do arquivo inválido.");
                    return "redirect:/filmes";
                }

                String contentType = imagemFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("error", "Por favor, selecione apenas arquivos de imagem (JPG, PNG, GIF, etc.).");
                    return "redirect:/filmes";
                }

                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                
                // Validar extensão
                if (!fileExtension.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                    redirectAttributes.addFlashAttribute("error", "Formato de imagem não suportado. Use JPG, PNG, GIF, BMP ou WebP.");
                    return "redirect:/filmes";
                }
                
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.write(filePath, imagemFile.getBytes());

                urlImagem = "/uploads/" + uniqueFilename;
            }

            serviceMovie.updateMovie(id, nome, diretor, lancamento, genero, classificacao, preco, urlImagem);
            redirectAttributes.addFlashAttribute("success", "Filme atualizado com sucesso!");

        } catch (RuntimeException e) {
            // Captura erros específicos do serviço (incluindo duplicidade)
            if (e.getMessage().contains("Já existe um filme")) {
                redirectAttributes.addFlashAttribute("error", "Não foi possível atualizar o filme. " + e.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", "Erro ao atualizar filme: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erro interno ao atualizar filme. Tente novamente.");
        }
        
        return "redirect:/filmes";
    }

    @PostMapping("/filmes/atualizar-ajax/{id}")
    @ResponseBody
    public Map<String, Object> updateMovieAjax(@PathVariable Long id,
                                              @RequestParam String nome,
                                              @RequestParam String diretor,
                                              @RequestParam int lancamento,
                                              @RequestParam String genero,
                                              @RequestParam String classificacao,
                                              @RequestParam double preco,
                                              @RequestParam(value = "imagemFile", required = false) MultipartFile imagemFile) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Movie existingMovie = serviceMovie.searchId(id);
            String urlImagem = existingMovie.getImagem(); // Mantém a imagem atual por padrão

            // Se uma nova imagem foi enviada, processa ela
            if (imagemFile != null && !imagemFile.isEmpty()) {
                // Validar tipo de arquivo
                String originalFilename = imagemFile.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "Nome do arquivo inválido.");
                    return response;
                }

                String contentType = imagemFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    response.put("success", false);
                    response.put("message", "Por favor, selecione apenas arquivos de imagem (JPG, PNG, GIF, etc.).");
                    return response;
                }

                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                
                // Validar extensão
                if (!fileExtension.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                    response.put("success", false);
                    response.put("message", "Formato de imagem não suportado. Use JPG, PNG, GIF, BMP ou WebP.");
                    return response;
                }
                
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.write(filePath, imagemFile.getBytes());

                urlImagem = "/uploads/" + uniqueFilename;
            }

            serviceMovie.updateMovie(id, nome, diretor, lancamento, genero, classificacao, preco, urlImagem);
            response.put("success", true);
            response.put("message", "Filme atualizado com sucesso!");

        } catch (RuntimeException e) {
            // Captura erros específicos do serviço (incluindo duplicidade)
            response.put("success", false);
            if (e.getMessage().contains("Já existe um filme")) {
                response.put("message", "Não foi possível atualizar o filme. " + e.getMessage());
            } else {
                response.put("message", e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erro interno ao atualizar filme. Tente novamente.");
        }
        
        return response;
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
