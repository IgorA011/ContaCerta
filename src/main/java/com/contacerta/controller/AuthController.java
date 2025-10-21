package com.contacerta.controller;

import com.contacerta.dto.LoginRequest;
import com.contacerta.dto.LoginResponse;
import com.contacerta.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realizar login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno no servidor");
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validar token JWT")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Token não fornecido ou formato inválido");
            }

            String token = authorizationHeader.substring(7);
            // A validação será feita automaticamente pelo filtro JWT
            return ResponseEntity.ok().body("Token válido");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token inválido");
        }
    }
}