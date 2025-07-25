package com.example.Locadora_Filmes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "filmes")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;
    private String diretor;
    private int lancamento;
    private String genero;
    private String classificacao;
    private double preco;

    @Column(length = 1000)
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(length = 1000)
    private String imagem;

    public Movie() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public int getLancamento() {
        return lancamento;
    }

    public void setLancamento(int lancamento) {
        this.lancamento = lancamento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Movie(String nome, String diretor, int lancamento, String genero, String classificacao, double preco, String imagem, String descricao){
        this.nome = nome;
        this.diretor = diretor;
        this.lancamento = lancamento;
        this.genero = genero;
        this.classificacao = classificacao;
        this.preco = preco;
        this.imagem = imagem;
        this.descricao = descricao;

    }
}
