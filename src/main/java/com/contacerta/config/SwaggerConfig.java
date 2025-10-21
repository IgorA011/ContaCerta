package com.contacerta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor Local")
                ))
                .info(new Info()
                        .title("Sistema Financeiro - Conta Certa API")
                        .version("1.0")
                        .description("""
                            API completa para gerenciamento de clientes, produtos e movimentações financeiras.
                            
                            ## Autenticação
                            1. Primeiro faça login em `/api/auth/login`
                            2. Copie o token retornado
                            3. Clique no botão **Authorize** acima e cole: `Bearer seu-token-aqui`
                            
                            ## Usuários de Teste
                            - **Admin:** admin@contacerta.com / admin123
                            - **Atendente:** atendente@contacerta.com / atendente123
                            """))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Cole o token JWT no formato: Bearer {token}")));
    }
}