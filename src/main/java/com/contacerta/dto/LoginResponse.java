package com.contacerta.dto;

import com.contacerta.entity.TipoUsuario;

public class LoginResponse {
    private String token;
    private String nome;
    private TipoUsuario tipo;
    private String email;

    public LoginResponse(String token, String nome, TipoUsuario tipo, String email) {
        this.token = token;
        this.nome = nome;
        this.tipo = tipo;
        this.email = email;
    }

    // Getters
    public String getToken() { return token; }
    public String getNome() { return nome; }
    public TipoUsuario getTipo() { return tipo; }
    public String getEmail() { return email; }
}