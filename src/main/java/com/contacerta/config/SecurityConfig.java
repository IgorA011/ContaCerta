package com.contacerta.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Arquivos estáticos - permitir sem autenticação
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()

                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Endpoints de consulta - permitidos para todos os usuários autenticados
                        .requestMatchers(
                                "/api/clientes",
                                "/api/clientes/{id}",
                                "/api/clientes/email/**",
                                "/api/clientes/search"
                        ).authenticated()

                        .requestMatchers(
                                "/api/produtos",
                                "/api/produtos/{id}"
                        ).authenticated()

                        .requestMatchers(
                                "/api/movimentacoes",
                                "/api/movimentacoes/{id}",
                                "/api/movimentacoes/saldo"
                        ).authenticated()

                        // Endpoints de criação/edição/exclusão - apenas para ADMIN
                        .requestMatchers(
                                "/api/clientes/**",
                                "/api/produtos/**",
                                "/api/movimentacoes/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Permite acesso aos recursos estáticos sem autenticação
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/css/**",
                "/js/**",
                "/img/**",
                "/favicon.ico",
                "/index.html"
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}