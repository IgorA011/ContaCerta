package com.contacerta.dto;

import com.contacerta.entity.TipoUsuario;

public class LoginRequest {
    private String email;
    private String senha;
    private TipoUsuario tipo;

    // Construtores
    public LoginRequest() {}

    public LoginRequest(String email, String senha, TipoUsuario tipo) {
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
}