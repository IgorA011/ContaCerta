package com.contacerta.config;

import com.contacerta.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AuthService authService;

    @Override
    public void run(String... args) throws Exception {
        // Criar usuários padrão ao iniciar a aplicação
        authService.criarUsuariosPadrao();
        System.out.println("✅ Usuários padrão verificados/criados com sucesso!");
    }
}