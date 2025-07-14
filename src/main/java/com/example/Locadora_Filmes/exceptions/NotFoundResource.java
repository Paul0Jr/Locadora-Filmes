package com.example.Locadora_Filmes.exceptions;

public class NotFoundResource extends RuntimeException{
    public NotFoundResource(String mensagem) {
        super(mensagem);
    }


}
